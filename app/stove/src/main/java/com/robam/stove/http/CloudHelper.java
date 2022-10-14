package com.robam.stove.http;

import com.google.gson.Gson;
import com.robam.common.bean.BaseResponse;
import com.robam.common.http.ILife;
import com.robam.common.http.RetrofitCallback;
import com.robam.common.http.RetrofitClient;
import com.robam.common.utils.LogUtils;
import com.robam.stove.bean.CurveStep;
import com.robam.stove.constant.HostServer;
import com.robam.stove.request.CreateCurveStartReq;
import com.robam.stove.request.CurveSaveReq;
import com.robam.stove.request.GetCurveDetailReq;
import com.robam.stove.request.GetRecipeDetailReq;
import com.robam.stove.request.GetRecipesByDeviceReq;
import com.robam.stove.request.GetUserReq;
import com.robam.stove.request.RecipeSearchReq;

import java.util.List;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;

public class CloudHelper {
    private static final String APPLICATION_JSON_ACCEPT_APPLICATION_JSON = "application/json; Accept: application/json";
    private static ICloudService svr = RetrofitClient.getInstance().createApi(ICloudService.class, HostServer.apiHost);

    //获取灶具分类菜谱
    public static <T extends BaseResponse> void getRecipesByDevice(ILife iLife, String dc, String recipeType, int start, int limit,
                                                                            Class<T> entity,
                                                              final RetrofitCallback<T> callback) {
        String json = new GetRecipesByDeviceReq(dc, recipeType, start, limit).toString();
        RequestBody requestBody =
                RequestBody.create(MediaType.parse(APPLICATION_JSON_ACCEPT_APPLICATION_JSON), json);
        Call<ResponseBody> call = svr.getRecipesByDevice(requestBody);
        enqueue(iLife, entity, call, callback);
    }

    //获取菜谱详情
    public static <T extends BaseResponse> void getRecipeDetail(ILife iLife, long cookid, String entranceCode, String needStepsInfo,
                                                                Class<T> entity, final RetrofitCallback<T> callback) {
        String json = new GetRecipeDetailReq(cookid, entranceCode, needStepsInfo).toString();
        RequestBody requestBody =
                RequestBody.create(MediaType.parse(APPLICATION_JSON_ACCEPT_APPLICATION_JSON), json);
        Call<ResponseBody> call = svr.getRecipeDetail(requestBody);
        enqueue(iLife, entity, call, callback);
    }

    //获取曲线列表
    public static <T extends BaseResponse> void queryCurveCookbooks(ILife iLife, long userid, Class<T> entity, final RetrofitCallback<T> callback) {
        String json = new GetUserReq(userid).toString();
        RequestBody requestBody =
                RequestBody.create(MediaType.parse(APPLICATION_JSON_ACCEPT_APPLICATION_JSON), json);
        Call<ResponseBody> call = svr.queryCurveCookbooks(requestBody);
        enqueue(iLife, entity, call, callback);
    }
    //删除曲线
    public static <T extends BaseResponse> void delCurve(ILife iLife, long userid, long curveid, Class<T> entity, final RetrofitCallback<T> callback) {
        Call<ResponseBody> call = svr.delCurve(userid, curveid);
        enqueue(iLife, entity, call, callback);
    }
    //获取曲线详情
    public static <T extends BaseResponse> void getCurvebookDetail(ILife iLife, long curveid, Class<T> entity, final RetrofitCallback<T> callback) {
        String json = new GetCurveDetailReq(curveid).toString();
        RequestBody requestBody =
                RequestBody.create(MediaType.parse(APPLICATION_JSON_ACCEPT_APPLICATION_JSON), json);
        Call<ResponseBody> call = svr.getCurveCookDetail(requestBody);
        enqueue(iLife, entity, call, callback);
    }
    //菜谱搜索
    public static <T extends BaseResponse> void getCookbooksByName(ILife iLife, String name, boolean contain3rd, long userId, boolean notNeedSearchHistory,
                                                                   boolean needStatisticCookbook, Class<T> entity, final RetrofitCallback<T> callback) {
        String json = new RecipeSearchReq(name, contain3rd, userId, notNeedSearchHistory, needStatisticCookbook).toString();
        RequestBody requestBody =
                RequestBody.create(MediaType.parse(APPLICATION_JSON_ACCEPT_APPLICATION_JSON), json);
        Call<ResponseBody> call = svr.getCookbooksByName(requestBody);
        enqueue(iLife, entity, call, callback);
    }
    //创建曲线开始记录
    public static <T extends BaseResponse> void createCurveStart(ILife iLife, long userId, String guid, int stoveId, Class<T> entity, final RetrofitCallback<T> callback) {
        String json = new CreateCurveStartReq(userId, guid, stoveId).toString();
        RequestBody requestBody =
                RequestBody.create(MediaType.parse(APPLICATION_JSON_ACCEPT_APPLICATION_JSON), json);
        Call<ResponseBody> call = svr.createCurveStart(requestBody);
        enqueue(iLife, entity, call, callback);
    }
    //曲线保存
    public static <T extends BaseResponse> void curveSave(ILife iLife, long userId, String guid, String name, List<CurveStep> stepList, Class<T> entity, final RetrofitCallback<T> callback) {
        String json = new CurveSaveReq(userId, guid, name, stepList).toString();
        RequestBody requestBody =
                RequestBody.create(MediaType.parse(APPLICATION_JSON_ACCEPT_APPLICATION_JSON), json);
        Call<ResponseBody> call = svr.curveSave(requestBody);
        enqueue(iLife, entity, call, callback);
    }

    //统一处理回调
    private static <T extends BaseResponse> void enqueue(ILife iLife, final Class<T> entity, Call<ResponseBody> call, final RetrofitCallback<T> callback) {
        call.enqueue(new retrofit2.Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                //生命周期判断
                if (iLife.isDestroyed()) {
                    LogUtils.e("page isDestroyed");
                    return;
                }
                try {
                    String body = response.body().string();
                    Gson gson = new Gson();
                    T object = gson.fromJson(body, entity);
                    BaseResponse rcReponse = object;
                    if (null != rcReponse && rcReponse.rc == 0) {
                        if (null != callback) {
                            callback.onSuccess(object);
                            return;
                        }
                    }
                    if (null != callback && null != rcReponse)
                        callback.onFaild(rcReponse.msg);
                } catch (Exception e) {
                    if (null != callback)
                        callback.onFaild("exception:" + e.getMessage());
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable throwable) {
                if (iLife.isDestroyed()) {
                    return;
                }
                if (null != callback)
                    callback.onFaild(throwable.toString());
            }
        });
    }
}

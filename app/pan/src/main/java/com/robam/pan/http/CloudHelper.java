package com.robam.pan.http;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.robam.common.bean.BaseResponse;
import com.robam.common.http.ILife;
import com.robam.common.http.RetrofitCallback;
import com.robam.common.http.RetrofitClient;
import com.robam.common.utils.LogUtils;
import com.robam.pan.bean.CurveStep;
import com.robam.pan.constant.HostServer;
import com.robam.pan.request.CookingCurveMarkStepReq;
import com.robam.pan.request.CreateCurveStartReq;
import com.robam.pan.request.CurveSaveReq;
import com.robam.pan.request.GetCurveDetailReq;
import com.robam.pan.request.GetRecipeDetailReq;
import com.robam.pan.request.GetRecipesByDeviceReq;
import com.robam.pan.request.GetUserReq;
import com.robam.pan.request.RecipeSearchReq;

import java.util.List;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.http.Headers;

public class CloudHelper {
    private static final String APPLICATION_JSON_ACCEPT_APPLICATION_JSON = "application/json; Accept: application/json";
    private static ICloudService svr = RetrofitClient.getInstance().createApi(ICloudService.class, HostServer.apiHost);

    //获取无人锅分类菜谱
    public static <T extends BaseResponse> void getRecipesByDevice(ILife iLife, long userId, String dc, int pageIndex, int pageSize, String dp, List excludeIds,
                                                                   Class<T> entity,
                                                                   final RetrofitCallback<T> callback) {
        String json = new GetRecipesByDeviceReq(userId, dc, pageIndex, pageSize, dp, excludeIds).toString();
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
    //菜谱搜索
    public static <T extends BaseResponse> void getCookbooksByName(ILife iLife, String devicePlat, boolean needSearchHistory, int pageIndex, int pageSize,
                                                                   String search, int searchType, long userId, Class<T> entity, final RetrofitCallback<T> callback) {
        String json = new RecipeSearchReq(devicePlat, needSearchHistory, pageIndex, pageSize, search, searchType, userId).toString();
        RequestBody requestBody =
                RequestBody.create(MediaType.parse(APPLICATION_JSON_ACCEPT_APPLICATION_JSON), json);
        Call<ResponseBody> call = svr.getCookbooksByName(requestBody);
        enqueue(iLife, entity, call, callback);
    }
    //获取曲线详情
    public static <T extends BaseResponse> void getCurvebookDetail(ILife iLife, long curveid, String guid, Class<T> entity, final RetrofitCallback<T> callback) {
        String json = null;
        if (curveid != 0) {
            Gson gson = new GsonBuilder().setExclusionStrategies(new ExclusionStrategy() {
                @Override
                public boolean shouldSkipField(FieldAttributes f) {
                    // 忽略某些字段
                    return f.getName().equals("deviceGuid");
                }

                @Override
                public boolean shouldSkipClass(Class<?> clazz) {
                    // 忽略某个class
                    return false;
                }
            }).create();
            json = gson.toJson(new GetCurveDetailReq(curveid));
        } else {
            Gson gson = new GsonBuilder().setExclusionStrategies(new ExclusionStrategy() {
                @Override
                public boolean shouldSkipField(FieldAttributes f) {
                    // 忽略某些字段
                    return f.getName().equals("id");
                }

                @Override
                public boolean shouldSkipClass(Class<?> clazz) {
                    // 忽略某个class
                    return false;
                }
            }).create();
            json = gson.toJson(new GetCurveDetailReq(guid));
        }
        RequestBody requestBody =
                RequestBody.create(MediaType.parse(APPLICATION_JSON_ACCEPT_APPLICATION_JSON), json);
        Call<ResponseBody> call = svr.getCurveCookDetail(requestBody);
        enqueue(iLife, entity, call, callback);
    }
    //删除曲线
    public static <T extends BaseResponse> void delCurve(ILife iLife, long userid, long curveid, Class<T> entity, final RetrofitCallback<T> callback) {
        Call<ResponseBody> call = svr.delCurve(userid, curveid);
        enqueue(iLife, entity, call, callback);
    }
    //获取我的最爱
    public static <T extends BaseResponse> void getPotPCookPage(ILife iLife, String guid, long userId, Class<T> entity, final RetrofitCallback<T> callback) {
        Call<ResponseBody> call = svr.getPotPCookPage(guid, userId, 0, 100);
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
    //创建曲线开始记录
    public static <T extends BaseResponse> void createCurveStart(ILife iLife, long userId, String guid, int stoveId, Class<T> entity, final RetrofitCallback<T> callback) {
        String json = new CreateCurveStartReq(userId, guid, stoveId).toString();
        RequestBody requestBody =
                RequestBody.create(MediaType.parse(APPLICATION_JSON_ACCEPT_APPLICATION_JSON), json);
        Call<ResponseBody> call = svr.createCurveStart(requestBody);
        enqueue(iLife, entity, call, callback);
    }
    //曲线保存
    public static <T extends BaseResponse> void curveSave(ILife iLife, long userId, long curveId, String guid, String name, int needTime, List<CurveStep> stepList, String curveStage, Class<T> entity, final RetrofitCallback<T> callback) {
        String json = new CurveSaveReq(userId, curveId, guid, name, needTime, stepList, curveStage).toString();
        RequestBody requestBody =
                RequestBody.create(MediaType.parse(APPLICATION_JSON_ACCEPT_APPLICATION_JSON), json);
        Call<ResponseBody> call = svr.curveSave(requestBody);
        enqueue(iLife, entity, call, callback);
    }
    //更新曲线 步骤标记
    public static <T extends BaseResponse> void cookingCurveMarkStep(ILife iLife, long curveId, List<CurveStep> stepList, Class<T> entity, final RetrofitCallback<T> callback) {
        String json = new CookingCurveMarkStepReq(curveId, stepList).toString();
        RequestBody requestBody =
                RequestBody.create(MediaType.parse(APPLICATION_JSON_ACCEPT_APPLICATION_JSON), json);
        Call<ResponseBody> call = svr.cookingCurveMarkStep(requestBody);
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

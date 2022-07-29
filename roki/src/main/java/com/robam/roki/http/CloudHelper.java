package com.robam.roki.http;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;

import com.google.gson.Gson;
import com.robam.common.bean.BaseResponse;
import com.robam.common.http.RetrofitCallback;
import com.robam.common.ui.HeadPage;
import com.robam.common.utils.LogUtils;
import com.robam.roki.constant.HostServer;
import com.robam.roki.request.CookbookbythemeIdReq;
import com.robam.roki.request.CookingKnowledgeReq;
import com.robam.roki.request.GetVerifyCodeReq;
import com.robam.roki.request.GroundingRecipesByDcReq;
import com.robam.roki.request.TagOtherCooksReq;
import com.robam.roki.request.TagOtherThemeReq;
import com.robam.roki.request.WeekTopsReq;

import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;

public class CloudHelper {

    private static final String APPLICATION_JSON_ACCEPT_APPLICATION_JSON = "application/json; Accept: application/json";
    private static ICloudService svr = RetrofitClient.getInstance().createApi(ICloudService.class, HostServer.apiHost);

    public static <T extends BaseResponse> void getVerifyCode(HeadPage headPage, String phone, Class<T> entity,
                                                                  final RetrofitCallback<T> callback) {
        String json = new GetVerifyCodeReq(phone).toString();
        RequestBody requestBody =
                RequestBody.create(MediaType.parse(APPLICATION_JSON_ACCEPT_APPLICATION_JSON), json);
        Call<ResponseBody> call = svr.getVerifyCode(requestBody);
        enqueue(headPage, entity, call, callback);
    }

    public static <T extends BaseResponse> void getCookingKnowledge(HeadPage headPage, String typeCode, int isActive, String lable, int pageNo, int pageSize, Class<T> entity, final RetrofitCallback<T> callback) {
        String json = new CookingKnowledgeReq(typeCode, isActive, lable, pageNo, pageSize).toString();

        //                JSONObject jsonObject = new JSONObject();
        //                jsonObject.putOpt("typeCode", typeCode);
        //                jsonObject.putOpt("isActive", isActive);
        //                jsonObject.putOpt("lable", lable);
        //                jsonObject.putOpt("pageNo", pageNo);
        //                jsonObject.putOpt("pageSize", pageSize);
        RequestBody requestBody =
                RequestBody.create(MediaType.parse(APPLICATION_JSON_ACCEPT_APPLICATION_JSON), json);
        Call<ResponseBody> call = svr.getCookingKnowledge(requestBody);
        enqueue(headPage, entity, call, callback);
    }
    /**
     * 获取某个标签或推荐或周上新的分页菜谱(随机并排除)
     */
    public static <T extends BaseResponse> void getbyTagOtherCooks(HeadPage headPage, Long cookbookTagId, boolean needStatisticCookbook, int pageNo, int pageSize, int type, List<Long> excludeCookIds, Class<T> entity,
                                                                   final RetrofitCallback<T> callback) {
        String json = new TagOtherCooksReq(cookbookTagId, needStatisticCookbook, pageNo, pageSize, type, excludeCookIds).toString();
        RequestBody requestBody =
                RequestBody.create(MediaType.parse(APPLICATION_JSON_ACCEPT_APPLICATION_JSON), json);
        Call<ResponseBody> call = svr.getbyTagOtherCooks(requestBody);
        enqueue(headPage, entity, call, callback);
    }
    /**
     * 根据主题id查询下属所有菜单
     *
     * @param lang
     * @param limit
     * @param start
     * @param themeId
     * @param callback
     */
    public static <T extends BaseResponse> void getCookBookBythemeId(HeadPage headPage, String lang, long limit, int start, int themeId, Class<T> entity,
                                                                     final RetrofitCallback<T> callback) {
        String json = new CookbookbythemeIdReq(lang, limit, start, themeId).toString();
        RequestBody requestBody =
                RequestBody.create(MediaType.parse(APPLICATION_JSON_ACCEPT_APPLICATION_JSON), json);
        Call<ResponseBody> call = svr.getCookBookBythemeId(requestBody);
        enqueue(headPage, entity, call, callback);

    }
    /**
     * 获取某个标签或推荐或周上新的主题
     */
    public static <T extends BaseResponse> void getByTagOtherThemes(HeadPage headPage, Long cookbookTagId, boolean needStatisticCookbook, int pageNo, int pageSize, int type, Class<T> entity,
                                                                    final RetrofitCallback<T> callback) {
        String json = new TagOtherThemeReq(cookbookTagId, needStatisticCookbook, pageNo, pageSize, type).toString();
        RequestBody requestBody =
                RequestBody.create(MediaType.parse(APPLICATION_JSON_ACCEPT_APPLICATION_JSON), json);
        Call<ResponseBody> call = svr.getByTagOtherThemes(requestBody);
        enqueue(headPage, entity, call, callback);
    }
    /**
     * 菜谱周排名
     */
    public static <T extends BaseResponse> void getWeekTops(HeadPage headPage, String weekTime, int pageNo, int pageSize, Class<T> entity,
                                                            final RetrofitCallback<T> callback) {
        String json = new WeekTopsReq(weekTime, pageNo, pageSize).toString();
        RequestBody requestBody =
                RequestBody.create(MediaType.parse(APPLICATION_JSON_ACCEPT_APPLICATION_JSON), json);
        Call<ResponseBody> call = svr.getWeekTops(requestBody);
        enqueue(headPage, entity, call, callback);
    }
    /**
     * 获取主题菜谱列表精选专题
     */
    public static <T extends BaseResponse> void getThemeRecipeList(HeadPage headPage, Class<T> entity, final RetrofitCallback<T> callback) {
        RequestBody requestBody =
                RequestBody.create(MediaType.parse(APPLICATION_JSON_ACCEPT_APPLICATION_JSON), "{}");
        Call<ResponseBody> call = svr.getThemeRecipeList(requestBody);
        enqueue(headPage, entity, call, callback);

    }

    /**
     *  设备菜谱分类
     * @param userId
     * @param dc
     * @param recipeType
     * @param start
     * @param limit
     * @param devicePlat
     * @param callback
     */
    public static <T extends BaseResponse> void getGroundingRecipesByDc(HeadPage headPage, long userId, String dc, String recipeType, int start, int limit, String devicePlat, Class<T> entity,
                                                                        final RetrofitCallback<T> callback) {
        String json = new GroundingRecipesByDcReq(userId, dc, recipeType, start, limit, devicePlat).toString();
        RequestBody requestBody =
                RequestBody.create(MediaType.parse(APPLICATION_JSON_ACCEPT_APPLICATION_JSON), json);
        Call<ResponseBody> call = svr.getGroundingRecipesByDevice(requestBody);
        enqueue(headPage, entity, call, callback);

    }

        //统一处理回调
    private static <T extends BaseResponse> void enqueue(HeadPage headPage, final Class<T> entity, Call<ResponseBody> call, final RetrofitCallback<T> callback) {
        call.enqueue(new retrofit2.Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                //生命周期判断
                if (headPage.isDestroyed()) {
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
                if (headPage.isDestroyed()) {
                    return;
                }
                if (null != callback)
                    callback.onFaild(throwable.toString());
            }
        });
    }

}

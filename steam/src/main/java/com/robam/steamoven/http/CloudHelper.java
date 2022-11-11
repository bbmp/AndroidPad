package com.robam.steamoven.http;

import com.google.gson.Gson;
import com.robam.common.bean.BaseResponse;
import com.robam.common.http.ILife;
import com.robam.common.http.RetrofitCallback;
import com.robam.common.http.RetrofitClient;
import com.robam.common.utils.LogUtils;
import com.robam.steamoven.bean.SteamCurveDetail;
import com.robam.steamoven.constant.HostServer;
import com.robam.steamoven.request.GetCurveDetailReq;
import com.robam.steamoven.request.GetCurveReq;
import com.robam.steamoven.request.GetDeviceParamsReq;
import com.robam.steamoven.request.GetRecipeDetailReq;
import com.robam.steamoven.request.GetUserReq;
import com.robam.steamoven.request.SaveCurveDetailReq;

import java.io.Serializable;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;

public class CloudHelper {
    private static final String APPLICATION_JSON_ACCEPT_APPLICATION_JSON = "application/json; Accept: application/json";
    private static ICloudService svr = RetrofitClient.getInstance().createApi(ICloudService.class, HostServer.apiHost);

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
    //获取设备参数
    public static <T extends BaseResponse> void getDeviceParams(ILife iLife, long userid, String deviceType, String category,
                                                                Class<T> entity, final RetrofitCallback<T> callback) {
        String json = new GetDeviceParamsReq(userid, deviceType, category).toString();
        RequestBody requestBody =
                RequestBody.create(MediaType.parse(APPLICATION_JSON_ACCEPT_APPLICATION_JSON), json);
        Call<ResponseBody> call = svr.getDeviceParams(requestBody);
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

    public static <T extends BaseResponse> void getCurveBookForDevice(ILife iLife, String guid, Class<T> entity, final RetrofitCallback<T> callback) {
        String json = new GetCurveReq(guid).toString();
        RequestBody requestBody =
                RequestBody.create(MediaType.parse(APPLICATION_JSON_ACCEPT_APPLICATION_JSON), json);
        Call<ResponseBody> call = svr.getCurveCookDetail(requestBody);
        enqueue(iLife, entity, call, callback);
    }

    //获取设备告警配置
    public static <T extends BaseResponse> void getDeviceErrorInfo(ILife iLife, Class<T> entity, final RetrofitCallback<T> callback) {
        //String json = new GetDeviceParamsReq(userid, deviceType, category).toString();
        RequestBody requestBody =
                RequestBody.create(MediaType.parse(APPLICATION_JSON_ACCEPT_APPLICATION_JSON), "");
        Call<ResponseBody> call = svr.getAllDeviceErrorInfo(requestBody);
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

    public static <T extends BaseResponse> void saveCurveData(ILife iLife, SteamCurveDetail payLoad, Class<T> entity, final RetrofitCallback<T> callback) {
        String json = new SaveCurveDetailReq(payLoad).toString();
        RequestBody requestBody =
                RequestBody.create(MediaType.parse(APPLICATION_JSON_ACCEPT_APPLICATION_JSON), json);
        Call<ResponseBody> call = svr.saveCurveData(requestBody);
        enqueue(iLife, entity, call, callback);
    }

    public static <T extends BaseResponse> void saveCurveStepData(ILife iLife, SteamCurveDetail payLoad, Class<T> entity, final RetrofitCallback<T> callback) {
        String json = new SaveCurveDetailReq(payLoad).toString();
        RequestBody requestBody =
                RequestBody.create(MediaType.parse(APPLICATION_JSON_ACCEPT_APPLICATION_JSON), json);
        Call<ResponseBody> call = svr.saveCurveStepData(requestBody);
        enqueue(iLife, entity, call, callback);
    }

    public static <T extends BaseResponse> void downDeviceErrorInfoData(ILife iLife, SteamCurveDetail payLoad, Class<T> entity, final RetrofitCallback<T> callback) {
        String json = new SaveCurveDetailReq(payLoad).toString();
        RequestBody requestBody =
                RequestBody.create(MediaType.parse(APPLICATION_JSON_ACCEPT_APPLICATION_JSON), json);

        Call<ResponseBody> call = svr.saveCurveStepData(requestBody);
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

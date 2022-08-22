package com.robam.ventilator.http;

import com.google.gson.Gson;
import com.robam.common.bean.BaseResponse;
import com.robam.common.http.ILife;
import com.robam.common.http.RetrofitCallback;
import com.robam.common.utils.LogUtils;
import com.robam.ventilator.constant.HostServer;
import com.robam.ventilator.request.BindDeviceReq;
import com.robam.ventilator.request.GetUserReq;
import com.robam.ventilator.request.GetVerifyCodeReq;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;

public class CloudHelper {
    private static final String APPLICATION_JSON_ACCEPT_APPLICATION_JSON = "application/json; Accept: application/json";
    private static ICloudService svr = RetrofitClient.getInstance().createApi(ICloudService.class, HostServer.apiHost);

    //获取验证码
    public static <T extends BaseResponse> void getVerifyCode(ILife iLife, String phone, Class<T> entity,
                                                              final RetrofitCallback<T> callback) {
        String json = new GetVerifyCodeReq(phone).toString();
        RequestBody requestBody =
                RequestBody.create(MediaType.parse(APPLICATION_JSON_ACCEPT_APPLICATION_JSON), json);
        Call<ResponseBody> call = svr.getVerifyCode(requestBody);
        enqueue(iLife, entity, call, callback);
    }
    //获取token
    public static <T extends BaseResponse> void getToken(ILife iLife, String loginType
            ,String sjhm
            ,String smsCode
            ,String password
            ,String client_id
            ,String client_secret
            ,String appType
            , Class<T> entity, final RetrofitCallback<T> callback) {

        Call<ResponseBody> call = svr.getToken(loginType ,sjhm ,smsCode ,password, client_id ,client_secret ,appType);
        enqueue(iLife, entity, call, callback);

    }

    /**
     * 获取用户信息
     * @param authorization
     * @param callback
     */
    public static <T extends BaseResponse> void getUserInfo(ILife iLife, String authorization,
                                                      Class<T> entity, final RetrofitCallback<T> callback) {
        Call<ResponseBody> call = svr.getOauth(authorization);
        enqueue(iLife, entity, call, callback);

    }
    //绑定设备
    public static <T extends BaseResponse> void bindDevice(ILife iLife, long userId, String guid, String name,
                                                           boolean isOwner, Class<T> entity, final RetrofitCallback<T> callback) {
        String json = new BindDeviceReq(userId, guid, name, isOwner).toString();
        RequestBody requestBody =
                RequestBody.create(MediaType.parse(APPLICATION_JSON_ACCEPT_APPLICATION_JSON), json);
        Call<ResponseBody> call = svr.bindDevice(requestBody);
        enqueue(iLife, entity, call, callback);
    }
    //获取设备
    public static <T extends BaseResponse> void getDevices(ILife iLife, long userId, Class<T> entity, final RetrofitCallback<T> callback) {
        String json = new GetUserReq(userId).toString();
        RequestBody requestBody =
                RequestBody.create(MediaType.parse(APPLICATION_JSON_ACCEPT_APPLICATION_JSON), json);
        Call<ResponseBody> call = svr.getDevices(requestBody);
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
                    if (null != rcReponse && rcReponse.rc == 0) { //统一判断rc =0为正常
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
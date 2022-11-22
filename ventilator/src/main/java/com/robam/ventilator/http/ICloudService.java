package com.robam.ventilator.http;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ICloudService {
    //获取设备参数
    String getDeviceByParams = "/rest/dms/api/device-configuration/get-by-params/new";
    //获取验证码
    String getVerifyCode = "/rest/api/cas/verify-code/get";

    String getToken = "/rest/auth/api/oauth/token?grant_type=roki";
    //用户信息
    String getUserInfo = "/rest/ums/api/user/get/oauth" ;
    //绑定设备
    String bindDevice = "/rest/dms/api/device/bind";
    //获取设备
    String getDevices = "/rest/dms/api/device/get";
    //获取二维码
//    String getCode = "/rest/gateway/api/auth/scan/key";

    String getLoginStatus = "/rest/gateway/api/auth/scan/login";
    //扫码登录
    String login = "/rest/api/cas/app/login";
    //获取设备绑定的用户
    String getDeviceUsers = "/rest/dms/api/device/user/get";
    //删除绑定的用户
    String unbindDevice = "/rest/dms/api/device/unbind";
    //检查版本
    String checkAppVersion = "/rest/dms/api/app/version/check";
    //获取烟蒸烤联动配置
    String getLinkageConfig = "/rest/dms/api/linkage/config/{guid}";
    //设置烟蒸烤联动信息
    String setLinkageConfig = "/rest/dms/api/linkage/config";

    @POST(getVerifyCode)
    @Headers("Content-Type: application/json")
    Call<ResponseBody> getVerifyCode(@Body RequestBody body);

    @FormUrlEncoded
    @POST(getToken)
    @Headers("Content-Type: application/x-www-form-urlencoded")
    Call<ResponseBody> getToken(@Field("loginType") String loginType
            ,@Field("sjhm") String sjhm
            ,  @Field("smsCode") String smsCode
            , @Field("password") String password
            , @Field("client_id") String client_id
            ,@Field("client_secret") String client_secret
            ,@Field("appType") String appType);

    @GET(getUserInfo)
    Call<ResponseBody> getOauth(@Header("authorization") String authorization);

    @POST(bindDevice)
    @Headers("Content-Type: application/json")
    Call<ResponseBody> bindDevice(@Body RequestBody body);

    @POST(getDevices)
    @Headers("Content-Type: application/json")
    Call<ResponseBody> getDevices(@Body RequestBody body);

    @GET(getLoginStatus)
    Call<ResponseBody> getLoginStatus(@Query("key") String key);

    @POST(login)
    @Headers("Content-Type: application/json")
    Call<ResponseBody> login(@Body RequestBody body);

    @POST(getDeviceUsers)
    @Headers("Content-Type: application/json")
    Call<ResponseBody> getDeviceUsers(@Body RequestBody body);

    @POST(unbindDevice)
    @Headers("Content-Type: application/json")
    Call<ResponseBody> unbindDevice(@Body RequestBody body);

    @POST(getDeviceByParams)
    @Headers("Content-Type: application/json")
    Call<ResponseBody> getDeviceParams(@Body RequestBody body);

    @POST(checkAppVersion)
    @Headers("Content-Type: application/json")
    Call<ResponseBody> checkAppVersion(@Body RequestBody body);

    @GET(getLinkageConfig)
    Call<ResponseBody> getLinkageConfig(@Path ("guid") String guid, @Query("userId") long userId);

    @POST(setLinkageConfig)
    @Headers("Content-Type: application/json")
    Call<ResponseBody> setLinkageConfig(@Body RequestBody body);
}

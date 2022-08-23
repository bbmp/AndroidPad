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
import retrofit2.http.Query;

public interface ICloudService {
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
    String getCode = "/rest/gateway/api/auth/scan/key";

    String getLoginStatus = "/rest/gateway/api/auth/scan/login";

    String login = "/rest/api/cas/app/login";

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

    @GET(getCode)
    Call<ResponseBody> getCode();

    @GET(getLoginStatus)
    Call<ResponseBody> getLoginStatus(@Query("key") String key);

    @POST(login)
    @Headers("Content-Type: application/json")
    Call<ResponseBody> login(@Body RequestBody body);

}

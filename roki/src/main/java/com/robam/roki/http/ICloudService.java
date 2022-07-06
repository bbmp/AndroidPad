package com.robam.roki.http;

import com.robam.common.bean.BaseResponse;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface ICloudService<T extends BaseResponse> {
    String getVerifyCode = "/rest/api/cas/verify-code/get";

    @POST(getVerifyCode)
    @Headers("Content-Type: application/json")
    Call<ResponseBody> getVerifyCode(@Body RequestBody body);

}

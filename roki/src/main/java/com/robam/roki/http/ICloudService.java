package com.robam.roki.http;

import com.robam.common.bean.BaseResponse;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface ICloudService {
    String getVerifyCode = "/rest/api/cas/verify-code/get";
    //获取厨房支持列表
    String getCookingKnowledge = "/rest/ops/api/cookingKnowledge/getCookingKnowledge";

    @POST(getVerifyCode)
    @Headers("Content-Type: application/json")
    Call<ResponseBody> getVerifyCode(@Body RequestBody body);

    /**
     * 获取厨房知识列表
     */
    @POST(getCookingKnowledge)
    @Headers("Content-Type: application/json")
    Call<ResponseBody> getCookingKnowledge(@Body RequestBody body);
}

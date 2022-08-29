package com.robam.pan.http;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface ICloudService {
    //根据设备品类获取所有菜谱(只包括导航菜谱)
    String getRecipesByDevice = "/rest/api/cookbook/grounding/get-by-dc";
    //获取菜谱详情
    String getRecipeDetail ="/rest/cks/api/cookbook/details/get-by-id";


    @POST(getRecipesByDevice)
    @Headers("Content-Type: application/json")
    Call<ResponseBody> getRecipesByDevice(@Body RequestBody body);

    @POST(getRecipeDetail)
    @Headers("Content-Type: application/json")
    Call<ResponseBody> getRecipeDetail(@Body RequestBody body);

}

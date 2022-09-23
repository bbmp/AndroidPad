package com.robam.steamoven.http;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface ICloudService {
    //获取设备参数
    String getDeviceByParams = "/rest/dms/api/device-configuration/get-by-params/new";
    //删除烹饪曲线
    String delCurveUrl= "/rest/cks/api/curve_cookbook/deleteCurveCookbook";
    //获取//烹饪曲线列表
    String queryCurveCookbooks = "/rest/cks/api/curve_cookbook/v2/cooking_curve/queryByUserId";

    @POST(queryCurveCookbooks)
    @Headers("Content-Type: application/json")
    Call<ResponseBody> queryCurveCookbooks(@Body RequestBody body);

    @GET(delCurveUrl)
    Call<ResponseBody> delCurve(@Query("userId") long userId, @Query("curveCookbookId") long curveid);

    @POST(getDeviceByParams)
    @Headers("Content-Type: application/json")
    Call<ResponseBody> getDeviceParams(@Body RequestBody body);
}

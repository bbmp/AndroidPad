package com.robam.stove.http;

import androidx.core.app.NotificationCompat;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface ICloudService {
    //根据设备品类获取所有菜谱(只包括导航菜谱)
    String getRecipesByDevice = "/rest/api/cookbook/grounding/get-by-dc";
    //获取菜谱详情
    String getRecipeDetail ="/rest/cks/api/cookbook/details/get-by-id";
    //获取//烹饪曲线列表
    String queryCurveCookbooks = "/rest/cks/api/curve_cookbook/v2/cooking_curve/queryByUserId";
    //删除烹饪曲线
    String delCurveUrl= "/rest/cks/api/curve_cookbook/deleteCurveCookbook";

    @POST(getRecipesByDevice)
    @Headers("Content-Type: application/json")
    Call<ResponseBody> getRecipesByDevice(@Body RequestBody body);

    @POST(getRecipeDetail)
    @Headers("Content-Type: application/json")
    Call<ResponseBody> getRecipeDetail(@Body RequestBody body);

    @POST(queryCurveCookbooks)
    @Headers("Content-Type: application/json")
    Call<ResponseBody> queryCurveCookbooks(@Body RequestBody body);

    @GET(delCurveUrl)
    Call<ResponseBody> delCurve(@Query("userId") long userId, @Query("curveCookbookId") long curveid);
}

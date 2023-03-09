package com.robam.stove.http;

import androidx.core.app.NotificationCompat;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ICloudService {
    //根据设备品类获取所有菜谱(只包括导航菜谱)
    String getRecipesByDevice = "/rest/cook/api/cookbook/random/getCookbook";
    //获取菜谱详情
//    String getRecipeDetail ="/rest/cks/api/cookbook/details/get-by-id";
    String getRecipeDetail = "/rest/cook/api/cookbook/getCookbookByIdAndUserId";
    //获取//烹饪曲线列表
//    String queryCurveCookbooks = "/rest/cks/api/curve_cookbook/v2/cooking_curve/queryByUserId";
    String queryCurveCookbooks = "/rest/cook/api/curve/query-by-userId";
    //删除烹饪曲线
//    String delCurveUrl= "/rest/cks/api/curve_cookbook/deleteCurveCookbook";
    String delCurveUrl = "/rest/cook/api/curve/{id}";
    //曲线详情
//    String curveCookDetailUrl= "/rest/cks/api/curve_cookbook/v2/cooking_curve/query";
    String curveCookDetailUrl = "/rest/cook/api/curve/query";
    //菜谱搜索
    String getCookbooksByName = "/rest/cook/api/cookbook/search/query";
    //曲线创建开始记录
//    String createCurveStart = "/rest/cks/api/curve_cookbook/v2/cooking_curve/save";
    String createCurveStart = "/rest/cook/api/curve/start";
    //更新曲线状态
//    String updateCurveState = "/rest/cks/api/curve_cookbook/v2/cooking_curve/updateCurveState";
    String updateCurveState = "/rest/cook/api/curve/update-curve-state";
    //曲线保存
//    String submitStepUrl="/rest/cks/api/curve_cookbook/v2/cooking_curve_step/update";
    String submitStepUrl = "/rest/cook/api/curve/step/update";
    //更新标记步骤
//    String cookingCurveMarkStep = "/rest/cks/api/curve_cookbook/v2/cooking_curve/markStep";
    String cookingCurveMarkStep = "/rest/cook/api/curve/mark-step";


    @POST(getRecipesByDevice)
    @Headers("Content-Type: application/json")
    Call<ResponseBody> getRecipesByDevice(@Body RequestBody body);

    @GET(getRecipeDetail)
    Call<ResponseBody> getRecipeDetail(@Query("userId") long userId, @Query("cookbookId") long cookbookId);

    @POST(queryCurveCookbooks)
    @Headers("Content-Type: application/json")
    Call<ResponseBody> queryCurveCookbooks(@Body RequestBody body);

    @DELETE(delCurveUrl)
    Call<ResponseBody> delCurve(@Path("id") long curveid, @Query("userId") long userId);

    @POST(curveCookDetailUrl)
    @Headers("Content-Type: application/json")
    Call<ResponseBody> getCurveCookDetail(@Body RequestBody body);

    @POST(getCookbooksByName)
    @Headers("Content-Type: application/json")
    Call<ResponseBody> getCookbooksByName(@Body RequestBody body);

    @POST(createCurveStart)
    @Headers("Content-Type: application/json")
    Call<ResponseBody> createCurveStart(@Body RequestBody body);

    @POST(updateCurveState)
    @Headers("Content-Type: application/json")
    Call<ResponseBody> updateCurveState(@Body RequestBody body);

    @POST(submitStepUrl)
    @Headers("Content-Type: application/json")
    Call<ResponseBody> curveSave(@Body RequestBody body);

    @POST(cookingCurveMarkStep)
    @Headers("Content-Type: application/json")
    Call<ResponseBody> cookingCurveMarkStep(@Body RequestBody body);
}

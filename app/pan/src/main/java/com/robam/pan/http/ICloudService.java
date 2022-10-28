package com.robam.pan.http;

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
    //菜谱搜索
    String getCookbooksByName = "/rest/cks/api/cookbook/by-name/search";
    //我的最爱
    String getPotPCookPage = "/rest/cks/api/smart-pan/page";
    //获取//烹饪曲线列表
    String queryCurveCookbooks = "/rest/cks/api/curve_cookbook/v2/cooking_curve/queryByUserId";
    //曲线详情
    String curveCookDetailUrl= "/rest/cks/api/curve_cookbook/v2/cooking_curve/query";
    //删除烹饪曲线
    String delCurveUrl= "/rest/cks/api/curve_cookbook/deleteCurveCookbook";
    //曲线创建开始记录
    String createCurveStart = "/rest/cks/api/curve_cookbook/v2/cooking_curve/save";
    //曲线保存
    String submitStepUrl="/rest/cks/api/curve_cookbook/v2/cooking_curve_step/update";
    //更新标记步骤
    String cookingCurveMarkStep = "/rest/cks/api/curve_cookbook/v2/cooking_curve/markStep"; //更新标记步骤

    @POST(getRecipesByDevice)
    @Headers("Content-Type: application/json")
    Call<ResponseBody> getRecipesByDevice(@Body RequestBody body);

    @POST(getRecipeDetail)
    @Headers("Content-Type: application/json")
    Call<ResponseBody> getRecipeDetail(@Body RequestBody body);

    @POST(getCookbooksByName)
    @Headers("Content-Type: application/json")
    Call<ResponseBody> getCookbooksByName(@Body RequestBody body);

    @GET(getPotPCookPage)
    Call<ResponseBody> getPotPCookPage(@Query("deviceGuid") String guid , @Query("userId") long userId , @Query("pageNo") Integer pageNo , @Query("pageSize") Integer pageSize);

    @POST(queryCurveCookbooks)
    @Headers("Content-Type: application/json")
    Call<ResponseBody> queryCurveCookbooks(@Body RequestBody body);

    @POST(curveCookDetailUrl)
    @Headers("Content-Type: application/json")
    Call<ResponseBody> getCurveCookDetail(@Body RequestBody body);

    @GET(delCurveUrl)
    Call<ResponseBody> delCurve(@Query("userId") long userId, @Query("curveCookbookId") long curveid);

    @POST(createCurveStart)
    @Headers("Content-Type: application/json")
    Call<ResponseBody> createCurveStart(@Body RequestBody body);

    @POST(submitStepUrl)
    @Headers("Content-Type: application/json")
    Call<ResponseBody> curveSave(@Body RequestBody body);

    @POST(cookingCurveMarkStep)
    @Headers("Content-Type: application/json")
    Call<ResponseBody> cookingCurveMarkStep(@Body RequestBody body);
}

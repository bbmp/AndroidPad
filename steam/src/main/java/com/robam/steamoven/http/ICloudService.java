package com.robam.steamoven.http;

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
    //获取设备参数
    String getDeviceByParams = "/rest/dms/api/device-configuration/get-by-params/new";
    //删除烹饪曲线
    //String delCurveUrl= "/rest/cks/api/curve_cookbook/deleteCurveCookbook";
    String delCurveUrl = "/rest/cook/api/curve/{id}";

    //获取//烹饪曲线列表
    //String queryCurveCookbooks = "/rest/cks/api/curve_cookbook/v2/cooking_curve/queryByUserId";
    String queryCurveCookbooks = "/rest/cook/api/curve/query-by-userId";

    //曲线详情
    //String curveCookDetailUrl= "/rest/cks/api/curve_cookbook/v2/cooking_curve/query";
    String curveCookDetailUrl= "/rest/cook/api/curve/query";

    //String curveCookUpdateUrl="/rest/cks/api/curve_cookbook/v2/cooking_curve/update";


    //String curveCookStepUpdateUrl="/rest/cks/api/curve_cookbook/v2/cooking_curve_step/update";
    String curveCookStepUpdateUrl = "/rest/cook/api/curve/step/update";

    //更新曲线状态
//    String updateCurveState = "/rest/cks/api/curve_cookbook/v2/cooking_curve/updateCurveState";
    String updateCurveState = "/rest/cook/api/curve/update-curve-state";

    //获取设备错误告警连接
    String getAllDeviceErrorInfo = "/rest/ops/api/error/config/get";

    //获取菜谱详情
    String getRecipeDetail ="/rest/cks/api/cookbook/details/get-by-id";

    @POST(queryCurveCookbooks)
    @Headers("Content-Type: application/json")
    Call<ResponseBody> queryCurveCookbooks(@Body RequestBody body);


    @DELETE(delCurveUrl)
    Call<ResponseBody> delCurve(@Path("id") long curveid, @Query("userId") long userId);

//    @GET(delCurveUrl)
//    Call<ResponseBody> delCurve(@Query("userId") long userId, @Query("curveCookbookId") long curveid);

    @POST(getDeviceByParams)
    @Headers("Content-Type: application/json")
    Call<ResponseBody> getDeviceParams(@Body RequestBody body);

    @POST(curveCookDetailUrl)
    @Headers("Content-Type: application/json")
    Call<ResponseBody> getCurveCookDetail(@Body RequestBody body);

    @POST(getRecipeDetail)
    @Headers("Content-Type: application/json")
    Call<ResponseBody> getRecipeDetail(@Body RequestBody body);

//    @POST(curveCookUpdateUrl)
//    @Headers("Content-Type: application/json")
//    Call<ResponseBody> saveCurveData(@Body RequestBody body);

    @POST(curveCookStepUpdateUrl)
    @Headers("Content-Type: application/json")
    Call<ResponseBody> saveCurveStepData(@Body RequestBody body);

    @POST(getAllDeviceErrorInfo)
    @Headers("Content-Type: application/json")
    Call<ResponseBody> getAllDeviceErrorInfo(@Body RequestBody body);

    @POST(updateCurveState)
    @Headers("Content-Type: application/json")
    Call<ResponseBody> updateCurveState(@Body RequestBody body);

}

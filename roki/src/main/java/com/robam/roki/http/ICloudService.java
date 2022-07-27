package com.robam.roki.http;

import com.robam.common.bean.BaseResponse;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface ICloudService {
    String getVerifyCode = "/rest/api/cas/verify-code/get";
    //获取厨房支持列表
    String getCookingKnowledge = "/rest/ops/api/cookingKnowledge/getCookingKnowledge";
    String getbyTagOtherCooks = "/rest/cks/api/cookbook/by-tag-other/get/cooks";//获取某个标签或推荐或周上新的分页菜谱
    String getCookbookbythemeId = "/rest/cks/api/cookbook/get-by-themeId";//根据主题id查询下属所有菜单

    String getCookBanner = "/api-cook-manage/cook-manage-admin/api/ops/carousel/show";//美食频道banner
    String getByTagOtherThemes = "/rest/cks/api/cookbook/by-tag-other/get/themes";//获取某个标签或推荐或周上新的主题
    String getWeekTops = "/rest/cks/api/cookbook/week/tops";//菜谱周排名
    //获取主题菜谱列表 精选专题
    String getThemeRecipeList = "/rest/cks/api/theme/list/get";

    @POST(getVerifyCode)
    @Headers("Content-Type: application/json")
    Call<ResponseBody> getVerifyCode(@Body RequestBody body);

    /**
     * 获取厨房知识列表
     */
    @POST(getCookingKnowledge)
    @Headers("Content-Type: application/json")
    Call<ResponseBody> getCookingKnowledge(@Body RequestBody body);

    /**
     * 获取某个标签或推荐或周上新的分页菜谱
     */
    @POST(getbyTagOtherCooks)
    @Headers("Content-Type: application/json")
    Call<ResponseBody> getbyTagOtherCooks(@Body RequestBody body);
    /**
     * 根据主题id查询下属所有菜单
     *
     * @param reqBody
     * @param callback
     */
    @POST(getCookbookbythemeId)
    @Headers("Content-Type: application/json")
    Call<ResponseBody> getCookBookBythemeId(@Body RequestBody body);
    /**
     *  美食频道banner
     */
    @GET(getCookBanner)
    Call<ResponseBody> getCookBanner();
    /**
     * 获取某个标签或推荐或周上新的主题
     */
    @POST(getByTagOtherThemes)
    @Headers("Content-Type: application/json")
    Call<ResponseBody> getByTagOtherThemes(@Body RequestBody body);
    /**
     * 菜谱周排名
     */
    @POST(getWeekTops)
    @Headers("Content-Type: application/json")
    Call<ResponseBody> getWeekTops(@Body RequestBody body);
    /**
     * 获取主题列表精选专题
     */
    @POST(getThemeRecipeList)
    @Headers("Content-Type: application/json")
    Call<ResponseBody> getThemeRecipeList(@Body RequestBody body);
}

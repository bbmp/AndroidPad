package com.robam.roki.http;

import com.google.gson.Gson;
import com.robam.common.bean.BaseResponse;
import com.robam.common.http.RetrofitCallback;
import com.robam.common.http.RetrofitClient;
import com.robam.roki.request.CookingKnowledgeReq;
import com.robam.roki.request.GetVerifyCodeReq;

import org.json.JSONObject;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;

public class CloudHelper {

        private static final String APPLICATION_JSON_ACCEPT_APPLICATION_JSON = "application/json; Accept: application/json";
        private static ICloudService svr = RetrofitClient.getInstance().createApi(ICloudService.class);

        public static <T extends BaseResponse> void getVerifyCode(String phone, Class<T> entity,
                                                                  final RetrofitCallback<T> callback) {
                String json = new GetVerifyCodeReq(phone).toString();
                RequestBody requestBody =
                        RequestBody.create(MediaType.parse(APPLICATION_JSON_ACCEPT_APPLICATION_JSON), json);
                Call<ResponseBody> call = svr.getVerifyCode(requestBody);
                enqueue(entity, call, callback);
        }

        public static <T extends BaseResponse> void getCookingKnowledge(String typeCode, int isActive, String lable, int pageNo, int pageSize, Class<T> entity, final RetrofitCallback<T> callback) {
                String json = new CookingKnowledgeReq(typeCode, isActive, lable, pageNo, pageSize).toString();

//                JSONObject jsonObject = new JSONObject();
//                jsonObject.putOpt("typeCode", typeCode);
//                jsonObject.putOpt("isActive", isActive);
//                jsonObject.putOpt("lable", lable);
//                jsonObject.putOpt("pageNo", pageNo);
//                jsonObject.putOpt("pageSize", pageSize);
                RequestBody requestBody =
                        RequestBody.create(MediaType.parse(APPLICATION_JSON_ACCEPT_APPLICATION_JSON), json);
                Call<ResponseBody> call = svr.getCookingKnowledge(requestBody);
                enqueue(entity, call, callback);
        }

        //统一处理回调
        private static <T extends BaseResponse> void enqueue(final Class<T> entity, Call<ResponseBody> call, final RetrofitCallback<T> callback) {
                call.enqueue(new retrofit2.Callback<ResponseBody>() {
                        @Override
                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                try {
                                        String body = response.body().string();
                                        Gson gson = new Gson();
                                        T object = gson.fromJson(body, entity);
                                        BaseResponse rcReponse = object;
                                        if (null != rcReponse && rcReponse.rc == 0) {
                                                if (null != callback) {
                                                        callback.onSuccess(object);
                                                        return;
                                                }
                                        }
                                        if (null != callback && null != rcReponse)
                                                callback.onFaild(rcReponse.msg);
                                } catch (Exception e) {
                                        if (null != callback)
                                                callback.onFaild("exception:" + e.getMessage());
                                }
                        }

                        @Override
                        public void onFailure(Call<ResponseBody> call, Throwable throwable) {
                                callback.onFaild(throwable.toString());
                        }
                });
        }

}

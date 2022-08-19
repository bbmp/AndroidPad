package com.robam.pan.http;

import com.google.gson.Gson;
import com.robam.common.bean.BaseResponse;
import com.robam.common.http.ILife;
import com.robam.common.http.RetrofitCallback;
import com.robam.common.http.RetrofitClient;
import com.robam.common.utils.LogUtils;
import com.robam.pan.constant.HostServer;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;

public class CloudHelper {
    private static final String APPLICATION_JSON_ACCEPT_APPLICATION_JSON = "application/json; Accept: application/json";
    private static ICloudService svr = RetrofitClient.getInstance().createApi(ICloudService.class, HostServer.apiHost);


    //统一处理回调
    private static <T extends BaseResponse> void enqueue(ILife iLife, final Class<T> entity, Call<ResponseBody> call, final RetrofitCallback<T> callback) {
        call.enqueue(new retrofit2.Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                //生命周期判断
                if (iLife.isDestroyed()) {
                    LogUtils.e("page isDestroyed");
                    return;
                }
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
                if (iLife.isDestroyed()) {
                    return;
                }
                if (null != callback)
                    callback.onFaild(throwable.toString());
            }
        });
    }
}

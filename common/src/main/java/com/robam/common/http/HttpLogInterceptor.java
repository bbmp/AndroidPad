package com.robam.common.http;


import androidx.constraintlayout.solver.state.State;

import com.robam.common.utils.LogUtils;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class HttpLogInterceptor implements Interceptor {

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        long startTime = System.currentTimeMillis();
        okhttp3.Response response = chain.proceed(chain.request());
        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;
        String content = response.body().string();
        LogUtils.e("请求：" + request);
        LogUtils.e( "请求体返回：| Response:" + content);
        LogUtils.e( "----------请求耗时:" + duration + "毫秒----------");
        return response;
    }
}

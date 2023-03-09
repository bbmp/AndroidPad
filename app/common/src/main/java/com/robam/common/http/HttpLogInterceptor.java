package com.robam.common.http;



import static java.nio.charset.StandardCharsets.UTF_8;

import com.robam.common.utils.LogUtils;

import java.io.IOException;
import java.nio.charset.Charset;

import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.Buffer;

public class HttpLogInterceptor implements Interceptor {

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        long startTime = System.currentTimeMillis();
        okhttp3.Response response = chain.proceed(chain.request());
        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;
        MediaType mediaType = response.body().contentType();
        String content = response.body().string();
        printParams(request);
        LogUtils.e( "请求体返回：| Response:" + content);
        LogUtils.e( "----------请求耗时:" + duration + "毫秒----------");
        return response.newBuilder()
                .body(ResponseBody.create(mediaType, content))
                .build();
    }

    private void printParams(Request request) {
        Buffer buffer = new Buffer();
        try {
            RequestBody body = request.body();
            body.writeTo(buffer);
            Charset charset = Charset.forName("UTF-8");
            MediaType contentType = body.contentType();
            if (contentType != null) {
                charset = contentType.charset(UTF_8);
            }
            String params = buffer.readString(charset);
            LogUtils.e("请求参数： | " + params);
        } catch (Exception e) {
            LogUtils.e("请求参数： ");
        }
    }
}

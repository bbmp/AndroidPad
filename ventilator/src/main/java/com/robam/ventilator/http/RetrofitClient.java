package com.robam.ventilator.http;

import com.robam.common.http.HttpLogInterceptor;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.ConnectionPool;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {
    private OkHttpClient client;
    private Retrofit retrofit;
    private String ecsHost = "118.178.157.97";
    private int ecsPort;
    private final static int CONNECT_TIMEOUT = 20;
    private final static int WRITE_TIMEOUT = 20;
    private String defaultHost = "https://api.github.com";
    private Map header;

    private static RetrofitClient instance = new RetrofitClient();

    public static RetrofitClient getInstance() { return instance; }

    private RetrofitClient() {
//        this(defaultHost, null);
    }

    //初始化
    public void init(String url, Map<String, String> headers) {
        defaultHost = url;
        header = headers;
        client = new OkHttpClient.Builder()
                .addInterceptor(new HttpLogInterceptor())
                .connectTimeout(CONNECT_TIMEOUT, TimeUnit.SECONDS)
                .writeTimeout(WRITE_TIMEOUT, TimeUnit.SECONDS)
                .connectionPool(new ConnectionPool(8, 15, TimeUnit.SECONDS))
                // 这里你可以根据自己的机型设置同时连接的个数和时间，我这里8个，和每个保持时间为15s
                .build();
        retrofit = new Retrofit.Builder()
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
//                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .baseUrl(url)
                .build();

    }

    //创建api
    public <T> T createApi(Class<T> clazz) {
        if(retrofit == null){
            throw new IllegalArgumentException("you must init first!!! ");
        }
        return retrofit.create(clazz);
    }
}

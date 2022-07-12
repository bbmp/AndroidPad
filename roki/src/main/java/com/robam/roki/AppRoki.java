package com.robam.roki;

import android.app.Application;

import com.robam.common.http.RetrofitClient;

public class AppRoki {
    public static void init(Application application) {
        //http
        RetrofitClient.getInstance().init("http://api.myroki.com:80", null);
    }
}

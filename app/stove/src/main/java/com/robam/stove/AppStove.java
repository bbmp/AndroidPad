package com.robam.stove;

import android.app.Application;

import com.robam.stove.device.StoveFactory;

public class AppStove {
    public static void onCreate(Application application) {
        //使用哪个平台
        StoveFactory.initPlat(application, StoveFactory.TUOBANG);
    }
}
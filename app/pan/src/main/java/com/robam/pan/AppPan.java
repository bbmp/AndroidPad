package com.robam.pan;

import android.app.Application;

import com.robam.pan.device.PanFactory;

public class AppPan {
    //
    public static void onCreate(Application application) {
        //使用哪个平台
        PanFactory.initPlat(application, PanFactory.TUOBANG);
    }
}

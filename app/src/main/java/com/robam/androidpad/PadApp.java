package com.robam.androidpad;



//import com.robam.stream.AppStream;
//import com.robam.ventilator.AppVentilator;

import android.app.Application;

import com.robam.ventilator.AppVentilator;

import com.robam.roki.AppRoki;

public class PadApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        CrashHandler.register(this);
        AppRoki.init(this);
//        AppVentilator.init(this);
    }
}

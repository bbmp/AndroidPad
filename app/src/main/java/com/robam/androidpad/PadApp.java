package com.robam.androidpad;



//import com.robam.stream.AppStream;
//import com.robam.ventilator.AppVentilator;

import android.app.Application;

import com.robam.ventilator.AppVentilator;
import com.tencent.mmkv.MMKV;

//import com.robam.roki.AppRoki;

import org.litepal.LitePal;

public class PadApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
//        CrashHandler.register(this);
        //LitePal注册 数据库
        LitePal.initialize(this);

        // MMKV 初始化
        MMKV.initialize(this);
//        AppRoki.init(this);
        AppVentilator.init(this);
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }
}

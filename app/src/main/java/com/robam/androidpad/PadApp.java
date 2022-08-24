package com.robam.androidpad;



//import com.robam.stream.AppStream;
//import com.robam.ventilator.AppVentilator;

import android.app.Application;

import com.bumptech.glide.Glide;
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
        AppVentilator.onCreate(this);
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        AppVentilator.onTerminate();
    }
    @Override
    public void onLowMemory() {
        super.onLowMemory();
        // 清理所有图片内存缓存
        Glide.get(this).onLowMemory();
    }

    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
        // 根据手机内存剩余情况清理图片内存缓存
        Glide.get(this).onTrimMemory(level);
    }
}

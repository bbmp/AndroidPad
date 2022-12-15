package com.robam.androidpad;



//import com.robam.stream.AppStream;
//import com.robam.ventilator.AppVentilator;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.multidex.MultiDex;

import com.bumptech.glide.Glide;
import com.robam.common.device.Plat;
import com.robam.common.manager.AppActivityManager;
import com.robam.ventilator.AppVentilator;
import com.tencent.mmkv.MMKV;

//import com.robam.roki.AppRoki;

import org.litepal.LitePal;

import java.io.File;

import xcrash.TombstoneManager;

public class PadApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        MultiDex.install(this);
        //崩溃注册
        xcrash.XCrash.init(this);
        //保留10个日志
        File[] files = TombstoneManager.getAllTombstones();
        if (files.length > 10) {
            TombstoneManager.deleteTombstone(files[0]);
        }
        //LitePal注册 数据库
        LitePal.initialize(this);
        //平台初始化
        Plat.initPlat(this, Plat.TUOBANG);
        // MMKV 初始化
        MMKV.initialize(this);
//        AppRoki.init(this);
        AppVentilator.onCreate(this);

        registerLifecycle();
    }

    private void registerLifecycle() {
        registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle savedInstanceState) {

            }

            @Override
            public void onActivityStarted(@NonNull Activity activity) {

            }

            @Override
            public void onActivityResumed(@NonNull Activity activity) {
                AppActivityManager.getInstance().setCurrentActivity(activity);
            }

            @Override
            public void onActivityPaused(@NonNull Activity activity) {

            }

            @Override
            public void onActivityStopped(@NonNull Activity activity) {

            }

            @Override
            public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle outState) {

            }

            @Override
            public void onActivityDestroyed(@NonNull Activity activity) {

            }
        });
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

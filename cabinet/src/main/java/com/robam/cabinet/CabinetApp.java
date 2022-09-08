package com.robam.cabinet;

import android.app.Application;

import com.robam.cabinet.device.CabinetFactory;

public class CabinetApp {
    public static void onCreate(Application application) {
        //使用哪个平台
        CabinetFactory.initPlat(application, CabinetFactory.TUOBANG);
    }
}

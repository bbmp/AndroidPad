package com.robam.dishwasher;

import android.app.Application;

import com.robam.dishwasher.device.DishWasherFactory;

public class DishWasherApp {
    public static void onCreate(Application application) {
        //使用哪个平台
        DishWasherFactory.initPlat(application, DishWasherFactory.TUOBANG);
    }
}

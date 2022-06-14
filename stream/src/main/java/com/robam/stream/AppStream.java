package com.robam.stream;

import android.app.Application;

import com.robam.foodmaterialdetect.FoodMaterialHelper;

public class AppStream extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        FoodMaterialHelper.init(this);
    }
}

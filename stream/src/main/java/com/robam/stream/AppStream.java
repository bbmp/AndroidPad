package com.robam.stream;

import android.app.Application;
import android.serialport.helper.SerialPortConfig;
import android.serialport.helper.SerialPortHelper;

import com.robam.common.http.RetrofitClient;
import com.robam.foodmaterialdetect.FoodMaterialHelper;

public class AppStream extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        //检测食物
        FoodMaterialHelper.init(this);
        //串口初始化
        SerialPortConfig serialPortConfig = new SerialPortConfig.Builder()
                .setMaxSize(16).build();
        SerialPortHelper.getInstance().init(serialPortConfig);
        //http
        RetrofitClient.getInstance().init("https://api.github.com", null);
    }
}

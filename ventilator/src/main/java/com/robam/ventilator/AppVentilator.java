package com.robam.ventilator;

import android.app.Application;

import com.clj.fastble.BleManager;
import com.robam.common.http.RetrofitClient;

public class AppVentilator extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        //检测食物
//        FoodMaterialHelper.init(this);
        //串口初始化
//        SerialPortConfig serialPortConfig = new SerialPortConfig.Builder()
//                .setMaxSize(16).build();
//        SerialPortHelper.getInstance().init(serialPortConfig);
        //http
        RetrofitClient.getInstance().init("https://api.github.com", null);
        //ble
        BleManager.getInstance().init(this);
        BleManager.getInstance()
                .enableLog(true)
                .setReConnectCount(1, 5000)
                .setConnectOverTime(20000)
                .setOperateTimeout(5000);
    }
}
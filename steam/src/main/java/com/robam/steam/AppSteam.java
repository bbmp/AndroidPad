package com.robam.steam;

import android.app.Application;
import android.serialport.helper.SerialPortConfig;
import android.serialport.helper.SerialPortHelper;

import com.clj.fastble.BleManager;
import com.robam.common.http.RetrofitClient;

public class AppSteam {

    public void init(Application application) {
        //检测食物
//        FoodMaterialHelper.init(this);
        //串口初始化
        SerialPortConfig serialPortConfig = new SerialPortConfig.Builder()
                .setMaxSize(16).build();
        SerialPortHelper.getInstance().init(serialPortConfig);
        //http
        RetrofitClient.getInstance().init("https://api.github.com", null);
        //ble
        BleManager.getInstance().init(application);
        BleManager.getInstance()
                .enableLog(true)
                .setReConnectCount(1, 5000)
                .setConnectOverTime(20000)
                .setOperateTimeout(5000);
    }
}

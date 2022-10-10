package com.robam.ventilator;

import android.app.Application;
import android.content.Intent;
import android.serialport.helper.SerialPortConfig;
import android.serialport.helper.SerialPortHelper;

import com.clj.fastble.BleManager;
import com.robam.common.http.RetrofitClient;
import com.robam.common.mqtt.MqttManager;
import com.robam.pan.device.PanAbstractControl;
import com.robam.pan.device.PanBluetoothControl;
import com.robam.steamoven.device.SteamAbstractControl;
import com.robam.steamoven.device.SteamMqttControl;
import com.robam.stove.device.StoveAbstractControl;
import com.robam.stove.device.StoveBluetoothControl;
import com.robam.ventilator.device.VentilatorAbstractControl;
import com.robam.ventilator.device.VentilatorFactory;
import com.robam.ventilator.device.VentilatorLocalControl;

public class AppVentilator {

    public static void onCreate(Application application) {
        //检测食物
//        FoodMaterialHelper.init(this);
        //串口初始化
        SerialPortConfig serialPortConfig = new SerialPortConfig.Builder()
                .setMaxSize(32)
                .setPath("/dev/ttyS3").build();
        SerialPortHelper.getInstance().init(serialPortConfig);
        //ble init
        BleManager.getInstance().init(application);
        BleManager.getInstance()
                .enableLog(BuildConfig.DEBUG)
                .setReConnectCount(1, 5000)
                .setConnectOverTime(20000)
                .setOperateTimeout(5000);

        //开启本地控制
        VentilatorAbstractControl.getInstance().init(new VentilatorLocalControl());
        //远程控制一体机
        SteamAbstractControl.getInstance().init(new SteamMqttControl());
        //蓝牙控制灶具
        StoveAbstractControl.getInstance().init(new StoveBluetoothControl());
        //蓝牙控制锅
        PanAbstractControl.getInstance().init(new PanBluetoothControl());

    }


    public static void onTerminate() {
        //关闭蓝牙
        BleManager.getInstance().disconnectAllDevice();
        BleManager.getInstance().destroy();
        //关闭mqtt
        MqttManager.getInstance().close();
    }
}
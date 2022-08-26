package com.robam.ventilator;

import android.app.Application;
import android.serialport.helper.SerialPortConfig;
import android.serialport.helper.SerialPortHelper;

import com.clj.fastble.BleManager;
import com.robam.common.http.RetrofitClient;
import com.robam.common.mqtt.MqttManager;
import com.robam.pan.constant.HostServer;
import com.robam.ventilator.device.VentilatorAbstractControl;
import com.robam.ventilator.device.VentilatorFactory;
import com.robam.ventilator.device.VentilatorLocalControl;

public class AppVentilator {

    public static void onCreate(Application application) {
        //检测食物
//        FoodMaterialHelper.init(this);
        //串口初始化
        SerialPortConfig serialPortConfig = new SerialPortConfig.Builder()
                .setMaxSize(23)
                .setPath("/dev/ttyS1").build();
        SerialPortHelper.getInstance().init(serialPortConfig);
        //http init
        RetrofitClient.getInstance().init(HostServer.apiHost, null);
        //ble init
        BleManager.getInstance().init(application);
        BleManager.getInstance()
                .enableLog(BuildConfig.DEBUG)
                .setReConnectCount(1, 5000)
                .setConnectOverTime(20000)
                .setOperateTimeout(5000);

        //使用哪个平台
        VentilatorFactory.initPlat(application, VentilatorFactory.TUOBANG);
        //开启本地控制
        VentilatorAbstractControl.getInstance().init(new VentilatorLocalControl());
        //协议解析和打包
        VentilatorFactory.initMqttProtocol();

    }


    public static void onTerminate() {
        //关闭蓝牙
        BleManager.getInstance().disconnectAllDevice();
        BleManager.getInstance().destroy();
        //关闭mqtt
        MqttManager.getInstance().close();
    }
}
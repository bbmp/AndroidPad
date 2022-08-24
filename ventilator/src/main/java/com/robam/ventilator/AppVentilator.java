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
                .setMaxSize(23).build();
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
        //初始化主设备mqtt收发 烟机端只要网络连接上就需要启动mqtt服务，锅和灶不用登录
        //监听网络状态
        MqttManager.getInstance().start(application, VentilatorFactory.getPlatform(), VentilatorFactory.getProtocol());
    }


    public static void onTerminate() {
        //关闭蓝牙
        BleManager.getInstance().disconnectAllDevice();
        BleManager.getInstance().destroy();
        //关闭mqtt
        MqttManager.getInstance().close();
    }
}
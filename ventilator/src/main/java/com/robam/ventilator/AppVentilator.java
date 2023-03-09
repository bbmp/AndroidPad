package com.robam.ventilator;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;
import android.serialport.helper.SerialPortConfig;
import android.serialport.helper.SerialPortHelper;
import android.serialport.helper.SphResultCallback;

import com.clj.fastble.BleManager;
import com.robam.cabinet.device.CabinetAbstractControl;
import com.robam.cabinet.device.CabinetMqttControl;
import com.robam.common.ITerminalType;
import com.robam.common.bean.AccountInfo;
import com.robam.common.bean.MqttDirective;
import com.robam.common.device.Plat;
import com.robam.common.http.RetrofitClient;
import com.robam.common.manager.AppActivityManager;
import com.robam.common.mqtt.MqttManager;
import com.robam.common.utils.LogUtils;
import com.robam.dishwasher.device.DishWasherAbstractControl;
import com.robam.dishwasher.device.DishWasherMqttControl;
import com.robam.dishwasher.util.DishWasherCommandHelper;
import com.robam.pan.device.PanAbstractControl;
import com.robam.pan.device.PanBluetoothControl;
import com.robam.steamoven.device.SteamAbstractControl;
import com.robam.steamoven.device.SteamMqttControl;
import com.robam.steamoven.protocol.SteamCommandHelper;
import com.robam.stove.device.StoveAbstractControl;
import com.robam.stove.device.StoveBluetoothControl;
import com.robam.ventilator.constant.VentilatorConstant;
import com.robam.ventilator.device.HomeVentilator;
import com.robam.ventilator.device.VentilatorAbstractControl;
import com.robam.ventilator.device.VentilatorFactory;
import com.robam.ventilator.device.VentilatorLocalControl;
import com.robam.ventilator.protocol.serial.SerialVentilator;
import com.robam.ventilator.ui.activity.HomeActivity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

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
        //洗碗机控制
        DishWasherAbstractControl.getInstance().init(new DishWasherMqttControl());
        //消毒柜
        CabinetAbstractControl.getInstance().init(new CabinetMqttControl());

        //初始化
        MqttDirective.getInstance();
        DishWasherCommandHelper.getInstance();
        SteamCommandHelper.getInstance();

        //初始化
        SerialVentilator.init_decoder();
        //打开串口
        SerialPortHelper.getInstance().openDevice(new SphResultCallback() {
            @Override
            public void onSendData(byte[] sendCom, int len) {
            }

            @Override
            public void onReceiveData(byte[] data, int len) {
                SerialVentilator.parseSerial(data, len);
            }

            @Override
            public void onOpenSuccess() {
                LogUtils.e("serial open success " + Thread.currentThread().getName());

            }

            @Override
            public void onOpenFailed() {
                LogUtils.e("serial open failed " + Thread.currentThread().getName());
            }
        });
        //用于熄屏时读取按键
        ThreadPoolExecutor keyMonitor = new ThreadPoolExecutor(1, 1, 0, TimeUnit.SECONDS, new SynchronousQueue<>(),
                new ThreadPoolExecutor.DiscardPolicy());//无法重复提交
        keyMonitor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    Process process = Runtime.getRuntime().exec("getevent /dev/input/event6");
                    if (null != process) {
                        BufferedReader br = new BufferedReader(new InputStreamReader(process.getInputStream(), "utf-8"));
                        String line = null;
                        long longTime = 0;
                        long downTime = 0;
//                        boolean consume = false;
                        while (true) {
                            line = br.readLine();

                            if (null != line) { //熄屏状态
//                                if (line.startsWith("0001") && line.contains("00000001")) { //按下事件
//                                    if (System.currentTimeMillis() - downTime < 1000) {
//                                        consume = false; //后面的事件停止消费
//                                        continue;
//                                    }
//                                    downTime = System.currentTimeMillis();
//                                    consume = true;
//                                }
//                                if (!consume)
//                                    continue;
                                if (line.contains("00a5")) { //左键
                                    if (line.contains("00000001")) {//down事件
                                        longTime = System.currentTimeMillis();
                                    } else if (line.contains("00000000")) { //up事件
                                        if (System.currentTimeMillis() - longTime > 2000) { //长按
                                           // 冷暖光切换
                                            VentilatorAbstractControl.getInstance().setColorLamp();
                                            Plat.getPlatform().openWaterLamp();

                                        } else {
                                            if (HomeVentilator.getInstance().lightOn == (byte) 0xA0) {
                                                Plat.getPlatform().openWaterLamp();
                                                HomeVentilator.getInstance().updateOperationTime(); //开关灯也算操作
                                                VentilatorAbstractControl.getInstance().setFanLight(VentilatorConstant.FAN_LIGHT_OPEN);
                                                //上报
                                                HomeVentilator.getInstance().eventReport(ITerminalType.PAD, AccountInfo.getInstance().getUserString(), VentilatorConstant.EVENT_LIGHT, 1);
                                            } else {
                                                Plat.getPlatform().closeWaterLamp();
                                                HomeVentilator.getInstance().updateOperationTime();
                                                VentilatorAbstractControl.getInstance().setFanLight(VentilatorConstant.FAN_LIGHT_CLOSE);
                                                //上报
                                                HomeVentilator.getInstance().eventReport(ITerminalType.PAD, AccountInfo.getInstance().getUserString(), VentilatorConstant.EVENT_LIGHT, 0);
                                            }
                                        }
                                    }
                                } else if (line.contains("00a4")) { //右键
                                    if (line.contains("00000001")) {//down事件
                                        longTime = System.currentTimeMillis();
                                    } else if (line.contains("00000000")) { //up事件
                                        LogUtils.e("startup = " + HomeVentilator.getInstance().isStartUp());
                                        if (HomeVentilator.getInstance().isStartUp()) { //开机状态
                                            //上报
                                            HomeVentilator.getInstance().eventReport(ITerminalType.PAD, AccountInfo.getInstance().getUserString(), VentilatorConstant.EVENT_SWITCH, 0);
                                            //延时关机
                                            Activity activity = AppActivityManager.getInstance().getCurrentActivity();
                                            if (null != activity)
                                                activity.runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        HomeVentilator.getInstance().delayShutDown(false); //主动关机
                                                    }
                                                });
                                        } else {
                                            //开机
                                            HomeVentilator.getInstance().openVentilator();
                                            //上报
                                            HomeVentilator.getInstance().eventReport(ITerminalType.PAD, AccountInfo.getInstance().getUserString(), VentilatorConstant.EVENT_SWITCH, 1);
                                            //对时
                                            HomeVentilator.getInstance().autoSetTime(application);
                                        }
                                    }
                                }
//                                LogUtils.e("line = " + line);
                            }
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }


    public static void onTerminate() {
        //关闭蓝牙
        BleManager.getInstance().disconnectAllDevice();
        BleManager.getInstance().destroy();
        //关闭mqtt
        MqttManager.getInstance().close();
    }
}
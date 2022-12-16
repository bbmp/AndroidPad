package com.robam.ventilator.ui.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.SystemClock;
import android.serialport.helper.SerialPortHelper;

import androidx.annotation.Nullable;

import com.robam.common.bean.AccountInfo;
import com.robam.common.bean.Device;
import com.robam.common.device.Plat;
import com.robam.common.device.subdevice.Pan;
import com.robam.common.device.subdevice.Stove;
import com.robam.common.manager.BlueToothManager;
import com.robam.common.manager.LiveDataBus;
import com.robam.common.utils.DateUtil;
import com.robam.common.utils.FileUtils;
import com.robam.common.utils.LogUtils;
import com.robam.common.utils.MMKVUtils;
import com.robam.ventilator.constant.VentilatorConstant;
import com.robam.ventilator.device.HomeVentilator;
import com.robam.ventilator.device.VentilatorAbstractControl;
import com.robam.ventilator.protocol.ble.BleVentilator;
import com.robam.ventilator.protocol.serial.SerialVentilator;
import com.robam.ventilator.ui.receiver.AlarmBleReceiver;
import com.robam.ventilator.ui.receiver.AlarmSerialReceiver;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;


public class AlarmVentilatorService extends Service {
    private static final int INTERVAL = 3000;
    private byte data[] = SerialVentilator.packQueryCmd();
    private AlarmManager alarmManager;
    private PendingIntent pIntent;
    private static final int PENDING_REQUEST = 0;

    //用于熄屏时读取按键
    private ThreadPoolExecutor keyMonitor = new ThreadPoolExecutor(1, 1, 0, TimeUnit.SECONDS, new SynchronousQueue<>(),
            new ThreadPoolExecutor.DiscardPolicy());//无法重复提交

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        Intent i = new Intent(this, AlarmSerialReceiver.class);
        pIntent = PendingIntent.getBroadcast(this, PENDING_REQUEST, i, PendingIntent.FLAG_UPDATE_CURRENT);

        keyMonitor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    Process process = Runtime.getRuntime().exec("getevent /dev/input/event6");
                    if (null != process) {
                        BufferedReader br = new BufferedReader(new InputStreamReader(process.getInputStream(), "utf-8"));
                        String line = null;
                        long downTime = 0;
                        while (true) {
                            line = br.readLine();
                            PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
                            boolean isScreenOn = pm.isInteractive();
                            if (!isScreenOn && null != line) { //熄屏状态
                                try {
                                    Thread.sleep(10);
                                } catch (Exception e) {}
                                if (line.contains("00a5")) { //左键
                                    if (line.contains("00000001")) {//down事件
                                        downTime = System.currentTimeMillis();
                                    } else if (line.contains("00000000")) { //up事件
                                        if (System.currentTimeMillis() - downTime > 2000) { //长按

                                            VentilatorAbstractControl.getInstance().setColorLamp();
                                            Plat.getPlatform().openWaterLamp();

                                        } else {
                                            if (HomeVentilator.getInstance().lightOn == (byte) 0xA0) {
                                                Plat.getPlatform().openWaterLamp();
                                                VentilatorAbstractControl.getInstance().setFanLight(VentilatorConstant.FAN_LIGHT_OPEN);
                                            } else {
                                                Plat.getPlatform().closeWaterLamp();
                                                VentilatorAbstractControl.getInstance().setFanLight(VentilatorConstant.FAN_LIGHT_CLOSE);
                                            }
                                        }
                                    }
                                } else if (line.contains("00a4")) { //右键
                                    if (line.contains("00000001")) {//down事件

                                    } else if (line.contains("00000000")) { //up事件
                                        if (HomeVentilator.getInstance().startup == (byte) 0x01) { //开机状态
                                            VentilatorAbstractControl.getInstance().beep();
                                            //延时关机
                                            HomeVentilator.getInstance().delayShutDown(false); //主动关机
                                        } else {
                                            //开机
                                            HomeVentilator.getInstance().openVentilator();
                                        }
                                    }
                                }
                                LogUtils.e("line = " + line);
                            }
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //通过AlarmManager定时启动广播,用handler熄屏后会停止

        if (null != alarmManager) {
            long triggerAtTime = SystemClock.elapsedRealtime() + INTERVAL;//从开机到现在的毫秒（手机睡眠(sleep)的时间也包括在内
            try {
                alarmManager.cancel(pIntent);
            } catch (Exception e) {}
            alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerAtTime, pIntent);
        }

        //串口查询
        SerialPortHelper.getInstance().addCommands(data);
        //自动连接蓝牙
        //自动连接
        autoConnectBle();

        //5分钟未操作自动关机
        if ((Math.abs(System.currentTimeMillis() - HomeVentilator.getInstance().operationTime) >= 5*60*1000)
            && HomeVentilator.getInstance().gear == (byte) 0xA0 //风机未开
                && HomeVentilator.getInstance().isStartUp() //开机状态
                && !HomeVentilator.getInstance().isLock() //非锁屏状态
                && HomeVentilator.getInstance().lightOn == (byte) 0xA0)  //关灯状态
            HomeVentilator.getInstance().closeVentilator(); //关闭烟机

        //检查油网清洗时间,油网清洗打开
        if (MMKVUtils.getOilClean() && HomeVentilator.getInstance().isStartUp()) {  //开机状态
            long runTime = MMKVUtils.getFanRuntime();
            if (runTime >= 60 * 60 * 60 * 1000) { //超过60小时
                LiveDataBus.get().with(VentilatorConstant.OIL_CLEAN, Boolean.class).setValue(true);
            }
        }
        //检查假日模式打开
        if (MMKVUtils.getHoliday() && (HomeVentilator.getInstance().startup == (byte) 0x00) //关机状态下
                && Math.abs(System.currentTimeMillis() - HomeVentilator.getInstance().fanStartTime) > 60000) {  //一分钟内不重复
            String curWeek = DateUtil.getWeek();
            String weekTime = MMKVUtils.getHolidayWeekTime(); //为了效率，不每次io
            String week = weekTime.substring(0, 2);

            if (curWeek.equals(week) && DateUtil.isNowTime(weekTime)) {
                LogUtils.e("week " + week + " weekTime " + weekTime);
                HomeVentilator.getInstance().startAutoCountDown(); //自动通风

                return super.onStartCommand(intent, flags, startId);

            }
        }
        //超过天数未使用
        int holidayDay = Integer.parseInt(MMKVUtils.getHolidayDay());
        if (MMKVUtils.getHoliday()
                && (Math.abs(System.currentTimeMillis() - HomeVentilator.getInstance().fanOffTime) > holidayDay * 86400 * 1000)
                && (HomeVentilator.getInstance().startup == (byte) 0x00) //关机状态下
                && Math.abs(System.currentTimeMillis() - HomeVentilator.getInstance().fanStartTime) > 60000) {  //一分钟内不重复
            //自动通风
            LogUtils.e("holidayDay " + holidayDay);
            if (DateUtil.isNowTime("周日14:00")) {
                HomeVentilator.getInstance().startAutoCountDown(); //自动通风

                return super.onStartCommand(intent, flags, startId);
            }

        }

        return super.onStartCommand(intent, flags, startId);
    }

    private void autoConnectBle() {
        List<String> names = new ArrayList();

        for (Device device: AccountInfo.getInstance().deviceList) {
            if (device instanceof Pan && null == ((Pan) device).bleDevice)
                names.add(BlueToothManager.pan);
            else if (device instanceof Stove && null == ((Stove) device).bleDevice)
                names.add(BlueToothManager.stove);
        }
        if (names.size() > 0) {
            BlueToothManager.setScanRule(names.toArray(new String[names.size()]));
            BleVentilator.startScan();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (null != alarmManager) {
            alarmManager.cancel(pIntent);
            alarmManager = null;
        }
    }
}

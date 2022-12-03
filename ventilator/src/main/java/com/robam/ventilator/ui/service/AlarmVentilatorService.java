package com.robam.ventilator.ui.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.PowerManager;
import android.serialport.helper.SerialPortHelper;

import androidx.annotation.Nullable;

import com.robam.common.device.Plat;
import com.robam.common.utils.DateUtil;
import com.robam.common.utils.LogUtils;
import com.robam.common.utils.MMKVUtils;
import com.robam.ventilator.constant.VentilatorConstant;
import com.robam.ventilator.device.HomeVentilator;
import com.robam.ventilator.device.VentilatorAbstractControl;
import com.robam.ventilator.protocol.serial.SerialVentilator;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Calendar;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;


public class AlarmVentilatorService extends Service {
    private static final int INTERVAL = 3000;
    private byte data[] = SerialVentilator.packQueryCmd();

    //用于熄屏时读取按键
    private ThreadPoolExecutor keyMonitor = new ThreadPoolExecutor(1, 1, 0, TimeUnit.SECONDS, new SynchronousQueue<>(),
            new ThreadPoolExecutor.DiscardPolicy());//无法重复提交

    private Handler mHandler = new Handler();
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            startService(new Intent(AlarmVentilatorService.this, AlarmVentilatorService.class));

            mHandler.postDelayed(runnable, INTERVAL);
        }
    };

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        mHandler.postDelayed(runnable, INTERVAL);
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
        //串口查询
        SerialPortHelper.getInstance().addCommands(data);

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
                HomeVentilator.getInstance().oilClean.setValue(true);
            }
        }
        //检查假日模式打开
        if (HomeVentilator.getInstance().holiday && (HomeVentilator.getInstance().startup == (byte) 0x00) //关机状态下
                && Math.abs(System.currentTimeMillis() - HomeVentilator.getInstance().fanStartTime) > 60000) {  //一分钟内不重复
            String curWeek = DateUtil.getWeek();
            String weekTime = HomeVentilator.getInstance().weekTime; //为了效率，不每次io
            String week = weekTime.substring(0, 2);

            if (curWeek.equals(week) && DateUtil.isNowTime(weekTime)) {
                LogUtils.e("week " + week + " weekTime " + weekTime);
                HomeVentilator.getInstance().startAutoCountDown(); //自动通风

                return super.onStartCommand(intent, flags, startId);

            }
        }
        //超过天数未使用
        int holidayDay = Integer.parseInt(HomeVentilator.getInstance().holidayDay);
        if (HomeVentilator.getInstance().holiday
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

    @Override
    public void onDestroy() {
        super.onDestroy();
        mHandler.removeCallbacks(runnable);

        mHandler.removeCallbacksAndMessages(null);
    }
}

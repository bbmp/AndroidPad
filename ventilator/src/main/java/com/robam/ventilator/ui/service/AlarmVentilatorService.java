package com.robam.ventilator.ui.service;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.serialport.helper.SerialPortHelper;

import androidx.annotation.Nullable;

import com.robam.common.utils.DateUtil;
import com.robam.common.utils.LogUtils;
import com.robam.common.utils.MMKVUtils;
import com.robam.ventilator.device.HomeVentilator;
import com.robam.ventilator.protocol.serial.SerialVentilator;

import java.util.Calendar;


public class AlarmVentilatorService extends Service {
    private static final int INTERVAL = 3000;
    private byte data[] = SerialVentilator.packQueryCmd();


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
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //串口查询
        SerialPortHelper.getInstance().addCommands(data);

        //检查油网清洗时间,油网清洗打开
        if (MMKVUtils.getOilClean()) {
            long runTime = MMKVUtils.getFanRuntime();
            if (runTime >= 60 * 60 * 60 * 1000) { //超过60小时
                HomeVentilator.getInstance().oilClean.setValue(true);
            }
        }
        //检查假日模式打开
        if (MMKVUtils.getHoliday()) {
            int dayOfWeek = Calendar.getInstance().get(Calendar.DAY_OF_WEEK);
            String weekTime = MMKVUtils.getHolidayWeekTime();
            String week = weekTime.substring(0, 2);
            LogUtils.e("weekTime " + weekTime);
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

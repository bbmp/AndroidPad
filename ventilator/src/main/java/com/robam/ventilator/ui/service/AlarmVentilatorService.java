package com.robam.ventilator.ui.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.SystemClock;
import android.serialport.helper.SerialPortHelper;

import androidx.annotation.Nullable;

import com.robam.ventilator.device.HomeVentilator;
import com.robam.ventilator.protocol.serial.SerialVentilator;
import com.robam.ventilator.ui.receiver.VentilatorReceiver;

public class AlarmVentilatorService extends Service {
    private static final int PENDING_REQUEST = 0;
    private static final int INTERVAL = 3000;
    private AlarmManager alarmManager;
    private PendingIntent pIntent;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        Intent i = new Intent(this, VentilatorReceiver.class);
        i.setAction(HomeVentilator.getInstance().ALARM_ACTION);
        pIntent = PendingIntent.getBroadcast(this, PENDING_REQUEST, i, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //通过AlarmManager定时启动广播

        if (null != alarmManager) {
            long triggerAtTime = SystemClock.elapsedRealtime() + INTERVAL;//从开机到现在的毫秒（手机睡眠(sleep)的时间也包括在内
            try {
                alarmManager.cancel(pIntent);
            } catch (Exception e) {
            }
            alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerAtTime, pIntent);
        }

        byte data[] = SerialVentilator.packQueryCmd();
        SerialPortHelper.getInstance().addCommands(data);

        return super.onStartCommand(intent, flags, startId);
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

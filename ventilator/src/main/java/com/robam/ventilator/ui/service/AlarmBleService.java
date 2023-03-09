package com.robam.ventilator.ui.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.SystemClock;

import com.robam.common.IDeviceType;
import com.robam.common.bean.AccountInfo;
import com.robam.common.bean.Device;
import com.robam.common.device.subdevice.Pan;
import com.robam.common.utils.LogUtils;
import com.robam.pan.device.PanAbstractControl;
import com.robam.common.device.subdevice.Stove;
import com.robam.stove.device.StoveAbstractControl;
import com.robam.ventilator.ui.receiver.AlarmBleReceiver;

//定时查询蓝牙设备
public class AlarmBleService extends Service {

    private static final int PENDING_REQUEST = 0;
    private static final int INTERVAL = 2000;
    private AlarmManager alarmManager;
    private PendingIntent pIntent;


    @Override
    public IBinder onBind(Intent intent) {
        //
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        Intent i = new Intent(this, AlarmBleReceiver.class);
        pIntent = PendingIntent.getBroadcast(this, PENDING_REQUEST, i, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //通过AlarmManager定时启动广播

        if (null != alarmManager) {
            long triggerAtTime = SystemClock.elapsedRealtime() + INTERVAL;//从开机到现在的毫秒（手机睡眠(sleep)的时间也包括在内
            try {
                alarmManager.cancel(pIntent);
            } catch (Exception e) {}
            alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerAtTime, pIntent);
        }

        //循环查询
        for (Device device: AccountInfo.getInstance().deviceList) {
            if (!IDeviceType.RRQZ.equals(device.dc) && !IDeviceType.RZNG.equals(device.dc))
                continue;
            LogUtils.e("dc = " + device.dc + " queryNum " + device.queryNum);
            if (device.queryNum > 1 && device.status == Device.ONLINE) { //已经查过一次
                device.status = Device.OFFLINE;
                AccountInfo.getInstance().getGuid().setValue(device.guid); //更新设备状态
            }
            device.queryNum++;
            if (device instanceof Pan && IDeviceType.RZNG.equals(device.dc)) { //查询锅
                if (((Pan) device).bleDevice == null) {
                    ((Pan) device).mode = 0;
                    continue;
                }
                //本机查询锅
                PanAbstractControl.getInstance().queryAttribute(device.guid);
            } else if (device instanceof Stove && IDeviceType.RRQZ.equals(device.dc)) { //查询灶具
                if (((Stove) device).bleDevice == null) { //未连接
                    continue;
                }
                //本机端查询灶具
                StoveAbstractControl.getInstance().queryAttribute(device.guid);
            }
        }
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
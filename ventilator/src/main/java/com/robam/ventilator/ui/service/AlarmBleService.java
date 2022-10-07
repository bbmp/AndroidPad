package com.robam.ventilator.ui.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.SystemClock;

import com.robam.common.IDeviceType;
import com.robam.common.bean.AccountInfo;
import com.robam.common.bean.Device;
import com.robam.common.bean.RTopic;
import com.robam.common.device.Plat;
import com.robam.common.manager.BlueToothManager;
import com.robam.common.mqtt.MqttManager;
import com.robam.common.mqtt.MqttMsg;
import com.robam.common.mqtt.MsgKeys;
import com.robam.common.utils.DeviceUtils;
import com.robam.pan.bean.Pan;
import com.robam.pan.device.PanAbstractControl;
import com.robam.stove.bean.Stove;
import com.robam.stove.device.StoveAbstractControl;
import com.robam.ventilator.device.VentilatorFactory;
import com.robam.ventilator.protocol.ble.BleVentilator;
import com.robam.ventilator.ui.receiver.AlarmBleReceiver;
import com.robam.ventilator.ui.receiver.AlarmMqttReceiver;

//定时查询蓝牙设备
public class AlarmBleService extends Service {

    private static final int PENDING_REQUEST = 0;
    private static final int INTERVAL = 15000;
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
            if (device.queryNum == 1) { //已经查过一次
                device.status = Device.OFFLINE;
                AccountInfo.getInstance().getGuid().setValue(device.guid); //更新设备状态
            }
            device.queryNum++;
            if (device instanceof Pan) { //查询锅
                if (((Pan) device).bleDevice == null) {
                    return super.onStartCommand(intent, flags, startId);
                }
                //本机查询锅
                PanAbstractControl.getInstance().queryAttribute(device.guid);
            } else if (device instanceof Stove) { //查询灶具
                if (((Stove) device).bleDevice == null) { //未连接

                    return super.onStartCommand(intent, flags, startId);
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
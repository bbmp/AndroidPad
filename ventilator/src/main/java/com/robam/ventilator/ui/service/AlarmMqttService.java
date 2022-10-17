package com.robam.ventilator.ui.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.SystemClock;

import androidx.annotation.Nullable;

import com.robam.common.IDeviceType;
import com.robam.common.bean.AccountInfo;
import com.robam.common.bean.Device;
import com.robam.common.bean.RTopic;
import com.robam.common.device.Plat;
import com.robam.common.mqtt.MqttManager;
import com.robam.common.mqtt.MqttMsg;
import com.robam.common.mqtt.MsgKeys;
import com.robam.common.utils.DeviceUtils;
import com.robam.common.utils.LogUtils;
import com.robam.dishwasher.bean.DishWasher;
import com.robam.steamoven.bean.SteamOven;
import com.robam.steamoven.device.SteamAbstractControl;
import com.robam.ventilator.bean.Ventilator;
import com.robam.ventilator.device.VentilatorFactory;
import com.robam.ventilator.ui.receiver.AlarmMqttReceiver;

//定时任务,mqtt查询设备
public class AlarmMqttService extends Service {

    private static final int PENDING_REQUEST = 0;
    private static final int INTERVAL = 15000;
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
        Intent i = new Intent(this, AlarmMqttReceiver.class);
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

        if (!MqttManager.getInstance().isConnected()) {
            //全部离线 除锅和灶
            for (Device device: AccountInfo.getInstance().deviceList) {
                if (device.status == Device.ONLINE && (!device.dc.equals(IDeviceType.RRQZ) && !device.dc.equals(IDeviceType.RZNG))) {
                    device.status = Device.OFFLINE;
                    AccountInfo.getInstance().getGuid().setValue(device.guid); //更新设备状态
                }
            }
            MqttManager.getInstance().reConnect(new MqttManager.IConncect() {
                @Override
                public void onSuccess() {
                    //重连成功 重新订阅
                    for (Device device: AccountInfo.getInstance().deviceList) {
                        MqttManager.getInstance().subscribe(DeviceUtils.getDeviceTypeId(device.guid), DeviceUtils.getDeviceNumber(device.guid));
                    }
                }
            });
            return super.onStartCommand(intent, flags, startId);
        }
        LogUtils.e("AlarmService onStartCommand " + AccountInfo.getInstance().deviceList.size());
        //循环查询
        for (Device device: AccountInfo.getInstance().deviceList) {
            if (IDeviceType.RRQZ.equals(device.dc) || IDeviceType.RZNG.equals(device.dc))
                continue;
            if (device.queryNum == 1 && device.status == Device.ONLINE) { //已经查过一次
                device.status = Device.OFFLINE;
                AccountInfo.getInstance().getGuid().setValue(device.guid); //更新设备状态
            }
            device.queryNum++;
           if (device instanceof Ventilator) {
                MqttMsg msg = new MqttMsg.Builder()
                        .setMsgId(MsgKeys.GetFanStatus_Req)
                        .setGuid(Plat.getPlatform().getDeviceOnlySign()) //源guid
                        .setDt(device.dt)
                        .setTopic(new RTopic(RTopic.TOPIC_UNICAST, DeviceUtils.getDeviceTypeId(device.guid), DeviceUtils.getDeviceNumber(device.guid)))
                        .build();
                MqttManager.getInstance().publish(msg, VentilatorFactory.getTransmitApi());
//            } else if (device instanceof Cabinet) {
//                MqttManager.getInstance().publish(msg, CabinetFactory.getProtocol());
            } else if (device instanceof DishWasher) {
                MqttMsg msg = new MqttMsg.Builder()
                        .setMsgId(MsgKeys.setDishWasherStatus)
                        .setGuid(Plat.getPlatform().getDeviceOnlySign()) //源guid
                        .setDt(device.dt)
                        .setTopic(new RTopic(RTopic.TOPIC_UNICAST, DeviceUtils.getDeviceTypeId(device.guid), DeviceUtils.getDeviceNumber(device.guid)))
                        .build();
                MqttManager.getInstance().publish(msg, VentilatorFactory.getTransmitApi());
            } else if (device instanceof SteamOven) {
                //查询一体机
                SteamAbstractControl.getInstance().queryAttribute(device.guid);
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

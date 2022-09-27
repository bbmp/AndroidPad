package com.robam.ventilator.ui.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.IBinder;
import android.os.SystemClock;

import androidx.annotation.Nullable;

import com.robam.cabinet.bean.Cabinet;
import com.robam.cabinet.device.CabinetFactory;
import com.robam.common.bean.AccountInfo;
import com.robam.common.bean.Device;
import com.robam.common.bean.RTopic;
import com.robam.common.device.Plat;
import com.robam.common.manager.BlueToothManager;
import com.robam.common.mqtt.MqttManager;
import com.robam.common.mqtt.MqttMsg;
import com.robam.common.mqtt.MsgKeys;
import com.robam.common.utils.DeviceUtils;
import com.robam.common.utils.LogUtils;
import com.robam.dishwasher.bean.DishWasher;
import com.robam.dishwasher.device.DishWasherFactory;
import com.robam.pan.bean.Pan;
import com.robam.pan.device.PanFactory;
import com.robam.pan.protocol.mqtt.MqttPan;
import com.robam.steamoven.bean.SteamOven;
import com.robam.steamoven.device.SteamAbstractControl;
import com.robam.steamoven.device.SteamFactory;
import com.robam.stove.bean.Stove;
import com.robam.stove.device.StoveFactory;
import com.robam.ventilator.bean.Ventilator;
import com.robam.ventilator.device.VentilatorFactory;
import com.robam.ventilator.ui.receiver.AlarmReceiver;

//定时任务
public class AlarmService extends Service {

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
        Intent i = new Intent(this, AlarmReceiver.class);
        pIntent = PendingIntent.getBroadcast(this, PENDING_REQUEST, i, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //通过AlarmManager定时启动广播

        if (null != alarmManager) {
            long triggerAtTime = SystemClock.elapsedRealtime() + INTERVAL;//从开机到现在的毫秒（手机睡眠(sleep)的时间也包括在内
//            try {
//                alarmManager.cancel(pIntent);
//            } catch (Exception e) {}
            alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerAtTime, pIntent);
        }
        //蓝牙扫描
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter.enable()) {
            String[] names = new String[] {"ROBAM"};
            BlueToothManager.setScanRule(names);
        }
        if (!MqttManager.getInstance().isConnected()) {
            //全部离线
            for (Device device: AccountInfo.getInstance().deviceList) {
                if (device.status == Device.ONLINE) {
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
            if (device.queryNum == 1) { //已经查过一次
                device.status = Device.OFFLINE;
                AccountInfo.getInstance().getGuid().setValue(device.guid); //更新设备状态
            }
            device.queryNum++;
            if (device instanceof Pan) { //查询锅
                MqttMsg msg = new MqttMsg.Builder()
                        .setMsgId(MsgKeys.GetPotTemp_Req)
                        .setGuid(Plat.getPlatform().getDeviceOnlySign())
                        .setDt(device.dt)
                        .setTopic(new RTopic(RTopic.TOPIC_UNICAST, DeviceUtils.getDeviceTypeId(device.guid), DeviceUtils.getDeviceNumber(device.guid)))
                        .build();
                MqttManager.getInstance().publish(msg, VentilatorFactory.getTransmitApi());
            } else if (device instanceof Stove) {
                MqttMsg msg = new MqttMsg.Builder()
                        .setMsgId(MsgKeys.SetStoveStatus_Req)
                        .setGuid(Plat.getPlatform().getDeviceOnlySign()) //源guid
                        .setDt(device.dt)
                        .setTopic(new RTopic(RTopic.TOPIC_UNICAST, DeviceUtils.getDeviceTypeId(device.guid), DeviceUtils.getDeviceNumber(device.guid)))
                        .build();
                MqttManager.getInstance().publish(msg, VentilatorFactory.getTransmitApi());
            } else if (device instanceof Ventilator) {
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

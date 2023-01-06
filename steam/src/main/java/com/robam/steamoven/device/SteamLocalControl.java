package com.robam.steamoven.device;

import com.robam.common.mqtt.MqttManager;

import java.util.Map;

//本地控制，串口通信,界面启动入口决定是本地控制还是远程控制
//只有主入口进入时才会开启串口控制
public class SteamLocalControl implements SteamFunction{
    @Override
    public void shutDown(String targetGuid) {

    }

    @Override
    public void powerOn(String targetGuid) {
//        byte[] payload = SerialPortMsgHelper.powerOn();
//        SteamOven.getInstance().marshaller(payload);
//        DeviceFactory.getPlatform().screenOn();
    }

    @Override
    public void orderWork() {

    }

    @Override
    public void stopWork() {

    }

    @Override
    public void startWork() {

    }

    @Override
    public void startCookForAppoint(String targetGuid) {

    }

    @Override
    public void pauseWork(String targetGuid) {

    }

    @Override
    public void continueWork(String targetGuid) {

    }

    @Override
    public void queryAttribute(String targetGuid) {

    }

    @Override
    public void sendCommonMsg(Map<String, Object> params, String targetGuid, short msg_id) {

    }

    @Override
    public void sendCommonMsg(Map<String, Object> params, String targetGuid, short msg_id, MqttManager.MqttSendMsgListener listening) {

    }
}

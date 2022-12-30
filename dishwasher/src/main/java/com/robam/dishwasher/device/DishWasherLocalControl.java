package com.robam.dishwasher.device;

import com.robam.common.mqtt.MqttManager;

import java.util.Map;

//本地控制，串口通信,界面启动入口决定是本地控制还是远程控制
//只有主入口进入时才会开启串口控制
public class DishWasherLocalControl implements DishWasherFunction{
    @Override
    public void shutDown() {

    }

    @Override
    public void powerOn() {

    }

    @Override
    public void shutDown(String targetGuid) {

    }

    @Override
    public void powerOn(String targetGuid) {

    }

    @Override
    public void endAppoint(String targetGuid) {

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
    public void pauseWork(String targetGuid) {

    }

    @Override
    public void continueWork(String targetGuid) {

    }

    @Override
    public void queryAttribute(String targetGuid) {

    }

    @Override
    public void sendCommonMsg(Map<String,Object> params, String targetGuid,short msg_id) {

    }

    @Override
    public void sendCommonMsg(Map<String, Object> params, String targetGuid, short msg_id, MqttManager.MqttSendMsgListener listening) {

    }
}

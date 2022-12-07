package com.robam.steamoven.device;

import com.robam.common.mqtt.MqttManager;

import java.util.Map;

public interface SteamFunction {
    void shutDown(String targetGuid);

    void powerOn(String targetGuid);

    void orderWork();

    void stopWork();

    void startWork();

    void pauseWork(String targetGuid);

    void continueWork(String targetGuid);

    //查询一体机状态
    void queryAttribute(String targetGuid);

    void sendCommonMsg(Map<String,Object> params, String targetGuid, short msg_id);

    void sendCommonMsg(Map<String,Object> params, String targetGuid, short msg_id, MqttManager.MqttSendMsgListener listening);

}

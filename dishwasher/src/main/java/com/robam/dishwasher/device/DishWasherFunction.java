package com.robam.dishwasher.device;

import com.robam.common.mqtt.MqttManager;

import java.util.Map;

public interface DishWasherFunction {
    void shutDown();

    void powerOn();

    void sendCommonMsg(Map<String,Object> params,String targetGuid,short msg_id);

    void sendCommonMsg(Map<String,Object> params, String targetGuid, short msg_id, MqttManager.MqttSendMsgListener listening);

}

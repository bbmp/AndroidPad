package com.robam.cabinet.device;

import com.robam.common.mqtt.MqttManager;

import java.util.Map;

public interface CabinetFunction {
    void shutDown();

    void powerOn();

    void queryAttribute(String targetGuid);

    void sendCommonMsg(Map<String,Object> params, String targetGuid, short msg_id);

    void sendCommonMsg(Map<String,Object> params, String targetGuid, short msg_id, MqttManager.MqttSendMsgListener listening);



}

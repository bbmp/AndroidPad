package com.robam.dishwasher.device;

import com.robam.common.mqtt.MqttManager;

import java.util.Map;

public interface DishWasherFunction {
    void shutDown();

    void powerOn();

    void shutDown(String targetGuid);

    void powerOn(String targetGuid);

    /**
     * 结束预约
     * @param targetGuid
     */
    void endAppoint(String targetGuid);

    void orderWork();

    void stopWork();

    void startWork();

    void pauseWork(String targetGuid);

    void continueWork(String targetGuid);

    //查询一体机状态
    void queryAttribute(String targetGuid);

    void sendCommonMsg(Map<String,Object> params,String targetGuid,short msg_id);

    void sendCommonMsg(Map<String,Object> params, String targetGuid, short msg_id, MqttManager.MqttSendMsgListener listening);

}

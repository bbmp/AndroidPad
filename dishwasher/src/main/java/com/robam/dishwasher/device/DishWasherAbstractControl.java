package com.robam.dishwasher.device;

import com.robam.common.bean.RTopic;
import com.robam.common.constant.PanConstant;
import com.robam.common.device.Plat;
import com.robam.common.mqtt.MqttManager;
import com.robam.common.mqtt.MqttMsg;
import com.robam.common.mqtt.MsgKeys;
import com.robam.common.utils.DeviceUtils;

import org.json.JSONObject;

import java.util.Iterator;
import java.util.Map;

//控制协议调用
public class DishWasherAbstractControl implements DishWasherFunction{
    private DishWasherFunction function;

    private static DishWasherAbstractControl instance = new DishWasherAbstractControl();

    public static DishWasherAbstractControl getInstance() {
        return instance;
    }

    public void init(DishWasherFunction dishWasherFunction) {
        this.function = dishWasherFunction;
    }

    @Override
    public void shutDown() {
        function.shutDown();
    }

    @Override
    public void powerOn() {
        function.powerOn();
    }

    @Override
    public void shutDown(String targetGuid) {
        function.shutDown(targetGuid);
    }

    @Override
    public void powerOn(String targetGuid) {
        function.powerOn(targetGuid);
    }

    @Override
    public void endAppoint(String targetGuid) {
        function.endAppoint(targetGuid);
    }

    @Override
    public void orderWork() {
        function.orderWork();
    }

    @Override
    public void stopWork() {
        function.stopWork();
    }

    @Override
    public void startWork() {
        function.startWork();
    }

    @Override
    public void pauseWork(String targetGuid) {
        function.pauseWork(targetGuid);
    }

    @Override
    public void continueWork(String targetGuid) {
        function.continueWork(targetGuid);
    }

    @Override
    public void queryAttribute(String targetGuid) {
        function.queryAttribute(targetGuid);
    }

    @Override
    public void sendCommonMsg(Map<String,Object> params, String targetGuid,short msg_id) {
        function.sendCommonMsg(params,targetGuid,msg_id);
    }

    @Override
    public void sendCommonMsg(Map<String, Object> params, String targetGuid, short msg_id, MqttManager.MqttSendMsgListener listening) {
        function.sendCommonMsg(params,targetGuid,msg_id,listening);
    }


}

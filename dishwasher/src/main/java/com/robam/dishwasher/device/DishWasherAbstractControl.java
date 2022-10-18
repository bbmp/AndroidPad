package com.robam.dishwasher.device;

import com.robam.common.bean.RTopic;
import com.robam.common.constant.PanConstant;
import com.robam.common.device.Plat;
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
    public void sendCommonMsg(Map<String,Object> params, String targetGuid) {
        function.sendCommonMsg(params,targetGuid);
    }


}

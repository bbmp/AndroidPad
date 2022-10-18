package com.robam.dishwasher.device;

import com.robam.common.bean.RTopic;
import com.robam.common.constant.PanConstant;
import com.robam.common.device.Plat;
import com.robam.common.mqtt.MqttManager;
import com.robam.common.mqtt.MqttMsg;
import com.robam.common.mqtt.MsgKeys;
import com.robam.common.utils.DeviceUtils;

import org.json.JSONException;

import java.util.Iterator;
import java.util.Map;

public class DishWasherMqttControl implements DishWasherFunction{

    @Override
    public void shutDown() {

    }

    @Override
    public void powerOn() {

    }

    @Override
    public void sendCommonMsg(Map<String,Object> params, String targetGuid) {
        try{
            MqttMsg msg = new MqttMsg.Builder()
                    .setMsgId(MsgKeys.getDeviceAttribute_Req) //查询一体机
                    .setGuid(Plat.getPlatform().getDeviceOnlySign())
                    .setTopic(new RTopic(RTopic.TOPIC_UNICAST, DeviceUtils.getDeviceTypeId(targetGuid),
                            DeviceUtils.getDeviceNumber(targetGuid)))
                    .build();
            Iterator iterator = params.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry entry = (Map.Entry) iterator.next();
                msg.put(PanConstant.key, entry.getKey());
            }
            MqttManager.getInstance().publish(msg, DishWasherFactory.getProtocol());
        }catch (JSONException e){
            e.printStackTrace();
        }

    }
}

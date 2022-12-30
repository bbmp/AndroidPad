package com.robam.dishwasher.device;

import com.robam.common.bean.AccountInfo;
import com.robam.common.bean.RTopic;
import com.robam.common.constant.PanConstant;
import com.robam.common.device.Plat;
import com.robam.common.mqtt.MqttManager;
import com.robam.common.mqtt.MqttMsg;
import com.robam.common.mqtt.MsgKeys;
import com.robam.common.utils.DeviceUtils;
import com.robam.dishwasher.constant.DishWasherConstant;
import com.robam.dishwasher.constant.DishWasherState;
import com.robam.dishwasher.util.DishWasherCommandHelper;

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
    public void shutDown(String targetGuid) {

    }

    @Override
    public void powerOn(String targetGuid) {

    }

    @Override
    public void endAppoint(String targetGuid) {
        try{
            MqttMsg msg = new MqttMsg.Builder()
                    .setMsgId(MsgKeys.setDishWasherPower)
                    .setGuid(Plat.getPlatform().getDeviceOnlySign())
                    .setTopic(new RTopic(RTopic.TOPIC_UNICAST, DeviceUtils.getDeviceTypeId(targetGuid),
                            DeviceUtils.getDeviceNumber(targetGuid)))
                    .build();
            msg.put(DishWasherConstant.UserId, AccountInfo.getInstance().getUserString());
            msg.put(DishWasherConstant.PowerMode, DishWasherState.OFF);
            MqttManager.getInstance().publish(msg, DishWasherFactory.getProtocol());
        }catch (JSONException e){
            e.printStackTrace();
        }
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
//        Map map = DishWasherCommandHelper.getCommonMap(MsgKeys.setDishWasherPower);
//        if(isStart){//回复运行
//            map.put(DishWasherConstant.PowerMode, DishWasherState.WORKING);
//            DishWasherCommandHelper.getInstance().sendCommonMsgForLiveData(map,DishWasherState.WORKING);
//        }else{//暂停
//            map.put(DishWasherConstant.PowerMode,DishWasherState.PAUSE);
//            DishWasherCommandHelper.getInstance().sendCommonMsgForLiveData(map,DishWasherState.PAUSE);
//        }

        try{
            MqttMsg msg = new MqttMsg.Builder()
                    .setMsgId(MsgKeys.setDishWasherPower)
                    .setGuid(Plat.getPlatform().getDeviceOnlySign())
                    .setTopic(new RTopic(RTopic.TOPIC_UNICAST, DeviceUtils.getDeviceTypeId(targetGuid),
                            DeviceUtils.getDeviceNumber(targetGuid)))
                    .build();
            msg.put(DishWasherConstant.UserId, AccountInfo.getInstance().getUserString());
            msg.put(DishWasherConstant.PowerMode, DishWasherState.PAUSE);
            MqttManager.getInstance().publish(msg, DishWasherFactory.getProtocol());
        }catch (JSONException e){
            e.printStackTrace();
        }

    }

    @Override
    public void continueWork(String targetGuid) {
        try{
            MqttMsg msg = new MqttMsg.Builder()
                    .setMsgId(MsgKeys.setDishWasherPower)
                    .setGuid(Plat.getPlatform().getDeviceOnlySign())
                    .setTopic(new RTopic(RTopic.TOPIC_UNICAST, DeviceUtils.getDeviceTypeId(targetGuid),
                            DeviceUtils.getDeviceNumber(targetGuid)))
                    .build();
            msg.put(DishWasherConstant.UserId, AccountInfo.getInstance().getUserString());
            msg.put(DishWasherConstant.PowerMode, DishWasherState.WORKING);
            MqttManager.getInstance().publish(msg, DishWasherFactory.getProtocol());
        }catch (JSONException e){
            e.printStackTrace();
        }
    }

    @Override
    public void queryAttribute(String targetGuid) {
        try {
            MqttMsg msg = new MqttMsg.Builder()
                    .setMsgId(MsgKeys.setDishWasherStatus) //洗碗机状态查询
                    .setGuid(Plat.getPlatform().getDeviceOnlySign())
                    .setTopic(new RTopic(RTopic.TOPIC_UNICAST, DeviceUtils.getDeviceTypeId(targetGuid),
                            DeviceUtils.getDeviceNumber(targetGuid)))
                    .build();
            MqttManager.getInstance().publish(msg, DishWasherFactory.getProtocol());
        } catch (Exception e) {}
    }

    @Override
    public void sendCommonMsg(Map<String,Object> params, String targetGuid,short msg_id) {
        try{
            MqttMsg msg = new MqttMsg.Builder()
                    .setMsgId(msg_id)
                    .setGuid(Plat.getPlatform().getDeviceOnlySign())
                    .setTopic(new RTopic(RTopic.TOPIC_UNICAST, DeviceUtils.getDeviceTypeId(targetGuid),
                            DeviceUtils.getDeviceNumber(targetGuid)))
                    .build();
            for(String key: params.keySet()){
                msg.putOpt(key,params.get(key));
            }
            MqttManager.getInstance().publish(msg, DishWasherFactory.getProtocol());
        }catch (JSONException e){
            e.printStackTrace();
        }

    }

    @Override
    public void sendCommonMsg(Map<String, Object> params, String targetGuid, short msg_id, MqttManager.MqttSendMsgListener listening) {
        try{
            MqttMsg msg = new MqttMsg.Builder()
                    .setMsgId(msg_id)
                    .setGuid(Plat.getPlatform().getDeviceOnlySign())
                    .setTopic(new RTopic(RTopic.TOPIC_UNICAST, DeviceUtils.getDeviceTypeId(targetGuid),
                            DeviceUtils.getDeviceNumber(targetGuid)))
                    .build();
            for(String key: params.keySet()){
                msg.putOpt(key,params.get(key));
            }
            MqttManager.getInstance().publish(msg, DishWasherFactory.getProtocol(),listening);
        }catch (JSONException e){
            e.printStackTrace();
        }
    }
}

package com.robam.cabinet.device;

import com.robam.cabinet.constant.CabinetConstant;
import com.robam.cabinet.util.CabinetCommonHelper;
import com.robam.common.bean.RTopic;
import com.robam.common.device.Plat;
import com.robam.common.mqtt.MqttManager;
import com.robam.common.mqtt.MqttMsg;
import com.robam.common.mqtt.MsgKeys;
import com.robam.common.utils.DeviceUtils;
import org.json.JSONException;
import java.util.Map;

//这边是控制协议打包
public class CabinetMqttControl implements CabinetFunction{
    @Override
    public void shutDown(String targetGuid) {
        Map map = CabinetCommonHelper.getCommonMap(MsgKeys.SetSteriPowerOnOff_Req);
        map.put(CabinetConstant.CABINET_STATUS, 0);
        map.put(CabinetConstant.CABINET_TIME, 0);
        map.put(CabinetConstant.ArgumentNumber,0);
        sendCommonMsg(map,targetGuid,MsgKeys.SetSteriPowerOnOff_Req);
    }

    @Override
    public void powerOn() {

    }

    @Override
    public void endAppoint(String targetGuid) {
        Map map = CabinetCommonHelper.getCommonMap(MsgKeys.SetSteriPowerOnOff_Req);
        map.put(CabinetConstant.CABINET_STATUS, 0);
        map.put(CabinetConstant.CABINET_TIME, 0);
        map.put(CabinetConstant.ArgumentNumber,0);
        sendCommonMsg(map,targetGuid,MsgKeys.SetSteriPowerOnOff_Req);

    }

    @Override
    public void queryAttribute(String targetGuid) {
        try {
            MqttMsg msg = new MqttMsg.Builder()
                    .setMsgId(MsgKeys.GetSteriStatus_Req) //洗碗机状态查询
                    .setGuid(Plat.getPlatform().getDeviceOnlySign())
                    .setTopic(new RTopic(RTopic.TOPIC_UNICAST, DeviceUtils.getDeviceTypeId(targetGuid),
                            DeviceUtils.getDeviceNumber(targetGuid)))
                    .build();
            MqttManager.getInstance().publish(msg, CabinetFactory.getProtocol());
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
            MqttManager.getInstance().publish(msg, CabinetFactory.getProtocol());
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
            MqttManager.getInstance().publish(msg, CabinetFactory.getProtocol(),listening);
        }catch (JSONException e){
            e.printStackTrace();
        }
    }
}

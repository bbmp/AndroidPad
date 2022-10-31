package com.robam.steamoven.device;


import com.robam.common.bean.RTopic;
import com.robam.common.device.Plat;
import com.robam.common.mqtt.MqttManager;
import com.robam.common.mqtt.MqttMsg;
import com.robam.common.mqtt.MsgKeys;
import com.robam.common.utils.DeviceUtils;
import com.robam.steamoven.constant.QualityKeys;

import org.json.JSONException;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Map;

//远程控制一体机,mqtt协议打包，从烟机到一体机界面,应该只有烟机会有,供烟机端调用
public class SteamMqttControl implements SteamFunction {
    protected final int BufferSize = 1024;
    protected final ByteOrder BYTE_ORDER = ByteOrder.LITTLE_ENDIAN;

    @Override
    public void shutDown(String targetGuid) { //必须传两个参数

        //mqtt指令打包
        MqttMsg msg = new MqttMsg.Builder()
                .setMsgId(MsgKeys.DeviceConnected_Noti)
                .setGuid(Plat.getPlatform().getDeviceOnlySign())
                .setTopic(new RTopic(RTopic.TOPIC_BROADCAST, DeviceUtils.getDeviceTypeId(targetGuid),
                        DeviceUtils.getDeviceNumber(targetGuid)))
                .build();
        MqttManager.getInstance().publish(msg, SteamFactory.getProtocol());
    }

    @Override
    public void powerOn(String targetGuid) {

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
    public void pauseWork(String targetGuid) { //暂停工作
        //mqtt指令打包
        try {
            ByteBuffer buf = ByteBuffer.allocate(BufferSize).order(BYTE_ORDER);
            buf.put((byte) 1); //参数个数
            buf.put((byte) QualityKeys.workCtrl);
            buf.put((byte) 0x01);
            buf.put((byte) 0x02); //工作中暂停
            // buf to byte[]
            byte[] data = new byte[buf.position()];
            System.arraycopy(buf.array(), 0, data, 0, data.length);
            buf.clear();
            MqttMsg msg = new MqttMsg.Builder()
                    .setMsgId(MsgKeys.setDeviceAttribute_Req)
                    .setGuid(Plat.getPlatform().getDeviceOnlySign())
                    .setPayload(data)
                    .setTopic(new RTopic(RTopic.TOPIC_UNICAST, DeviceUtils.getDeviceTypeId(targetGuid),
                            DeviceUtils.getDeviceNumber(targetGuid)))
                    .build();
            MqttManager.getInstance().publish(msg, SteamFactory.getProtocol());
        } catch (Exception e) {}
    }

    @Override
    public void continueWork(String targetGuid) { //继续工作
        try {
            ByteBuffer buf = ByteBuffer.allocate(BufferSize).order(BYTE_ORDER);
            buf.put((byte) 1); //参数个数
            buf.put((byte) QualityKeys.workCtrl);
            buf.put((byte) 0x01);
            buf.put((byte) 0x04); //继续工作
            // buf to byte[]
            byte[] data = new byte[buf.position()];
            System.arraycopy(buf.array(), 0, data, 0, data.length);
            buf.clear();
            MqttMsg msg = new MqttMsg.Builder()
                    .setMsgId(MsgKeys.setDeviceAttribute_Req)
                    .setGuid(Plat.getPlatform().getDeviceOnlySign())
                    .setPayload(data)
                    .setTopic(new RTopic(RTopic.TOPIC_UNICAST, DeviceUtils.getDeviceTypeId(targetGuid),
                            DeviceUtils.getDeviceNumber(targetGuid)))
                    .build();
            MqttManager.getInstance().publish(msg, SteamFactory.getProtocol());
        } catch (Exception e) {}
    }

    @Override
    public void queryAttribute(String targetGuid) {
        try {
            MqttMsg msg = new MqttMsg.Builder()
                    .setMsgId(MsgKeys.getDeviceAttribute_Req) //查询一体机
                    .setGuid(Plat.getPlatform().getDeviceOnlySign())
                    .setTopic(new RTopic(RTopic.TOPIC_UNICAST, DeviceUtils.getDeviceTypeId(targetGuid),
                            DeviceUtils.getDeviceNumber(targetGuid)))
                    .build();
            MqttManager.getInstance().publish(msg, SteamFactory.getProtocol());
        } catch (Exception e) {}
    }

    public void command(String targetGuid, Map<String,Object> params){
        try {
            MqttMsg msg = new MqttMsg.Builder()
                    .setMsgId(MsgKeys.getDeviceAttribute_Req) //查询一体机
                    .setGuid(Plat.getPlatform().getDeviceOnlySign())
                    .setTopic(new RTopic(RTopic.TOPIC_UNICAST, DeviceUtils.getDeviceTypeId(targetGuid),
                            DeviceUtils.getDeviceNumber(targetGuid)))
                    .build();
            MqttManager.getInstance().publish(msg, SteamFactory.getProtocol());
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
            MqttManager.getInstance().publish(msg, SteamFactory.getProtocol());
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
            MqttManager.getInstance().publish(msg, SteamFactory.getProtocol(),listening);
        }catch (JSONException e){
            e.printStackTrace();
        }
    }


}

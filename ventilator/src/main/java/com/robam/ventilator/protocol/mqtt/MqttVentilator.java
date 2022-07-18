package com.robam.ventilator.protocol.mqtt;

import com.robam.common.mqtt.MqttMsg;
import com.robam.common.mqtt.MqttPublic;
import com.robam.common.mqtt.MsgKeys;
import com.robam.common.utils.ByteUtils;

import java.nio.ByteBuffer;

//烟机mqtt私有协议
public class MqttVentilator extends MqttPublic {
    @Override
    protected void onDecodeMsg(int msgId, byte[] payload, int offset) {
//从payload中取值角标
        //远程被控制
        switch (msgId) {
            case MsgKeys.getDeviceAttribute_Req:

                break;
            case MsgKeys.setDeviceAttribute_Req:
                //属性个数
                short number = ByteUtils.toShort(payload[offset]);
                break;
        }
    }

    @Override
    protected void onEncodeMsg(ByteBuffer buf, MqttMsg msg) {
        //远程控制其他设备或通知上报
        int msgId = msg.getID();
        switch (msgId) {
            case MsgKeys.DeviceConnected_Noti:
                break;
            case MsgKeys.setDeviceAttribute_Rep:
                buf.put((byte) 1);
                buf.put((byte) 0);
                break;
            case MsgKeys.getDeviceAttribute_Rep:
                break;
        }
    }
}

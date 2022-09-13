package com.robam.stove.protocol.mqtt;

import com.robam.common.mqtt.IProtocol;
import com.robam.common.mqtt.MqttMsg;
import com.robam.common.mqtt.MqttPublic;
import com.robam.common.mqtt.MsgKeys;

import java.nio.ByteBuffer;

//灶具mqtt实现
public class MqttStove extends MqttPublic {
    @Override
    public byte[] encode(MqttMsg msg) {
        return new byte[0];
    }

    @Override
    public int decode(String topic, byte[] payload) {
        return 0;
    }

    @Override
    protected void onDecodeMsg(int msgId, String srcGuid, byte[] payload, int offset) {
        switch (msgId) {
            case MsgKeys.SetStoveStatus_Rep: //灶具查询返回
                break;
        }
    }

    @Override
    protected void onEncodeMsg(ByteBuffer buf, MqttMsg msg) {

    }
}

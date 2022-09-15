package com.robam.stove.protocol.mqtt;

import com.robam.common.mqtt.IProtocol;
import com.robam.common.mqtt.MqttMsg;
import com.robam.common.mqtt.MqttPublic;
import com.robam.common.mqtt.MsgKeys;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

//灶具mqtt实现
public class MqttStove extends MqttPublic {


    @Override
    protected void onDecodeMsg(MqttMsg msg, byte[] payload, int offset) throws Exception {
        switch (msg.getID()) {
            case MsgKeys.SetStoveStatus_Rep: //灶具查询返回
                break;
        }
    }

    @Override
    protected void onEncodeMsg(ByteBuffer buf, MqttMsg msg) {

    }
}

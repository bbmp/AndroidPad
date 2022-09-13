package com.robam.dishwasher.protocol.mqtt;

import com.robam.common.ITerminalType;
import com.robam.common.mqtt.IProtocol;
import com.robam.common.mqtt.MqttMsg;
import com.robam.common.mqtt.MqttPublic;
import com.robam.common.mqtt.MsgKeys;

import java.nio.ByteBuffer;

//mqtt洗碗机
public class MqttDishWasher extends MqttPublic {

    @Override
    protected void onDecodeMsg(int msgId, String srcGuid, byte[] payload, int offset) {

    }

    @Override
    protected void onEncodeMsg(ByteBuffer buf, MqttMsg msg) {
        switch (msg.getID()) {
            case MsgKeys.setDishWasherStatus:
                buf.put((byte) ITerminalType.PAD);
                break;
        }
    }
}

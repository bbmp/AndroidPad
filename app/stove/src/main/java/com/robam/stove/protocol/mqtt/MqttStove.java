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
    protected Map onDecodeMsg(int msgId, String srcGuid, byte[] payload, int offset) {
        Map map = new HashMap();
        switch (msgId) {
            case MsgKeys.SetStoveStatus_Rep: //灶具查询返回
                break;
        }
        return map;
    }

    @Override
    protected void onEncodeMsg(ByteBuffer buf, MqttMsg msg) {

    }
}

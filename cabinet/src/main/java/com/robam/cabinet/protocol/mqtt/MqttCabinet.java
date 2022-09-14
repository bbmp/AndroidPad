package com.robam.cabinet.protocol.mqtt;

import com.robam.common.mqtt.IProtocol;
import com.robam.common.mqtt.MqttMsg;
import com.robam.common.mqtt.MqttPublic;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

//消毒柜mqtt
public class MqttCabinet extends MqttPublic {

    @Override
    protected Map onDecodeMsg(int msgId, String srcGuid, byte[] payload, int offset) {
        Map map = new HashMap();

        return map;
    }

    @Override
    protected void onEncodeMsg(ByteBuffer buf, MqttMsg msg) {

    }

}

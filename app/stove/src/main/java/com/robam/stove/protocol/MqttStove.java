package com.robam.stove.protocol;

import com.robam.common.mqtt.IProtocol;
import com.robam.common.mqtt.MqttMsg;

public class MqttStove implements IProtocol {
    @Override
    public byte[] encode(MqttMsg msg) {
        return new byte[0];
    }

    @Override
    public int decode(String topic, byte[] payload) {
        return 0;
    }
}
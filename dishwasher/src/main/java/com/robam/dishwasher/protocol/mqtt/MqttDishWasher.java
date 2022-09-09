package com.robam.dishwasher.protocol.mqtt;

import com.robam.common.mqtt.IProtocol;
import com.robam.common.mqtt.MqttMsg;

//mqtt洗碗机
public class MqttDishWasher implements IProtocol {
    @Override
    public byte[] encode(MqttMsg msg) {
        return new byte[0];
    }

    @Override
    public int decode(String topic, byte[] payload) {
        return 0;
    }
}

package com.robam.pan.protocol.mqtt;

import com.robam.common.mqtt.IProtocol;
import com.robam.common.mqtt.MqttMsg;

//锅mqtt协议，通过烟机转发
public class MqttPan implements IProtocol {
    @Override
    public byte[] encode(MqttMsg msg) {
        return new byte[0];
    }

    @Override
    public int decode(String topic, byte[] payload) {
        return 0;
    }
}

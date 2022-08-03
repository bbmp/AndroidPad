package com.robam.common.mqtt;

public interface IProtocol {
    byte[] encode(MqttMsg msg);

    int decode(String topic, byte[] payload);
}

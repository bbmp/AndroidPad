package com.robam.common.mqtt;

public interface IProtocol {
    byte[] encode(MqttMsg msg) throws Exception;

    int decode(String topic, byte[] data) throws Exception;
}

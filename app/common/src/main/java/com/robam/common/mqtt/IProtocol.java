package com.robam.common.mqtt;

import java.util.Map;

public interface IProtocol {
    byte[] encode(MqttMsg msg);

    MqttMsg decode(String topic, byte[] payload);
}

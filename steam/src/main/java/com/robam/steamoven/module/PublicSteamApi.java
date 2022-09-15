package com.robam.steamoven.module;

import com.robam.common.module.IPublicSteamApi;
import com.robam.common.mqtt.MqttMsg;

import java.util.Map;

public class PublicSteamApi implements IPublicSteamApi {
    @Override
    public byte[] encode(MqttMsg msg) {
        return new byte[0];
    }

    @Override
    public MqttMsg decode(String topic, byte[] payload) {
        return null;
    }
}

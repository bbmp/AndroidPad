package com.robam.stove.module;

import com.robam.common.module.IPublicStoveApi;
import com.robam.common.mqtt.MqttMsg;

//mqtt协议解析和打包
public class PublicStoveApi implements IPublicStoveApi {
    @Override
    public byte[] encode(MqttMsg msg) {
        return new byte[0];
    }

    @Override
    public int decode(String topic, byte[] payload) {
        return 0;
    }
}

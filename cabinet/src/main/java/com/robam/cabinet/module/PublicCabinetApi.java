package com.robam.cabinet.module;

import com.robam.common.module.IPublicCabinetApi;
import com.robam.common.mqtt.IProtocol;
import com.robam.common.mqtt.MqttMsg;

//消毒柜远程控制
public class PublicCabinetApi implements IPublicCabinetApi {
    @Override
    public byte[] encode(MqttMsg msg) {
        return new byte[0];
    }

    @Override
    public int decode(String topic, byte[] payload) {
        return 0;
    }
}

package com.robam.pan.module;

import androidx.lifecycle.MutableLiveData;

import com.robam.common.module.IPublicPanApi;
import com.robam.common.mqtt.MqttMsg;
import com.robam.pan.device.HomePan;

//锅mqtt协议，通过烟机转发
public class PublicPanApi implements IPublicPanApi {
    @Override
    public byte[] encode(MqttMsg msg) {
        return new byte[0];
    }

    @Override
    public int decode(String topic, byte[] payload) {
        return 0;
    }

    @Override
    public MutableLiveData<Integer> getPanTemp() {
        return HomePan.getInstance().panTemp;
    }
}

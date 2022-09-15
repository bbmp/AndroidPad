package com.robam.stove.module;

import androidx.lifecycle.MutableLiveData;

import com.robam.common.module.IPublicStoveApi;
import com.robam.common.mqtt.MqttMsg;
import com.robam.stove.device.HomeStove;

import java.util.Map;

//mqtt协议解析和打包
public class PublicStoveApi implements IPublicStoveApi {
    @Override
    public byte[] encode(MqttMsg msg) {
        return new byte[0];
    }

    @Override
    public MqttMsg decode(String topic, byte[] payload) {
        return null;
    }

    @Override
    public MutableLiveData<Boolean> getLeftStove() {
        return HomeStove.getInstance().leftStove;
    }

    @Override
    public MutableLiveData<Boolean> getRightStove() {
        return HomeStove.getInstance().rightStove;
    }

    @Override
    public int getLeftWorkMode() {
        return HomeStove.getInstance().leftWorkMode;
    }

    @Override
    public int getRightWorkMode() {
        return HomeStove.getInstance().rightWorkMode;
    }
}

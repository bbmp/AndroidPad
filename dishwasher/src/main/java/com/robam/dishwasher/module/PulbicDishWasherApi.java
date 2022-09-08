package com.robam.dishwasher.module;

import com.robam.common.module.IPublicDishWasherApi;
import com.robam.common.mqtt.IProtocol;
import com.robam.common.mqtt.MqttMsg;
import com.robam.common.mqtt.MqttPublic;

import java.nio.ByteBuffer;

//洗碗机私有协议
public class PulbicDishWasherApi implements IPublicDishWasherApi {
    private void onDecodeMsg(int msgId, byte[] payload, int offset) {
        //远程被控制
    }

    private void onEncodeMsg(ByteBuffer buf, MqttMsg msg) {
        //协议打包，上传通知
    }

    @Override
    public byte[] encode(MqttMsg msg) {
        return new byte[0];
    }

    @Override
    public int decode(String topic, byte[] payload) {
        return 0;
    }
}

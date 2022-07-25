package com.robam.dishwasher.protocol.mqtt;

import com.robam.common.mqtt.MqttMsg;
import com.robam.common.mqtt.MqttPublic;

import java.nio.ByteBuffer;

//洗碗机私有协议
public class MqttDishWasher extends MqttPublic {
    @Override
    protected void onDecodeMsg(int msgId, byte[] payload, int offset) {
        //远程被控制
    }

    @Override
    protected void onEncodeMsg(ByteBuffer buf, MqttMsg msg) {
        //协议打包，上传通知
    }
}

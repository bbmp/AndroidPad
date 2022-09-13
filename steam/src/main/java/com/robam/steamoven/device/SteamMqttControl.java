package com.robam.steamoven.device;

import androidx.core.util.Preconditions;

import com.robam.common.bean.RTopic;
import com.robam.common.mqtt.MqttManager;
import com.robam.common.mqtt.MqttMsg;
import com.robam.common.mqtt.MsgKeys;
import com.robam.common.utils.DeviceUtils;

//远程控制一体机,mqtt协议打包，从烟机到一体机界面,应该只有烟机会有,供烟机端调用
public class SteamMqttControl implements SteamFunction {
    @Override
    public void shutDown(String... args) { //必须传两个参数
        if (null == args || args.length < 2)
            return;
        //mqtt指令打包
        MqttMsg msg = new MqttMsg.Builder()
                .setMsgId(MsgKeys.DeviceConnected_Noti)
                .setGuid(args[0])
                .setSignNum(SteamFactory.getPlatform().getMac())
                .setTopic(new RTopic(RTopic.TOPIC_BROADCAST, DeviceUtils.getDeviceTypeId(args[1]),
                        DeviceUtils.getDeviceNumber(args[1])))
                .build();
        MqttManager.getInstance().publish(msg, SteamFactory.getProtocol());
    }

    @Override
    public void powerOn(String... args) {

    }

    @Override
    public void orderWork() {

    }

    @Override
    public void stopWork() {

    }

    @Override
    public void startWork() {

    }

    @Override
    public void pauseWork() {

    }

    @Override
    public void continueWork() {

    }
}

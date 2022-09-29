package com.robam.stove.device;

import com.clj.fastble.callback.BleWriteCallback;
import com.clj.fastble.exception.BleException;
import com.robam.common.bean.Device;
import com.robam.common.bean.RTopic;
import com.robam.common.ble.BleDecoder;
import com.robam.common.device.Plat;
import com.robam.common.manager.BlueToothManager;
import com.robam.common.mqtt.MqttManager;
import com.robam.common.mqtt.MqttMsg;
import com.robam.common.mqtt.MsgKeys;
import com.robam.common.utils.ByteUtils;
import com.robam.common.utils.DeviceUtils;
import com.robam.stove.bean.Stove;
import com.robam.stove.constant.StoveConstant;

import org.json.JSONException;

import java.util.Arrays;

//灶具现在只有蓝牙控制
public class StoveBluetoothControl implements StoveFunction{
    @Override
    public void shutDown() {

    }

    @Override
    public void powerOn() {

    }

    @Override
    public void queryAttribute(Stove stove) {
        //模拟收发
        MqttMsg msg = new MqttMsg.Builder()
                .setMsgId(MsgKeys.GetStoveStatus_Req)
                .setGuid(Plat.getPlatform().getDeviceOnlySign()) //源guid
                .setTopic(new RTopic(RTopic.TOPIC_UNICAST, DeviceUtils.getDeviceTypeId(stove.guid), DeviceUtils.getDeviceNumber(stove.guid)))
                .build();
        //打包payload
        byte[] mqtt_data = StoveFactory.getProtocol().encode(msg);
        //解析
        MqttMsg newMsg = StoveFactory.getProtocol().decode(msg.getrTopic().getTopic(), mqtt_data);
        stove.onMsgReceived(newMsg);
    }

    @Override
    public void setAttribute(Stove stove) {
        if (null == stove.guid)
            return;
        //模拟收发
        MqttMsg msg = new MqttMsg.Builder()
                .setMsgId(MsgKeys.SetStoveStatus_Req)
                .setGuid(Plat.getPlatform().getDeviceOnlySign()) //源guid
                .setTopic(new RTopic(RTopic.TOPIC_UNICAST, DeviceUtils.getDeviceTypeId(stove.guid), DeviceUtils.getDeviceNumber(stove.guid)))
                .build();
        //设置灶具id
        try {
            msg.putOpt(StoveConstant.stoveId, stove.stoveId);
            msg.putOpt(StoveConstant.isCook, stove.isCook);
            msg.putOpt(StoveConstant.workStatus, stove.workStatus);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        //打包payload
        byte[] mqtt_data = StoveFactory.getProtocol().encode(msg);
        //解析
        MqttMsg newMsg = StoveFactory.getProtocol().decode(msg.getrTopic().getTopic(), mqtt_data);
        stove.onMsgReceived(newMsg);
    }

    @Override
    public void setLevel(Stove stove) {
        //模拟收发
        MqttMsg msg = new MqttMsg.Builder()
                .setMsgId(MsgKeys.SetStoveLevel_Req)
                .setGuid(Plat.getPlatform().getDeviceOnlySign()) //源guid
                .setTopic(new RTopic(RTopic.TOPIC_UNICAST, DeviceUtils.getDeviceTypeId(stove.guid), DeviceUtils.getDeviceNumber(stove.guid)))
                .build();
        //设置灶具id
        try {
            msg.putOpt(StoveConstant.stoveId, stove.stoveId);
            msg.putOpt(StoveConstant.isCook, stove.isCook);
            msg.putOpt(StoveConstant.level, stove.level);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        //打包payload
        byte[] mqtt_data = StoveFactory.getProtocol().encode(msg);
        //解析
        MqttMsg newMsg = StoveFactory.getProtocol().decode(msg.getrTopic().getTopic(), mqtt_data);
        stove.onMsgReceived(newMsg);
    }

    @Override
    public void setTiming(Stove stove) {
        //模拟收发
        MqttMsg msg = new MqttMsg.Builder()
                .setMsgId(MsgKeys.SetStoveShutdown_Req)
                .setGuid(Plat.getPlatform().getDeviceOnlySign()) //源guid
                .setTopic(new RTopic(RTopic.TOPIC_UNICAST, DeviceUtils.getDeviceTypeId(stove.guid), DeviceUtils.getDeviceNumber(stove.guid)))
                .build();
        //设置灶具id
        try {
            msg.putOpt(StoveConstant.stoveId, stove.stoveId);
            msg.putOpt(StoveConstant.timingtime, stove.timingTime);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        //打包payload
        byte[] mqtt_data = StoveFactory.getProtocol().encode(msg);
        //解析
        MqttMsg newMsg = StoveFactory.getProtocol().decode(msg.getrTopic().getTopic(), mqtt_data);
        stove.onMsgReceived(newMsg);
    }

    @Override
    public void setRecipe(Stove stove) {
        //模拟收发
        MqttMsg msg = new MqttMsg.Builder()
                .setMsgId(MsgKeys.setStoveRecipe_Req)
                .setGuid(Plat.getPlatform().getDeviceOnlySign()) //源guid
                .setTopic(new RTopic(RTopic.TOPIC_UNICAST, DeviceUtils.getDeviceTypeId(stove.guid), DeviceUtils.getDeviceNumber(stove.guid)))
                .build();
        //设置灶具id
        try {
            msg.putOpt(StoveConstant.stoveId, stove.stoveId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        //打包payload
        byte[] mqtt_data = StoveFactory.getProtocol().encode(msg);
        //解析
        MqttMsg newMsg = StoveFactory.getProtocol().decode(msg.getrTopic().getTopic(), mqtt_data);
        stove.onMsgReceived(newMsg);
    }
}

package com.robam.stove.device;

import com.clj.fastble.callback.BleWriteCallback;
import com.clj.fastble.exception.BleException;
import com.robam.common.bean.AccountInfo;
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
    //需用内部指令
    @Override
    public void queryAttribute(String targetGuid) {
        //模拟收发
        for (Device device: AccountInfo.getInstance().deviceList) {
            if (device instanceof Stove && null != device.guid && device.guid.equals(targetGuid)) {
                MqttMsg msg = new MqttMsg.Builder()
                        .setMsgId(MsgKeys.GetStoveStatus_Req)
                        .setGuid(Plat.getPlatform().getDeviceOnlySign()) //源guid
                        .setTopic(new RTopic(RTopic.TOPIC_UNICAST, DeviceUtils.getDeviceTypeId(device.guid), DeviceUtils.getDeviceNumber(device.guid)))
                        .build();
                //打包payload
                byte[] mqtt_data = StoveFactory.getProtocol().encode(msg);
                //解析
                MqttMsg newMsg = StoveFactory.getProtocol().decode(msg.getrTopic().getTopic(), mqtt_data);
                device.onMsgReceived(newMsg);
                break;
            }
        }
    }

    @Override
    public void setAttribute(String targetGuid, byte stoveId, byte isCook, byte workStatus) {
        for (Device device: AccountInfo.getInstance().deviceList) {
            if (device instanceof Stove && null != device.guid && device.guid.equals(targetGuid)) {
                //模拟收发
                MqttMsg msg = new MqttMsg.Builder()
                        .setMsgId(MsgKeys.SetStoveStatus_Req)
                        .setGuid(Plat.getPlatform().getDeviceOnlySign()) //源guid
                        .setTopic(new RTopic(RTopic.TOPIC_UNICAST, DeviceUtils.getDeviceTypeId(device.guid), DeviceUtils.getDeviceNumber(device.guid)))
                        .build();
                //设置灶具id
                try {
                    msg.putOpt(StoveConstant.stoveId, stoveId);
                    msg.putOpt(StoveConstant.isCook, isCook);
                    msg.putOpt(StoveConstant.workStatus, workStatus);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                //打包payload
                byte[] mqtt_data = StoveFactory.getProtocol().encode(msg);
                //解析
                MqttMsg newMsg = StoveFactory.getProtocol().decode(msg.getrTopic().getTopic(), mqtt_data);
                device.onMsgReceived(newMsg);
                break;
            }
        }
    }

    @Override
    public void setLevel(String targetGuid, byte stoveId, byte isCook, byte level) {
        for (Device device: AccountInfo.getInstance().deviceList) {
            if (device instanceof Stove && null != device.guid && device.guid.equals(targetGuid)) {
                //模拟收发
                MqttMsg msg = new MqttMsg.Builder()
                        .setMsgId(MsgKeys.SetStoveLevel_Req)
                        .setGuid(Plat.getPlatform().getDeviceOnlySign()) //源guid
                        .setTopic(new RTopic(RTopic.TOPIC_UNICAST, DeviceUtils.getDeviceTypeId(device.guid), DeviceUtils.getDeviceNumber(device.guid)))
                        .build();
                //设置灶具id
                try {
                    msg.putOpt(StoveConstant.stoveId, stoveId);
                    msg.putOpt(StoveConstant.isCook, isCook);
                    msg.putOpt(StoveConstant.level, level);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                //打包payload
                byte[] mqtt_data = StoveFactory.getProtocol().encode(msg);
                //解析
                MqttMsg newMsg = StoveFactory.getProtocol().decode(msg.getrTopic().getTopic(), mqtt_data);
                device.onMsgReceived(newMsg);
                break;
            }
        }
    }

    @Override
    public void setTiming(String targetGuid, byte stoveId, short timingTime) {
        for (Device device: AccountInfo.getInstance().deviceList) {
            if (device instanceof Stove && null != device.guid && device.guid.equals(targetGuid)) {
                //模拟收发
                MqttMsg msg = new MqttMsg.Builder()
                        .setMsgId(MsgKeys.SetStoveShutdown_Req)
                        .setGuid(Plat.getPlatform().getDeviceOnlySign()) //源guid
                        .setTopic(new RTopic(RTopic.TOPIC_UNICAST, DeviceUtils.getDeviceTypeId(device.guid), DeviceUtils.getDeviceNumber(device.guid)))
                        .build();
                //设置灶具id
                try {
                    msg.putOpt(StoveConstant.stoveId, stoveId);
                    msg.putOpt(StoveConstant.timingtime, timingTime);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                //打包payload
                byte[] mqtt_data = StoveFactory.getProtocol().encode(msg);
                //解析
                MqttMsg newMsg = StoveFactory.getProtocol().decode(msg.getrTopic().getTopic(), mqtt_data);
                device.onMsgReceived(newMsg);
                break;
            }
        }
    }

    @Override
    public void setRecipe(String targetGuid, byte stoveId) {
        for (Device device: AccountInfo.getInstance().deviceList) {
            if (device instanceof Stove && null != device.guid && device.guid.equals(targetGuid)) {
                //模拟收发
                MqttMsg msg = new MqttMsg.Builder()
                        .setMsgId(MsgKeys.setStoveRecipe_Req)
                        .setGuid(Plat.getPlatform().getDeviceOnlySign()) //源guid
                        .setTopic(new RTopic(RTopic.TOPIC_UNICAST, DeviceUtils.getDeviceTypeId(device.guid), DeviceUtils.getDeviceNumber(device.guid)))
                        .build();
                //设置灶具id
                try {
                    msg.putOpt(StoveConstant.stoveId, stoveId);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                //打包payload
                byte[] mqtt_data = StoveFactory.getProtocol().encode(msg);
                //解析
                MqttMsg newMsg = StoveFactory.getProtocol().decode(msg.getrTopic().getTopic(), mqtt_data);
                device.onMsgReceived(newMsg);
                break;
            }
        }
    }
}

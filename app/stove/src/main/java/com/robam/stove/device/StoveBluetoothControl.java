package com.robam.stove.device;

import android.bluetooth.BluetoothGattCharacteristic;

import com.clj.fastble.callback.BleWriteCallback;
import com.clj.fastble.data.BleDevice;
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
import com.robam.common.utils.LogUtils;
import com.robam.stove.bean.Stove;
import com.robam.stove.constant.StoveConstant;

import org.json.JSONException;

import java.util.Arrays;

//灶具现在只有蓝牙控制
public class StoveBluetoothControl implements StoveFunction{
    //烟机内部控制
    private void write_no_response(BleDevice bleDevice, BluetoothGattCharacteristic characteristic, byte[] mqtt_data) {
        int cmd_id = ByteUtils.toInt(mqtt_data[BleDecoder.GUID_LEN]);
        Byte[] mqtt_payload = BleDecoder.byteArraysToByteArrays(Arrays.copyOfRange(mqtt_data, BleDecoder.GUID_LEN + 1, mqtt_data.length - 1));
        //封装成内部命令
        Byte[] data = BleDecoder.make_internal_send_packet(cmd_id, mqtt_payload);

        //发送蓝牙数据
        BlueToothManager.write_no_response(bleDevice, characteristic, BleDecoder.ByteArraysTobyteArrays(data), new BleWriteCallback() {

            @Override
            public void onWriteSuccess(final int current, final int total, final byte[] justWrite) {
                LogUtils.e("onWriteSuccess");
            }

            @Override
            public void onWriteFailure(final BleException exception) {
                LogUtils.e("onWriteFailure");
            }
        });
    }

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
        try {
            for (Device device : AccountInfo.getInstance().deviceList) {
                if (device instanceof Stove && null != device.guid && device.guid.equals(targetGuid)) {
                    MqttMsg msg = new MqttMsg.Builder()
                            .setMsgId(MsgKeys.FanGetStoveStatus_req)
                            .setGuid(Plat.getPlatform().getDeviceOnlySign()) //源guid
                            .setTopic(new RTopic(RTopic.TOPIC_UNICAST, DeviceUtils.getDeviceTypeId(device.guid), DeviceUtils.getDeviceNumber(device.guid)))
                            .build();
                    //打包payload
                    byte[] mqtt_data = StoveFactory.getProtocol().encode(msg);

                    write_no_response(((Stove) device).bleDevice, ((Stove) device).characteristic, mqtt_data);
                    break;
                }
            }
        } catch (Exception e) {}
    }

    @Override
    public void setAttribute(String targetGuid, byte stoveId, byte isCook, byte workStatus) {
        try {
            for (Device device : AccountInfo.getInstance().deviceList) {
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

                    write_no_response(((Stove) device).bleDevice, ((Stove) device).characteristic, mqtt_data);
                    break;
                }
            }
        } catch (Exception e) {}
    }

    @Override
    public void setLevel(String targetGuid, byte stoveId, byte isCook, byte level) {
        try {
            for (Device device : AccountInfo.getInstance().deviceList) {
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

                    write_no_response(((Stove) device).bleDevice, ((Stove) device).characteristic, mqtt_data);
                    break;
                }
            }
        } catch (Exception e) {}
    }

    @Override
    public void setTiming(String targetGuid, byte stoveId, short timingTime) {
        try {
            for (Device device : AccountInfo.getInstance().deviceList) {
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

                    write_no_response(((Stove) device).bleDevice, ((Stove) device).characteristic, mqtt_data);
                    break;
                }
            }
        } catch (Exception e) {}
    }

    @Override
    public void setRecipe(String targetGuid, byte stoveId) {
        try {
            for (Device device : AccountInfo.getInstance().deviceList) {
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

                    write_no_response(((Stove) device).bleDevice, ((Stove) device).characteristic, mqtt_data);
                    break;
                }
            }
        } catch (Exception e) {}
    }
}

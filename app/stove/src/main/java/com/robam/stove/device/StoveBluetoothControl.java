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
import com.robam.common.mqtt.MqttMsg;
import com.robam.common.mqtt.MsgKeys;
import com.robam.common.utils.ByteUtils;
import com.robam.common.utils.DeviceUtils;
import com.robam.common.utils.LogUtils;
import com.robam.stove.bean.CurveStep;
import com.robam.common.device.subdevice.Stove;
import com.robam.common.constant.StoveConstant;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.List;

//灶具现在只有蓝牙控制
public class StoveBluetoothControl implements StoveFunction{
    //烟机内部控制
    private void write_no_response(String targetGuid, BleDevice bleDevice, BluetoothGattCharacteristic characteristic, byte[] mqtt_data) {
        int cmd_id = ByteUtils.toInt(mqtt_data[BleDecoder.GUID_LEN]);
        Byte[] mqtt_payload = BleDecoder.byteArraysToByteArrays(Arrays.copyOfRange(mqtt_data, BleDecoder.GUID_LEN + 1, mqtt_data.length));
        //封装成内部命令
        Byte[] data = BleDecoder.make_internal_send_packet(cmd_id, mqtt_payload);

        //发送蓝牙数据
        BlueToothManager.write_no_response(bleDevice, characteristic, BleDecoder.ByteArraysTobyteArrays(data), new BleWriteCallback() {

            @Override
            public void onWriteSuccess(final int current, final int total, final byte[] justWrite) {
                LogUtils.e("onWriteSuccess");
//                if (cmd_id == MsgKeys.SetStoveLock_Req || cmd_id == MsgKeys.SetStoveStatus_Req || cmd_id == MsgKeys.SetStoveShutdown_Req)
//                    queryAttribute(targetGuid); //立即查询
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

    @Override
    public void setLock(String targetGuid, int status) {
        try {
            for (Device device : AccountInfo.getInstance().deviceList) {
                if (device instanceof Stove && null != device.guid && device.guid.equals(targetGuid)) {
                    MqttMsg msg = new MqttMsg.Builder()
                            .setMsgId(MsgKeys.SetStoveLock_Req)
                            .setGuid(Plat.getPlatform().getDeviceOnlySign()) //源guid
                            .setTopic(new RTopic(RTopic.TOPIC_UNICAST, DeviceUtils.getDeviceTypeId(device.guid), DeviceUtils.getDeviceNumber(device.guid)))
                            .build();
                    //设置灶具id
                    try {
                        msg.putOpt(StoveConstant.lockStatus, status);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    //打包payload
                    byte[] mqtt_data = StoveFactory.getProtocol().encode(msg);

                    write_no_response(targetGuid, ((Stove) device).bleDevice, ((Stove) device).characteristic, mqtt_data);
                    break;
                }
            }
        } catch (Exception e) {}
    }

    //需用内部指令
    @Override
    public void queryAttribute(String targetGuid) {
        //模拟收发
        try {
            for (Device device : AccountInfo.getInstance().deviceList) {
                if (device instanceof Stove && null != device.guid && device.guid.equals(targetGuid)) {
                    MqttMsg msg = new MqttMsg.Builder()
                            .setMsgId(MsgKeys.GetStoveStatus_Req)
                            .setGuid(Plat.getPlatform().getDeviceOnlySign()) //源guid
                            .setTopic(new RTopic(RTopic.TOPIC_UNICAST, DeviceUtils.getDeviceTypeId(device.guid), DeviceUtils.getDeviceNumber(device.guid)))
                            .build();
                    //打包payload
                    byte[] mqtt_data = StoveFactory.getProtocol().encode(msg);

                    write_no_response(targetGuid, ((Stove) device).bleDevice, ((Stove) device).characteristic, mqtt_data);
                    break;
                }
            }
        } catch (Exception e) {}
    }

    @Override
    public void setAttribute(String targetGuid, int stoveId, int isCook, int workStatus) {
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

                    write_no_response(targetGuid, ((Stove) device).bleDevice, ((Stove) device).characteristic, mqtt_data);
                    break;
                }
            }
        } catch (Exception e) {}
    }

    @Override
    public void setLevel(String targetGuid, int stoveId, int isCook, int level, int recipeId, int step) {
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
                        msg.putOpt(StoveConstant.recipeId, recipeId);
                        msg.putOpt(StoveConstant.recipeStep, step);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    //打包payload
                    byte[] mqtt_data = StoveFactory.getProtocol().encode(msg);

                    write_no_response(targetGuid, ((Stove) device).bleDevice, ((Stove) device).characteristic, mqtt_data);
                    break;
                }
            }
        } catch (Exception e) {}
    }

    @Override
    public void setTiming(String targetGuid, int stoveId, int timingTime) {
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

                    write_no_response(targetGuid, ((Stove) device).bleDevice, ((Stove) device).characteristic, mqtt_data);
                    break;
                }
            }
        } catch (Exception e) {}
    }

    @Override
    public void setRecipe(String targetGuid, int stoveId) {
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

                    write_no_response(targetGuid, ((Stove) device).bleDevice, ((Stove) device).characteristic, mqtt_data);
                    break;
                }
            }
        } catch (Exception e) {}
    }

    @Override
    public void setCurveStepParams(String targetGuid, int stoveId, List<CurveStep> curveSteps) {
        try {
            for (Device device : AccountInfo.getInstance().deviceList) {
                if (device instanceof Stove && null != device.guid && device.guid.equals(targetGuid)) {
                    MqttMsg msg = new MqttMsg.Builder()
                            .setMsgId(MsgKeys.setStoveStep_Req)
                            .setGuid(Plat.getPlatform().getDeviceOnlySign()) //源guid
                            .setTopic(new RTopic(RTopic.TOPIC_UNICAST, DeviceUtils.getDeviceTypeId(device.guid), DeviceUtils.getDeviceNumber(device.guid)))
                            .build();

                    JSONArray jsonArray = new JSONArray();
                    msg.putOpt(StoveConstant.stoveId, stoveId);
                    if (null != curveSteps) {
                        for (CurveStep curveStep : curveSteps) {
                            JSONObject jsonObject = new JSONObject();
                            jsonObject.putOpt(StoveConstant.control, 0x00); //控制方式
                            jsonObject.putOpt(StoveConstant.level, 0x00);//挡位
                            jsonObject.putOpt(StoveConstant.stepTemp, 0x00);
                            jsonObject.putOpt(StoveConstant.stepTime, 0x00);
                            jsonArray.put(jsonObject);
                        }
                        msg.putOpt(StoveConstant.attributeNum, jsonArray.length());
                        msg.putOpt(StoveConstant.steps, jsonArray);
                    }

                    //打包payload
                    byte[] mqtt_data = StoveFactory.getProtocol().encode(msg);

                    write_no_response(targetGuid, ((Stove) device).bleDevice, ((Stove) device).characteristic, mqtt_data);
                    break;
                }
            }
        } catch (Exception e) {}
    }

    @Override
    public void setStoveParams(int cmd, byte[] payload) {
        try {
            for (Device device : AccountInfo.getInstance().deviceList) {
                if (device instanceof Stove) { //这里没有guid校验
                    //加密数据
                    Byte[] data = BleDecoder.mcu_uart_pack(BleDecoder.byteArraysToByteArrays(payload));

                    //发送蓝牙数据
                    BlueToothManager.write_no_response(((Stove) device).bleDevice, ((Stove) device).characteristic, BleDecoder.ByteArraysTobyteArrays(data), new BleWriteCallback() {

                        @Override
                        public void onWriteSuccess(final int current, final int total, final byte[] justWrite) {
                            LogUtils.e("onWriteSuccess");
                        }

                        @Override
                        public void onWriteFailure(final BleException exception) {
                            LogUtils.e("onWriteFailure");
                        }
                    });
                    break;
                }
            }
        } catch (Exception e) {}
    }
}

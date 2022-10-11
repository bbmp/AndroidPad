package com.robam.pan.device;

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
import com.robam.pan.bean.CurveStep;
import com.robam.pan.bean.Pan;
import com.robam.pan.constant.PanConstant;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.List;

//锅现在只有蓝牙控制
public class PanBluetoothControl implements PanFunction{

    //烟机内部控制
    private void write_no_response(BleDevice bleDevice, BluetoothGattCharacteristic characteristic, byte[] mqtt_data) {
        int cmd_id = ByteUtils.toInt(mqtt_data[BleDecoder.GUID_LEN]);
        Byte[] mqtt_payload = BleDecoder.byteArraysToByteArrays(Arrays.copyOfRange(mqtt_data, BleDecoder.GUID_LEN + 1, mqtt_data.length));
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

    @Override
    public void queryAttribute(String targetGuid) {
        //模拟收发
        try {
            for (Device device : AccountInfo.getInstance().deviceList) {
                if (device instanceof Pan && null != device.guid && device.guid.equals(targetGuid)) {
                    MqttMsg msg = new MqttMsg.Builder()
                            .setMsgId(MsgKeys.FanGetPanStatus_Req)
                            .setGuid(Plat.getPlatform().getDeviceOnlySign()) //源guid
                            .setTopic(new RTopic(RTopic.TOPIC_UNICAST, DeviceUtils.getDeviceTypeId(device.guid), DeviceUtils.getDeviceNumber(device.guid)))
                            .build();
                    //打包payload
                    byte[] mqtt_data = PanFactory.getProtocol().encode(msg);

                    write_no_response(((Pan) device).bleDevice, ((Pan) device).characteristic, mqtt_data);
                    break;
                }
            }
        } catch (Exception e) {}
    }

    @Override
    public void setCurveStepParams(String targetGuid, int stoveId, List<CurveStep> curveSteps) {
        try {
            for (Device device : AccountInfo.getInstance().deviceList) {
                if (device instanceof Pan && null != device.guid && device.guid.equals(targetGuid)) {
                    MqttMsg msg = new MqttMsg.Builder()
                            .setMsgId(MsgKeys.POT_CURVEElectric_Req)
                            .setGuid(Plat.getPlatform().getDeviceOnlySign()) //源guid
                            .setTopic(new RTopic(RTopic.TOPIC_UNICAST, DeviceUtils.getDeviceTypeId(device.guid), DeviceUtils.getDeviceNumber(device.guid)))
                            .build();

                    JSONArray jsonArray = new JSONArray();
                    msg.putOpt(PanConstant.stoveId, stoveId);
                    if (null != curveSteps) {
                        for (CurveStep curveStep : curveSteps) {
                            JSONObject jsonObject = new JSONObject();
                            jsonObject.putOpt(PanConstant.control, 0x00); //控制方式
                            jsonObject.putOpt(PanConstant.level, 0x00);//挡位
                            jsonObject.putOpt(PanConstant.stepTemp, 0x00);
                            jsonObject.putOpt(PanConstant.stepTime, 0x00);
                            jsonArray.put(jsonObject);
                        }
                        msg.putOpt(PanConstant.attributeNum, jsonArray.length());
                        msg.putOpt(PanConstant.steps, jsonArray);
                    }

                    //打包payload
                    byte[] mqtt_data = PanFactory.getProtocol().encode(msg);

                    write_no_response(((Pan) device).bleDevice, ((Pan) device).characteristic, mqtt_data);
                    break;
                }
            }
        } catch (Exception e) {}
    }

    @Override
    public void setFryMode(String targetGuid, int mode) {
        try {
            for (Device device : AccountInfo.getInstance().deviceList) {
                if (device instanceof Pan && null != device.guid && device.guid.equals(targetGuid)) {
                    MqttMsg msg = new MqttMsg.Builder()
                            .setMsgId(MsgKeys.FanInteractPan_req)
                            .setGuid(Plat.getPlatform().getDeviceOnlySign()) //源guid
                            .setTopic(new RTopic(RTopic.TOPIC_UNICAST, DeviceUtils.getDeviceTypeId(device.guid), DeviceUtils.getDeviceNumber(device.guid)))
                            .build();

                    msg.putOpt(PanConstant.fryMode, mode);
                    //打包payload
                    byte[] mqtt_data = PanFactory.getProtocol().encode(msg);

                    write_no_response(((Pan) device).bleDevice, ((Pan) device).characteristic, mqtt_data);
                    break;
                }
            }
        } catch (Exception e) {}
    }
}

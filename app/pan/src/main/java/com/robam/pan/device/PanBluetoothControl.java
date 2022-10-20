package com.robam.pan.device;

import android.bluetooth.BluetoothGattCharacteristic;

import com.clj.fastble.callback.BleWriteCallback;
import com.clj.fastble.data.BleDevice;
import com.clj.fastble.exception.BleException;
import com.github.mikephil.charting.data.Entry;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.robam.common.bean.AccountInfo;
import com.robam.common.bean.Device;
import com.robam.common.bean.LineChartDataBean;
import com.robam.common.bean.RTopic;
import com.robam.common.bean.SetPotCurveStageParams;
import com.robam.common.ble.BleDecoder;
import com.robam.common.manager.BlueToothManager;
import com.robam.common.mqtt.MqttMsg;
import com.robam.common.mqtt.MsgKeys;
import com.robam.common.utils.ByteUtils;
import com.robam.common.utils.CurveUtils;
import com.robam.common.utils.DeviceUtils;
import com.robam.common.utils.LogUtils;
import com.robam.common.device.subdevice.Pan;
import com.robam.common.constant.PanConstant;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

//锅现在只有蓝牙控制
public class PanBluetoothControl implements PanFunction{

    //烟机内部控制
    private void write_no_response(MqttMsg msg, BleDevice bleDevice, BluetoothGattCharacteristic characteristic, byte[] mqtt_data) {
        int cmd_id = ByteUtils.toInt(mqtt_data[BleDecoder.GUID_LEN]);
        String send_guid = msg.getGuid();
        Byte[] mqtt_payload = BleDecoder.byteArraysToByteArrays(Arrays.copyOfRange(mqtt_data, BleDecoder.GUID_LEN + 1, mqtt_data.length));
        //封装成内部命令
        BleDecoder.ExternBleData data = BleDecoder.make_external_send_packet(cmd_id, mqtt_payload);
        //保存回复guid
        BlueToothManager.send_map.put(data.cmd_key, send_guid);
        //发送蓝牙数据
        BlueToothManager.write_no_response(bleDevice, characteristic, BleDecoder.ByteArraysTobyteArrays(data.payload), new BleWriteCallback() {

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
                            .setMsgId(MsgKeys.GetPotTemp_Req)
                            .setGuid(targetGuid) //源guid
                            .setTopic(new RTopic(RTopic.TOPIC_UNICAST, DeviceUtils.getDeviceTypeId(device.guid), DeviceUtils.getDeviceNumber(device.guid)))
                            .build();
                    //打包payload
                    byte[] mqtt_data = PanFactory.getProtocol().encode(msg);

                    write_no_response(msg, ((Pan) device).bleDevice, ((Pan) device).characteristic, mqtt_data);
                    break;
                }
            }
        } catch (Exception e) {}
    }

    @Override
    public void setCurvePanParams(String targetGuid, String smartPanCurveParams) {
        try {
            for (Device device : AccountInfo.getInstance().deviceList) {
                if (device instanceof Pan && null != device.guid && device.guid.equals(targetGuid)) {
                    MqttMsg msg = new MqttMsg.Builder()
                            .setMsgId(MsgKeys.POT_CURVEElectric_Req)
                            .setGuid(targetGuid) //源guid
                            .setTopic(new RTopic(RTopic.TOPIC_UNICAST, DeviceUtils.getDeviceTypeId(device.guid), DeviceUtils.getDeviceNumber(device.guid)))
                            .build();

                    Map<String, String> mapPanCurve = new Gson().fromJson(smartPanCurveParams, new TypeToken<LinkedHashMap<String, String>>(){}.getType());

                    JSONArray jsonArray = new JSONArray();

                    if (null != mapPanCurve) {
                        int no = 1; //从1开始
                        for (Map.Entry<String, String> entry : mapPanCurve.entrySet())  {
                            String[] data = entry.getValue().split("-");
                            JSONObject jsonObject = new JSONObject();
                            jsonObject.putOpt(PanConstant.key, no); //第几步
                            jsonObject.putOpt(PanConstant.fryMode, Integer.parseInt(data[0]));//搅拌参数
                            jsonObject.putOpt(PanConstant.stepTime, Integer.parseInt(data[1])); //步骤时间
                            jsonArray.put(jsonObject);
                            no++;
                        }
                        msg.putOpt(PanConstant.attributeNum, jsonArray.length());
                        msg.putOpt(PanConstant.steps, jsonArray);
                    }

                    //打包payload
                    byte[] mqtt_data = PanFactory.getProtocol().encode(msg);

                    write_no_response(msg, ((Pan) device).bleDevice, ((Pan) device).characteristic, mqtt_data);
                    break;
                }
            }
        } catch (Exception e) {}
    }

    @Override
    public void setCurveStoveParams(String targetGuid, int stoveId, String curveStageParams, String curveTempParams) {
        try {
            for (Device device : AccountInfo.getInstance().deviceList) {
                if (device instanceof Pan && null != device.guid && device.guid.equals(targetGuid)) {
                    MqttMsg msg = new MqttMsg.Builder()
                            .setMsgId(MsgKeys.POT_CURVETEMP_Req)
                            .setGuid(targetGuid) //源guid
                            .setTopic(new RTopic(RTopic.TOPIC_UNICAST, DeviceUtils.getDeviceTypeId(device.guid), DeviceUtils.getDeviceNumber(device.guid)))
                            .build();

                    JSONObject object = new JSONObject(curveStageParams);
                    String strData = object.get("cmdPoints").toString();
                    List<SetPotCurveStageParams> ParamsList = CurveUtils.curveStageParamsToList(strData);

                    List<LineChartDataBean> dataBeanList = CurveUtils.curveDataToLine(curveTempParams);

                    List<SetPotCurveStageParams> ParamsHasGearList = CurveUtils.curveStageParamsListSetGear(ParamsList, dataBeanList);

                    JSONArray jsonArray = new JSONArray();

                    for (int i = 1; i < ParamsHasGearList.size(); i++)  {

                        JSONObject jsonObject = new JSONObject();
                        jsonObject.putOpt(PanConstant.key, i); //第几步
                        jsonObject.putOpt(PanConstant.control, ParamsHasGearList.get(i).control); //控制方式
                        jsonObject.putOpt(PanConstant.level, ParamsHasGearList.get(i).gear); //灶具挡位
                        jsonObject.putOpt(PanConstant.stepTemp, ParamsHasGearList.get(i).temp); //标记温度
                        jsonObject.putOpt(PanConstant.stepTime, ParamsHasGearList.get(i).time - ParamsHasGearList.get(i - 1).time); //步骤时间
                        jsonArray.put(jsonObject);

                    }
                    msg.putOpt(PanConstant.stoveId, stoveId);//炉头id
                    msg.putOpt(PanConstant.attributeNum, jsonArray.length());
                    msg.putOpt(PanConstant.steps, jsonArray);

                    //打包payload
                    byte[] mqtt_data = PanFactory.getProtocol().encode(msg);

                    write_no_response(msg, ((Pan) device).bleDevice, ((Pan) device).characteristic, mqtt_data);
                    break;
                }
            }
        } catch (Exception e) {}
    }

    @Override
    public void setInteractionParams(String targetGuid, Map params) {
        try {
            for (Device device : AccountInfo.getInstance().deviceList) {
                if (device instanceof Pan && null != device.guid && device.guid.equals(targetGuid)) {
                    MqttMsg msg = new MqttMsg.Builder()
                            .setMsgId(MsgKeys.POT_INTERACTION_Req)
                            .setGuid(targetGuid) //源guid
                            .setTopic(new RTopic(RTopic.TOPIC_UNICAST, DeviceUtils.getDeviceTypeId(device.guid), DeviceUtils.getDeviceNumber(device.guid)))
                            .build();
                    Iterator iterator = params.entrySet().iterator();
                    JSONArray jsonArray = new JSONArray();
                    while (iterator.hasNext()) {
                        Map.Entry entry = (Map.Entry) iterator.next();
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.putOpt(PanConstant.key, entry.getKey());
                        jsonObject.putOpt(PanConstant.value, entry.getValue());
                        jsonArray.put(jsonObject);
                    }
                    msg.putOpt(PanConstant.interaction, jsonArray);
                    //打包payload
                    byte[] mqtt_data = PanFactory.getProtocol().encode(msg);

                    write_no_response(msg, ((Pan) device).bleDevice, ((Pan) device).characteristic, mqtt_data);
                    break;
                }
            }
        } catch (Exception e) {}
    }
}

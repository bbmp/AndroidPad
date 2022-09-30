package com.robam.pan.bean;

import android.bluetooth.BluetoothGattCharacteristic;

import androidx.lifecycle.MutableLiveData;

import com.clj.fastble.callback.BleWriteCallback;
import com.clj.fastble.data.BleDevice;
import com.clj.fastble.exception.BleException;
import com.robam.common.bean.Device;
import com.robam.common.ble.BleDecoder;
import com.robam.common.manager.BlueToothManager;
import com.robam.common.mqtt.MqttMsg;
import com.robam.common.mqtt.MsgKeys;
import com.robam.common.utils.ByteUtils;

import java.util.Arrays;

public class Pan extends Device {
    //锅温度
    public int panTemp;
    //蓝牙设备
    public BleDevice bleDevice;
    //蓝牙特征符
    public BluetoothGattCharacteristic characteristic;
    //蓝牙解析
    public BleDecoder bleDecoder;

    public Pan(Device device) {
        this.ownerId = device.ownerId;
        this.mac = device.mac;
        this.guid = device.guid;
        this.bid = device.bid;
        this.dc = device.dc;
        this.dt = device.dt;
        this.displayType = device.displayType;
        this.categoryName = device.categoryName;
        this.deviceTypeIconUrl = device.deviceTypeIconUrl;
        this.subDevices = device.subDevices;
    }

    public Pan(String name, String dc, String displayType) {
        super(name, dc, displayType);
    }


    @Override
    public boolean onMsgReceived(MqttMsg msg) {
        byte[] mqtt_data = msg.getBytes();
        byte[] send_guid_bytes = Arrays.copyOfRange(mqtt_data, 0, BleDecoder.GUID_LEN);
        int cmd_id = ByteUtils.toInt(mqtt_data[BleDecoder.GUID_LEN]);
        Byte[] mqtt_payload = BleDecoder.byteArraysToByteArrays(Arrays.copyOfRange(mqtt_data, BleDecoder.GUID_LEN + 1, mqtt_data.length - 1));
        //转化成蓝牙包
        BleDecoder.ExternBleData data = BleDecoder.make_external_send_packet(cmd_id, mqtt_payload);
//        send_map.put(data.cmd_key, new String(send_guid_bytes));
//        ble_write_no_resp(dev.getChan(), BleDecoder.ByteArraysTobyteArrays(data.payload));
        //发送蓝牙数据
        BlueToothManager.write_no_response(bleDevice, characteristic, BleDecoder.ByteArraysTobyteArrays(data.payload), new BleWriteCallback() {

            @Override
            public void onWriteSuccess(final int current, final int total, final byte[] justWrite) {

            }

            @Override
            public void onWriteFailure(final BleException exception) {

            }
        });
        return super.onMsgReceived(msg);
    }
}

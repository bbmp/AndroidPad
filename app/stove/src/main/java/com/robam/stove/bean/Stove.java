package com.robam.stove.bean;

import android.bluetooth.BluetoothGattCharacteristic;

import androidx.lifecycle.MutableLiveData;

import com.clj.fastble.callback.BleWriteCallback;
import com.clj.fastble.data.BleDevice;
import com.clj.fastble.exception.BleException;
import com.robam.common.bean.Device;
import com.robam.common.ble.BleDecoder;
import com.robam.common.manager.BlueToothManager;
import com.robam.common.mqtt.MqttMsg;
import com.robam.common.utils.ByteUtils;
import com.robam.common.utils.LogUtils;

import java.util.Arrays;


/**
 * 灶具
 */
public class Stove extends Device {

    //蓝牙设备
    public BleDevice bleDevice;
    //蓝牙特征符
    public BluetoothGattCharacteristic characteristic;
    //蓝牙解析
    public BleDecoder bleDecoder;

    public Stove(Device device) {
        this.ownerId = device.ownerId;
        this.guid = device.guid;
        this.bid = device.bid;
        this.dc = device.dc;
        this.dt = device.dt;
        this.displayType = device.displayType;
        this.categoryName = device.categoryName;
        this.subDevices = device.subDevices;
    }

    public Stove(String name, String dc, String displayType) {
        super(name, dc, displayType);
    }


    /**
     * 当前功能
     */
    public int funCode;
    //炉头id
    public byte stoveId;
    /**
     *  设置菜谱
     */
    public byte isCook;
    /**
     * 功率等级
     */
    public byte level;
    /**
     * 定时时间 s
     */
    public byte timingTime;
    /**
     * 左灶工作模式
     */
    public int leftWorkMode;
    /**
     * 左灶工作时长
     */
    public String leftWorkHours;
    //左灶工作温度
    public String leftWorkTemp;
    //左灶
    public MutableLiveData<Boolean> leftStove = new MutableLiveData<>(false);
    /**
     * 右灶工作模式
     */
    public int rightWorkMode;
    /**
     * 右灶工作时长
     */
    public String rightWorkHours;
    //右灶工作温度
    public String rightWorkTemp;
    //右灶
    public MutableLiveData<Boolean> rightStove = new MutableLiveData<>(false);

    @Override
    public boolean onMsgReceived(MqttMsg msg) {
        byte[] mqtt_data = msg.getBytes();
        String send_guid = msg.getGuid();
        int cmd_id = ByteUtils.toInt(mqtt_data[BleDecoder.GUID_LEN]);
        Byte[] mqtt_payload = BleDecoder.byteArraysToByteArrays(Arrays.copyOfRange(mqtt_data, BleDecoder.GUID_LEN + 1, mqtt_data.length - 1));
        //转化成蓝牙包
        BleDecoder.ExternBleData data = BleDecoder.make_external_send_packet(cmd_id, mqtt_payload);
        //保存回复guid
        BlueToothManager.send_map.put(data.cmd_key, send_guid);
//        ble_write_no_resp(dev.getChan(), BleDecoder.ByteArraysTobyteArrays(data.payload));
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
        return super.onMsgReceived(msg);
    }
}

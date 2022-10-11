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
import com.robam.common.mqtt.MsgKeys;
import com.robam.common.utils.ByteUtils;
import com.robam.common.utils.LogUtils;
import com.robam.stove.constant.StoveConstant;

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

    public int lockStatus; //童锁状态

    /**
     * 左灶工作模式
     */
    public int leftWorkMode;

    public int leftStatus;

    public int leftLevel;
    /**
     * 左灶定时时长
     */
    public int leftTimeHours;//剩余秒数
    //左灶工作温度
    public float leftWorkTemp;
    //左灶
    public int leftStove;
    //
    public int leftAlarm;
    /**
     * 右灶工作模式
     */
    public int rightWorkMode;

    public int rightStatus;

    public int rightLevel;
    /**
     * 右灶工作时长
     */
    public int rightTimeHours;
    //右灶工作温度
    public float rightWorkTemp;
    //右灶
    public int rightStove;
    //
    public int rightAlarm;

    @Override
    public boolean onMsgReceived(MqttMsg msg) {
        if (null != msg && msg.getID() != MsgKeys.GetStoveStatus_Req) { //非查询消息
            byte[] mqtt_data = msg.getBytes();
            String send_guid = msg.getGuid();
            int cmd_id = ByteUtils.toInt(mqtt_data[BleDecoder.GUID_LEN]);
            Byte[] mqtt_payload = BleDecoder.byteArraysToByteArrays(Arrays.copyOfRange(mqtt_data, BleDecoder.GUID_LEN + 1, mqtt_data.length));
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
        }
        return super.onMsgReceived(msg);
    }
    //收到蓝牙消息
    public boolean onBleReceived(MqttMsg msg) {
        if (null != msg && null != msg.opt(StoveConstant.stoveNum)) {
            queryNum = 0; //查询超过一次无响应离线
            status = Device.ONLINE;
            lockStatus = (int) msg.opt(StoveConstant.lockStatus);
            leftStatus = (int) msg.opt(StoveConstant.leftStatus);
            leftLevel = (int) msg.opt(StoveConstant.leftLevel);
            leftTimeHours = (int) msg.opt(StoveConstant.leftTime);
            if (msg.has(StoveConstant.leftTemp))
                leftWorkTemp = (float) msg.opt(StoveConstant.leftTemp);

            rightStatus = (int) msg.opt(StoveConstant.rightStatus);
            rightLevel = (int) msg.opt(StoveConstant.rightLevel);
            rightTimeHours = (int) msg.opt(StoveConstant.rightTime);
            if (msg.has(StoveConstant.rightTemp))
                rightWorkTemp = (float) msg.opt(StoveConstant.rightTemp);
            return true;
        }
        return false;
    }
}

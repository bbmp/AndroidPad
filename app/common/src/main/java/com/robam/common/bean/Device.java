package com.robam.common.bean;

import android.bluetooth.BluetoothGattCharacteristic;

import com.clj.fastble.data.BleDevice;
import com.google.gson.annotations.Expose;
import com.robam.common.ble.BleDecoder;
import com.robam.common.mqtt.MqttMsg;
import com.robam.common.utils.LogUtils;

import java.nio.ByteBuffer;
import java.util.List;

public class Device {
    public final static String EXTRA_GUID = "guid";
    public final static int OFFLINE = 0;
    public final static int ONLINE  = 1;
    //在线状态
    public int status = OFFLINE;

    //工作状态
    public int workStatus = 0;

    public int queryNum = 0;   //查询次数
    /**
     * 拥有者编码
     */
    @Expose
    public long ownerId;
    /**
     * 设备组编码
     */
    @Expose
    public long groupId;
    /**
     * MAC地址
     */
    @Expose
    public String mac;
    /**
     * 唯一编码
     */
    @Expose
    public String guid;
    /**
     * 业务编码（供应商定制ID）
     */
    @Expose
    public String bid;

    /**
     * 设备名称
     */
    @Expose
    public String dc;

    /**
     * 设备平台
     */
    @Expose
    public String dp;

    /**
     * 设备类型
     */
    @Expose
    public String dt;

    /**
     * 展示的设备类型名称
     */
    @Expose
    public String displayType;
    //
    @Expose
    public String categoryName;

    //子设备
    public List<Device> subDevices;

    //故障ID
    public int faultId;

    /**
     * 蓝牙产品品类
     */
    @Expose
    public int bleType;
    //内部设备类型(1B)+内部设备编码(3B)
    @Expose
    public String int_guid;
    //蓝牙版本
    @Expose
    public int bleVer;

    //设备名称
    @Expose
    public String name;

    public Device() {
    }
    //带蓝牙的设备
    public Device(String mac) {
        this.mac = mac;
    }

    public Device(String guid, String int_guid, String bid, int bleType) {
        this.guid = guid;
        this.int_guid =  int_guid;
        this.bid = bid;
        this.bleType = bleType;
    }

    public Device(String name, String dc, String displayType) {
        this.categoryName = name;
        this.dc = dc;
        this.displayType = displayType;
    }

    public String getDisplayType() {
        return displayType;
    }

    public String getCategoryName() {
        return categoryName;
   }

    public int getStatus() {
        return status;
    }

    public int getWorkStatus() {
        return workStatus;
    }
    //更新设备参数用
    public boolean onMsgReceived(MqttMsg msg) {
        return false;
    }

    public String getName() {
        return name;
    }
}

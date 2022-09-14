package com.robam.pan.bean;

import androidx.lifecycle.MutableLiveData;

import com.robam.common.bean.Device;
import com.robam.common.mqtt.MsgKeys;
import com.robam.common.utils.ByteUtils;

public class Pan extends Device {
    //锅温度
    public int panTemp;

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


}

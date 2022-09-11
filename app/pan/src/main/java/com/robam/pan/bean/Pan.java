package com.robam.pan.bean;

import androidx.lifecycle.MutableLiveData;

import com.robam.common.bean.Device;

public class Pan extends Device {
    public Pan(Device device) {
        this.ownerId = device.ownerId;
        this.guid = device.guid;
        this.bid = device.bid;
        this.dc = device.dc;
        this.dt = device.dt;
        this.displayType = device.displayType;
        this.categoryName = device.categoryName;
        this.deviceTypeIconUrl = device.deviceTypeIconUrl;
        this.subDevices = device.subDevices;
    }

    public Pan(String name, String displayType) {
        super(name, displayType);
    }

    @Override
    public void onReceivedMsg(int msgId, String guid, byte[] payload, int offset) {

    }

    //锅温度
    public MutableLiveData<Integer> panTemp = new MutableLiveData<Integer>(0);
}

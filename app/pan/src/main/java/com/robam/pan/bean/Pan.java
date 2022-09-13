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

    public Pan(String name, String displayType) {
        super(name, displayType);
    }

    @Override
    public void onReceivedMsg(int msgId, String guid, byte[] payload, int offset) {
        if (!this.guid.equals(guid))
            return; //非当前设备
        switch (msgId) {
            case MsgKeys.getDeviceAttribute_Req:  //属性查询
                break;
            case MsgKeys.getDeviceAttribute_Rep: {  //属性查询回复
                //属性个数
                short number = ByteUtils.toShort(payload[offset]);
                offset++;
                while (number > 0) {
                    short key =  ByteUtils.toShort(payload[offset]);
                    offset++;
                    short length =  ByteUtils.toShort(payload[offset]);
                    offset++;
                }
            }
            break;
        }

    }

}

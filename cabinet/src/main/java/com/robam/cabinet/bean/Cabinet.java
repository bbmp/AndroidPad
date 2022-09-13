package com.robam.cabinet.bean;

import com.robam.common.bean.Device;

/**
 * 消毒柜
 */
public class Cabinet extends Device{
    public Cabinet(Device device) {
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

    public Cabinet(String name, String displayType) {
        super(name, displayType);
    }

    /**
     * 预约开始时间
     */
    public String orderTime;
    /**
     * 工作模式
     */
    public int workMode;
    /**
     * 工作时长
     */
    public int workHours;

    @Override
    public void unmarshaller(int msgId, String guid, byte[] payload, int offset) {

    }
}

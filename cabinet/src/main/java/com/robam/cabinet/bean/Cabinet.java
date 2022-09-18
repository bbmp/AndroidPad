package com.robam.cabinet.bean;

import com.robam.cabinet.constant.CabinetConstant;
import com.robam.common.bean.Device;
import com.robam.common.mqtt.MqttMsg;

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

    public Cabinet(String name, String dc, String displayType) {
        super(name, dc, displayType);
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
    public boolean onMsgReceived(MqttMsg msg) {
        if (null != msg && null != msg.opt(CabinetConstant.SteriStatus)) {
            queryNum = 0;
            status = Device.ONLINE;
            return true;
        }
        return super.onMsgReceived(msg);
    }
}

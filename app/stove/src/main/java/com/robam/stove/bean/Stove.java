package com.robam.stove.bean;

import androidx.lifecycle.MutableLiveData;

import com.robam.common.bean.Device;
import com.robam.common.mqtt.MqttMsg;


/**
 * 灶具
 */
public class Stove extends Device {

    public Stove(Device device) {
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

    public Stove(String name, String dc, String displayType) {
        super(name, dc, displayType);
    }


    /**
     * 当前功能
     */
    public int funCode;

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

        return super.onMsgReceived(msg);
    }
}

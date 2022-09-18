package com.robam.dishwasher.bean;

import android.content.Context;

import com.robam.common.bean.Device;
import com.robam.common.manager.FunctionManager;
import com.robam.common.mqtt.MqttMsg;
import com.robam.common.mqtt.MsgKeys;
import com.robam.common.utils.ByteUtils;
import com.robam.dishwasher.R;
import com.robam.dishwasher.constant.DishWasherConstant;

import java.util.List;

//洗碗机
public class DishWasher extends Device{
    public DishWasher(Device device) {
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

    public DishWasher(String name, String dc, String displayType) {
        super(name, dc, displayType);
    }

    private List<DishWaherModeBean> dishWaherModeBeans;


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
    /**
    *   辅助模式
     */
    public int auxMode;


    @Override
    public boolean onMsgReceived(MqttMsg msg) {
        if (msg != null && null != msg.opt(DishWasherConstant.powerStatus)) {
            queryNum = 0;
            status = Device.ONLINE;
            return true;
        }
        return super.onMsgReceived(msg);
    }
}

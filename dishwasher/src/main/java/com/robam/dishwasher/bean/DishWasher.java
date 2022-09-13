package com.robam.dishwasher.bean;

import android.content.Context;

import com.robam.common.bean.Device;
import com.robam.common.manager.FunctionManager;
import com.robam.common.mqtt.MsgKeys;
import com.robam.common.utils.ByteUtils;
import com.robam.dishwasher.R;

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

    public DishWasher(String name, String displayType) {
        super(name, displayType);
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
    public void onReceivedMsg(int msgId, String guid, byte[] payload, int offset) {
        switch (msgId) {
            case MsgKeys.getDishWasherStatus:
                short powerStatus =
                        ByteUtils.toShort(payload[offset++]);
                short stoveLock =
                        ByteUtils.toShort(payload[offset++]);
                short dishWasherWorkMode =
                        ByteUtils.toShort(payload[offset++]);
                int dishWasherRemainingWorkingTime =
                        ByteUtils.toInt32(payload, offset++, ByteUtils.BYTE_ORDER);
                offset++;
                short lowerLayerWasher =
                        ByteUtils.toShort(payload[offset++]);
                short enhancedDryStatus =
                        ByteUtils.toShort(payload[offset++]);
                short appointmentSwitchStatus =
                        ByteUtils.toShort(payload[offset++]);
                short autoVentilation =
                        ByteUtils.toShort(payload[offset++]);
                int appointmentTime =
                        ByteUtils.toInt32(payload, offset++, ByteUtils.BYTE_ORDER);
                offset++;
                int appointmentRemainingTime =
                        ByteUtils.toInt32(payload, offset++, ByteUtils.BYTE_ORDER);
                offset++;
                short rinseAgentPositionKey =
                        ByteUtils.toShort(payload[offset++]);
                short saltFlushValue =
                        ByteUtils.toShort(payload[offset++]);
                short dishWasherFanSwitch =
                        ByteUtils.toShort(payload[offset++]);
                short doorOpenState =
                        ByteUtils.toShort(payload[offset++]);
                short lackRinseStatus =
                        ByteUtils.toShort(payload[offset++]);
                short lackSaltStatus =
                        ByteUtils.toShort(payload[offset++]);
                short abnormalAlarmStatus =
                        ByteUtils.toShort(payload[offset++]);

                short ADD_AUX =
                        ByteUtils.toShort(payload[payload.length - 1]);

                short argument = ByteUtils.toShort(payload[offset++]);
                break;
        }
    }
}

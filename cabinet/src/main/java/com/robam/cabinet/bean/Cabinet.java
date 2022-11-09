package com.robam.cabinet.bean;

import com.robam.cabinet.constant.CabinetConstant;
import com.robam.common.bean.Device;
import com.robam.common.mqtt.MqttMsg;
import com.robam.common.mqtt.MsgKeys;

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
            parserDeviceInfo(msg);
        }
        return true;
        //return super.onMsgReceived(msg);
    }

    private void parserDeviceInfo(MqttMsg msg){

        try {
            int key = msg.getID();
            switch (key) {
                case MsgKeys.SteriAlarm_Noti:
                    short alarmId = (short) msg.optInt(CabinetConstant.AlarmId);
                    break;
                case MsgKeys.GetSteriStatus_Rep:
                    status = (short) msg.optInt(CabinetConstant.SteriStatus);
                    isChildLock = (short) msg.optInt(CabinetConstant.SteriLock);
                    remainingModeWorkTime = (short) msg.optInt(CabinetConstant.SteriWorkLeftTimeL);
                    doorLock = (short) msg.optInt(CabinetConstant.SteriDoorLock);
                    AlarmStautus = (short) msg.optInt(CabinetConstant.SteriAlarmStatus);
                    temp = (short) msg.optInt(CabinetConstant.SteriParaTem);
                    hum = (short) msg.optInt(CabinetConstant.SteriParaHum);
                    germ = (short) msg.optInt(CabinetConstant.SteriParaGerm);
                    ozone = (short) msg.optInt(CabinetConstant.SteriParaOzone);
                    argumentNumber = (short) msg.optInt(CabinetConstant.ArgumentNumber);
                    if (argumentNumber > 0) {
                        // 预约剩余时间
                        remainingAppointTime = (short) msg.optInt(CabinetConstant.REMAINING_APPOINT_TIME);
                        //停止工作时是否进入安全锁
                        steriSecurityLock = (short) msg.optInt(CabinetConstant.SteriSecurityLock);
                    }
                    break;
                case MsgKeys.GetSteriPVConfig_Rep:
                    break;
                case MsgKeys.SteriEvent_Noti:
                    short eventId = (short) msg.optInt(CabinetConstant.EventId);
                    short eventParam = (short) msg.optInt(CabinetConstant.EventParam);
                    break;
                default:
                    break;

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    public short setSteriTem;
    public short oldstatus;
    public short isChildLock;
    public boolean isDoorLock;
    public short AlarmStautus;
    public short remainingAppointTime;//预约剩余时间
    public short SteriReserveTime;
    public short SteriDrying;
    public short remainingModeWorkTime; //剩余工作时间
    public short temp, hum, germ, ozone;
    //short weeklySteri_week;
    public short doorLock;
    public short argumentNumber;
    public short steriSecurityLock;
}

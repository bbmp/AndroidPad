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
            return true;
        }
        return super.onMsgReceived(msg);
    }

    private void parserDeviceInfo(MqttMsg msg){

        try {
            int key = msg.getID();
            switch (key) {
                case MsgKeys.SteriAlarm_Noti:
                    short alarmId = (short) msg.optInt(CabinetConstant.AlarmId);
                    break;
                case MsgKeys.GetSteriStatus_Rep:
                    //oldstatus = status;
                    status = (short) msg.optInt(CabinetConstant.SteriStatus);
                    isChildLock = (short) msg.optInt(CabinetConstant.SteriLock);
                    work_left_time_l = (short) msg.optInt(CabinetConstant.SteriWorkLeftTimeL);
                    work_left_time_h = (short) msg.optInt(CabinetConstant.SteriWorkLeftTimeH);
                    AlarmStautus = (short) msg.optInt(CabinetConstant.SteriAlarmStatus);

                    break;
                case MsgKeys.GetSteriParam_Rep:
                    temp = (short) msg.optInt(CabinetConstant.SteriParaTem);
                    hum = (short) msg.optInt(CabinetConstant.SteriParaHum);
                    germ = (short) msg.optInt(CabinetConstant.SteriParaGerm);
                    ozone = (short) msg.optInt(CabinetConstant.SteriParaOzone);
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
    public short SteriReserveTime;
    public short SteriDrying;
    public short SteriCleanTime;
    public short SteriDisinfectTime, work_left_time_l, work_left_time_h;
    public short temp, hum, germ, ozone;
    short weeklySteri_week;
    private static int times = 0;
}

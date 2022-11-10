package com.robam.dishwasher.bean;

import com.robam.common.bean.Device;
import com.robam.common.mqtt.MqttMsg;
import com.robam.common.mqtt.MsgKeys;
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
        this.subDevices = device.subDevices;
    }

    public DishWasher(String name, String dc, String displayType) {
        super(name, dc, displayType);
    }

    private List<DishWasherModeBean> dishWaherModeBeans;


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
        if(msg != null &&  null != msg.opt(DishWasherConstant.powerStatus)){
            queryNum = 0;
            status = Device.ONLINE;
            parserMsg(msg.getID(),msg);
            return true;
        }
        return true;
    }
    private void parserMsg(int key,MqttMsg msg){
        switch (key) {
            case MsgKeys.getDishWasherStatus:
                this.powerStatus = (short) msg.optInt(DishWasherConstant.powerStatus);
                this.workStatus = powerStatus;
                this.StoveLock = (short) msg.optInt(DishWasherConstant.StoveLock);
                this.workMode = (short) msg.optInt(DishWasherConstant.DishWasherWorkMode);//模式名称 washer.DishWasherWorkMode
                this.remainingWorkingTime = (short) msg.optInt(DishWasherConstant.REMAINING_WORKING_TIME);

                this.LowerLayerWasher = (short) msg.optInt(DishWasherConstant.LowerLayerWasher);//下层洗
                this.EnhancedDryStatus = (short) msg.optInt(DishWasherConstant.EnhancedDryStatus);
                this.AppointmentSwitchStatus = (short) msg.optInt(DishWasherConstant.AppointmentSwitchStatus);
                this.AutoVentilation = (short) msg.optInt(DishWasherConstant.AutoVentilation);
                this.AppointmentTime = (short) msg.optInt(DishWasherConstant.AppointmentTime);
                this.AppointmentRemainingTime = (short) msg.optInt(DishWasherConstant.AppointmentRemainingTime);
                this.RinseAgentPositionValue = (short) msg.optInt(DishWasherConstant.RinseAgentPositionValue);
                this.SaltFlushValue = (short) msg.optInt(DishWasherConstant.SaltFlushValue);
                this.DishWasherFanSwitch = (short) msg.optInt(DishWasherConstant.DishWasherFanSwitch);
                this.DoorOpenState = (short) msg.optInt(DishWasherConstant.DoorOpenState);
                this.LackRinseStatus = (short) msg.optInt(DishWasherConstant.LackRinseStatus);
                this.LackSaltStatus = (short) msg.optInt(DishWasherConstant.LackSaltStatus);
                this.abnormalAlarmStatus = (short) msg.optInt(DishWasherConstant.AbnormalAlarmStatus);
                this.ArgumentNumber = (short) msg.optInt(DishWasherConstant.ArgumentNumber);
                this.CurrentWaterTemperatureValue = (short) msg.optInt(DishWasherConstant.CurrentWaterTemperatureValue);
                this.SetWorkTimeValue = (short) msg.optInt(DishWasherConstant.SetWorkTimeValue);
                this.auxMode = (short) msg.optInt(DishWasherConstant.ADD_AUX);
                break;
            case MsgKeys.getAlarmEventReport:
                this.alarmId = (short) msg.optInt(DishWasherConstant.DishWasherAlarm);
                //postEvent(new DishWasherAlarmEvent(this, alarmId));
                break;
            case MsgKeys.getEventReport:
                parserEvent(msg);
                break;


//            case MsgKeys.getEventReport:
//
//
//                switch (eventId) {
//                    case Event_DishWasher_Power_Control_Reset:
//
//                        postEvent(new DishWasherSwitchControlResetEvent(this, (short) msg.optInt(DishWasherConstant.EventParam)));
//                        break;
//                    case Event_DishWasher_Work_Mode_Reset:
//                        postEvent(new DishWasherWorkModeResetEvent(this, (short) msg.optInt(DishWasherConstant.EventParam)));
//                        break;
//                    case Event_DishWasher_Work_Complete_Reset:
//                        this.powerConsumption = (short) msg.optInt(DishWasherConstant.powerConsumption);
//                        this.waterConsumption = (short) msg.optInt(DishWasherConstant.waterConsumption);
//                        postEvent(new DishWasherWorkCompleteResetEvent(this, powerConsumption,waterConsumption));
//                        break;
//                    case Event_DishWasher_Lack_Rinse_Reset:
//                        postEvent(new DishWasherLackRinseResetEvent(this, (short) msg.optInt(DishWasherConstant.EventParam)));
//                        break;
//                    case Event_DishWasher_Lack_Salt_Reset:
//                        postEvent(new DishWasherLackSaltResetEvent(this, (short) msg.optInt(DishWasherConstant.EventParam)));
//                        break;
//                    case Event_DishWasher_Open_Door_Reset:
//                        postEvent(new DishWasherOpenDoorResetEvent(this, (short) msg.optInt(DishWasherConstant.EventParam)));
//                        break;
//
//                }
//                break;
        }
    }

    private void parserEvent(MqttMsg msg){
        short eventId = (short) msg.optInt(DishWasherConstant.EventId);
        switch (eventId){
            case Event_DishWasher_Work_Complete_Reset:
                this.powerConsumption = (short) msg.optInt(DishWasherConstant.POWER_CONSUMPTION);
                this.waterConsumption = (short) msg.optInt(DishWasherConstant.WATER_CONSUMPTION);
                break;
        }
    }

    /**
     * 电源状态
     */
    public short powerStatus;
    /**
     * 锁
     */
    public short StoveLock;
    /**
     * 工作模式
     */
    //public short DishWasherWorkMode;
    /**
     * 剩余工作时间
     */
    public short remainingWorkingTime;
    /**
     * 下层洗
     */
    public short LowerLayerWasher;
    /**
     * 加强干燥
     */
    public short EnhancedDryStatus;
    public short AppointmentSwitchStatus;
    public short AutoVentilation;

    //public short addAux ;
    /**
     * 设置的预约时间
     */
    public short AppointmentTime;
    /**
     * 预约剩余时间
     */
    public short AppointmentRemainingTime;
    /**
     * 漂洗剂档位
     */
    public short RinseAgentPositionValue;
    /**
     * 冲盐档位
     */
    public short SaltFlushValue;
    /**
     * 风机开关
     */
    public short DishWasherFanSwitch;
    /**
     * 开门开关
     */
    public short DoorOpenState;
    /**
     * 漂洗剂状态呢
     */
    public short LackRinseStatus;
    /**
     * 冲盐状态
     */
    public short LackSaltStatus;
    /**
     * 异常报警状态
     */
    public short abnormalAlarmStatus;
    public short ArgumentNumber;
    public short CurrentWaterTemperatureValue;
    public short SetWorkTimeValue;
    public short powerConsumption;
    public short waterConsumption;
    public short alarmId;
    //protected short terminalType = TerminalType.getType();
    //事件
    static final public short Event_DishWasher_Power_Control_Reset = 10;//电源状态控制事件
    static final public short Event_DishWasher_Work_Mode_Reset = 11;//工作模式调整事件
    static final public short Event_DishWasher_Work_Complete_Reset = 12;//工作完成事件
    static final public short Event_DishWasher_Lack_Rinse_Reset = 13;//却漂洗剂事件
    static final public short Event_DishWasher_Lack_Salt_Reset = 14;//缺盐事件
    static final public short Event_DishWasher_Open_Door_Reset = 15;//开门事件
    /**
     * 漂洗剂通知
     */
    public boolean rinse;
    /**
     * 水软盐通知
     */
    public boolean salt;
}

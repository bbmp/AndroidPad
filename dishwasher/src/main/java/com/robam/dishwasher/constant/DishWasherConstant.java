package com.robam.dishwasher.constant;

public class DishWasherConstant {
    public final static int MODE_SMART = 1;
    public final static int MODE_POWFULL = 2;
    public final static int MODE_QUICK = 3;
    public final static int MODE_DAILY = 4;
    public final static int MODE_SAVING = 5;
    public final static int MODE_BRIGHT = 6;
    public final static int MODE_BABYCARE = 7;
    public final static int MODE_SELFCLEAN = 8;
    public final static int MODE_FLUSH = 9;

    public final static int AUX_PAN_POWFULL = 10;
    public final static int AUX_KILL_POWFULL = 11;
    public final static int AUX_FLUSH = 12;
    public final static int AUX_DOWN_WASH = 13;

    public final static String EXTRA_MODEBEAN = "modebean";

    public final static String powerStatus = "powerStatus";//电源状态
    public final static String DishWasherWorkMode = "DishWasherWorkMode";//洗碗机工作模式
    public final static String DishWasherRemainingWorkingTime = "DishWasherRemainingWorkingTime";//剩余工作时间


    public final static String LowerLayerWasher = "LowerLayerWasher";//下层洗开关
    public final static String AutoVentilation = "AutoVentilation";//自动换气
    public final static String EnhancedDrySwitch = "EnhancedDrySwitch";//加强干燥开关
    public final static String AppointmentSwitch = "AppointmentSwitch";//预约开关
    public final static String AppointmentTime = "AppointmentTime";//预约时间


    /**
     * 用户编码[10Byte]
     */
    public final static String UserId = "UserId";

    public final static String OvenRecipeStep = "OvenRecipeStep";//菜谱步骤
    public final static String ArgumentNumber = "ArgumentNumber";//参数个数

    public final static  String SaltFlushKey = "SaltFlushKey";//冲盐挡位
    public final static  String SaltFlushLength = "SaltFlushLength";//冲盐挡位
    public final static   String SaltFlushValue = "SaltFlushValue";//冲盐挡位

    public final static String RinseAgentPositionKey = "RinseAgentPositionKey";//漂洗剂档位
    public final static String RinseAgentPositionLength = "RinseAgentPositionLength";//漂洗剂档位
    public final static  String RinseAgentPositionValue = "RinseAgentPositionValue";//漂洗剂档位

    public final static  String AppointmentSwitchStatus = "AppointmentSwitchStatus";//预约开关状态

    public final static  String AppointmentRemainingTime = "AppointmentRemainingTime";//预约剩余时间

    /**
     * 附加功能
     */
    public final static String ADD_AUX = "add_aux";

    public final static String PowerMode = "PowerMode";//电源模式

    /**
     * 童锁状态[1Byte]，{0：解锁，1上锁}
     */
    public final static String StoveLock = "StoveLock";

    public final static  String EnhancedDryStatus = "EnhancedDryStatus";//将强干燥状态

    public final static  String SetWorkTimeValue = "SetWorkTimeValue";//设置工作时间

    public final static  String AbnormalAlarmStatus = "AbnormalAlarmStatus";//异常报警状态
    public final static String CurrentWaterTemperatureKey = "CurrentWaterTemperatureKey";//当前水温
    public final static String CurrentWaterTemperatureLength = "CurrentWaterTemperatureLength";//当前水温
    public final static String CurrentWaterTemperatureValue = "CurrentWaterTemperatureValue";//当前水温

    public final static String DoorOpenState = "DoorOpenState";//开门状态
    public final static String LackSaltStatus = "LackSaltStatus";//缺盐状态
    public final static String DishWasherFanSwitch = "DishWasherFanSwitch";//风机开关
    public final static String LackRinseStatus = "LackRinseStatus";//缺漂洗剂状态
    public final static String DishWasherAlarm = "DishWasherAlarm";//洗碗机报警


    public final static String MSG_ID = "MSG_ID";//预约时间targetGuid
    public final static String TARGET_GUID = "target_guid";


   /* public final static short OFF=0;//关机
    public final static short WAIT=1;//待机
    public final static short WORKING=2;//工作中
    public final static short PAUSE=3;//暂停
    public final static short END=4;//结束*/

}

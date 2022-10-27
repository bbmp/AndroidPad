package com.robam.dishwasher.constant;

public class DishWasherConstant {
    public final static int MODE_SMART = 5;//智能洗
    public final static int MODE_POWFULL = 1;//强力洗
    public final static int MODE_QUICK = 4;//快速洗
    public final static int MODE_DAILY = 2;//日常洗
    public final static int MODE_SAVING = 3;//节能洗
    public final static int MODE_BRIGHT = 6;//晶亮洗
    public final static int MODE_BABYCARE = 14;// 护婴洗
    public final static int MODE_SELFCLEAN = 15;//自清洁
    public final static int MODE_FLUSH = 9;//护婴净存/自动换气

    //附加模式
    public final static int AUX_NONE = 0;//无
    public final static int AUX_PAN_POWFULL = 1;//锅具强洗
    public final static int AUX_KILL_POWFULL = 6;//加强除菌
    public final static int AUX_FLUSH = 8;//长效净存
    public final static int AUX_DOWN_WASH = 3;//下层洗

    public final static int DEVICE_WARING_E0 = 0;//下层洗
    public final static int DEVICE_WARING_E1 = 1;//下层洗
    public final static int DEVICE_WARING_E3 = 2;//下层洗
    public final static int DEVICE_WARING_E4 = 4;//下层洗
    public final static int DEVICE_WARING_E5 = 5;//下层洗
    public final static int DEVICE_WARING_E6 = 6;//下层洗
    public final static int DEVICE_WARING_E7 = 7;//下层洗
    //public final static int DEVICE_WARING_E8 = 8;//下层洗
    public final static int DEVICE_WARING_E9 = 9;//下层洗
    public final static int DEVICE_WARING_E10 = 10;//下层洗


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

    public final static String RC = "RC";
    public final static String WARING_CODE = "RC";


   /* public final static short OFF=0;//关机
    public final static short WAIT=1;//待机
    public final static short WORKING=2;//工作中
    public final static short PAUSE=3;//暂停
    public final static short END=4;//结束*/

}

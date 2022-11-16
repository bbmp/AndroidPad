package com.robam.cabinet.constant;

public class
CabinetConstant {
    //主功能
    //消毒
    public final static int FUN_DISINFECT = 2;
    //快洁
    public final static int FUN_CLEAN = 3;
    //烘干
    public final static int FUN_DRY = 4;

    //净存
    public final static int FUN_FLUSH = 13;//待核对
    //智能
    public final static int FUN_SMART = 8;//待核对

    public final static int FUN_WARING = 6;//报警

    //预约
    //public final static int APPOINTMENT = 5;
    public final static int OFF = 0;
    public final static int ON = 1;

    //模式
    public final static int MODE_DISINFECT = 1;
    public final static int MODE_CLEAN = 2;
    public final static int MODE_DRY = 3;
    public final static int MODE_FLUSH = 4;
    public final static int MODE_SMART = 5;

    /**
     * 预约剩余时间
     */
    public final static String REMAINING_APPOINT_TIME = "remainingAppointTime";
    //public final static String SteriReminderTime = "SteriReminderTime";


    /**
     * 报警码[1Byte]
     */
    public final static String AlarmId = "AlarmId";

    public final static String EXTRA_MODE_BEAN = "modebean";

    /**
     * 消毒柜工作状态[1Byte]（0关机，1开机）
     */
    public final static String SteriStatus = "SteriStatus";

    /**
     * 设置消毒柜工作时间
     */
    public final static String SteriTime = "SteriTime";






    /**
     * 安全锁定
     */
    public final static String SteriSecurityLock = "SteriSecurityLock";


    /**
     * ORDER_TIME[1Byte] {0:预约取消，1，2，3，4…24预约时间}
     */
    public final static String SteriReserveTime = "SteriReserveTime";

    /**
     * DRYING_TIME[1Byte] {0取消烘干，>1 为烘干时间}
     */
    public final static String SteriDryingTime = "SteriDryingTime";
    /**
     * CLEAN_TIME[1Byte] {0取消保洁，60，保洁时间}
     */
    public final static String SteriCleanTime = "SteriCleanTime";
    /**
     * DISINFECT_TIME[1Byte] {0取消消毒,150消毒时间}
     */
    public final static String SteriDisinfectTime = "SteriDisinfectTime";
    /**
     * ON_OFF [1Byte] {0取消童锁，1，开童锁}
     */
    public final static String SteriLock = "SteriLock";
    /**
     * WORK_TIME_LEFT{0:无/剩余时间到，>剩余时间} 分钟字节
     */
    public final static String SteriWorkLeftTimeL = "SteriWorkLeftTimeL";
    /**
     * WORK_TIME_LEFT{0:无/剩余时间到，>剩余时间} 小时字节
     */
    public final static String SteriWorkLeftTimeH = "SteriWorkLeftTimeH";
    /**
     * DOORLOCK{0：门锁关，1 门锁关}
     */
    public final static String SteriDoorLock = "SteriDoorLock";
    /**
     * ALARM {0xff:无报警，
     * 0x00:门控报警，
     * 0x01: 紫外线灯管不工作或上层传感器不良
     * 0x02: 温度传感器不良}
     */
    public final static String SteriAlarmStatus = "SteriAlarmStatus";
    /**
     * 是否开启定时消毒[1BYTE]
     */
    public final static String SteriSwitchDisinfect = "SteriSwitchDisinfect";
    /**
     * 定时消毒间隔时间[1BYTE],单位天
     */
    public final static String SteriInternalDisinfect = "SteriInternalDisinfect";
    /**
     * 是否开启每周消毒[1BYTE]
     */
    public final static String SteriSwitchWeekDisinfect = "SteriSwitchWeekDisinfect";
    /**
     * 每周消毒的时时间[1BYTE]
     */
    public final static String SteriWeekInternalDisinfect = "SteriWeekInternalDisinfect";
    /**
     * 消毒柜峰谷电时间[1BYTE]
     */
    public final static String SteriPVDisinfectTime = "SteriPVDisinfectTime";

    /**
     * 暖碟温度
     */
    public final static String warmDishTempValue = "warmDishTempValue";
    public final static String warmDishKey = "warmDishKey";
    public final static String warmDishLength = "warmDishLength";
    /**
     * 消毒柜参数：
     * TEM[1Byte]  温度值
     * HUM [1Byte]  湿度值
     * GERM [1Byte] 细菌值
     * 臭氧[1Byte]
     */
    public final static String SteriParaTem = "SteriParaTem";
    public final static String SteriParaHum = "SteriParaHum";
    public final static String SteriParaGerm = "SteriParaGerm";
    public final static String SteriParaOzone = "SteriParaOzone";

    /**
     * 事件码[1Byte]
     */
    public final static String EventId = "EventId";


    /**
     * 事件参数 1Byte
     */
    public final static String EventParam = "EventParam";

    /**
     * 用户编码[10Byte]
     */
    public final static String UserId = "UserId";

    public final static String ArgumentNumber = "ArgumentNumber";//参数个数

    public final static String Key = "KEY";

    public final static String Length = "LENGTH";
    public final static String RC = "RC";

    public final static String MSG_ID = "MSG_ID";//预约时间targetGuid
    public final static String TARGET_GUID = "target_guid";

    /**
     * • 控制端类型[1Byte]，参考编码表
     */
    public final static String TerminalType = "TerminalType";

}

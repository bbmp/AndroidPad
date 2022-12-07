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

    public final static int OFF = 0;
    public final static int ON = 1;


    /**
     * 预约剩余时间
     */
    public final static String REMAINING_APPOINT_TIME = "remaining_appoint_time";
    /**
     * 工作设定时间
     */
    public final static String WORK_SETTING_TIME = "work_setting_time";

    /**
     * 工作设定时间
     */
    public final static String SMART_CRUISING = "smart_cruising";//智能巡航
    public final static String SMART_CRUISING_KEY = "smart_cruising_key";//智能巡航
    public final static String SMART_CRUISING_LEN = "smart_cruising_len";//智能巡航


    /**
     * 报警码[1Byte]
     */
    public final static String CABINET_ALARM_ID = "cabinet_alarm_id";


    /**
     * 消毒柜工作状态[1Byte]（0关机，1开机）
     */
    public final static String CABINET_STATUS = "cabinet_Status";

    /**
     * 设置消毒柜工作时间
     */
    public final static String CABINET_TIME = "cabinet_time";


    /**
     * 安全锁定
     */
    public final static String CABINET_SAFE_LOCK = "cabinet_lock";


    /**
     * ORDER_TIME[1Byte] {0:预约取消，1，2，3，4…24预约时间}
     */
    public final static String CABINET_APPOINT_TIME = "cabinet_appoint_time";

    /**
     * ON_OFF [1Byte] {0取消童锁，1，开童锁}
     */
    public final static String CABINET_LOCK = "cabinet_lock";
    /**
     * WORK_TIME_LEFT{0:无/剩余时间到，>剩余时间} 分钟字节
     */
    public final static String REMAINING_WORK_TIME = "remaining_work_time";

    /**
     * DOORLOCK{0：门锁关，1 门锁关}
     */
    public final static String CABINET_DOOR = "cabinet_door";
    /**
     * ALARM {0xff:无报警，
     * 0x00:门控报警，
     * 0x01: 紫外线灯管不工作或上层传感器不良
     * 0x02: 温度传感器不良}
     */
    public final static String CABINET_ALARM_STATUS = "cabinet_alarm_status";
    /**
     * 是否开启定时消毒[1BYTE]
     */
    public final static String CABINET_SWITCH_DISINFECH = "cabinet_switch_disinfech";
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

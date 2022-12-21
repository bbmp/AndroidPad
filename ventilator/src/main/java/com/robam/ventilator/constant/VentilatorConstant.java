package com.robam.ventilator.constant;

public class VentilatorConstant {
    //主功能
    public final static int FUN = 1;
    public final static int PERSONAL_CENTER = 1;
    public final static int DATE_SETTING = 2;
    public final static int WIFI_CONNECT = 3;
    public final static int RESET= 4;
    public final static int SCREEN_BRIGHTNESS = 5;
    public final static int SALE_SERVICE = 6;
    public final static int ABOUT_PRODUCT = 7;
    public final static int SMART_SETTING = 8;
    public final static int SIMPLE_MODE = 9;

    public final static int LOW_WIND = 5; //低风阻
    public final static int MID_WIND = 8; //高风阻

    public final static String EXTRA_FIRST = "ven";
    public final static String EXTRA_WIFI_SSID = "ssid";
    public final static String EXTRA_MODEL = "model";// 设备类型
    public final static String EXTRA_CLASSNAME = "classname";//当前activity classname
    public final static String EXTRA_WARNING_CODE = "warning_code";

    //告警信息
    public final static int DEVICE_WARING_E0 = 0;
    public final static int DEVICE_WARING_E1 = 1;
    public final static int DEVICE_WARING_E2 = 2;
    public final static int DEVICE_WARING_E3 = 3;
    public final static int DEVICE_WARING_E4 = 4;
    public final static int DEVICE_WARING_E5 = 5;
    public final static int DEVICE_WARING_E6 = 6;

    /**
     * 工作状态[1Byte]（0关机，1开机）
     */
    public final static String FanStatus = "FanStatus";
    public final static String FanGear = "fan_gear";//烟机挡位
    public final static String DelayTime = "delay_time";//定时关机时间
    public final static String PRecipe1 = "p_recipe1";//无人锅p档菜谱1
    public final static String PRecipe2 = "p_recipe2";//无人锅p档菜谱2
    public final static String PRecipe3 = "p_recipe3";//无人锅p档菜谱3
    public final static String PRecipe4 = "p_recipe4";//无人锅p档菜谱4
    public final static String PRecipe5 = "p_recipe5";//无人锅p档菜谱5
    public final static String BleType = "ble_type";//蓝牙品类


    //开关机
    public final static int FAN_SHUTDOWN = 0;
    public final static int FAN_POWERON = 1;
    public final static int FAN_LOCK = 4; //清洗锁定状态

    //挡位
    public final static int FAN_GEAR_CLOSE = 0; //关档
    public final static int FAN_GEAR_WEAK = 1;
    public final static int FAN_GEAR_MID = 3;
    public final static int FAN_GEAR_FRY = 6; //爆炒
    //灯开关
    public final static int FAN_LIGHT_CLOSE = 0;
    public final static int FAN_LIGHT_OPEN = 1;
    //智感恒吸
    public final static int FAN_SMART_CLOSE = 0;
    public final static int FAN_SMART_OPEN = 1;

    //mqtt消息字段
    public final static String SUB_DEVICES = "sub_devices";
    public final static String DEVICE_GUID = "device_guid";
    public final static String DEVICE_BIZ = "device_biz"; //业务编吗
    public final static String DEVICE_STATUS = "device_status";
    public final static String KEYS = "keys";
    public final static String FAN_STEAM = "fan_steam";//烟蒸烤联动开关
    public final static String FAN_STEAM_GEAR = "fan_steam_gear";//烟蒸烤自动匹配风量
    //livedata
    //智能设置
    public final static String SMART_SET = "smartset";
    //油网清洗自动提示
    public final static String OIL_CLEAN = "oilclean";
    public final static String DT = "dt"; //设备类型
    public final static String VENTILATOR_USER = "ventilator_user";//更新烟机用户
}

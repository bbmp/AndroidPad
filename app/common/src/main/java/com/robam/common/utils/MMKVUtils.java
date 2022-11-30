package com.robam.common.utils;

import com.tencent.mmkv.MMKV;

import java.util.Set;

public class MMKVUtils {
    private static final String SMART_SET = "智感恒吸设置";
    private static final String MMKV_NAME = "STEAM_OVEN";
    private static final String LOGIN = "login";
    private static final String USER_INFO = "user_info";
    private static final String INIT_DATA = "init_data";
    private static final String SUBDEVICE_INFO = "subdevice_info";
    private static final String FAN_OFFTIME = "fan_offtime";//烟机风机最后运行时间
    private static final String FAN_RUNTIME = "fan_runtime"; //风机运行时间
    private static final String HOLIDAY = "holiday";//假日模式
    private static final String HOLIDAY_DAY = "HOLIDAY_DAY";//假日模式天数
    private static final String HOLIDAY_WEEK_TIME = "holiday_week_time";//假日模式每周固定时间
    private static final String OIL_CLEAN = "oil_clean";//油网清洗提醒功能
    private static final String DELAY_SHUTDOWN = "delay_shutdown";//延时关机
    private static final String DELAY_SHUTDOWN_TIME = "delay_shutdown_time";//延时关机时间
    private static final String FAN_STOVE = "fan_stove";//烟灶联动
    private static final String FAN_PAN = "fan_pan";//烟锅联动
    private static final String FAN_STEAM = "fan_steam";//烟蒸烤联动
    private static final String FAN_STOVE_GEAR = "fan_stove_gear";//烟灶联动匹配风量
    private static final String FAN_PAN_GEAR = "fan_pan_gear";//烟锅联动匹配风量
    private static final String FAN_STEAM_GEAR = "fan_steam_gear";//烟蒸烤联动匹配风量
    private static final String FAN_RELATION_STEAM = "fan_relation_steam";//烟蒸烤关联设备

    /**
     * 获取是否登录账号
     *
     * @return
     */
    public static boolean isLogin() {
        MMKV mmkv = MMKV.defaultMMKV();
        return mmkv.decodeBool(LOGIN, false);
    }

    /**
     * 是否登录账号
     *
     * @param login
     */
    public static void login(boolean login) {
        MMKV mmkv = MMKV.defaultMMKV();
        mmkv.encode(LOGIN, login);
    }

    /**
     * 获取用户信息
     *
     * @return
     */
    public static String getUser() {
        MMKV mmkv = MMKV.defaultMMKV();
        return mmkv.decodeString(USER_INFO);
    }

    //假日模式
    public static void setHoliday(boolean holiday) {
        MMKV mmkv = MMKV.defaultMMKV();
        mmkv.encode(HOLIDAY, holiday);
    }
    //假日模式，默认打开
    public static boolean getHoliday() {
        MMKV mmkv = MMKV.defaultMMKV();
        return mmkv.decodeBool(HOLIDAY, true);
    }

    //假日模式天数 默认7天
    public static String getHolidayDay() {
        MMKV mmkv = MMKV.defaultMMKV();
        return mmkv.decodeString(HOLIDAY_DAY,"7");
    }

    public static void setHolidayDay(String holiday) {
        MMKV mmkv = MMKV.defaultMMKV();
        mmkv.encode(HOLIDAY_DAY, holiday);
    }

    //假日模式每周固定时间 默认 周日13:00
    public static String getHolidayWeekTime() {
        MMKV mmkv = MMKV.defaultMMKV();
        return mmkv.decodeString(HOLIDAY_WEEK_TIME,"周日13:00");
    }

    public static void setHolidayWeekTime(String holiday) {
        MMKV mmkv = MMKV.defaultMMKV();
        mmkv.encode(HOLIDAY_WEEK_TIME, holiday);
    }

    //延时关机时间 默认1分钟
    public static String getDelayShutdownTime() {
        MMKV mmkv = MMKV.defaultMMKV();
        return mmkv.decodeString(DELAY_SHUTDOWN_TIME,"1");
    }

    public static void setDelayShutdownTime(String minute) {
        MMKV mmkv = MMKV.defaultMMKV();
        mmkv.encode(DELAY_SHUTDOWN_TIME, minute);
    }

    //油网清洗
    public static void setOilClean(boolean oilClean) {
        MMKV mmkv = MMKV.defaultMMKV();
        mmkv.encode(OIL_CLEAN, oilClean);
    }
    //油网清洗，默认打开
    public static boolean getOilClean() {
        MMKV mmkv = MMKV.defaultMMKV();
        return mmkv.decodeBool(OIL_CLEAN, true);
    }

    /**
     * 设置用户信息
     *
     * @param user
     */
    public static void setUser(String user) {
        MMKV mmkv = MMKV.defaultMMKV();
        mmkv.encode(USER_INFO, user);
    }

    //保存子设备
    public static void setSubDevice(Set<String> subDevice) {
        MMKV mmkv = MMKV.defaultMMKV();
        mmkv.encode(SUBDEVICE_INFO, subDevice);
    }

    public static Set<String> getSubDevice() {
        MMKV mmkv = MMKV.defaultMMKV();
        return mmkv.decodeStringSet(SUBDEVICE_INFO);
    }

    /**
     * initData
     *
     * @param initData
     */
    public static void initData(boolean initData) {
        MMKV mmkv = MMKV.defaultMMKV();
        mmkv.encode(INIT_DATA, initData);
    }

    /**
     * 获取是否初始化数据
     */
    public static boolean isInitData() {
        MMKV mmkv = MMKV.defaultMMKV();
        return mmkv.decodeBool(INIT_DATA, false);
    }
    //设置烟机风机最后运行时间
    public static void setFanOffTime(long time) {
        MMKV mmkv = MMKV.defaultMMKV();
        mmkv.encode(FAN_OFFTIME, time);
    }
    //获取风机最后运行时间
    public static long getFanOffTime() {
        MMKV mmkv = MMKV.defaultMMKV();
        long offTime = mmkv.decodeLong(FAN_OFFTIME, 0);
        if (offTime == 0) //首次
            mmkv.encode(FAN_OFFTIME, System.currentTimeMillis());
        return mmkv.decodeLong(FAN_OFFTIME);
    }

    //设置风机运行时间
    public static void setFanRuntime(long runTime) {
        MMKV mmkv = MMKV.defaultMMKV();
        mmkv.encode(FAN_RUNTIME, runTime);
    }
    //获取风机运行时间
    public static long getFanRuntime() {
        MMKV mmkv = MMKV.defaultMMKV();
        return mmkv.decodeLong(FAN_RUNTIME, 0);
    }

    //设置延时关机时间
    public static void setDelayShutdown(boolean delayTime) {
        MMKV mmkv = MMKV.defaultMMKV();
        mmkv.encode(DELAY_SHUTDOWN, delayTime);
    }

    //获取延时关机,默认开
    public static boolean getDelayShutdown() {
        MMKV mmkv = MMKV.defaultMMKV();
        return mmkv.decodeBool(DELAY_SHUTDOWN, true);
    }

    //设置烟灶联动
    public static void setFanStove(boolean fanStove) {
        MMKV mmkv = MMKV.defaultMMKV();
        mmkv.encode(FAN_STOVE, fanStove);
        if (!fanStove) //烟灶联动关闭，自动匹配风量关闭
            mmkv.encode(FAN_STOVE_GEAR, false);
    }
    //设置烟灶联动自动匹配风量
    public static void setFanStoveGear(boolean onOff) {
        MMKV mmkv = MMKV.defaultMMKV();
        mmkv.encode(FAN_STOVE_GEAR, onOff);
    }

    //获取烟灶联动
    public static boolean getFanStove() {
        MMKV mmkv = MMKV.defaultMMKV();
        return mmkv.decodeBool(FAN_STOVE, true);
    }
    //烟灶联动匹配风量
    public static boolean getFanStoveGear() {
        MMKV mmkv = MMKV.defaultMMKV();
        return mmkv.decodeBool(FAN_STOVE_GEAR);
    }

    //烟锅联动
//    public static void setFanPan(boolean fanPan) {
//        MMKV mmkv = MMKV.defaultMMKV();
//        mmkv.encode(FAN_PAN, fanPan);
//        if (!fanPan)
//            mmkv.encode(FAN_PAN_GEAR, false);
//    }

    //烟锅联动匹配风量
//    public static void setFanPanGear(boolean onOff) {
//        MMKV mmkv = MMKV.defaultMMKV();
//        mmkv.encode(FAN_PAN_GEAR, onOff);
//    }
    //获取烟锅联动
//    public static boolean getFanPan() {
//        MMKV mmkv = MMKV.defaultMMKV();
//        return mmkv.decodeBool(FAN_PAN, true);
//    }
    //烟锅联动匹配风量
//    public static boolean getFanPanGear() {
//        MMKV mmkv = MMKV.defaultMMKV();
//        return mmkv.decodeBool(FAN_PAN_GEAR);
//    }

    //烟蒸烤联动
    public static void setFanSteam(boolean fanSteam) {
        MMKV mmkv = MMKV.defaultMMKV();
        mmkv.encode(FAN_STEAM, fanSteam);
        if (!fanSteam)
            mmkv.encode(FAN_STEAM_GEAR, false);
    }
    //烟蒸烤联动匹配风量
    public static void setFanSteamGear(boolean onOff) {
        MMKV mmkv = MMKV.defaultMMKV();
        mmkv.encode(FAN_STEAM_GEAR, onOff);
    }
    //获取烟蒸烤联动
    public static boolean getFanSteam() {
        MMKV mmkv = MMKV.defaultMMKV();
        return mmkv.decodeBool(FAN_STEAM, true);
    }
    //烟蒸烤联动匹配风量
    public static boolean getFanSteamGear() {
        MMKV mmkv = MMKV.defaultMMKV();
        return mmkv.decodeBool(FAN_STEAM_GEAR);
    }
    //设置烟蒸烤关联设备
    public static void setFanSteamDevice(String guid) {
        MMKV mmkv = MMKV.defaultMMKV();
        mmkv.encode(FAN_RELATION_STEAM, guid);
    }
    //获取烟蒸烤关联设备
    public static String getFanSteamDevice() {
        MMKV mmkv = MMKV.defaultMMKV();
        return mmkv.decodeString(FAN_RELATION_STEAM, "");
    }
    //恢复初始，智能设置部分
    public static void resetSmartSet() {
        MMKV mmkv = MMKV.defaultMMKV();

        mmkv.clearAll();
    }
    //智感恒吸设置
    public static void setSmartSet(boolean status) {
        MMKV mmkv = MMKV.defaultMMKV();
        mmkv.encode(SMART_SET, status);
    }
    //获取智感恒吸状态
    public static boolean getSmartSet() {
        MMKV mmkv = MMKV.defaultMMKV();
        return mmkv.decodeBool(SMART_SET, false);
    }
    //wifi密码
    public static void setWifi(String ssid, String pwd) {
        MMKV mmkv = MMKV.defaultMMKV();
        mmkv.encode(ssid, pwd);
    }
    //获取密码
    public static String getWifiPwd(String ssid) {
        MMKV mmkv = MMKV.defaultMMKV();
        return mmkv.decodeString(ssid, "");
    }
}

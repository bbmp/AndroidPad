package com.robam.common.utils;

import com.tencent.mmkv.MMKV;

import java.util.Set;

public class MMKVUtils {
    public static String MMKV_NAME = "STEAM_OVEN";
    public static String LOGIN = "login";
    public static String USER_INFO = "user_info";
    public static String INIT_DATA = "init_data";
    public static String SUBDEVICE_INFO = "subdevice_info";
    public static String AUTO_AIR = "auto_air";
    public static String FAN_RUNTIME = "fan_runtime"; //风机运行时间
    public static String HOLIDAY = "holiday";//假日模式
    public static String HOLIDAY_DAY = "HOLIDAY_DAY";//假日模式天数
    public static String HOLIDAY_WEEK_TIME = "holiday_week_time";//假日模式每周固定时间
    public static String OIL_CLEAN = "oil_clean";//油网清洗提醒功能
    public static String DELAY_SHUTDOWN = "delay_shutdown";//延时关机
    public static String DELAY_SHUTDOWN_TIME = "delay_shutdown_time";//延时关机时间
    public static String FAN_STOVE = "fan_stove";//烟灶联动
    public static String FAN_PAN = "fan_pan";//烟锅联动
    public static String FAN_STEAM = "fan_steam";//烟蒸烤联动

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

    public static void setDelayShutdownTime(String holiday) {
        MMKV mmkv = MMKV.defaultMMKV();
        mmkv.encode(DELAY_SHUTDOWN_TIME, holiday);
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

    //自动换气
    public static void setAutoAir(boolean autoAir) {
        MMKV mmkv = MMKV.defaultMMKV();
        mmkv.encode(AUTO_AIR, autoAir);
    }

    public static boolean getAutoAir() {
        MMKV mmkv = MMKV.defaultMMKV();
        return mmkv.decodeBool(AUTO_AIR, false);
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

    //设置风机运行时间
    public static void setFanRuntime(int addTime) {
        MMKV mmkv = MMKV.defaultMMKV();
        int runTime = mmkv.decodeInt(FAN_RUNTIME, 0);
        mmkv.encode(FAN_RUNTIME, runTime + addTime);
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
    }

    //获取烟灶联动
    public static boolean getFanStove() {
        MMKV mmkv = MMKV.defaultMMKV();
        return mmkv.decodeBool(FAN_STOVE);
    }

    //烟锅联动
    public static void setFanPan(boolean fanStove) {
        MMKV mmkv = MMKV.defaultMMKV();
        mmkv.encode(FAN_PAN, fanStove);
    }

    public static boolean getFanPan() {
        MMKV mmkv = MMKV.defaultMMKV();
        return mmkv.decodeBool(FAN_PAN);
    }

    //烟蒸烤联动
    public static void setFanSteam(boolean fanStove) {
        MMKV mmkv = MMKV.defaultMMKV();
        mmkv.encode(FAN_STEAM, fanStove);
    }

    public static boolean getFanSteam() {
        MMKV mmkv = MMKV.defaultMMKV();
        return mmkv.decodeBool(FAN_STEAM);
    }
}

package com.robam.common.utils;

import com.tencent.mmkv.MMKV;

import java.util.Set;

public class MMKVUtils {
    public static String MMKV_NAME = "STEAM_OVEN" ;
    public static String LOGIN  = "login";
    public static String USER_INFO  = "user_info";
    public static String INIT_DATA  = "init_data";
    public static String SUBDEVICE_INFO = "subdevice_info";
    public static String OIL_CLEAN = "oil_clean";
    public static String AUTO_AIR = "auto_air";
    public static String FAN_RUNTIME = "fan_runtime"; //风机运行时间
    public static String DELAY_SHUTDOWN = "delay_shutdown";//延时关机时间
    public static String FAN_STOVE = "fan_stove";//烟灶联动
    public static String FAN_PAN = "fan_pan";//烟锅联动
    public static String FAN_STEAM = "fan_steam";//烟蒸烤联动

    /**
     * 获取是否登录账号
     * @return
     */
    public static boolean isLogin (){
        MMKV mmkv = MMKV.defaultMMKV();
        return mmkv.decodeBool(LOGIN , false) ;
    }

    /**
     * 是否登录账号
     * @param login
     */
    public static void login(boolean login){
        MMKV mmkv = MMKV.defaultMMKV();
        mmkv.encode(LOGIN, login);
    }

    /**
     * 获取用户信息
     * @return
     */
    public static String getUser(){
        MMKV mmkv = MMKV.defaultMMKV();
        return mmkv.decodeString(USER_INFO ) ;
    }
    //油网清洗
    public static void setOilClean(boolean oilClean) {
        MMKV mmkv = MMKV.defaultMMKV();
        mmkv.encode(OIL_CLEAN, oilClean);
    }

    public static boolean getOilClean() {
        MMKV mmkv = MMKV.defaultMMKV();
        return mmkv.decodeBool(OIL_CLEAN, false);
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
     * @param user
     */
    public static void setUser(String user){
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
     * @param initData
     */
    public static void initData(boolean initData){
        MMKV mmkv = MMKV.defaultMMKV();
        mmkv.encode(INIT_DATA, initData);
    }

    /**
     * 获取是否初始化数据
     */
    public static boolean isInitData(){
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
    public static void setDelayShutdown(int delayTime) {
        MMKV mmkv = MMKV.defaultMMKV();
        mmkv.encode(DELAY_SHUTDOWN, delayTime);
    }
    //获取延时关机时间
    public static int getDelayShutdown() {
        MMKV mmkv = MMKV.defaultMMKV();
        return mmkv.decodeInt(DELAY_SHUTDOWN);
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
}

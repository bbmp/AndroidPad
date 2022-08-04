package com.robam.common.utils;

import com.tencent.mmkv.MMKV;

public class MMKVUtils {
    public static String MMKV_NAME = "STEAM_OVEN" ;
    public static String LOGIN  = "login";
    public static String USER_INFO  = "user_info";
    public static String INIT_DATA  = "init_data";

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

    /**
     * 设置用户信息
     * @param user
     */
    public static void setUser(String user){
        MMKV mmkv = MMKV.defaultMMKV();
        mmkv.encode(USER_INFO, user);
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
}

package com.robam.steamoven.utils;

import com.tencent.mmkv.MMKV;

public class SteamDataUtil {

    public static final String KEY_STEAM = "key_steam";

    public static void saveSteam(String content){
        MMKV mmkv = MMKV.defaultMMKV();
        mmkv.putString(KEY_STEAM,content);
    }

    public static String getSteamContent(){
        return MMKV.defaultMMKV().getString(KEY_STEAM,null);
    }


}

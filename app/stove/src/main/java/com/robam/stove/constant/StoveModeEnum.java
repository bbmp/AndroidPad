package com.robam.stove.constant;

import com.robam.stove.bean.ModeBean;

import java.util.ArrayList;
import java.util.List;

public enum StoveModeEnum {
    MODE_STEW(StoveConstant.MODE_STEW,"炖煮", 5, 1, 10, 0, 0, 0),
    MODE_STEAM(StoveConstant.MODE_STEAM,"清蒸", 10,5, 15, 0, 0, 0),
    MODE_FRY(StoveConstant.MODE_FRY,"煎炸", 150, 0, 0, 150, 100, 200),
    MODE_TIMING(StoveConstant.MODE_TIMING,"定时", 20, 15, 25, 0, 0, 0),;

    //模式
    public int code;
    /**
     * 模式名
     */
    public String name;
    /**
     * 模式时间
     */
    public int defTime;
    /**
     * 最小时间
     */
    public int minTime;
    /**
     * 最大时间
     */
    public int maxTime;
    //默认温度
    public int defTemp;
    //最低温度
    public int minTemp;
    //最高温度
    public int maxTemp;

    StoveModeEnum(int code, String name, int defTime, int minTime, int maxTime, int defTemp, int minTemp, int maxTemp) {
        this.code = code;
        this.name = name;
        this.defTime = defTime;
        this.minTime = minTime;
        this.maxTime = maxTime;
        this.defTemp = defTemp;
        this.minTemp = minTemp;
        this.maxTemp = maxTemp;
    }

    /**
     * 将枚举值转化成list集合
     *
     * @return
     */
    public static List<ModeBean> getModeList(){
        List<ModeBean> list = new ArrayList<>();

        list.add(new ModeBean(MODE_STEW.code, MODE_STEW.name, MODE_STEW.defTime, MODE_STEW.minTime, MODE_STEW.maxTime, 0, 0, 0));
        list.add(new ModeBean(MODE_STEAM.code, MODE_STEAM.name, MODE_STEAM.defTime, MODE_STEAM.minTime, MODE_STEAM.maxTime, 0, 0, 0));
        list.add(new ModeBean(MODE_FRY.code, MODE_FRY.name,0, 0, 0, MODE_FRY.defTemp, MODE_FRY.minTemp, MODE_FRY.maxTemp));

        return list;
    }

}

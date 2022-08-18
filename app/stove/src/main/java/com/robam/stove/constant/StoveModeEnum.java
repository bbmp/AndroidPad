package com.robam.stove.constant;

import com.robam.stove.bean.ModeBean;

import java.util.ArrayList;
import java.util.List;

public enum StoveModeEnum {
    MODE_STEW(StoveConstant.MODE_STEW,"炖煮", 5, 1, 10),
    MODE_STEAM(StoveConstant.MODE_STEAM,"清蒸", 10,5, 15),
    MODE_FRY(StoveConstant.MODE_FRY,"煎炸", 15, 10, 20),
    MODE_TIMING(StoveConstant.MODE_TIMING,"定时", 20, 15, 25),;

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

    StoveModeEnum(int code, String name, int defTime, int minTime, int maxTime) {
        this.code = code;
        this.name = name;
        this.defTime = defTime;
        this.minTime = minTime;
        this.maxTime = maxTime;
    }

    /**
     * 将枚举值转化成list集合
     *
     * @return
     */
    public static List<ModeBean> getModeList(){
        List<ModeBean> list = new ArrayList<>();

        list.add(new ModeBean(MODE_STEW.code, MODE_STEW.name, MODE_STEW.defTime, MODE_STEW.minTime, MODE_STEW.maxTime));
        list.add(new ModeBean(MODE_STEAM.code, MODE_STEAM.name, MODE_STEAM.defTime, MODE_STEAM.minTime, MODE_STEAM.maxTime));
        list.add(new ModeBean(MODE_FRY.code, MODE_FRY.name, MODE_FRY.defTime, MODE_FRY.minTime, MODE_FRY.maxTime));

        return list;
    }

}

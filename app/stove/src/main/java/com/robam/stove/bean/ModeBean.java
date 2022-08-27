package com.robam.stove.bean;

public class ModeBean {
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
    //最小温度
    public int minTemp;
    //最大温度
    public int maxTemp;

    public ModeBean(int code, String name, int defTime, int minTime, int maxTime, int defTemp, int minTemp, int maxTemp) {
        this.code = code;
        this.name = name;
        this.defTime = defTime;
        this.minTime = minTime;
        this.maxTime = maxTime;
        this.defTemp = defTemp;
        this.minTemp = minTemp;
        this.maxTemp = maxTemp;
    }
}

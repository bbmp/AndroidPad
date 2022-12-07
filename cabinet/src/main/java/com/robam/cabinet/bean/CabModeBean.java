package com.robam.cabinet.bean;

import java.io.Serializable;

public class CabModeBean implements Serializable {
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
    /**
     * 时间间隔
     */
    public int stepTime;

    public CabModeBean(int code, String name, int defTime, int minTime, int maxTime, int stepTime) {
        this.code = code;
        this.name = name;
        this.defTime = defTime;
        this.minTime = minTime;
        this.maxTime = maxTime;
        this.stepTime = stepTime;
    }

    public CabModeBean newCab(){
        return new CabModeBean(this.code,this.name,this.defTime,this.minTime,this.maxTime,this.stepTime);
    }
}

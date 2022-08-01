package com.robam.steam.bean;

public class ModeBean  {
    public int funCode ;
    /**
     * 协议对应code
     */
    public int code;
    /**
     * 模式名
     */
    public String name;
    /**
     * action
     */
    public String into;
    /**
     * 默认温度
     */
    public int defTemp;
    /**
     * 模式时间
     */
    public int defTime;
    /**
     * 最小温度
     */
    public int minTemp;
    /**
     * 最大温度
     */
    public int maxTemp;
    /**
     * 最小时间
     */
    public int minTime;
    /**
     * 最大时间
     */
    public int maxTime;

    /**
     * 是否可以开门工作
     */
    public int openDoorWork ;
    /**
     * 是否需要水箱
     */
    public int needWater ;
    /**
     * 是否可以加蒸汽 0：不可以 1：可以
     */
    public int addSteam ;

    /**
     * 默认温度index
     * @return
     */
    public int getDefTempIndex(){
        return defTemp - minTemp ;
    }

    /**
     * 默认时间index
     * @return
     */
    public int getDefTimeIndex(){
        return defTime - minTime ;
    }
}

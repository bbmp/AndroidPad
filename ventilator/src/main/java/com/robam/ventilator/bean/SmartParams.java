package com.robam.ventilator.bean;

public class SmartParams {


    /**
     * 烟机电磁灶开关联动(0关，1开)[1Byte]
     */
    public boolean IsPowerLinkage = true;

    /**
     * 烟机档位联动开关（0关，1开）[1Byte]
     */
    public boolean IsLevelLinkage = true;

    /**
     * 电磁灶关机后烟机延时关机开关（0关，1开）[1Byte]
     */
    public boolean IsShutdownLinkage = true;


    /**
     * 电磁灶关机后烟机延时关机时间（延时时间，单位分钟，1~5分钟）[1Byte]
     */
    public short ShutdownDelay = 1;


    /**
     * 油烟机清洗提示开关[1Byte], 0不提示，1提示
     */
    public boolean IsNoticClean = true;

    /**
     * 是否开启定时通风[1BYTE]
     */
    public boolean IsTimingVentilation = false;
    /**
     * 定时通风间隔时间[1BYTE],单位天
     */
    public short TimingVentilationPeriod = 3;

    /**
     * 是否开启每周通风[1BYTE]
     */
    public boolean IsWeeklyVentilation = false;

    /**
     * 每周通风的时间--周几
     */
    public short WeeklyVentilationDate_Week = 1;

    /**
     * 每周通风的时间--小时
     */
    public short WeeklyVentilationDate_Hour = 12;

    /**
     * 每周通风的时间--分钟
     */
    public short WeeklyVentilationDate_Minute = 30;
    public short R8230S_Switch = 0;//0 _ 是关 1 _ 是开
    public short R8230S_Time = 3;
    /**
     * •	倒油杯提示功能开关
     */
    public short FanCupOilSwitch = 0;//0 _ 是关 1 _ 是开
    /**
     * •	智能烟感开关
     */
    public short FanReducePower = 0;//0 _ 是关 1 _ 是开
    /**
     * 3D手势开关
     */
    public short gestureRecognitionSwitch = 1;//0 _ 是关 1 _ 是开
    /**
     * •	防干烧提示开关 0不提示 1提示
     */
    public short dryBurningPromptSwitch = 0;

    /**
     * •	防干烧开关
     */
    public short dryBurningSwitch = 0;//0 _ 是关 1 _ 是开

    @Override
    public String toString() {
        return "SmartParams{" +
                "IsPowerLinkage=" + IsPowerLinkage +
                ", IsLevelLinkage=" + IsLevelLinkage +
                ", IsShutdownLinkage=" + IsShutdownLinkage +
                ", ShutdownDelay=" + ShutdownDelay +
                ", IsNoticClean=" + IsNoticClean +
                ", IsTimingVentilation=" + IsTimingVentilation +
                ", TimingVentilationPeriod=" + TimingVentilationPeriod +
                ", IsWeeklyVentilation=" + IsWeeklyVentilation +
                ", WeeklyVentilationDate_Week=" + WeeklyVentilationDate_Week +
                ", WeeklyVentilationDate_Hour=" + WeeklyVentilationDate_Hour +
                ", WeeklyVentilationDate_Minute=" + WeeklyVentilationDate_Minute +
                ", R8230S_Switch=" + R8230S_Switch +
                ", R8230S_Time=" + R8230S_Time +
                ", FanCupOilSwitch=" + FanCupOilSwitch +
                ", FanReducePower=" + FanReducePower +
                ", gestureRecognitionSwitch=" + gestureRecognitionSwitch +
                '}';
    }
}

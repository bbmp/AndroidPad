package com.robam.ventilator.bean;

import android.os.PowerManager;

public class Ventilator {
    public short PowerLevel_0 = 0;
    public short PowerLevel_1 = 1;
    public short PowerLevel_2 = 2;
    public short PowerLevel_3 = 3;
    public short PowerLevel_6 = 6;

    public short Event_Power = 10;
    public short Event_TimingCompleted = 11;
    public short Event_Level = 12;
    public short Event_Light = 13;
    public short Event_CleanNotic = 14;
    public short Event_CleanLock = 15;
    public short Event_PlateRemove = 16;
    public short Event_OilCup = 20;
    public short Event_TimingRemind = 22;//定时提醒结束事件
    public short Event_3D_gesture = 23;//3D手势事件
    public SmartParams smartParams = new SmartParams();
    public short status;
    public short prestatus;
    public short level;
    public short prelevel;
    public short timeLevel;
    public short timeWork;
    public boolean light;
    public boolean clean;
    public short wifi = -1;
    public short argument;
    public short backsmoke = 255;//回烟
    public short checkFan = 0;//止回阀
    public short waitTime = 0;//等待时间
    public short gasCheck = 0;//空气质量检测
    public short isNeedCupOil = 0;//是否需要倒油杯
    public short fanFeelStatus = 0;//智能烟感状态
    public short temperatureReportOne;
    public short temperatureReportTwo;
    public short braiseAlarm;
    public short regularVentilationRemainingTime;
    public short fanStoveLinkageVentilationRemainingTime;
    public short periodicallyRemindTheRemainingTime;
    public short presTurnOffRemainingTime;
    public short leftStoveBraiseAlarm;
    public short rightStoveBraiseAlarm;
}

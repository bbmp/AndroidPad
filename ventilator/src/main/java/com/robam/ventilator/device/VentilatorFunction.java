package com.robam.ventilator.device;

public interface VentilatorFunction {
    void shutDown();

    void powerOn();

    void beep(); //单蜂鸣声

    void powerOnGear(int gear); //开机并设置挡位

    void openOilClean();

    void closeOilClean();

    void setFanStatus(int status); //设置烟机工作状态

    void setFanGear(int gear); //设置烟机挡位

    void setFanLight(int light); //设置灯开关

    void setFanAll(int gear, int light); //设置整体参数

    void setSmart(int smart);// 设置智感恒吸

    void queryAttribute(); //查询烟机状态

    void setColorLamp();//冷暖光切换
}

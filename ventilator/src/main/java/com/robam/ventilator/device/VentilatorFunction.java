package com.robam.ventilator.device;

public interface VentilatorFunction {
    void shutDown();

    void powerOn();

    void setFanStatus(int status); //设置烟机工作状态

    void setFanGear(int gear); //设置烟机挡位

    void setFanLight(int light); //设置灯开关

    void setFanAll(int gear, int light); //设置整体参数

    void setSmart(int smart);// 设置智感恒吸

    void queryAttribute(); //查询烟机状态
}

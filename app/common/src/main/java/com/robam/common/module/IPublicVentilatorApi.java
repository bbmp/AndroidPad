package com.robam.common.module;

import android.content.Context;

import com.robam.common.mqtt.IProtocol;

public interface IPublicVentilatorApi extends IPublicApi {
    String VENTILATOR_PUBLIC = "com.robam.ventilator.device.VentilatorFactory";
    String VENTILATOR_HOME = "com.robam.ventilator.ui.activity.HomeActivity";
    //设置烟机挡位，对外接口
    void setFanGear(int gear);
    //获取烟机挡位
    int getFanGear();
    //设置灯
    void setFanLight(int lightOn);
    //获取烟机灯状态
    int getFanLight();
    //调用网络匹配
    void startMatchNetwork(Context context, String model);
    //调用登录
    void startLogin(Context context);
    //查询烟机状态
    void queryAttribute();
    //是否开机
    boolean isStartUp();
    //关机
    void shutDown();
    //关闭定时任务
    void closeService(Context context);
    //开机
    void powerOn();
    //蜂鸣声
    void beep();
    //启动定时任务
    void startService(Context context);
    //冷暖光切换
    void setColorLamp();
    //灶具挡位变化
    void stoveLevelChanged(String stoveGuid, int leftLevel, int rightLevel);
    //延时关机
    void delayShutDown();
    //关闭延时关机
    void closeDelayDialog();
    //智能设置更新
    void setSmartSet();
}

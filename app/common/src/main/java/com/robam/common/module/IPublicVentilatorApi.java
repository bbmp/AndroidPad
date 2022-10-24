package com.robam.common.module;

import android.content.Context;

import com.robam.common.mqtt.IProtocol;

public interface IPublicVentilatorApi extends IPublicApi {
    String VENTILATOR_PUBLIC = "com.robam.ventilator.device.VentilatorFactory";
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
    //
    boolean isScreenOn(Context context);
    //关机
    void shutDown();
}

package com.robam.common.device;

import android.content.Context;

/**
 * 厂商方法接口
 */
public interface IPlat {
    /**
     * 初始化
     * @param context
     */
    void init(Context context );
    /**
     * 门控灯 开
     */
    void openDoorLamp();
    /**
     * 门控灯 关
     */
    void closeDoorLamp();
    /**
     * 水箱电机灯 开
     */
    void openWaterLamp();
    /**
     * 水箱电机灯 关
     */
    void closeWaterLamp();
    /**
     * 电源灯 开
     */
    void openPowerLamp();
    /**
     * 电源灯 关
     */
    void closePowerLamp();

    /**
     * 获取设备型号
     * @return
     */
    String getDt() ;

    /**
     * 获取唯一标识
     * @return
     */
    String getDeviceOnlySign() ;

    /**
     * 获取mac地址
     * @return
     */
    String getMac();
    /**
     * 固件版本
     */
    String getFirmwareVersion();

    /**
     * 关闭状态栏
     */
    default void closeStatusBar(){

    }

    /**
     * 关闭导航栏
     */
    default void closeNavBar(){

    }

    /**
     * 唤醒屏幕
     */
    default void screenOn(){

    }
    /**
     * 关闭屏幕
     */
    default void screenOff(){

    }
}

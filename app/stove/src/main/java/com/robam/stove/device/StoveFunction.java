package com.robam.stove.device;

import com.robam.common.bean.Device;
import com.robam.common.mqtt.MqttMsg;
import com.robam.stove.bean.Stove;

//灶具功能，灶属于子设备
public interface StoveFunction {
    void shutDown();

    void powerOn();
    //设置灶具童锁
    void setLock(byte status);
    //查询灶具状态
    void queryAttribute(String targetGuid);
    //设置灶具状态
    void setAttribute(byte stoveId, byte isCook, byte workStatus);
    //设置灶具功率
    void setLevel(String targetGuid, byte stoveId, byte isCook, byte level);
    //设置定时关火
    void setTiming(byte stoveId, short timingTime);
    //灶具菜谱设置
    void setRecipe(String targetGuid, byte stoveId);
}

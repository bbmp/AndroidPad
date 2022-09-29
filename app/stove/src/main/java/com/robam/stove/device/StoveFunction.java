package com.robam.stove.device;

import com.robam.common.bean.Device;
import com.robam.common.mqtt.MqttMsg;
import com.robam.stove.bean.Stove;

//灶具功能，灶属于子设备
public interface StoveFunction {
    void shutDown();

    void powerOn();
    //查询灶具状态
    void queryAttribute(Stove stove);
    //设置灶具状态
    void setAttribute(Stove stove);
    //设置灶具功率
    void setLevel(Stove stove);
    //设置定时关火
    void setTiming(Stove stove);
    //灶具菜谱设置
    void setRecipe(Stove stove);
}

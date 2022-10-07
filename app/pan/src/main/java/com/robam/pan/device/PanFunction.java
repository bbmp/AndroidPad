package com.robam.pan.device;

//添加功能
public interface PanFunction {
    void shutDown();

    void powerOn();
    //查询锅状态
    void queryAttribute(String targetGuid);
}

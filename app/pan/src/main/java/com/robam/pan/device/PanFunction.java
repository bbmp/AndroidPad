package com.robam.pan.device;

import com.robam.pan.bean.CurveStep;

import java.util.List;

//添加功能
public interface PanFunction {
    void shutDown();

    void powerOn();
    //查询锅状态
    void queryAttribute(String targetGuid);
    //设置曲线步骤参数
    void setCurveStepParams(String targetGuid, int stoveId, List<CurveStep> curveSteps);
    //设置电机模式
    void setFryMode(String targetGuid, int mode);
}

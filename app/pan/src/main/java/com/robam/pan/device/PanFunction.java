package com.robam.pan.device;

import com.robam.pan.bean.CurveStep;

import java.util.List;
import java.util.Map;

//添加功能
public interface PanFunction {
    void shutDown();

    void powerOn();
    //查询锅状态
    void queryAttribute(String targetGuid);
    //设置曲线步骤参数
    void setCurveStepParams(String targetGuid, int stoveId, List<CurveStep> curveSteps);
    //设置智能锅互动参数
    void setInteractionParams(String targetGuid, Map params);
}

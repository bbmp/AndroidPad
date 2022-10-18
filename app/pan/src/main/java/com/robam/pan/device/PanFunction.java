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
    //设置曲线还原，P档菜谱锅参数
    void setCurvePanParams(String targetGuid, String smartPanCurveParams);
    //设置曲线还原，P档菜谱灶参数
    void setCurveStoveParams(String targetGuid, int stoveId, String curveStageParams);
    //设置智能锅互动参数
    void setInteractionParams(String targetGuid, Map params);
}

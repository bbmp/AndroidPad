package com.robam.stove.device;

import com.robam.stove.bean.CurveStep;

import java.util.List;

//灶具功能，灶属于子设备
public interface StoveFunction {
    void shutDown();

    void powerOn();
    //设置灶具童锁
    void setLock(String targetGuid, int status);
    //查询灶具状态
    void queryAttribute(String targetGuid);
    //设置灶具状态
    void setAttribute(String targetGuid, int stoveId, int isCook, int workStatus);
    //设置灶具功率
    void setLevel(String targetGuid, int stoveId, int isCook, int level, int recipeId, int step);
    //设置定时关火
    void setTiming(String targetGuid, int stoveId, int timingTime);
    //灶具菜谱设置
    void setRecipe(String targetGuid, int stoveId);
    //设置曲线步骤参数
    void setCurveStepParams(String targetGuid, int stoveId, List<CurveStep> curveSteps);
    //锅上报转发给灶
    void setStoveParams(int cmd, byte[] payload);
}

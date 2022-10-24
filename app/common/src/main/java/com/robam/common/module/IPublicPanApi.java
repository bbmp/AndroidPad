package com.robam.common.module;

import android.content.Context;


import java.util.List;
import java.util.Map;

//锅对外接口
public interface IPublicPanApi extends IPublicApi {
    String PAN_HOME = "com.robam.pan.ui.activity.MainActivity";
    String PAN_PUBLIC = "com.robam.pan.device.PanFactory";
    //设置无人锅智能互动参数
    void setInteractionParams(String targetGuid, Map params);
    //设置无人锅参数
    void setCurvePanParams(String targetGuid, long recipeId, String smartPanCurveParams);
    //设置灶参数
    void setCurveStoveParams(String targetGuid, long recipeId, int stoveId, String curveStageParams, String curveTempParams);

    void lowBatteryHint(Context context);  //低电量提示
}

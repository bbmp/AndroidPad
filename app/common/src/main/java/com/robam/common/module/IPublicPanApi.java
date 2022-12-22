package com.robam.common.module;

import android.content.Context;


import java.util.List;
import java.util.Map;

//锅对外接口
public interface IPublicPanApi extends IPublicApi {
    String PAN_HOME = "com.robam.pan.ui.activity.MainActivity";
    String PAN_PUBLIC = "com.robam.pan.device.PanFactory";
    String PAN_WARNING = "com.robam.pan.ui.activity.WarningActivity";
    //查询锅状态
    void queryAttribute(String targetGuid);
    //设置无人锅智能互动参数
    void setInteractionParams(String targetGuid, Map params);
    //设置无人锅参数
    void setCurvePanParams(String targetGuid, long recipeId, String smartPanCurveParams);
    //设置灶参数
    void setCurveStoveParams(String targetGuid, long recipeId, int stoveId, String curveStageParams, String curveTempParams);
    //低电量提示
    void lowBatteryHint(Context context);
    //烟锅联动开关查询
    void queryFanPan();
    //烟锅联动开关设置
    void setFanPan(int onOff);
    //断开蓝牙
    void disConnectBle(String targetGuid);
}

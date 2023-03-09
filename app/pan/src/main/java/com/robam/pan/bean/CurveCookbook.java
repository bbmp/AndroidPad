package com.robam.pan.bean;

import java.util.List;

public class CurveCookbook {
    //曲线id
    public long curveCookbookId;
    //电机参数
    public String smartPanModeCurveParams;
    //温度参数
    public String temperatureCurveParams;

    public List<CurveStep> curveStepList;
}

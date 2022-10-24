package com.robam.pan.bean;

import java.io.Serializable;
import java.util.List;

//我的最爱P档菜谱
public class PPanRecipe implements Serializable {
    public String deviceGuid;
    //菜谱id
    public long cookbookId;
    //菜谱名字
    public String cookbookName;
    //图片
    public String cookbookImgCover;
    //曲线id
    public long curveCookbookId;

    public boolean isSent;
    //曲线名
//    public String curveCookbookName;
//    //温度参数
//    public String temperatureCurveParams;
//    //点机参数
//    public String curveStageParams;
//
//    public List<CurveStep> curveStepDtoList;
}

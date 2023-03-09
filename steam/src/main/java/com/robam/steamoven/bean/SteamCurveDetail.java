package com.robam.steamoven.bean;

import java.io.Serializable;
import java.util.List;

public class SteamCurveDetail implements Serializable {
//    //曲线id
//    public long curveCookbookId;
//    //曲线名称
//    public String name;
//    //图片
//    public String imageCover;
//    //温度
//    public String temperatureCurveParams;
//    //
//    public String needTime;
//    //步骤
//    public List<CurveStep> stepList;
//    //设备参数
//    public String deviceParams;
//    //是否选中,用于显示
//    private boolean selected;
//
//    public String  userId;
//
//    public String deviceGuid;
//
//    public String curveSettingParams;
//
//    public long gmtCreate;

    public String  userId;
    public String needTime;
    public long gmtCreate;

    public String deviceGuid;
    public Long cookbookId;
    public String cookbookName;
    public String cookbookImgCover;
    public long curveCookbookId;
    public String curveCookbookName;
    public String curveCookbookImgCover;
    public Boolean isSent;
    public Integer orderNo;
    public String temperatureCurveParams;
    public String smartPanModeCurveParams;
    public String curveStageParams;
    public List<CurveData.CurveStepListDTO> curveStepList;
    //设备参数
    public String deviceParams;

    public String curveSettingParams;

    public String name;

    private boolean selected;

    //    //图片
    public String imageCover;


    /** todo lei 还没有字段
     * 是否下架 false下架 true上架
     */
    public boolean grounding;

    public SteamCurveDetail(String name) {
        this.name = name;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }
}

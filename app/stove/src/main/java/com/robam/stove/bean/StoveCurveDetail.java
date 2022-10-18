package com.robam.stove.bean;


import java.io.Serializable;
import java.util.List;

//曲线详情
public class StoveCurveDetail implements Serializable {
    //曲线id
    public long curveCookbookId;
    //曲线名称
    public String name;
    //图片
    public String imageCover;
    //温度
    public String temperatureCurveParams;
    //电机
    public String curveStageParams;
    //
    public int needTime;
    //步骤
    public List<CurveStep> stepList;
    //设备参数
    public String deviceParams;
    //是否选中,用于显示
    private boolean selected;

    public StoveCurveDetail(String name) {
        this.name = name;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

}

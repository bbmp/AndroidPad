package com.robam.pan.bean;

import java.io.Serializable;
import java.util.List;

public class PanCurveDetail implements Serializable {
    //曲线id
    public long curveCookbookId;
    //曲线名称
    public String name;
    //图片
    public String imageCover;
    //温度
    public String temperatureCurveParams;
    //灶具控制方式
    public String curveStageParams;
    //无人锅搅拌参数
    public String smartPanModeCurveParams;
    //
    public int needTime;
    //步骤
    public List<CurveStep> stepList;
    //设备参数
    public String deviceParams;
    //是否选中,用于显示
    private boolean selected;

    public PanCurveDetail(String name) {
        this.name = name;
    }
    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }
}

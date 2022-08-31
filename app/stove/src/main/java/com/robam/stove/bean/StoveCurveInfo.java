package com.robam.stove.bean;

import java.util.List;

public class StoveCurveInfo {
    //曲线id
    public long curveCookbookId;
    //曲线名称
    public String name;
    //图片
    public String imageCover;
    //温度
    public String temperatureCurveParams;
    //
    public int needTime;
    //步骤
    public List<CurveStep> stepList;
    //是否选中,用于显示
    private boolean selected;

    public StoveCurveInfo(String name) {
        this.name = name;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }
}

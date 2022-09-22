package com.robam.steamoven.bean;

import java.io.Serializable;
import java.util.List;

public class SteamCurveDetail implements Serializable {
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
    //设备参数
    public String deviceParams;
    //是否选中,用于显示
    private boolean selected;

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

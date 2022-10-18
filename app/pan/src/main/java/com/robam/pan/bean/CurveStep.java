package com.robam.pan.bean;

import java.io.Serializable;

//曲线步骤
public class CurveStep implements Serializable {
    public int no;
    //步骤名
    public String markName;
    //步骤描述
    public String description;
    //标记时间
    public String markTime;
    //温度
    public float markTemp;
    //图片
    public String imageUrl;
    //步骤时间
    public int needTime;
    //已经过去的时间 用于显示
    public int elapsedTime;
    //搅拌参数
    public int stirMode;
    //灶具挡位
    public int level;

    public CurveStep(int no, String markTime, float markTemp) {
        this.no = no;
        this.markTime = markTime;
        this.markTemp = markTemp;
    }
}

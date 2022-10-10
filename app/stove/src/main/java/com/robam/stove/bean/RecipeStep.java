package com.robam.stove.bean;

import java.io.Serializable;
import java.util.List;

public class RecipeStep implements Serializable {
    /**
     * 步骤描述
     */
    public String desc;

    /**
     * 步骤时间
     */
    public int needTime;

    /**
     * 已过时间
     */
    public int elapsedTime;

    //步骤图片
    public String image;
    /**
     * 步骤
     */
    public int no;
    //步骤参数
    public List<StepParams> params;

    //设备
    private String dc;

    public String getDesc() {
        return desc;
    }

    public int getNo() {
        return no;
    }
}

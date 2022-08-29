package com.robam.pan.bean;

import java.io.Serializable;

public class RecipeStep implements Serializable {
    /**
     * 步骤描述
     */
    public String desc;

    /**
     * 步骤时间
     */
    public int needTime;

    //步骤图片
    public String image;
    /**
     * 步骤
     */
    private int no;
    //设备
    private String dc;
    //隐藏步骤图片，用于显示
    public boolean hideImage;

    public String getDesc() {
        return desc;
    }

    public int getNo() {
        return no;
    }

}

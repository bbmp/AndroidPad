package com.robam.stove.bean;

public class RecipeStep {
    /**
     * 步骤描述
     */
    public String desc;

    /**
     * 步骤时间
     */
    public String needTime;

    //步骤图片
    public String image;
    /**
     * 步骤
     */
    private int no;
    //设备
    private String dc;

    public String getDesc() {
        return desc;
    }

    public int getNo() {
        return no;
    }
}

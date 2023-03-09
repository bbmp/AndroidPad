package com.robam.stove.bean;

import java.io.Serializable;
import java.util.List;

public class RecipeStep implements Serializable {
    /**
     * 步骤描述
     */
    public String description;

    /**
     * 步骤时间
     */
    public int needTime;
    //烟机挡位
    public int fanGear;
    //灶具挡位
    public int stoveGear;
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

    //设备分类
    public List<String> deviceCategoryStrSet;
    //设备平台
    public List<String> devicePlatformStrList;

    public String getDesc() {
        return description;
    }

    public int getNo() {
        return no;
    }
}

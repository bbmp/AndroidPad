package com.robam.roki.bean;

import java.util.List;

public class Recipe3rd extends AbsRecipe{
    public String imgUrl;

    public String detailUrl;

    /**
     * 小图
     */
    public String imgSmall;

    /**
     * 中图
     */
    public String imgMedium;

    /**
     * 获取菜谱用到的设备品类
     */
    public List<Dc> dcs;

    public List<Dc> getDcs() {

        return dcs;
    }

    public List<CookBookTagGroup> cookbookTagGroups;
}

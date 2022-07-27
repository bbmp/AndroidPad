package com.robam.roki.bean;

import com.chad.library.adapter.base.entity.MultiItemEntity;

import java.util.List;

public class Recipe extends AbsRecipe implements MultiItemEntity {
    public static final int TEXT = 1;
    public static final int IMG = 2;
    private int itemType;

    @Override
    public int getItemType() {
        return itemType;
    }

    private String cookbookType;
    /**
     * 菜谱描述
     */
    private String introduction;

    /**
     * 所需时间 （秒）
     */
    private int needTime;

    /**
     * 难度系数
     */
    public int difficulty;

    /**
     * 小图
     */
    public String imgSmall;
    /**
     * 中图
     */
    public String imgMedium;
    /**
     * 大图
     */
    public String imgLarge;
    /**
     * 海报图
     */
    public String imgPoster;

    public int sourceType;

    public String providerImage;

    public String stampLogo;
    //收藏菜谱
    public boolean collected;

    public String video;

    public String showType ;
    /**
     * 获取菜谱用到的设备品类
     */
    protected List<Dc> dcs;

    public List<CookbookPlatforms> cookbookPlatforms;

    protected List<CookBookTagGroup> cookbookTagGroups;

    public Materials materials;

    public PreStep prepareSteps;

    public List<CookStep> steps;

    public List<Categories> categories;

    public List<Dc> getDcs() {
        return dcs;
    }

    public void setItemType(int itemType) {
        this.itemType = itemType;
    }

    public Recipe(int itemType) {
        this.itemType = itemType;
    }
}

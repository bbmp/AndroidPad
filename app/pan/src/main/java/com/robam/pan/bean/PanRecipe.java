package com.robam.pan.bean;

public class PanRecipe {
    //菜谱id
    public long id;
    //图片
    private String imgSmall;
    //菜谱名称
    private String name;

    public PanRecipe(String name, String imgUrl) {
        this.name = name;
        this.imgSmall = imgUrl;
    }

    public String getImgUrl() {
        return imgSmall;
    }

    public String getName() {
        return name;
    }

}

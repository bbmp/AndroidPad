package com.robam.stove.bean;

public class StoveRecipe {
    //图片
    private String imgUrl;
    //菜谱名称
    private String name;

    public StoveRecipe(String name, String imgUrl) {
        this.name = name;
        this.imgUrl = imgUrl;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public String getName() {
        return name;
    }
}
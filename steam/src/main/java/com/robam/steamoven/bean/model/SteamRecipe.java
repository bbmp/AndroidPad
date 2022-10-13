package com.robam.steamoven.bean.model;

public class SteamRecipe {
    //图片
    private String imgUrl;
    //菜谱名称
    private String name;
    public long id;

    public SteamRecipe(long id,String name, String imgUrl) {
        this.id = id;
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

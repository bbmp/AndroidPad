package com.robam.stove.bean;

public class StoveRecipe {
    //菜谱id
    public long id;
    //图片
    private String imgSmall;
    //菜谱名称
    private String name;


    public StoveRecipe(String name, String imgUrl) {
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

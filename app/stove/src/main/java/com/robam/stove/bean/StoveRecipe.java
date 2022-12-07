package com.robam.stove.bean;

import java.util.List;

public class StoveRecipe {
    //菜谱id
    public long id;
    //图片
    private String imgSmall;
    //菜谱名称
    private String name;
    //设备类型
    public List<DCS> dcs;


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

    public class DCS {
        public String dc;
    }
}

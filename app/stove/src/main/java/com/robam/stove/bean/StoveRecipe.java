package com.robam.stove.bean;

import java.util.List;

public class StoveRecipe {
    //菜谱id
    public long id;
    //菜谱名称
    public String name;
    //图片
    public String imgCover11;
    //设备类型
    public List<DCS> deviceCategoryList;

    public StoveRecipe(String name, String imgCover11) {
        this.name = name;
        this.imgCover11 = imgCover11;
    }

    public class DCS {
        public String cookbookId;

        public String categoryCode;

        public String categoryName;
    }
}

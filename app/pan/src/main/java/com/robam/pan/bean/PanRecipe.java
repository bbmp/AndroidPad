package com.robam.pan.bean;

import java.util.List;

public class PanRecipe {
    //菜谱id
    public long id;
    //菜谱名称
    public String name;
    //图片
    public String imgCover11;
    //设备类型
    public List<DCS> deviceCategoryList;
    //曲线id
    public long curveId;
    //我的最爱序号
    public int orderNo;

    public PanRecipe(String name, String imgCover11) {
        this.name = name;
        this.imgCover11 = imgCover11;
    }

    public PanRecipe(int no, long id, String imgCover11, String name, long curveId) {
        this.orderNo = no;
        this.id = id;
        this.imgCover11 = imgCover11;
        this.name = name;
        this.curveId = curveId;
    }

    public class DCS {
        public String cookbookId;

        public String categoryCode;

        public String categoryName;
    }
}

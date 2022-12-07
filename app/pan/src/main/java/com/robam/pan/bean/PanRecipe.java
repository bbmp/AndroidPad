package com.robam.pan.bean;

import java.util.List;

public class PanRecipe {
    //菜谱id
    public long id = 15312;
    //图片
    private String imgSmall;
    //菜谱名称
    private String name;
    //设备类型
    public List<DCS> dcs;
    //曲线id
    public long curveId;
    //我的最爱序号
    public int orderNo;


    public PanRecipe(String name, String imgUrl) {
        this.name = name;
        this.imgSmall = imgUrl;
    }

    public PanRecipe(int no, long id, String imgSmall, String name, long curveId) {
        this.orderNo = no;
        this.id = id;
        this.imgSmall = imgSmall;
        this.name = name;
        this.curveId = curveId;
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

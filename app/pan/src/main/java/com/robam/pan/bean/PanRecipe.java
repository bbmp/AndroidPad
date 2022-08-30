package com.robam.pan.bean;

public class PanRecipe {
    //菜谱id
    public long id;
    //图片
    private String imgSmall;
    //菜谱名称
    private String name;

    //是否选中,用于显示
    private boolean selected;

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
    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }
}

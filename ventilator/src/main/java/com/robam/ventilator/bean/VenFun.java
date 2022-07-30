package com.robam.ventilator.bean;

//功能
public class VenFun {
    private String title;
    private String imgUrl;

    public VenFun(String title, String imgUrl) {
        this.title = title;
        this.imgUrl = imgUrl;
    }

    public String getTitle() {
        return title;
    }

    public String getImgUrl() {
        return imgUrl;
    }
}

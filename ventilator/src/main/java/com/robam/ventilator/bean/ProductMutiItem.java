package com.robam.ventilator.bean;

import com.chad.library.adapter.base.entity.MultiItemEntity;

public class ProductMutiItem implements MultiItemEntity {
    private int itemType;
    /**
     * 图片
     */
    public final static int IMAGE = 1;

    public final static int BUTTON = 2;

    public final static int DEVICE = 3;

    private String imgurl;

    private Device device;

    @Override
    public int getItemType() {
        return itemType;
    }

    public ProductMutiItem(int itemType, String imgurl) {
        this.itemType = itemType;
        this.imgurl = imgurl;
    }

    public ProductMutiItem(int itemType, Device device) {
        this.itemType = itemType;
        this.device = device;
    }
}

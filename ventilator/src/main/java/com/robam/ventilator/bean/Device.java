package com.robam.ventilator.bean;

public class Device {
    /**
     * 拥有者编码
     */
    public long ownerId;
    /**
     * 设备组编码
     */
    public long groupId;
    /**
     * MAC地址
     */
    public String mac;
    /**
     * 唯一编码
     */
    public String guid;
    /**
     * 业务编码（供应商定制ID）
     */
    public String bid;
    /**
     * 设备名称
     */
    public String dc;

    /**
     * 设备平台
     */
    public String dp;

    /**
     * 设备类型
     */
    public String dt;

    /**
     * 展示的设备类型名称
     */
    public String displayType;


    public String categoryName;


    public String categoryEnglishName;

    public String categoryIconUrl;

    public String deviceTypeIconUrl;
    //在线状态
    private int status;

    //工作状态
    private int workStatus;

    public Device() {
    }

    public Device(String name, String displayType) {
        this.categoryName = name;
        this.displayType = displayType;
    }

    public int getStatus() {
        return status;
    }

    public String getCategoryName() {
        return categoryName;
    }


    public int getWorkStatus() {
        return workStatus;
    }

    public String getDisplayType() {
        return displayType;
    }
}

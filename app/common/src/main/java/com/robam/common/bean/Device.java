package com.robam.common.bean;

import androidx.lifecycle.LiveData;

import java.util.List;

public class Device {
    public final static int OFFLINE = 0;
    public final static int ONLINE  = 1;
    //在线状态
    public int status = 1;

    //工作状态
    public int workStatus = 1;

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
    //
    public String categoryName;


    public String categoryEnglishName;

    public String categoryIconUrl;

    public String deviceTypeIconUrl;

    //子设备
    public List<Device> subDevices;



    public Device() {
    }

    public Device(String name, String displayType) {
        this.categoryName = name;
        this.displayType = displayType;
    }


    public String getDisplayType() {
        return displayType;
    }

   public String getCategoryName() {
        return categoryName;
   }

    public int getStatus() {
        return status;
    }

    public int getWorkStatus() {
        return workStatus;
    }
}

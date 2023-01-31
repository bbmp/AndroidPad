package com.robam.ventilator.request;

import com.google.gson.GsonBuilder;

public class AppTypeReq {
    //app类型
    public String appType;
    //设备类型
    public String deviceType;
    //升级方式
    public String upgradeMode;

    public AppTypeReq(String appType, String deviceType, String upgradeMode) {
        this.appType = appType;
        this.deviceType = deviceType;
        this.upgradeMode = upgradeMode;
    }
    @Override
    public String toString() {
        return new GsonBuilder().create().toJson(this, AppTypeReq.class);
    }
}

package com.robam.ventilator.request;

import com.google.gson.GsonBuilder;

public class AppTypeReq {
    //app类型
    public String appType;
    //设备类型
    public String deviceType;

    public AppTypeReq(String appType, String deviceType) {
        this.appType = appType;
        this.deviceType = deviceType;
    }
    @Override
    public String toString() {
        return new GsonBuilder().create().toJson(this, AppTypeReq.class);
    }
}

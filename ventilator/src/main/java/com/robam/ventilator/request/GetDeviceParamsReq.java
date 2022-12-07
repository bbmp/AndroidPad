package com.robam.ventilator.request;

import com.google.gson.Gson;

public class GetDeviceParamsReq extends GetUserReq{
    public String deviceType;
    public String deviceCategory;

    public GetDeviceParamsReq(long userId, String deviceType, String deviceCategory) {
        super(userId);
        this.deviceType = deviceType;
        this.deviceCategory = deviceCategory;
    }

    @Override
    public String toString() {
        return new Gson().toJson(this, GetDeviceParamsReq.class);
    }
}

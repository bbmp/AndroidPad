package com.robam.stove.request;

import com.google.gson.Gson;

import java.util.List;

public class GetRecipesByDeviceReq {
    public long userId;
    public String deviceCategory;
    public int pageIndex;
    public int pageSize;
    public String devicePlat;
    public List excludeCookIds;

    public GetRecipesByDeviceReq(long userId, String deviceCategory, int pageIndex, int pageSize, String devicePlat, List excludeCookIds) {
        this.userId = userId;
        this.deviceCategory = deviceCategory;
        this.pageIndex = pageIndex;
        this.pageSize = pageSize;
        this.devicePlat = devicePlat;
        this.excludeCookIds = excludeCookIds;
    }

    @Override
    public String toString() {
        return new Gson().toJson(this, GetRecipesByDeviceReq.class);
    }
}

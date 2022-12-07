package com.robam.roki.request;

import com.google.gson.GsonBuilder;

public class GroundingRecipesByDcReq {
    public long userId;
    public String dc;
    public int start;
    public int limit;
    public String cookbookType;
    public String lang;
    //2020年6月4日 新增传改设备平台（由后台配置文件决定）
    public String devicePlat;

    public GroundingRecipesByDcReq(long userId, String dc, String cookbookType, int start, int limit, String devicePlat) {
        this.userId = userId;
        this.dc = dc;
        this.start = start;
        this.limit = limit;
        this.cookbookType = cookbookType;
        this.devicePlat = devicePlat;
    }

    @Override
    public String toString() {
        return new GsonBuilder().create().toJson(this, GroundingRecipesByDcReq.class);
    }
}

package com.robam.stove.request;

import com.google.gson.Gson;

public class GetRecipesByDeviceReq {
    public long userId;
    public String dc;
    public int start;
    public int limit;
    public String cookbookType;
    public String devicePlat;

    public GetRecipesByDeviceReq(long userid, String dc, int start, int limit, String cookbookType, String devicePlat) {
        this.userId = userid;
        this.dc = dc;
        this.start = start;
        this.limit = limit;
        this.cookbookType = cookbookType;
        this.devicePlat = devicePlat;
    }

    @Override
    public String toString() {
        return new Gson().toJson(this, GetRecipesByDeviceReq.class);
    }
}

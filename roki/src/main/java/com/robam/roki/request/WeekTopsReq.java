package com.robam.roki.request;

import com.google.gson.GsonBuilder;

public class WeekTopsReq {
    public String weekTime;

    public int pageNo;

    public int pageSize;

    public WeekTopsReq(String weekTime, int pageNo, int pageSize) {
        this.weekTime = weekTime;
        this.pageNo = pageNo;
        this.pageSize = pageSize;
    }

    @Override
    public String toString() {
        return new GsonBuilder().create().toJson(this, WeekTopsReq.class);
    }
}

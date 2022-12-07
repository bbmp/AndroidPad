package com.robam.steamoven.request;

import com.google.gson.GsonBuilder;

public class GetUserReq {
    /** 用户id */
    private long userId;

    public GetUserReq(long userId) {
        this.userId = userId;
    }

    @Override
    public String toString() {
        return new GsonBuilder().create().toJson(this, GetUserReq.class);
    }
}

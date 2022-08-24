package com.robam.ventilator.request;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class GetDeviceUserReq extends GetUserReq{
    /** guid */
    private String guid;

    public GetDeviceUserReq(long userId, String guid) {
        super(userId);
        this.guid = guid;
    }

    @Override
    public String toString() {
        return new Gson().toJson(this, GetDeviceUserReq.class);
    }
}

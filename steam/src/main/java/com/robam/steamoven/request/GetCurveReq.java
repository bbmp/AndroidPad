package com.robam.steamoven.request;

import com.google.gson.Gson;

//曲线详情
public class GetCurveReq {
    //曲线id
    public String deviceGuid;

    public GetCurveReq(String deviceGuid) {
        this.deviceGuid = deviceGuid;
    }

    @Override
    public String toString() {
        return new Gson().toJson(this, GetCurveReq.class);
    }
}

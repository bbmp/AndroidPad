package com.robam.stove.request;

import com.google.gson.Gson;

//曲线详情
public class GetCurveDetailReq {
    //曲线id
    public long id;
    //设备id
    public String deviceGuid;

    public GetCurveDetailReq(long id) {
        this.id = id;
    }

    public GetCurveDetailReq(String deviceGuid) {
        this.deviceGuid = deviceGuid;
    }

    @Override
    public String toString() {
        return new Gson().toJson(this, GetCurveDetailReq.class);
    }
}

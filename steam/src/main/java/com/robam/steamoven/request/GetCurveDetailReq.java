package com.robam.steamoven.request;

import com.google.gson.Gson;

//曲线详情
public class GetCurveDetailReq {
    //曲线id
    public long id;

    public GetCurveDetailReq(long id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return new Gson().toJson(this, GetCurveDetailReq.class);
    }
}

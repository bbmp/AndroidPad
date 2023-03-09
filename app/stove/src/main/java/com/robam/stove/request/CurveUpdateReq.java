package com.robam.stove.request;

import com.google.gson.Gson;

public class CurveUpdateReq {
    private long id;
    private int state;

    public CurveUpdateReq(long id, int state) {
        this.id = id;
        this.state = state;
    }

    @Override
    public String toString() {
        return new Gson().toJson(this, CurveUpdateReq.class);
    }
}

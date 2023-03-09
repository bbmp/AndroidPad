package com.robam.pan.request;

import com.google.gson.Gson;

public class CreateCurveStartReq {
    private int id;
    private String deviceGuid;
    private int headId;

    public String mode;
    public String setTemp;
    public String setTime;

    public CreateCurveStartReq(String guid, int stoveId) {
        this.deviceGuid = guid;
        this.id = 0;
        this.headId = stoveId;
        this.mode = "";
        this.setTemp = "";
        this.setTime = "";
    }

    @Override
    public String toString() {
        return new Gson().toJson(this, CreateCurveStartReq.class);
    }
}

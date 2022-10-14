package com.robam.stove.request;

import com.google.gson.Gson;
import com.robam.stove.bean.CurveStep;

import java.util.List;

public class CurveSaveReq extends GetUserReq{

    private String deviceGuid;
    private String name;
    private List<CurveStep> stepList;

    public CurveSaveReq(long userId, String guid, String name, List<CurveStep> stepList) {
        super(userId);
        this.deviceGuid = guid;
        this.name = name;
        this.stepList = stepList;
    }

    @Override
    public String toString() {
        return new Gson().toJson(this, CurveSaveReq.class);
    }
}

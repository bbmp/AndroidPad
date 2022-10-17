package com.robam.pan.request;

import com.google.gson.Gson;
import com.robam.pan.bean.CurveStep;

import java.util.List;

public class CurveSaveReq extends GetUserReq{

    private long curveCookbookId;
    public int needTime;
    private String deviceGuid;
    private String name;
    private List<CurveStep> stepList;

    public CurveSaveReq(long userId, long curveId, String guid, String name, int needTime, List<CurveStep> stepList) {
        super(userId);
        this.curveCookbookId = curveId;
        this.deviceGuid = guid;
        this.name = name;
        this.stepList = stepList;
        this.needTime = needTime;
    }

    @Override
    public String toString() {
        return new Gson().toJson(this, CurveSaveReq.class);
    }
}

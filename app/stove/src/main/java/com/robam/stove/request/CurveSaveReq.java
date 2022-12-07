package com.robam.stove.request;

import com.google.gson.Gson;
import com.robam.stove.bean.CurveStep;

import java.util.List;

public class CurveSaveReq extends GetUserReq{

    private long curveCookbookId;
    public int needTime;
    private String deviceGuid;
    private String name;
    private List<CurveStep> stepList;
    private String curveStageParams; //控制方式

    public CurveSaveReq(long userId, long curveId, String guid, String name, int needTime, List<CurveStep> stepList, String curveStage) {
        super(userId);
        this.curveCookbookId = curveId;
        this.deviceGuid = guid;
        this.name = name;
        this.stepList = stepList;
        this.needTime = needTime;
        this.curveStageParams = curveStage;
    }

    @Override
    public String toString() {
        return new Gson().toJson(this, CurveSaveReq.class);
    }
}

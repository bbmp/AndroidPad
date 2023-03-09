package com.robam.steamoven.request;

import com.google.gson.Gson;
import com.robam.steamoven.bean.CurveData;
import java.util.List;


public class CurveSaveReq extends GetUserReq{

    private long curveCookbookId;
    public int needTime;
    private String deviceGuid;
    private String name;
    private List<CurveData.CurveStepListDTO> curveStepList;
    private String curveStageParams; //控制方式
    private String deviceParams;
    private String curveSettingParams;


    public CurveSaveReq(long userId, CurveData curveData) {
        super(userId);
        this.curveCookbookId = curveData.curveCookbookId;
        this.name = curveData.name;
        this.needTime = curveData.needTime;
        this.curveStepList = curveData.curveStepList;
        this.deviceParams = curveData.deviceParams;
        this.deviceGuid = curveData.deviceGuid;
        this.curveSettingParams = curveData.curveSettingParams;
    }


    @Override
    public String toString() {
        return new Gson().toJson(this, CurveSaveReq.class);
    }
}

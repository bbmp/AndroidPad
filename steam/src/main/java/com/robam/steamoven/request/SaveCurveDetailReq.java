package com.robam.steamoven.request;

import com.google.gson.Gson;
import com.robam.steamoven.bean.CurveData;
import com.robam.steamoven.bean.CurveStep;
import com.robam.steamoven.bean.SteamCurveDetail;

import java.util.List;

//曲线详情
public class SaveCurveDetailReq {
    //曲线id
    //曲线id
    public long curveCookbookId;
    //曲线名称
    public String name;
    //
    public String needTime;
    //步骤
    public List<CurveData.CurveStepListDTO> curveStepList;
    //设备参数
    public String deviceParams;
    //是否选中,用于显示
    private boolean selected;

    public String  userId;

    public String deviceGuid;

    public String curveSettingParams;

    public long gmtCreate;

    public SaveCurveDetailReq(SteamCurveDetail payload) {
        this.curveCookbookId = payload.curveCookbookId;
        this.name = payload.name;
        this.needTime = payload.needTime;
        this.curveStepList = payload.curveStepList;
        this.deviceParams = payload.deviceParams;
        this.userId = payload.userId;
        this.deviceGuid = payload.deviceGuid;
        this.curveSettingParams = payload.curveSettingParams;
        this.gmtCreate = payload.gmtCreate;
        this.selected = payload.isSelected();
    }

    @Override
    public String toString() {
        return new Gson().toJson(this, SaveCurveDetailReq.class);
    }
}

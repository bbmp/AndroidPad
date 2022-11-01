package com.robam.stove.request;

import com.google.gson.Gson;
import com.robam.stove.bean.CurveStep;

import java.util.List;

public class CookingCurveMarkStepReq {
    public long id;

    public List<CurveStep> stepDtoList;

    public CookingCurveMarkStepReq(long id, List<CurveStep> stepDtoList) {
        this.id = id;
        this.stepDtoList = stepDtoList;
    }

    @Override
    public String toString() {
        return new Gson().toJson(this, CookingCurveMarkStepReq.class);
    }
}

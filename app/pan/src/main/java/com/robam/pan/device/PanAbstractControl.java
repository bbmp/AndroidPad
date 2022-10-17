package com.robam.pan.device;

import com.robam.pan.bean.CurveStep;

import java.util.List;
import java.util.Map;

public class PanAbstractControl implements PanFunction{
    private PanFunction function;

    private static PanAbstractControl instance = new PanAbstractControl();

    public static PanAbstractControl getInstance() {
        return instance;
    }

    public void init(PanFunction panFunction) {
        this.function = panFunction;
    }

    @Override
    public void shutDown() {
        function.shutDown();
    }

    @Override
    public void powerOn() {
        function.powerOn();
    }

    @Override
    public void queryAttribute(String targetGuid) {
        function.queryAttribute(targetGuid);
    }

    @Override
    public void setCurvePanParams(String targetGuid, String smartPanCurveParams) {
        function.setCurvePanParams(targetGuid, smartPanCurveParams);
    }

    @Override
    public void setCurveStoveParams(String targetGuid, int stoveId, String curveStageParams) {
        function.setCurveStoveParams(targetGuid, stoveId, curveStageParams);
    }

    @Override
    public void setInteractionParams(String targetGuid, Map params) {
        function.setInteractionParams(targetGuid, params);
    }
}

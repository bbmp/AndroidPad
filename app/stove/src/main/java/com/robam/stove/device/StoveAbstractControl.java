package com.robam.stove.device;

import com.robam.common.bean.Device;
import com.robam.common.mqtt.MqttMsg;
import com.robam.stove.bean.CurveStep;
import com.robam.stove.bean.Stove;

import java.util.List;

public class StoveAbstractControl implements StoveFunction{
    private StoveFunction function;

    private static StoveAbstractControl instance = new StoveAbstractControl();

    public static StoveAbstractControl getInstance() {
        return instance;
    }

    public void init(StoveFunction stoveFunction) {
        this.function = stoveFunction;
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
    public void setLock(String targetGuid, byte status) {
        function.setLock(targetGuid, status);
    }

    @Override
    public void queryAttribute(String targetGuid) {
        function.queryAttribute(targetGuid);
    }

    @Override
    public void setAttribute(String targetGuid, byte stoveId, byte isCook, byte workStatus) {
        function.setAttribute(targetGuid, stoveId, isCook, workStatus);
    }

    @Override
    public void setLevel(String targetGuid, byte stoveId, byte isCook, byte level) {
        function.setLevel(targetGuid, stoveId, isCook, level);
    }

    @Override
    public void setTiming(String targetGuid, byte stoveId, short timingTime) {
        function.setTiming(targetGuid, stoveId, timingTime);
    }

    @Override
    public void setRecipe(String targetGuid, byte stoveId) {
        function.setRecipe(targetGuid, stoveId);
    }

    @Override
    public void setCurveStepParams(String targetGuid, int stoveId, List<CurveStep> curveSteps) {
        function.setCurveStepParams(targetGuid, stoveId, curveSteps);
    }

}

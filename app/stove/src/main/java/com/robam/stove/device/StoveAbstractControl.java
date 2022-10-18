package com.robam.stove.device;


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
    public void setLock(String targetGuid, int status) {
        function.setLock(targetGuid, status);
    }

    @Override
    public void queryAttribute(String targetGuid) {
        function.queryAttribute(targetGuid);
    }

    @Override
    public void setAttribute(String targetGuid, int stoveId, int isCook, int workStatus) {
        function.setAttribute(targetGuid, stoveId, isCook, workStatus);
    }

    @Override
    public void setLevel(String targetGuid, int stoveId, int isCook, int level, int recipeId, int step) {
        function.setLevel(targetGuid, stoveId, isCook, level, recipeId, step);
    }

    @Override
    public void setTiming(String targetGuid, int stoveId, int timingTime) {
        function.setTiming(targetGuid, stoveId, timingTime);
    }

    @Override
    public void setRecipe(String targetGuid, int stoveId) {
        function.setRecipe(targetGuid, stoveId);
    }

    @Override
    public void setStoveParams(int cmd, byte[] payload) {
        function.setStoveParams(cmd, payload);
    }

}

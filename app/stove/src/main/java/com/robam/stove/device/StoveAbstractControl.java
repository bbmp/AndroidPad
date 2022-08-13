package com.robam.stove.device;

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
}

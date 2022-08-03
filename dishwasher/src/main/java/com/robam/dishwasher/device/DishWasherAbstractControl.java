package com.robam.dishwasher.device;

//控制协议调用
public class DishWasherAbstractControl implements DishWasherFunction{
    private DishWasherFunction function;

    private static DishWasherAbstractControl instance = new DishWasherAbstractControl();

    public static DishWasherAbstractControl getInstance() {
        return instance;
    }

    public void init(DishWasherFunction dishWasherFunction) {
        this.function = dishWasherFunction;
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

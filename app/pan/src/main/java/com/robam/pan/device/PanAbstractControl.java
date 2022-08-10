package com.robam.pan.device;

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
}

package com.robam.cabinet.device;

//控制协议切换和调用
public class CabinetAbstractControl implements CabinetFunction{

    private CabinetFunction function;

    private static CabinetAbstractControl instance = new CabinetAbstractControl();

    public static CabinetAbstractControl getInstance() {
        return instance;
    }

    public void init(CabinetFunction cabinetFunction) {
        this.function = cabinetFunction;
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

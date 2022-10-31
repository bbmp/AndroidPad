package com.robam.ventilator.device;

//烟机主控端，只有本地控制
public class VentilatorAbstractControl implements VentilatorFunction{

    private VentilatorFunction function;
    private static VentilatorAbstractControl instance = new VentilatorAbstractControl();


    public static VentilatorAbstractControl getInstance() {
        return instance;
    }

    public void init(VentilatorFunction ventilatorFunction) {
        function = ventilatorFunction;
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
    public void openOilClean() {
        function.openOilClean();
    }

    @Override
    public void closeOilClean() {
        function.closeOilClean();
    }

    @Override
    public void setFanStatus(int status) {
        function.setFanStatus(status);
    }

    @Override
    public void setFanGear(int gear) {
        function.setFanGear(gear);
    }

    @Override
    public void setFanLight(int light) {
        function.setFanLight(light);
    }

    @Override
    public void setFanAll(int gear, int light) {
        function.setFanAll(gear, light);
    }

    @Override
    public void setSmart(int smart) {
        function.setSmart(smart);
    }

    @Override
    public void queryAttribute() {
        function.queryAttribute();
    }

    @Override
    public void setColorLamp() {
        function.setColorLamp();
    }
}

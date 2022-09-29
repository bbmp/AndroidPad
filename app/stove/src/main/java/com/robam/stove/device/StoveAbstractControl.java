package com.robam.stove.device;

import com.robam.common.bean.Device;
import com.robam.common.mqtt.MqttMsg;
import com.robam.stove.bean.Stove;

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
    public void queryAttribute(Stove stove) {
        function.queryAttribute(stove);
    }

    @Override
    public void setAttribute(Stove stove) {
        function.setAttribute(stove);
    }

    @Override
    public void setLevel(Stove stove) {
        function.setLevel(stove);
    }

    @Override
    public void setTiming(Stove stove) {
        function.setTiming(stove);
    }

    @Override
    public void setRecipe(Stove stove) {
        function.setRecipe(stove);
    }
}

package com.robam.ventilator.module;

import com.robam.common.module.IPublicVentilatorApi;
import com.robam.ventilator.device.VentilatorAbstractControl;

public class PublicVentilatorApi implements IPublicVentilatorApi {
    //给外部模块调用
    @Override
    public void setFanGear(int gear) {
        VentilatorAbstractControl.getInstance().setFanGear(gear);
    }
}

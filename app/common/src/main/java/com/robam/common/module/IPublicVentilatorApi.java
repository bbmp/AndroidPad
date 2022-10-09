package com.robam.common.module;

import com.robam.common.mqtt.IProtocol;

public interface IPublicVentilatorApi extends IPublicApi {
    String VENTILATOR_PUBLIC = "com.robam.ventilator.device.VentilatorFactory";
    //设置烟机挡位，对外接口
    void setFanGear(int gear);
}

package com.robam.ventilator.device;

import android.content.Context;

import com.robam.common.device.IPlat;
import com.robam.common.device.TbangPlat;
import com.robam.common.module.IPublicVentilatorApi;
import com.robam.common.mqtt.IProtocol;
import com.robam.ventilator.module.PublicVentilatorApi;
import com.robam.ventilator.module.TransmitApi;
import com.robam.ventilator.protocol.mqtt.MqttVentilator;

public class VentilatorFactory {

    //mqtt协议
    private static IProtocol protocol = new MqttVentilator();

    private static IProtocol transmitApi = new TransmitApi();

    private static IPublicVentilatorApi ventilatorApi = new PublicVentilatorApi();

    public static IProtocol getProtocol() {
        return protocol;
    }

    public static IProtocol getTransmitApi() {
        return transmitApi;
    }

    public static IPublicVentilatorApi getPublicApi() {
        return ventilatorApi;
    }
}

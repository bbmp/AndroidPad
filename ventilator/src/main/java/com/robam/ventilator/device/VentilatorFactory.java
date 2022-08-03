package com.robam.ventilator.device;

import android.content.Context;

import com.robam.common.device.IPlat;
import com.robam.common.device.TbangPlat;
import com.robam.common.mqtt.IProtocol;
import com.robam.ventilator.protocol.mqtt.MqttVentilator;

public class VentilatorFactory {
    public final static String TUOBANG = "tuobang" ;
    public final static String CQ926 = "DB620" ;
    //平台
    private static IPlat platform ;
    //mqtt协议
    private static IProtocol protocol ;


    public static void initPlat(Context context, String plat) {
        if (TUOBANG.equals(plat))
            platform = new TbangPlat();
    }

    public static void initMqttProtocol() {
        if (null == protocol)
            protocol = new MqttVentilator();
    }

    public static IPlat getPlatform() {
        return platform;
    }

    public static IProtocol getProtocol() {
        return protocol;
    }
}

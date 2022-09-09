package com.robam.pan.device;

import android.content.Context;

import com.robam.common.device.IPlat;
import com.robam.common.device.TbangPlat;
import com.robam.common.module.IPublicPanApi;
import com.robam.common.mqtt.IProtocol;
import com.robam.pan.module.PublicPanApi;
import com.robam.pan.protocol.mqtt.MqttPan;

public class PanFactory {
    public final static String TUOBANG = "tuobang" ;
    public final static String CQ926 = "DB620" ;
    //平台
    private static IPlat platform ;
    //对外开放接口
//    private static IPublicPanApi panApi = new PublicPanApi();
    private static IProtocol iProtocol = new MqttPan();


    public static void initPlat(Context context, String plat) {
        if (TUOBANG.equals(plat))
            platform = new TbangPlat();
    }

    public static IPlat getPlatform() {
        return platform;
    }

//    public static IPublicPanApi getPublicApi() {
//        return panApi;
//    }

    public static IProtocol getProtocol() {
        return iProtocol;
    }
}

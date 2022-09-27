package com.robam.stove.device;

import android.content.Context;

import com.robam.common.device.IPlat;
import com.robam.common.device.TbangPlat;
import com.robam.common.module.IPublicStoveApi;
import com.robam.common.mqtt.IProtocol;
import com.robam.stove.module.PublicStoveApi;
import com.robam.stove.protocol.mqtt.MqttStove;

public class StoveFactory {

    //对外开放接口
    private static IPublicStoveApi stoveApi = new PublicStoveApi() ;

    private static IProtocol iProtocol = new MqttStove();


    public static IPublicStoveApi getPublicApi() {
        return stoveApi;
    }

    public static IProtocol getProtocol() {
        return iProtocol;
    }
}

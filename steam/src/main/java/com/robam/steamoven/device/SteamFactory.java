package com.robam.steamoven.device;

import android.content.Context;

import com.robam.common.device.IPlat;
import com.robam.common.device.TbangPlat;
import com.robam.common.mqtt.IProtocol;
import com.robam.common.module.IPublicSteamApi;
import com.robam.steamoven.module.PublicSteamApi;
import com.robam.steamoven.protocol.mqtt.MqttSteamOven;

public class SteamFactory {
    //mqtt协议
    private static IProtocol protocol = new MqttSteamOven();

    private static IPublicSteamApi steamApi = new PublicSteamApi();


    public static IProtocol getProtocol() {
        return protocol;
    }

    public static IPublicSteamApi getPublicApi() {
        return steamApi;
    }
}

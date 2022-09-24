package com.robam.dishwasher.device;

import android.content.Context;

import com.robam.common.device.IPlat;
import com.robam.common.device.TbangPlat;
import com.robam.common.module.IPublicDishWasherApi;
import com.robam.common.mqtt.IProtocol;
import com.robam.dishwasher.module.PulbicDishWasherApi;
import com.robam.dishwasher.protocol.mqtt.MqttDishWasher;

public class DishWasherFactory {

    //mqtt协议
    private static IPublicDishWasherApi dishWasherApi = new PulbicDishWasherApi();

    private static IProtocol iProtocol = new MqttDishWasher();


    public static IPublicDishWasherApi getPublicApi() {
        return dishWasherApi;
    }

    public static IProtocol getProtocol() {
        return iProtocol;
    }
}

package com.robam.dishwasher.device;

import android.content.Context;

import com.robam.common.device.IPlat;
import com.robam.common.device.TbangPlat;
import com.robam.common.module.IPublicDishWasherApi;
import com.robam.common.mqtt.IProtocol;
import com.robam.dishwasher.module.PulbicDishWasherApi;

public class DishWasherFactory {
    public final static String TUOBANG = "tuobang" ;
    public final static String CQ926 = "DB620" ;
    //平台
    private static IPlat platform ;
    //mqtt协议
    private static IPublicDishWasherApi dishWasherApi = new PulbicDishWasherApi();


    public static void initPlat(Context context, String plat) {
        if (TUOBANG.equals(plat))
            platform = new TbangPlat();
    }

    public static IPlat getPlatform() {
        return platform;
    }

    public static IPublicDishWasherApi getPublicApi() {
        return dishWasherApi;
    }
}

package com.robam.stove.device;

import android.content.Context;

import com.robam.common.device.IPlat;
import com.robam.common.device.TbangPlat;
import com.robam.common.module.IPublicStoveApi;
import com.robam.stove.module.PublicStoveApi;

public class StoveFactory {
    public final static String TUOBANG = "tuobang" ;
    public final static String CQ926 = "DB620" ;
    //平台
    private static IPlat platform ;
    //对外开放接口
    private static IPublicStoveApi stoveApi = new PublicStoveApi() ;


    public static void initPlat(Context context, String plat) {
        if (TUOBANG.equals(plat))
            platform = new TbangPlat();
    }

    public static IPlat getPlatform() {
        return platform;
    }

    public static IPublicStoveApi getPublicApi() {
        return stoveApi;
    }
}

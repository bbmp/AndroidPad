package com.robam.pan.device;

import android.content.Context;

import com.robam.common.device.IPlat;
import com.robam.common.device.TbangPlat;
import com.robam.common.module.IPublicPanApi;
import com.robam.pan.module.PublicPanApi;

public class PanFactory {
    public final static String TUOBANG = "tuobang" ;
    public final static String CQ926 = "DB620" ;
    //平台
    private static IPlat platform ;
    //对外开放接口
    private static IPublicPanApi panApi = new PublicPanApi();


    public static void initPlat(Context context, String plat) {
        if (TUOBANG.equals(plat))
            platform = new TbangPlat();
    }

    public static IPlat getPlatform() {
        return platform;
    }

    public static IPublicPanApi getPublicApi() {
        return panApi;
    }
}

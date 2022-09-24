package com.robam.common.device;

import android.content.Context;

public class Plat {
    public final static String TUOBANG = "tuobang" ;
    public final static String CQ926 = "DB620" ;
    //平台
    private static IPlat platform ;

    //平台初始化
    public static void initPlat(Context context, String plat) {
        if (TUOBANG.equals(plat)) {
            platform = new TbangPlat();
            platform.init(context);
        }
    }

    public static IPlat getPlatform() {
        return platform;
    }
}

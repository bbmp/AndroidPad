package com.robam.ventilator.constant;

public enum VentilatorEnum {
    PERSONAL_CENTER("personal_center", "个人中心"),
    DATE_SETTING("date_setting", "时间设定"),
    WIFI_CONNECT("wifi_connect", "网络连接"),
    RESET("reset", "恢复出厂"),
    SCREEN_BRIGHTNESS("screen_brightness", "屏幕亮度"),
    SALE_SERVICE("sale_service", "关于售后"),
    ABOUT_PRODUCT("about_product", "关于产品"),
    SMART_SETTING("smart_setting", "智能设置"),
    SIMPLE_MODE("simple_mode", "极简模式");

    private String name;
    private String desc;

    VentilatorEnum(String name, String desc) {
        this.name = name;
        this.desc = desc;
    }

    public String getName() {
        return name;
    }

    public String getDesc() {
        return desc;
    }

    public static String match(String name) {

        String result = null;

        for (VentilatorEnum s : values()) {
            if (s.getName().equals(name)) {
                result = s.getDesc();
                break;
            }
        }

        return result;
    }
}

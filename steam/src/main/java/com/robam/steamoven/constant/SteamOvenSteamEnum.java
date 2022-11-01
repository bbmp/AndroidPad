package com.robam.steamoven.constant;

public enum SteamOvenSteamEnum {
    /**
     *
     */
    SMALL_STEAM(1,"小"),//小蒸汽
    MID_STEAM(2,"中"),//中蒸汽
    MAX_STEAM(3,"大");//大蒸汽

    private int code;
    private String value;

    private SteamOvenSteamEnum(int code, String message) {
        this.code = code;
        this.value = message;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String message) {
        this.value = message;
    }

    public static String match(int key) {

        String result = "";

        for (SteamOvenSteamEnum s : values()) {
            if (s.getCode()==key) {
                result = s.getValue();
                break;
            }
        }

        return result;
    }

    public static int matchValue(String value){
        for (SteamOvenSteamEnum s : values()) {
            if (s.getValue().equals(value)) {
                return s.getCode();
            }
        }
        return 0;
    }


    public static SteamOvenSteamEnum catchMessage(String msg) {

        SteamOvenSteamEnum result = null;

        for (SteamOvenSteamEnum s : values()) {
            if (s.getValue().equals(msg)) {
                result = s;
                break;
            }
        }

        return result;
    }

}

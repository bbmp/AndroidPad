package com.robam.steamoven.constant;

public enum SteamOvenSteamEnum {
    /**
     *
     */
    SMALL_STEAM(1,"小蒸汽"),
    MID_STEAM(2,"中蒸汽"),
    MAX_STEAM(3,"大蒸汽"),
    ;

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

    public static SteamOvenSteamEnum match(int key) {

        SteamOvenSteamEnum result = null;

        for (SteamOvenSteamEnum s : values()) {
            if (s.getCode()==key) {
                result = s;
                break;
            }
        }

        return result;
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

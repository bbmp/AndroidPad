package com.robam.steam.protocol.serial;

public enum SteamOvenStatusEnum {
        /**
         *
         */
    WORK_STATE_NO(0,"结束工作"),
    WORK_STATE_PREHEAT(1,"预热中"),
    WORK_STATE_PREHEAT_PAUSE(2,"预热暂停"),
    WORK_STATE_WORK(3,"工作中"),
    WORK_STATE_WORK_PAUSE(4,"工作暂停 ");

    private int code;
    private String value;

    SteamOvenStatusEnum(int code, String message) {
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

    public static SteamOvenStatusEnum match(int key) {

        SteamOvenStatusEnum result = null;

        for (SteamOvenStatusEnum s : values()) {
            if (s.getCode()==key) {
                result = s;
                break;
            }
        }

        return result;
    }

    public static SteamOvenStatusEnum catchMessage(String msg) {

        SteamOvenStatusEnum result = null;

        for (SteamOvenStatusEnum s : values()) {
            if (s.getValue().equals(msg)) {
                result = s;
                break;
            }
        }

        return result;
    }

}

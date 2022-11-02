package com.robam.steamoven.constant;


/**
 * 多端，每段默认模式code
 */
public enum MultiSegmentEnum {


    SEGMENT_NONE(-1,SteamConstant.NO_MOEL),
    SEGMENT_0(0,SteamConstant.YIYANGZHENG),//营养蒸
    SEGMENT_1(1,SteamConstant.BEIKAO),//烘焙
    SEGMENT_2(2,SteamConstant.KONGQIZHA);//空气炸
    /**
     *
     */
    private int index;
    private int modeCode;


    MultiSegmentEnum(int index, int modeCode) {
        this.index = index;
        this.modeCode = modeCode;
    }

    public int getIndex() {
        return index;
    }

    public int getModeCode() {
        return modeCode;
    }

    public static int matchCode(int modeCode) {
        for (MultiSegmentEnum s : values()) {
            if (s.getModeCode() == modeCode) {
                return s.getIndex();
            }
        }
        return -1;
    }


    public static int matchIndex(int index) {
        for (MultiSegmentEnum s : values()) {
            if (s.getIndex() == index) {
                return s.getModeCode();
            }
        }
        return -1;
    }

}

package com.robam.stove.bean;

/**
 * 灶具
 */
public class Stove {
    public static Stove getInstance() {
        return StoveHolder.instance;
    }


    private static class StoveHolder {
        private static final Stove instance = new Stove();
    }
    /**
     * 工作模式
     */
    public int workMode;
    /**
     * 工作时长
     */
    public int workHours;
}

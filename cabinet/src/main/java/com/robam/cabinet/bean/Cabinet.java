package com.robam.cabinet.bean;

/**
 * 消毒柜
 */
public class Cabinet {
    public static Cabinet getInstance() {
        return CabinetHolder.instance;
    }


    private static class CabinetHolder {
        private static final Cabinet instance = new Cabinet();
    }
    /**
     * 预约开始时间
     */
    public String orderTime;
    /**
     * 工作模式
     */
    public short workMode;
    /**
     * 工作时长
     */
    public int workHours;
}

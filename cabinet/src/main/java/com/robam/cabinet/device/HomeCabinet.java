package com.robam.cabinet.device;

public class HomeCabinet {
    //当前进入的消毒柜首页
    public static HomeCabinet getInstance() {
        return HomeCabinet.CabinetHolder.instance;
    }
    private static class CabinetHolder {
        private static final HomeCabinet instance = new HomeCabinet();
    }

    public String guid;

    /**
     *剩余预约时间
     */
    public int orderTime;
    /**
     * 工作模式
     */
    public int workMode;
    /**
     * 工作时长
     */
    public int workHours;

    /**
     * 是否开启童锁
     */
    public boolean lock = false;
}

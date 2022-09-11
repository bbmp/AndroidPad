package com.robam.dishwasher.device;

public class HomeDishWasher {
    //当前进入的洗碗机
    public static HomeDishWasher getInstance() {
        return HomeDishWasher.DishWasherHolder.instance;
    }
    private static class DishWasherHolder {
        private static final HomeDishWasher instance = new HomeDishWasher();
    }

    /**
     * 预约开始时间
     */
    public String orderTime;
    /**
     * 工作模式
     */
    public int workMode;
    /**
     * 工作时长
     */
    public int workHours;
    /**
     *   辅助模式
     */
    public int auxMode;
}

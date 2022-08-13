package com.robam.dishwasher.bean;

//洗碗机
public class DishWasher {
    public static DishWasher getInstance() {
        return DishWasherHolder.instance;
    }


    private static class DishWasherHolder {
        private static final DishWasher instance = new DishWasher();
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

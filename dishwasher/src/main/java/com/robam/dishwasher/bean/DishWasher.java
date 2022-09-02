package com.robam.dishwasher.bean;

import android.content.Context;

import com.robam.common.manager.FunctionManager;
import com.robam.dishwasher.R;

import java.util.List;

//洗碗机
public class DishWasher {
    public static DishWasher getInstance() {
        return DishWasherHolder.instance;
    }

    private List<DishWaherFunBean> dishWaherFunBeans;

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

    public void init(Context context) {
        if (null == dishWaherFunBeans)
            dishWaherFunBeans = FunctionManager.getFuntionList(context, DishWaherFunBean.class, R.raw.dishwahser);
    }

    public List<DishWaherFunBean> getDishWaherFunBeans() {
        return dishWaherFunBeans;
    }
}

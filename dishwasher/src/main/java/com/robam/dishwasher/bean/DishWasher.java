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

    private List<DishWaherModeBean> dishWaherModeBeans;

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
        if (null == dishWaherModeBeans)
            dishWaherModeBeans = FunctionManager.getFuntionList(context, DishWaherModeBean.class, R.raw.dishwahser);
    }

    public List<DishWaherModeBean> getDishWaherModeBeans() {
        return dishWaherModeBeans;
    }
    //当前模式
    public DishWaherModeBean getDishWaherModeBean(int code) {
        if (null != dishWaherModeBeans) {
            for (int i = 0; i< dishWaherModeBeans.size(); i++) {
                if (dishWaherModeBeans.get(i).code == code)
                    return dishWaherModeBeans.get(i);
            }
        }
        return null;
    }
}

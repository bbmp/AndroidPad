package com.robam.dishwasher.util;

import com.robam.dishwasher.bean.DishWasher;
import com.robam.dishwasher.bean.DishWasherModeBean;

import java.util.List;

public class DishWasherModelUtil {
    public static DishWasherModeBean getDishWasher(List<DishWasherModeBean> allModelBean, int key){
        if(allModelBean == null || allModelBean.size() == 0){
            return null;
        }
        for(DishWasherModeBean modeBean:allModelBean){
            if(key == modeBean.code){
                return modeBean;
            }
        }
        return null;
    }

    /**
     * 获取附加模式名称 TODO(后期需完善)
     * @param dishWasher
     * @return
     */
    public static String autoMode(DishWasher dishWasher){
        String lowerLayerWasher = dishWasher.LowerLayerWasher == (short) 0 ? "" : "下层洗";
        String enhancedDryStatus = dishWasher.EnhancedDryStatus == (short) 0 ? "" : "加强干燥";
        String autoVentilation = dishWasher.AutoVentilation == (short) 0 ? "" : "自动换气";
        return lowerLayerWasher+enhancedDryStatus+autoVentilation;
    }


}

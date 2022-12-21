package com.robam.dishwasher.util;

import com.robam.dishwasher.bean.DishWasher;
import com.robam.dishwasher.bean.DishWasherModeBean;
import com.robam.dishwasher.constant.DishWasherEnum;

import java.util.List;

public class DishWasherModelUtil {
    public static DishWasherModeBean getDishWasher(List<DishWasherModeBean> allModelBean, int key){
        if(key == DishWasherEnum.AUTO_AERATION.getCode()){
            key = DishWasherEnum.FLUSH.getCode();
        }
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

    public static void initWorkingInfo( DishWasherModeBean curWasherModel,DishWasher dishWasher){
        curWasherModel.restTime = dishWasher.remainingWorkingTime * 60;
        curWasherModel.code = (short) dishWasher.workMode;
        curWasherModel.auxCode = dishWasher.auxMode;
        curWasherModel.time = dishWasher.SetWorkTimeValue * 60;
        /*if(dishWasher.auxMode != 0 && curWasherModel != null && curWasherModel.auxList.size() != 0){
            for(int i = 0; i < curWasherModel.auxList.size() ;i++){
                if(dishWasher.auxMode == curWasherModel.auxList.get(i).auxCode){
                    curWasherModel.time = curWasherModel.auxList.get(i).time;
                    break;
                }
            }
        }*/
    }



}

package com.robam.dishwasher.util;

import android.app.Activity;
import android.content.Intent;

import com.robam.dishwasher.bean.DishWasher;
import com.robam.dishwasher.bean.DishWasherModeBean;
import com.robam.dishwasher.constant.DishWasherConstant;
import com.robam.dishwasher.constant.DishWasherState;
import com.robam.dishwasher.device.HomeDishWasher;
import com.robam.dishwasher.ui.activity.AppointingActivity;
import com.robam.dishwasher.ui.activity.WorkActivity;

public class SkipUtils {

    public static void dealWasherWorkingState(DishWasher dishWasher, Activity activity){
        if(dishWasher.workMode == 0){
            return;
        }
        switch (dishWasher.AppointmentSwitchStatus){
            case DishWasherState.APPOINTMENT_OFF:
                if(dishWasher.powerStatus == DishWasherState.WAIT){//待机状态下，无工作模式
                    return;
                }
                if(dishWasher.remainingWorkingTime == 0){//无剩余工作时间
                    return;
                }
                Intent intent = new Intent();
                DishWasherModeBean newMode  = new DishWasherModeBean();
                DishWasherModelUtil.initWorkingInfo(newMode,dishWasher);
                intent.putExtra(DishWasherConstant.EXTRA_MODEBEAN, newMode);
                intent.setClass(activity, WorkActivity.class);
                activity.startActivity(intent);
                break;
            case DishWasherState.APPOINTMENT_ON:
                if( dishWasher.AppointmentRemainingTime == 0){
                    return;
                }
                Intent appointingIntent = new Intent();
                DishWasherModeBean curMode  = new DishWasherModeBean();
                DishWasherModelUtil.initWorkingInfo(curMode,dishWasher);
                appointingIntent.putExtra(DishWasherConstant.EXTRA_MODEBEAN, curMode);
                appointingIntent.setClass(activity, AppointingActivity.class);
                activity.startActivity(appointingIntent);
                HomeDishWasher.getInstance().workHours = curMode.time;
                HomeDishWasher.getInstance().orderWorkTime = dishWasher.AppointmentRemainingTime;
                break;
        }
    }
}

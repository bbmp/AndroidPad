package com.robam.dishwasher.util;

import android.os.Handler;

import com.robam.common.bean.AccountInfo;
import com.robam.common.bean.Device;
import com.robam.common.utils.LogUtils;
import com.robam.dishwasher.bean.DishWasher;
import com.robam.dishwasher.device.DishWasherAbstractControl;
import com.robam.dishwasher.device.HomeDishWasher;

public class MqttSignal {
    private Runnable runnable;
    private Handler mHandler;
    private boolean isPageHide = false;
    private static final long TIME_DELAY = 2000L;

    public MqttSignal(){
        mHandler = new Handler();
    }


    public void startLoop(){
        runnable = () -> {
            LogUtils.d("dishwasher startLoop isPageHide "+isPageHide);
            DishWasher steamOven = getDishWasher();
            if(steamOven == null){
                mHandler.postDelayed(runnable, TIME_DELAY);
                return;
            }
            if(isPageHide){
                mHandler.postDelayed(runnable, TIME_DELAY);
                return;
            }
            DishWasherAbstractControl.getInstance().queryAttribute(steamOven.guid); //查询洗碗机
            mHandler.postDelayed(runnable, TIME_DELAY);
        };
        mHandler.postDelayed(runnable, TIME_DELAY);
    }

    public void pageHide(){
        this.isPageHide = true;
    }

    public void pageShow(){
        this.isPageHide = false;
    }

    public void clear(){
        if(runnable != null){
            mHandler.removeCallbacks(runnable);
        }
        mHandler.removeCallbacksAndMessages(null);
    }

    protected DishWasher getDishWasher(){
        if(AccountInfo.getInstance().deviceList == null){
            return null;
        }
        for (Device device: AccountInfo.getInstance().deviceList) {
            if (device instanceof DishWasher && device.guid.equals(HomeDishWasher.getInstance().guid)) {
                return (DishWasher) device;
            }
        }
        return null;
    }



}

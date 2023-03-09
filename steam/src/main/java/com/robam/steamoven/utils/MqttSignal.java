package com.robam.steamoven.utils;

import android.os.Handler;
import com.robam.common.bean.AccountInfo;
import com.robam.common.bean.Device;
import com.robam.common.utils.LogUtils;
import com.robam.steamoven.bean.SteamOven;
import com.robam.steamoven.device.HomeSteamOven;
import com.robam.steamoven.device.SteamAbstractControl;

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
            LogUtils.d("startLoop");
            SteamOven steamOven = getSteamOven();
            if(steamOven == null){
                mHandler.postDelayed(runnable, TIME_DELAY);
                return;
            }
            if(isPageHide){
                mHandler.postDelayed(runnable, TIME_DELAY);
                return;
            }
            SteamAbstractControl.getInstance().queryAttribute(steamOven.guid); //查询一体机状态
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

    protected SteamOven getSteamOven(){
        if(AccountInfo.getInstance().deviceList == null){
            return null;
        }
        for (Device device: AccountInfo.getInstance().deviceList) {
            if (device instanceof SteamOven && device.guid.equals(HomeSteamOven.getInstance().guid)) {
                return (SteamOven) device;
            }
        }
        return null;
    }



}

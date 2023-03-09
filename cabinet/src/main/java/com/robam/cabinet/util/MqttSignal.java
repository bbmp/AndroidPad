package com.robam.cabinet.util;

import android.os.Handler;

import com.robam.cabinet.bean.Cabinet;
import com.robam.cabinet.device.CabinetAbstractControl;
import com.robam.cabinet.device.HomeCabinet;
import com.robam.common.bean.AccountInfo;
import com.robam.common.bean.Device;
import com.robam.common.utils.LogUtils;

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
            LogUtils.d("cabinet startLoop");
            Cabinet steamOven = getCabinet();
            if(steamOven == null){
                mHandler.postDelayed(runnable, TIME_DELAY);
                return;
            }
            if(isPageHide){
                mHandler.postDelayed(runnable, TIME_DELAY);
                return;
            }
            CabinetAbstractControl.getInstance().queryAttribute(HomeCabinet.getInstance().guid);//查询消毒柜
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

    protected Cabinet getCabinet(){
        if(AccountInfo.getInstance().deviceList == null){
            return null;
        }
        for (Device device: AccountInfo.getInstance().deviceList) {
            if (device instanceof Cabinet && device.guid.equals(HomeCabinet.getInstance().guid)) {
                return (Cabinet) device;
            }
        }
        return null;
    }



}

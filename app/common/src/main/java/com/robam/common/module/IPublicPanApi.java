package com.robam.common.module;

import androidx.lifecycle.MutableLiveData;

import com.robam.common.mqtt.IProtocol;

//锅对外接口
public interface IPublicPanApi extends IProtocol {
    String PAN_HOME = "com.robam.pan.ui.activity.MainActivity";
    //只会有一个锅连接
    MutableLiveData<Integer> getPanTemp();
}

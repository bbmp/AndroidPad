package com.robam.common.module;

import androidx.lifecycle.MutableLiveData;

import com.robam.common.mqtt.IProtocol;

//灶具对外接口
public interface IPublicStoveApi extends IProtocol {
    String STOVE_HOME = "com.robam.stove.ui.activity.MainActivity";
    String STOVE_PUBLIC = "com.robam.stove.device.StoveFactory";

    int STOVE_LEFT = 0;
    int STOVE_RIGHT = 1;

    //左灶
    MutableLiveData<Boolean> getLeftStove();
    //右灶
    MutableLiveData<Boolean> getRightStove();
    //左灶工作模式
    int getLeftWorkMode();
    //右灶工作模式
    int getRightWorkMode();
}

package com.robam.common.module;

import androidx.lifecycle.MutableLiveData;

import com.robam.common.mqtt.IProtocol;

//灶具对外接口
public interface IPublicStoveApi extends IPublicApi {
    String STOVE_HOME = "com.robam.stove.ui.activity.MainActivity";
    String STOVE_PUBLIC = "com.robam.stove.device.StoveFactory";

    int STOVE_LEFT = 0;
    int STOVE_RIGHT = 1;
    //设置灶具状态
    void setAttribute(String targetGuid, int stoveId, int isCook, int workStatus);
}

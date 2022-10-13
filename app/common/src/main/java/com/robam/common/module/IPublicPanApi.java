package com.robam.common.module;

import androidx.lifecycle.MutableLiveData;

import com.robam.common.mqtt.IProtocol;

import java.util.Map;

//锅对外接口
public interface IPublicPanApi extends IPublicApi {
    String PAN_HOME = "com.robam.pan.ui.activity.MainActivity";
    String PAN_PUBLIC = "com.robam.pan.device.PanFactory";
    //设置无人锅智能互动参数
    void setInteractionParams(String targetGuid, Map params);
}

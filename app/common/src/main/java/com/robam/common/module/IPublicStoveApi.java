package com.robam.common.module;

import androidx.lifecycle.MutableLiveData;

import com.robam.common.mqtt.IProtocol;

import java.util.Map;

//灶具对外接口
public interface IPublicStoveApi extends IPublicApi {
    String STOVE_HOME = "com.robam.stove.ui.activity.MainActivity";
    String STOVE_PUBLIC = "com.robam.stove.device.StoveFactory";
    String STOVE_WARNING = "com.robam.stove.ui.activity.WarningActivity";

    int STOVE_LEFT = 0;
    int STOVE_RIGHT = 1;
    //设置灶具状态
    void setAttribute(String targetGuid, int stoveId, int isCook, int workStatus);
    //查询灶具状态
    void queryAttribute(String targetGuid);
    //断开蓝牙
    void disConnectBle(String targetGuid);
    //设置灶具互动参数
    void setStoveInteraction(String targetGuid, int stoveId, Map params);
}

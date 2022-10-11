package com.robam.stove.module;

import androidx.lifecycle.MutableLiveData;

import com.robam.common.module.IPublicStoveApi;
import com.robam.common.mqtt.MqttMsg;
import com.robam.stove.device.HomeStove;
import com.robam.stove.device.StoveAbstractControl;

import java.util.Map;

//
public class PublicStoveApi implements IPublicStoveApi {

    @Override
    public void setAttribute(String targetGuid, int stoveId, int isCook, int workStatus) {
        StoveAbstractControl.getInstance().setAttribute(targetGuid, stoveId, isCook, workStatus);
    }
}

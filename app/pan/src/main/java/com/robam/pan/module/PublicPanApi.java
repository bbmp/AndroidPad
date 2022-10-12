package com.robam.pan.module;

import androidx.lifecycle.MutableLiveData;

import com.robam.common.module.IPublicPanApi;
import com.robam.common.mqtt.MqttMsg;
import com.robam.pan.device.HomePan;
import com.robam.pan.device.PanAbstractControl;

import java.util.Map;

//锅
public class PublicPanApi implements IPublicPanApi {

    @Override
    public void setInteractionParams(String targetGuid, Map params) {
        PanAbstractControl.getInstance().setInteractionParams(targetGuid, params);
    }
}

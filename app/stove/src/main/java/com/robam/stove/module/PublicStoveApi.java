package com.robam.stove.module;

import com.robam.common.module.IPublicStoveApi;
import com.robam.stove.device.StoveAbstractControl;

//
public class PublicStoveApi implements IPublicStoveApi {

    @Override
    public void setAttribute(String targetGuid, int stoveId, int isCook, int workStatus) {
        StoveAbstractControl.getInstance().setAttribute(targetGuid, stoveId, isCook, workStatus);
    }

    @Override
    public void queryAttribute(String targetGuid) {
        StoveAbstractControl.getInstance().queryAttribute(targetGuid);
    }
}

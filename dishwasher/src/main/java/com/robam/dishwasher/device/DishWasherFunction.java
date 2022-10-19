package com.robam.dishwasher.device;

import java.util.Map;

public interface DishWasherFunction {
    void shutDown();

    void powerOn();

    void sendCommonMsg(Map<String,Object> params,String targetGuid,short msg_id);
}

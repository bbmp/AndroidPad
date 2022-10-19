package com.robam.dishwasher.device;

import java.util.Map;

//本地控制，串口通信,界面启动入口决定是本地控制还是远程控制
//只有主入口进入时才会开启串口控制
public class DishWasherLocalControl implements DishWasherFunction{
    @Override
    public void shutDown() {

    }

    @Override
    public void powerOn() {

    }

    @Override
    public void sendCommonMsg(Map<String,Object> params, String targetGuid,short msg_id) {

    }
}

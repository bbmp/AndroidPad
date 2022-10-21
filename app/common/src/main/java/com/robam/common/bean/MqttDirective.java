package com.robam.common.bean;

import androidx.lifecycle.MutableLiveData;

/**
 * 设备指令快速接收
 */
public class MqttDirective {

    public static final String DATE_SPLIT_FLAG = "&!&";

    private MutableLiveData<Integer> directive = new MutableLiveData<Integer>(-100); //设备状态变化

    private MqttDirective(){

    }

    private static class Holder {
        private static MqttDirective instance = new MqttDirective();
    }

    public static MqttDirective getInstance() {
        return MqttDirective.Holder.instance;
    }

    public MutableLiveData<Integer> getDirective() {
        return directive;
    }

   /* public void setDirectiveData(String top,short msgId){
        directive.setValue(top+DATE_SPLIT_FLAG+msgId);
    }*/

    /*public static short getMsgId(String content){
        return Short.parseShort(content.split(DATE_SPLIT_FLAG)[1]);
    }*/



}

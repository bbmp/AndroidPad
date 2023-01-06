package com.robam.steamoven.device;

import com.robam.common.mqtt.MqttManager;
import com.robam.common.mqtt.MsgKeys;
import com.robam.steamoven.bean.MultiSegment;
import com.robam.steamoven.bean.SteamOven;
import com.robam.steamoven.constant.SteamModeEnum;
import com.robam.steamoven.protocol.SteamCommandHelper;
import com.robam.steamoven.utils.MultiSegmentUtil;

import java.util.Map;

//控制层切换，本地控制和远程被控制
public class SteamAbstractControl implements SteamFunction {

    private SteamFunction function;
    private static SteamAbstractControl instance = new SteamAbstractControl();


    public static SteamAbstractControl getInstance() {
        return instance;
    }

    public void init(SteamFunction steamFunction) {
        function = steamFunction;
    }
    @Override
    public void shutDown(String targetGuid) {
        function.shutDown(targetGuid);
    }

    @Override
    public void powerOn(String targetGuid) {
        function.powerOn(targetGuid);
    }

    @Override
    public void orderWork() {
        function.orderWork();
    }

    @Override
    public void stopWork() {
        function.stopWork();
    }

    @Override
    public void startWork() {
        function.startWork();
    }

    @Override
    public void startCookForAppoint(String targetGuid) {
        function.startCookForAppoint(targetGuid);

    }

    public void startCookForAppoint(String targetGuid, SteamOven steamOven){
        MultiSegment result = MultiSegmentUtil.getSkipResult(steamOven);
        if(SteamModeEnum.EXP.getMode() == steamOven.mode){
            SteamCommandHelper.sendCommandForExp(result,targetGuid,0, MsgKeys.setDeviceAttribute_Req);
        }else{
            SteamCommandHelper.startModelWork(result,targetGuid,0);
        }
    }

    @Override
    public void pauseWork(String targetGuid) {
        function.pauseWork(targetGuid);
    }

    @Override
    public void continueWork(String targetGuid) {
        function.continueWork(targetGuid);
    }

    @Override
    public void queryAttribute(String targetGuid) {
        function.queryAttribute(targetGuid);
    }

    @Override
    public void sendCommonMsg(Map<String,Object> params, String targetGuid,short msg_id) {
        function.sendCommonMsg(params,targetGuid,msg_id);
    }

    @Override
    public void sendCommonMsg(Map<String, Object> params, String targetGuid, short msg_id, MqttManager.MqttSendMsgListener listening) {
        function.sendCommonMsg(params,targetGuid,msg_id,listening);
    }
}

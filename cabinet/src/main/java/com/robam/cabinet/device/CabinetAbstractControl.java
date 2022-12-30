package com.robam.cabinet.device;

import com.robam.common.mqtt.MqttManager;
import java.util.Map;

//控制协议切换和调用
public class CabinetAbstractControl implements CabinetFunction{

    private CabinetFunction function;

    private static CabinetAbstractControl instance = new CabinetAbstractControl();

    public static CabinetAbstractControl getInstance() {
        return instance;
    }

    public void init(CabinetFunction cabinetFunction) {
        this.function = cabinetFunction;
    }
    @Override
    public void shutDown(String guid) {
        function.shutDown(guid);
    }

    @Override
    public void powerOn() {
        function.powerOn();
    }

    @Override
    public void endAppoint(String targetGuid) {
        function.endAppoint(targetGuid);
    }

    @Override
    public void queryAttribute(String targetGuid) {
        function.queryAttribute(targetGuid);
    }

    @Override
    public void sendCommonMsg(Map<String, Object> params, String targetGuid, short msg_id) {
        function.sendCommonMsg(params,targetGuid,msg_id);
    }

    @Override
    public void sendCommonMsg(Map<String, Object> params, String targetGuid, short msg_id, MqttManager.MqttSendMsgListener listening) {
        function.sendCommonMsg(params,targetGuid,msg_id,listening);
    }
}

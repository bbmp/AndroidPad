package com.robam.cabinet.util;

import com.robam.cabinet.constant.CabinetConstant;
import com.robam.cabinet.device.CabinetAbstractControl;
import com.robam.cabinet.device.HomeCabinet;
import com.robam.common.ITerminalType;
import com.robam.common.bean.AccountInfo;
import com.robam.common.bean.MqttDirective;
import com.robam.common.mqtt.MqttManager;

import java.util.HashMap;
import java.util.Map;

public class CabinetCommonHelper {

    public static long perOrderTimeMin = System.currentTimeMillis() ;
    public static final long COMMON_DELAY_DUR = 4*1000 ;
    public static short preCommonId = -100;


    public static void sendCommonMsg(Map map, MqttManager.MqttSendMsgListener listener){
        perOrderTimeMin = System.currentTimeMillis();
        CabinetAbstractControl.getInstance().sendCommonMsg(map,(String) map.get(CabinetConstant.TARGET_GUID), (Short) map.get(CabinetConstant.MSG_ID),listener);
    }


    public static void sendCommonMsgForLiveData(Map map,final int bsCode){
        perOrderTimeMin = System.currentTimeMillis();
        CabinetAbstractControl.getInstance().sendCommonMsg(map, (String) map.get(CabinetConstant.TARGET_GUID), (Short) map.get(CabinetConstant.MSG_ID), new MqttManager.MqttSendMsgListener() {
            @Override
            public void onSuccess(String top, short msgId) {
                MqttDirective.getInstance().getDirective().setValue(bsCode);
            }

            @Override
            public void onFailure() {

            }
        });
    }


    public static Map getCommonMap(short msgId){
        Map map = new HashMap();
        map.put(CabinetConstant.UserId, AccountInfo.getInstance().getUserString());
        map.put(CabinetConstant.TARGET_GUID, HomeCabinet.getInstance().guid);
        map.put(CabinetConstant.MSG_ID, msgId);
        map.put(CabinetConstant.TerminalType, ITerminalType.PAD);
        return map;
    }


    /**
     * 获取预约执行时间
     * @return
     */
    public static int getAppointingTimeMin(String appointTimeStr){
        return 40;
    }

    public static boolean isSafe(){
        return System.currentTimeMillis()  - perOrderTimeMin >= COMMON_DELAY_DUR;
    }
}

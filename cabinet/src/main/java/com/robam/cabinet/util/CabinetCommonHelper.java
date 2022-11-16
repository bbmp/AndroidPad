package com.robam.cabinet.util;

import com.robam.cabinet.constant.CabinetConstant;
import com.robam.cabinet.device.CabinetAbstractControl;
import com.robam.cabinet.device.HomeCabinet;
import com.robam.common.ITerminalType;
import com.robam.common.bean.AccountInfo;
import com.robam.common.bean.MqttDirective;
import com.robam.common.mqtt.MqttManager;
import com.robam.common.mqtt.MsgKeys;
import java.util.HashMap;
import java.util.Map;

public class CabinetCommonHelper {

    public static long perOrderTimeMin = System.currentTimeMillis() ;
    public static final float COMMON_DELAY_DUR = 0.1f*1000;

    public static void sendCommonMsg(Map map){
        perOrderTimeMin = System.currentTimeMillis();
        CabinetAbstractControl.getInstance().sendCommonMsg(map,(String) map.get(CabinetConstant.TARGET_GUID), (Short) map.get(CabinetConstant.MSG_ID));
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

    public static boolean isSafe(){
        return System.currentTimeMillis()  - perOrderTimeMin >= COMMON_DELAY_DUR;
    }

    /**
     * 开始预约按钮
     * @param code
     * @param defTime
     * @param appointTime
     * @param flag
     */
    public static void startAppointCommand(int code,int defTime,int appointTime,int flag){
        Map map = CabinetCommonHelper.getCommonMap(MsgKeys.SetSteriPowerOnOff_Req);
        map.put(CabinetConstant.SteriStatus, code);
        map.put(CabinetConstant.SteriTime, defTime);
        map.put(CabinetConstant.ArgumentNumber,2);//附加参数 - 预约放在附加参数中

        //TODO(若不加，设备无法正常获取预约时间，待设备方查找问题，暂时先设置上)
        map.put(CabinetConstant.warmDishKey,1);
        map.put(CabinetConstant.warmDishLength,1);
        map.put(CabinetConstant.warmDishTempValue,35);

        //预约时间
        map.put(CabinetConstant.Key,2);//附加参数 - 预约放在附加参数中
        map.put(CabinetConstant.Length,2);//附加参数 - 预约放在附加参数中
        map.put(CabinetConstant.SteriReserveTime, appointTime);//附加参数 - 预约放在附加参数中
        CabinetCommonHelper.sendCommonMsgForLiveData(map,flag);

    }

    public static void startPowerOn(int bsCode){
        Map map = CabinetCommonHelper.getCommonMap(MsgKeys.SetSteriPowerOnOff_Req);
        map.put(CabinetConstant.SteriStatus, 1);
        map.put(CabinetConstant.SteriTime, 0);
        map.put(CabinetConstant.ArgumentNumber,0);
        CabinetCommonHelper.sendCommonMsgForLiveData(map,bsCode);

    }

}

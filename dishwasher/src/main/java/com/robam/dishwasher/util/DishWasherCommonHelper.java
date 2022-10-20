package com.robam.dishwasher.util;

import com.robam.common.bean.AccountInfo;
import com.robam.common.mqtt.MqttManager;
import com.robam.common.mqtt.MsgKeys;
import com.robam.dishwasher.constant.DishWasherConstant;
import com.robam.dishwasher.device.DishWasherAbstractControl;
import com.robam.dishwasher.device.HomeDishWasher;

import java.util.HashMap;
import java.util.Map;

public class DishWasherCommonHelper {


    public static void sendCommonMsg(Map map){
        DishWasherAbstractControl.getInstance().sendCommonMsg(map,(String) map.get(DishWasherConstant.TARGET_GUID), (Short) map.get(DishWasherConstant.MSG_ID));
    }

    public static void sendCommonMsg(Map map, MqttManager.MqttSendMsgListener listener){
        DishWasherAbstractControl.getInstance().sendCommonMsg(map,(String) map.get(DishWasherConstant.TARGET_GUID), (Short) map.get(DishWasherConstant.MSG_ID),listener);
    }

    public static Map getModelMap(short commonId,short workMode,short appointFlag,int appointTime){
        Map map = new HashMap();
        map.put(DishWasherConstant.MSG_ID, commonId);
        map.put(DishWasherConstant.UserId, AccountInfo.getInstance().getUserString());
        map.put(DishWasherConstant.TARGET_GUID, HomeDishWasher.getInstance().guid);
        map.put(DishWasherConstant.DishWasherWorkMode, workMode);
        //map.put(DishWasherConstant.LowerLayerWasher, lowerWash);

       /* map.put(DishWasherConstant.AutoVentilation, 0);
        map.put(DishWasherConstant.EnhancedDrySwitch, 0);*/
        map.put(DishWasherConstant.AppointmentSwitch, appointFlag);
        map.put(DishWasherConstant.AppointmentTime, appointTime);
        return map;
    }

    /**
     * 获取预约执行时间
     * @return
     */
    public static int getAppointingTimeMin(String appointTimeStr){
        return 40;
    }


}
package com.robam.dishwasher.util;

import android.content.Context;
import android.widget.Toast;

import com.robam.common.bean.AccountInfo;
import com.robam.common.bean.Device;
import com.robam.common.bean.MqttDirective;
import com.robam.common.mqtt.MqttManager;
import com.robam.common.utils.ToastUtils;
import com.robam.dishwasher.R;
import com.robam.dishwasher.bean.DishWasher;
import com.robam.dishwasher.constant.DishWasherConstant;
import com.robam.dishwasher.device.DishWasherAbstractControl;
import com.robam.dishwasher.device.HomeDishWasher;

import java.util.HashMap;
import java.util.Map;

public class DishWasherCommandHelper {

    private  long perOrderTimeMin = System.currentTimeMillis() ;
    private  final float COMMON_DELAY_DUR = 2f * 1000 ;

    private DishWasherCommandHelper(){

    }

    private static class Holder {
        private static DishWasherCommandHelper instance = new DishWasherCommandHelper();
    }

    public static DishWasherCommandHelper getInstance() {
        return DishWasherCommandHelper.Holder.instance;
    }

    public void sendCommonMsg(Map map,final int bsCode){
        perOrderTimeMin = System.currentTimeMillis();
        DishWasherAbstractControl.getInstance().sendCommonMsg(map, (String) map.get(DishWasherConstant.TARGET_GUID),(Short) map.get(DishWasherConstant.MSG_ID));
    }

    public void sendCommonMsgForLiveData(Map map,final int bsCode){
        perOrderTimeMin = System.currentTimeMillis();
        DishWasherAbstractControl.getInstance().sendCommonMsg(map, (String) map.get(DishWasherConstant.TARGET_GUID), (Short) map.get(DishWasherConstant.MSG_ID), new MqttManager.MqttSendMsgListener() {
            @Override
            public void onSuccess(String top, short msgId) {
                MqttDirective.getInstance().getDirective().setValue(bsCode);
            }

            @Override
            public void onFailure() {

            }
        });
    }

    public static Map getModelMap(short commonId,short workMode,short appointFlag,int appointTime){
        Map map = new HashMap();
        map.put(DishWasherConstant.MSG_ID, commonId);
        map.put(DishWasherConstant.UserId, AccountInfo.getInstance().getUserString());
        map.put(DishWasherConstant.TARGET_GUID, HomeDishWasher.getInstance().guid);
        map.put(DishWasherConstant.DishWasherWorkMode, workMode);
        map.put(DishWasherConstant.AppointmentSwitch, appointFlag);
        map.put(DishWasherConstant.AppointmentTime, appointTime);
        return map;
    }

    public static Map getCommonMap(short msgId){
        Map map = new HashMap();
        map.put(DishWasherConstant.UserId, AccountInfo.getInstance().getUserString());
        map.put(DishWasherConstant.TARGET_GUID, HomeDishWasher.getInstance().guid);
        map.put(DishWasherConstant.MSG_ID, msgId);
        return map;
    }



    public  boolean isSafe(){
        return System.currentTimeMillis()  - perOrderTimeMin >= COMMON_DELAY_DUR;
    }

    /**
     * 检测洗碗是否处于开门或者离线状态，若处于离线或开门状态，则提示并返回false，否则返回true
     * @param context
     * @param curDevice
     * @return
     */
    public static boolean checkDishWasherState(Context context, DishWasher curDevice){
        if(curDevice == null || curDevice.status != Device.ONLINE){
            ToastUtils.show(context, R.string.dishwasher_offline, Toast.LENGTH_LONG);
            return false;
        }
        if(curDevice.DoorOpenState == 1){
            ToastUtils.show(context,R.string.dishwasher_close_door_prompt,Toast.LENGTH_LONG);
            return false;

        }
        return true;
    }


}

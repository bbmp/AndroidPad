package com.robam.steamoven.protocol;

import com.robam.common.bean.AccountInfo;
import com.robam.common.bean.MqttDirective;
import com.robam.common.mqtt.MqttManager;
import com.robam.steamoven.constant.SteamConstant;
import com.robam.steamoven.device.HomeSteamOven;
import com.robam.steamoven.device.SteamAbstractControl;


import java.util.HashMap;
import java.util.Map;

public class SteamCommandHelper {

    private  long perOrderTimeMin = System.currentTimeMillis() ;
    private  final float COMMON_DELAY_DUR = 2f * 1000 ;

    private SteamCommandHelper(){

    }

    private static class Holder {
        private static SteamCommandHelper instance = new SteamCommandHelper();
    }

    public static SteamCommandHelper getInstance() {
        return SteamCommandHelper.Holder.instance;
    }

    public void sendCommonMsgForLiveData(Map map,final int bsCode){
        perOrderTimeMin = System.currentTimeMillis();
        SteamAbstractControl.getInstance().sendCommonMsg(map, (String) map.get(SteamConstant.TARGET_GUID), (Short) map.get(SteamConstant.MSG_ID), new MqttManager.MqttSendMsgListener() {
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
        map.put(SteamConstant.UserId, AccountInfo.getInstance().getUserString());
        map.put(SteamConstant.TARGET_GUID, HomeSteamOven.getInstance().guid);
        map.put(SteamConstant.MSG_ID, msgId);
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
//    public static boolean checkDishWasherState(Context context, DishWasher curDevice){
//        if(curDevice.status != Device.ONLINE){
//            ToastUtils.show(context, R.string.dishwasher_offline, Toast.LENGTH_LONG);
//            return false;
//        }
//        if(curDevice.DoorOpenState == 1){
//            ToastUtils.show(context,R.string.dishwasher_close_door_prompt,Toast.LENGTH_LONG);
//            return false;
//
//        }
//        return true;
//    }


}

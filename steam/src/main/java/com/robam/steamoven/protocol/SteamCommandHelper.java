package com.robam.steamoven.protocol;

import android.content.Context;
import android.widget.Toast;

import com.robam.common.bean.AccountInfo;
import com.robam.common.bean.Device;
import com.robam.common.bean.MqttDirective;
import com.robam.common.mqtt.MqttManager;
import com.robam.common.mqtt.MsgKeys;
import com.robam.common.utils.ToastUtils;
import com.robam.steamoven.R;
import com.robam.steamoven.bean.MultiSegment;
import com.robam.steamoven.bean.SteamOven;
import com.robam.steamoven.constant.Constant;
import com.robam.steamoven.constant.SteamConstant;
import com.robam.steamoven.device.HomeSteamOven;
import com.robam.steamoven.device.SteamAbstractControl;


import java.text.ParseException;
import java.util.HashMap;
import java.util.List;
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
     * 多段开始工作命令
     * @param context
     * @param multiSegments
     * @param flag
     */
    public static void sendMultiWork(Context context, List<MultiSegment> multiSegments,int flag){
        if(!multiSegments.get(0).isStart() && multiSegments.size() < 2){
            Toast.makeText(context, R.string.steam_cook_start_prompt,Toast.LENGTH_LONG).show();
            return;
        }
        Map commonMap = SteamCommandHelper.getCommonMap(MsgKeys.setDeviceAttribute_Req);

        commonMap.put(SteamConstant.ARGUMENT_NUMBER, multiSegments.size()*5+3);
        commonMap.put(SteamConstant.BS_TYPE , SteamConstant.BS_TYPE_2) ;
        //一体机电源控制
        commonMap.put(SteamConstant.powerCtrlKey, 2);
        commonMap.put(SteamConstant.powerCtrlLength, 1);
        commonMap.put(SteamConstant.powerCtrl, 1);
        //一体机工作控制
        commonMap.put(SteamConstant.workCtrlKey, 4);
        commonMap.put(SteamConstant.workCtrlLength, 1);
        commonMap.put(SteamConstant.workCtrl, 1);
        //预约时间
        // commonMap.put(SteamConstant.setOrderMinutesKey, 5);
        //commonMap.put(SteamConstant.setOrderMinutesLength, 1);
        //commonMap.put(SteamConstant.setOrderMinutes, 0);

        //段数
        commonMap.put(SteamConstant.sectionNumberKey, 100) ;
        commonMap.put(SteamConstant.sectionNumberLength, 1) ;
        commonMap.put(SteamConstant.sectionNumber, multiSegments.size() ) ;
        // commonMap.put(SteamConstant.sectionNumber, recipeStepList.size() ) ;
        for (int i = 0; i < multiSegments.size(); i++) {
            MultiSegment bean = multiSegments.get(i);

            //TODO(需要安全检测)
//            if (!Util.workBeforeCheck(Integer.parseInt(bean.modelCode),steameOvenOne,true,false)){
//                return;
//            }
            //模式
            commonMap.put(SteamConstant.modeKey + i, 101 + i *10  ) ;
            commonMap.put(SteamConstant.modeLength + i, 1) ;
            commonMap.put(SteamConstant.mode + i,bean.code) ;
            //温度上温度
            commonMap.put(SteamConstant.setUpTempKey + i  , 102 + i *10 );
            commonMap.put(SteamConstant.setUpTempLength + i, 1);
            commonMap.put(SteamConstant.setUpTemp + i ,bean.defTemp);

            commonMap.put(SteamConstant.setDownTempKey + i  , 103 + i *10 );
            commonMap.put(SteamConstant.setDownTempLength + i, 1);
            commonMap.put(SteamConstant.setDownTemp + i ,bean.downTemp);

            //时间
            // int time =Integer.parseInt(bean.time)*60;
            //TODO(检查时间传递是否正确)
            int time = bean.duration * 60;//(秒)
            commonMap.put(SteamConstant.setTimeKey + i , 104 + i *10 );
            commonMap.put(SteamConstant.setTimeLength + i, 1);
            short lowTime = time > 255 ? (short) (time & 0Xff):(short)time;
            //final short lowTime = time > 255 ? (short) (time & 0Xff):(short)time;
            if (time<=255){
                commonMap.put(SteamConstant.setTime0b+i, lowTime);
            }else{
                commonMap.put(SteamConstant.setTimeKey+i, 104 + i *10);
                commonMap.put(SteamConstant.setTimeLength+i, 2);
                short ltime = (short)(time & 0xff);
                commonMap.put(SteamConstant.setTime0b+i, ltime);
                short htime = (short) ((time >> 8) & 0Xff);
                commonMap.put(SteamConstant.setTime1b+i, htime);
            }
            //commonMap.put(SteamConstant.setTime + i, bean.getTime()*60);
            //TODO(检测蒸汽量传递是否正确,暂时传递0)
            commonMap.put(SteamConstant.steamKey + i, 106 + i *10 );
            commonMap.put(SteamConstant.steamLength + i , 1);
            //commonMap.put(SteamConstant.steam + i, bean.steam);
            commonMap.put(SteamConstant.steam + i, 0);
        }
        SteamCommandHelper.getInstance().sendCommonMsgForLiveData(commonMap,flag);
    }


    /**
     *
     * @param segment
     * @param flag
     */
    public static void startModelWork(MultiSegment segment,int flag){
        int mode = segment.code;
        int setTemp = segment.defTemp;
        int setTime = segment.duration;
        int steamFlow = segment.steam;
        Map commonMap = SteamCommandHelper.getCommonMap(MsgKeys.setDeviceAttribute_Req);
        if (steamFlow == 0){
            if (setTemp == 0){
                commonMap.put(SteamConstant.ARGUMENT_NUMBER, 7);
            }else {
                commonMap.put(SteamConstant.ARGUMENT_NUMBER, 8);
            }
        }else {
            if (setTemp == 0){
                commonMap.put(SteamConstant.ARGUMENT_NUMBER, 8);
            }else {
                commonMap.put(SteamConstant.ARGUMENT_NUMBER, 9);
            }
        }
        commonMap.put(SteamConstant.BS_TYPE , SteamConstant.BS_TYPE_0) ;
        //一体机电源控制
        commonMap.put(SteamConstant.powerCtrlKey, 2);
        commonMap.put(SteamConstant.powerCtrlLength, 1);
        commonMap.put(SteamConstant.powerCtrl, 1);

        //一体机工作控制
        commonMap.put(SteamConstant.workCtrlKey, 4);
        commonMap.put(SteamConstant.workCtrlLength, 1);
        commonMap.put(SteamConstant.workCtrl, 1);

        //预约时间
        commonMap.put(SteamConstant.setOrderMinutesKey, 5);
        commonMap.put(SteamConstant.setOrderMinutesLength, 1);
        commonMap.put(SteamConstant.setOrderMinutes01, 0);


        //commonMap.put(SteamConstant.setOrderMinutes, orderTime);

        //段数
        commonMap.put(SteamConstant.sectionNumberKey, 100) ;
        commonMap.put(SteamConstant.sectionNumberLength, 1) ;
        commonMap.put(SteamConstant.sectionNumber, 1) ;

        commonMap.put(SteamConstant.rotateSwitchKey, 9) ;
        commonMap.put(SteamConstant.rotateSwitchLength, 1) ;
        commonMap.put(SteamConstant.rotateSwitch, 0) ;
        //模式
        commonMap.put(SteamConstant.modeKey, 101) ;
        commonMap.put(SteamConstant.modeLength, 1) ;
        commonMap.put(SteamConstant.mode, mode) ;
        //温度上温度

        if (setTemp!=0) {
            commonMap.put(SteamConstant.setUpTempKey, 102);
            commonMap.put(SteamConstant.setUpTempLength, 1);
            commonMap.put(SteamConstant.setUpTemp, setTemp);
        }
        //时间
        setTime*=60;
        commonMap.put(SteamConstant.setTimeKey, 104);
        commonMap.put(SteamConstant.setTimeLength, 1);

        final short lowTime = setTime > 255 ? (short) (setTime & 0Xff):(short)setTime;
        if (setTime<=255){
            commonMap.put(SteamConstant.setTime0b, lowTime);
        }else{
            commonMap.put(SteamConstant.setTimeKey, 104);
            commonMap.put(SteamConstant.setTimeLength, 2);
            short time = (short)(setTime & 0xff);
            commonMap.put(SteamConstant.setTime0b, time);
            short highTime = (short) ((setTime >> 8) & 0Xff);
            commonMap.put(SteamConstant.setTime1b, highTime);
        }

        if (steamFlow!=0) {
            //蒸汽量
            commonMap.put(SteamConstant.steamKey, 106);
            commonMap.put(SteamConstant.steamLength, 1);
            commonMap.put(SteamConstant.steam, steamFlow);
        }
        //SteamCommandHelper.getInstance().sendCommonMsgForLiveData(commonMap,MsgKeys.setDeviceAttribute_Req+directive_offset);
        SteamCommandHelper.getInstance().sendCommonMsgForLiveData(commonMap,flag);
    }

    /**
     * 一体机本地菜谱
     * @param recipeId
     * @param recipeSetMinutes
     * @param flag
     */
    public static void sendRecipeWork(long recipeId, int recipeSetMinutes,int flag) {

//        if (this.descaleFlag==1){
//            if (isWater(SteamOvenModeEnum.match(mode))) {
//                ToastUtils.show("水箱缺水",Toast.LENGTH_LONG);
//                return;
//            }
//
//        }
            Map msg = getCommonMap(MsgKeys.setDeviceAttribute_Req);
            msg.put(SteamConstant.ARGUMENT_NUMBER, 4);
            msg.put(SteamConstant.BS_TYPE , SteamConstant.BS_TYPE_3) ;
            //一体机电源控制
            msg.put(SteamConstant.powerCtrlKey, 2);
            msg.put(SteamConstant.powerCtrlLength, 1);
            msg.put(SteamConstant.powerCtrl, 1);
            //一体机工作控制
            msg.put(SteamConstant.workCtrlKey, 4);
            msg.put(SteamConstant.workCtrlLength, 1);
            msg.put(SteamConstant.workCtrl, 1);
            //菜谱id

            msg.put(SteamConstant.recipeIdKey, 17);
            msg.put(SteamConstant.recipeIdLength, 1);
            short recipeIdTemp = recipeId > 255 ? (short) (recipeId & 0Xff):(short)recipeId;
            if (recipeId<=255){
                msg.put(SteamConstant.recipeId, recipeIdTemp);
            }else{
                msg.put(SteamConstant.recipeIdKey, 17);
                msg.put(SteamConstant.recipeIdLength, 2);
                short lowPower = (short)(recipeIdTemp & 0xff);
                msg.put(SteamConstant.recipeId, lowPower);
                short highPower = (short) ((recipeIdTemp >> 8) & 0Xff);
                msg.put(SteamConstant.recipeId01, highPower);
            }

            //msg.put(SteamConstant.recipeId, recipeId);

            //菜谱时间
            msg.put(SteamConstant.recipeSetMinutesKey, 18);
            msg.put(SteamConstant.recipeSetMinutesLength, 1);
            short SetMinutesTemp = recipeSetMinutes > 255 ? (short) (recipeSetMinutes & 0Xff):(short)recipeSetMinutes;
            if (recipeSetMinutes<=255){
                msg.put(SteamConstant.recipeSetMinutes, SetMinutesTemp);
            }else{
                msg.put(SteamConstant.recipeSetMinutesLength, 2);
                short time = (short)(recipeSetMinutes & 0xff);
                msg.put(SteamConstant.recipeSetMinutes, time);
                short highTime = (short) ((recipeSetMinutes >> 8) & 0Xff);
                msg.put(SteamConstant.recipeSetMinutesH, highTime);
            }
           getInstance().sendCommonMsgForLiveData(msg,flag);
    }

    /**
     * EXP 工作命令
     * @param segment 参数对象
     * @param appointTime 预约时间
     * @param flag 命令标识
     */
    public static void sendCommandForExp(MultiSegment segment,int appointTime,int flag) {
        int mode = segment.code;
        int setTime = segment.duration;
        int setTemp = segment.defTemp;
        int setDownTemp = segment.downTemp;
        int orderTime = appointTime;
        int steamFlow = segment.steam;
        try {
            Map msg = getCommonMap(MsgKeys.setDeviceAttribute_Req);
            if (steamFlow==0){
                msg.put(SteamConstant.ARGUMENT_NUMBER, 9);
            }else {
                msg.put(SteamConstant.ARGUMENT_NUMBER, 10);
            }
            msg.put(SteamConstant.BS_TYPE , SteamConstant.BS_TYPE_0) ;
            //一体机电源控制
            msg.put(SteamConstant.powerCtrlKey, 2);
            msg.put(SteamConstant.powerCtrlLength, 1);
            msg.put(SteamConstant.powerCtrl, 1);

            msg.put(SteamConstant.setDownTempKey  , 103);
            msg.put(SteamConstant.setDownTempLength, 1);
            msg.put(SteamConstant.setDownTemp, setDownTemp);

            msg.put(SteamConstant.workCtrlKey, 4);
            msg.put(SteamConstant.workCtrlLength, 1);
            if (orderTime==0){
                msg.put(SteamConstant.workCtrl, 1);
            }else {
                msg.put(SteamConstant.workCtrl, 3);
            }

            //预约时间
            msg.put(SteamConstant.setOrderMinutesKey, 5);
            msg.put(SteamConstant.setOrderMinutesLength, 1);
//            final short lowOrderTime = orderTime > 255 ? (short) (orderTime & 0Xff):(short)orderTime;
            if (orderTime<=255){
                msg.put(SteamConstant.setOrderMinutes01, orderTime);
            }else{
                if (orderTime<=(256*256)&&orderTime>255) {
                    msg.put(SteamConstant.setOrderMinutesKey, 5);
                    msg.put(SteamConstant.setOrderMinutesLength, 2);
                    short time = (short) (orderTime & 0xff);
                    msg.put(SteamConstant.setOrderMinutes01, time);
                    short highTime = (short) ((orderTime >> 8) & 0Xff);
                    msg.put(SteamConstant.setOrderMinutes02, highTime);
                }else if (orderTime<=255*255*255&&orderTime>255*255){
                    msg.put(SteamConstant.setOrderMinutesKey, 5);
                    msg.put(SteamConstant.setOrderMinutesLength, 4);
                    short time = (short) (orderTime & 0xff);
                    msg.put(SteamConstant.setOrderMinutes01, time);
                    short highTime = (short) ((orderTime >> 8) & 0Xff);
                    msg.put(SteamConstant.setOrderMinutes02, highTime);
                    short time1 = (short) ((orderTime >> 16) & 0Xff);
                    msg.put(SteamConstant.setOrderMinutes03, time1);
                }
            }
//          msg.put(SteamConstant.setOrderMinutes, orderTime);
            //段数
            msg.put(SteamConstant.sectionNumberKey, 100) ;
            msg.put(SteamConstant.sectionNumberLength, 1) ;
            msg.put(SteamConstant.sectionNumber, 1) ;

            msg.put(SteamConstant.rotateSwitchKey, 9) ;
            msg.put(SteamConstant.rotateSwitchLength, 1) ;
            msg.put(SteamConstant.rotateSwitch, 0) ;
            //模式
            msg.put(SteamConstant.modeKey, 101) ;
            msg.put(SteamConstant.modeLength, 1) ;
            msg.put(SteamConstant.mode, mode) ;
            //温度上温度
            msg.put(SteamConstant.setUpTempKey  , 102);
            msg.put(SteamConstant.setUpTempLength, 1);
            msg.put(SteamConstant.setUpTemp, setTemp);
            //时间
            setTime*=60;
            msg.put(SteamConstant.setTimeKey, 104);
            msg.put(SteamConstant.setTimeLength, 1);

            final short lowTime = setTime > 255 ? (short) (setTime & 0Xff):(short)setTime;
            if (setTime<=255){
                msg.put(SteamConstant.setTime0b, lowTime);
            }else{
                msg.put(SteamConstant.setTimeKey, 104);
                msg.put(SteamConstant.setTimeLength, 2);
                short time = (short)(setTime & 0xff);
                msg.put(SteamConstant.setTime0b, time);
                short highTime = (short) ((setTime >> 8) & 0Xff);
                msg.put(SteamConstant.setTime1b, highTime);
            }
            if (steamFlow!=0) {
                //蒸汽量
                msg.put(SteamConstant.steamKey, 106);
                msg.put(SteamConstant.steamLength, 1);
                msg.put(SteamConstant.steam, steamFlow);
            }
            getInstance().sendCommonMsgForLiveData(msg,flag);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    public static void sendAppointCommand(MultiSegment multiSegment,int appointTime,int flag){
        int steamFlow = multiSegment.steam;
        int setTemp = multiSegment.defTemp;
        int mode = multiSegment.code;
        int setTime = multiSegment.duration;
        int orderTime = appointTime;
        Map commonMap = SteamCommandHelper.getCommonMap(MsgKeys.setDeviceAttribute_Req);
        if (steamFlow == 0){
            if (setTemp == 0){
                commonMap.put(SteamConstant.ARGUMENT_NUMBER, 7);
            }else {
                commonMap.put(SteamConstant.ARGUMENT_NUMBER, 8);
            }
        }else {
            if (setTemp == 0){
                commonMap.put(SteamConstant.ARGUMENT_NUMBER, 8);
            }else {
                commonMap.put(SteamConstant.ARGUMENT_NUMBER, 9);
            }
        }
        commonMap.put(SteamConstant.BS_TYPE , SteamConstant.BS_TYPE_0) ;
        //一体机电源控制
        commonMap.put(SteamConstant.powerCtrlKey, 2);
        commonMap.put(SteamConstant.powerCtrlLength, 1);
        commonMap.put(SteamConstant.powerCtrl, 1);

        //一体机工作控制
        commonMap.put(SteamConstant.workCtrlKey, 4);
        commonMap.put(SteamConstant.workCtrlLength, 1);
        if (orderTime==0){
            commonMap.put(SteamConstant.workCtrl, 1);
        }else {
            commonMap.put(SteamConstant.workCtrl, 3);
        }

        //预约时间
        commonMap.put(SteamConstant.setOrderMinutesKey, 5);
        commonMap.put(SteamConstant.setOrderMinutesLength, 1);
        if (orderTime<=255){
            commonMap.put(SteamConstant.setOrderMinutes01, orderTime);
        }else{
            if (orderTime<=(256*256)&&orderTime>255) {
                commonMap.put(SteamConstant.setOrderMinutesKey, 5);
                commonMap.put(SteamConstant.setOrderMinutesLength, 2);
                short time = (short) (orderTime & 0xff);
                commonMap.put(SteamConstant.setOrderMinutes01, time);
                short highTime = (short) ((orderTime >> 8) & 0Xff);
                commonMap.put(SteamConstant.setOrderMinutes02, highTime);
            }else if (orderTime<=255*255*255&&orderTime>255*255){
                commonMap.put(SteamConstant.setOrderMinutesKey, 5);
                commonMap.put(SteamConstant.setOrderMinutesLength, 4);
                short time = (short) (orderTime & 0xff);
                commonMap.put(SteamConstant.setOrderMinutes01, time);
                short highTime = (short) ((orderTime >> 8) & 0Xff);
                commonMap.put(SteamConstant.setOrderMinutes02, highTime);
                short time1 = (short) ((orderTime >> 16) & 0Xff);
                commonMap.put(SteamConstant.setOrderMinutes03, time1);
            }
        }

        //commonMap.put(SteamConstant.setOrderMinutes, orderTime);

        //段数
        commonMap.put(SteamConstant.sectionNumberKey, 100) ;
        commonMap.put(SteamConstant.sectionNumberLength, 1) ;
        commonMap.put(SteamConstant.sectionNumber, 1) ;

        commonMap.put(SteamConstant.rotateSwitchKey, 9) ;
        commonMap.put(SteamConstant.rotateSwitchLength, 1) ;
        commonMap.put(SteamConstant.rotateSwitch, 0) ;
        //模式
        commonMap.put(SteamConstant.modeKey, 101) ;
        commonMap.put(SteamConstant.modeLength, 1) ;
        commonMap.put(SteamConstant.mode, mode) ;
        //温度上温度

        if (setTemp!=0) {
            commonMap.put(SteamConstant.setUpTempKey, 102);
            commonMap.put(SteamConstant.setUpTempLength, 1);
            commonMap.put(SteamConstant.setUpTemp, setTemp);
        }
        //时间
        setTime*=60;
        commonMap.put(SteamConstant.setTimeKey, 104);
        commonMap.put(SteamConstant.setTimeLength, 1);

        final short lowTime = setTime > 255 ? (short) (setTime & 0Xff):(short)setTime;
        if (setTime<=255){
            commonMap.put(SteamConstant.setTime0b, lowTime);
        }else{
            commonMap.put(SteamConstant.setTimeKey, 104);
            commonMap.put(SteamConstant.setTimeLength, 2);
            short time = (short)(setTime & 0xff);
            commonMap.put(SteamConstant.setTime0b, time);
            short highTime = (short) ((setTime >> 8) & 0Xff);
            commonMap.put(SteamConstant.setTime1b, highTime);
        }

        if (steamFlow!=0) {
            //蒸汽量
            commonMap.put(SteamConstant.steamKey, 106);
            commonMap.put(SteamConstant.steamLength, 1);
            commonMap.put(SteamConstant.steam, steamFlow);
        }
        getInstance().sendCommonMsgForLiveData(commonMap,flag);
    }


    /**
     * 发送手动加湿/旋转/照明指令
     * @param commandCode 8 - 照明、9 - 旋转、16 - 加湿
     * @param flag
     */
    public static void sendCommand(int commandCode,int flag){
        Map commonMap = getCommonMap(MsgKeys.setDeviceAttribute_Req);
        commonMap.put(SteamConstant.ARGUMENT_NUMBER, 1);
        commonMap.put(SteamConstant.BS_TYPE, SteamConstant.BS_TYPE_7) ;
        commonMap.put(SteamConstant.steamCtrlKey, commandCode);
        commonMap.put(SteamConstant.steamCtrlLength, 1);
        commonMap.put(SteamConstant.steam, 1);
        getInstance().sendCommonMsgForLiveData(commonMap,flag);
    }

    /**
     * 检测洗碗是否处于开门或者离线状态，若处于离线或开门状态，则提示并返回false，否则返回true
     * @param context
     * @param curDevice
     * @return
     */
    public static boolean checkSteamState(Context context, SteamOven curDevice,int modeCode){
        if(curDevice.status != Device.ONLINE){
            ToastUtils.showLong(context, R.string.steam_offline);
            return false;
        }
        if(curDevice.mode != 0){
            ToastUtils.showLong(context, R.string.steam_working_prompt);
            //return false;
        }
        if(curDevice.doorState != 0){//门状态检测
            ToastUtils.showLong(context,R.string.steam_close_door_prompt);
            return false;
        }
        if(curDevice.descaleFlag == 1 || curDevice.waterLevelState == 1){
            if (modeCode == SteamConstant.XIANNENZHENG
                    || modeCode == SteamConstant.YIYANGZHENG
                    || modeCode == SteamConstant.GAOWENZHENG
                    || modeCode == SteamConstant.WEIBOZHENG
                    || modeCode == SteamConstant.ZHIKONGZHENG
                    || modeCode == SteamConstant.JIASHIBEIKAO
                    || modeCode == SteamConstant.SHAJUN
                    || modeCode == SteamConstant.JIEDONG
                    || modeCode == SteamConstant.FAJIAO
            ){
                if(curDevice.waterBoxState != 0){
                    ToastUtils.show(context,R.string.steam_water_box_prompt,Toast.LENGTH_LONG);
                    return false;
                }

                if(curDevice.waterLevelState == 1){
                    ToastUtils.showLong(context, R.string.steam_water_deficient);
                    return false;
                }

                if(curDevice.descaleFlag == 1){//除垢
                    ToastUtils.showLong(context, R.string.steam_descaling_prompt);
                    return false;
                }
            }
        }
        return true;
    }

    public static void sendWorkCtrCommand(boolean isWork,int flag){
        Map commonMap = getCommonMap(MsgKeys.setDeviceAttribute_Req);
        commonMap.put(SteamConstant.BS_TYPE , SteamConstant.BS_TYPE_1) ;
        commonMap.put(SteamConstant.ARGUMENT_NUMBER, 1);
        //一体机工作控制
        commonMap.put(SteamConstant.workCtrlKey, 4);
        commonMap.put(SteamConstant.workCtrlLength, 1);
        if(isWork){
            commonMap.put(SteamConstant.workCtrl, SteamConstant.WORK_CTRL_CONTINUE);//继续工作
        }else{
            commonMap.put(SteamConstant.workCtrl, SteamConstant.WORK_CTRL_TIME_OUT);//暂停工作
        }
        getInstance().sendCommonMsgForLiveData(commonMap,flag);
    }


    /**
     *  发送主动结束工作指令
      * @param flag
     */
    public static void sendEndWorkCommand(int flag){
        Map commonMap = SteamCommandHelper.getCommonMap(MsgKeys.setDeviceAttribute_Req);
        commonMap.put(SteamConstant.BS_TYPE , SteamConstant.BS_TYPE_1) ;
        commonMap.put(SteamConstant.ARGUMENT_NUMBER, 1);
        //一体机工作控制
        commonMap.put(SteamConstant.workCtrlKey, 4);
        commonMap.put(SteamConstant.workCtrlLength, 1);
        commonMap.put(SteamConstant.workCtrl, SteamConstant.WORK_CTRL_STOP);//结束工作
        SteamCommandHelper.getInstance().sendCommonMsgForLiveData(commonMap,flag);
    }

    /**
     * 工作正常完成，发送结束指令
     * @param flag
     */
    public static void sendWorkFinishCommand(int flag){
        Map commonMap = SteamCommandHelper.getCommonMap(MsgKeys.setDeviceAttribute_Req);
        commonMap.put(SteamConstant.BS_TYPE , SteamConstant.BS_TYPE_1) ;
        commonMap.put(SteamConstant.ARGUMENT_NUMBER, 1);
        //一体机工作控制
        commonMap.put(SteamConstant.workCtrlKey, 2);
        commonMap.put(SteamConstant.workCtrlLength, 1);
        commonMap.put(SteamConstant.workCtrl, SteamConstant.WORK_CTRL_STOP);//结束工作
        SteamCommandHelper.getInstance().sendCommonMsgForLiveData(commonMap,flag);
    }









}

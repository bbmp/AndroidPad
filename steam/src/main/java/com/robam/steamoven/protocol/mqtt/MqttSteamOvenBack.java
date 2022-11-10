package com.robam.steamoven.protocol.mqtt;

import android.util.Log;

import com.robam.common.mqtt.MqttMsg;
import com.robam.common.mqtt.MqttPublic;
import com.robam.common.mqtt.MsgKeys;
import com.robam.common.utils.ByteUtils;
import com.robam.common.utils.MsgUtils;
import com.robam.steamoven.constant.QualityKeys;
import com.robam.steamoven.constant.SteamConstant;
import com.robam.steamoven.device.SteamAbstractControl;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class MqttSteamOvenBack extends MqttPublic {
    public static final ByteOrder BYTE_ORDER = ByteOrder.LITTLE_ENDIAN;

    @Override
    protected void onEncodeMsg(ByteBuffer buf, MqttMsg msg) {
        int msgId = msg.getID();
        switch (msgId) {
            case MsgKeys.DeviceConnected_Noti:
                break;
            case MsgKeys.setDeviceAttribute_Req: //属性设置
//                if (null != msg.getBytes())
//                    buf.put(msg.getBytes());
                int type = msg.optInt(SteamConstant.BS_TYPE);
                switch (type) {
                    //专业模式 单一 设置
                    case SteamConstant.BS_TYPE_0:
                        byte number = (byte) msg.optInt(SteamConstant.ARGUMENT_NUMBER);
                        buf.put(number);
                        //一体机电源控制
                        byte powerCtrlKey = (byte) msg.optInt(SteamConstant.powerCtrlKey);
                        buf.put(powerCtrlKey);
                        byte powerCtrlLength = (byte) msg.optInt(SteamConstant.powerCtrlLength);
                        buf.put(powerCtrlLength);
                        byte powerCtrlKeyValue = (byte) msg.optInt(SteamConstant.powerCtrl);
                        buf.put(powerCtrlKeyValue);
                        //一体机工作控制
                        byte SteameOvenStatus_Key = (byte) msg.optInt(SteamConstant.workCtrlKey);
                        buf.put(SteameOvenStatus_Key);
                        byte SteameOvenStatus_Length = (byte) msg.optInt(SteamConstant.workCtrlLength);
                        buf.put(SteameOvenStatus_Length);
                        byte SteameOvenStatus = (byte) msg.optInt(SteamConstant.workCtrl);
                        buf.put(SteameOvenStatus);
                        //预约时间
                        byte OrderTime_key = (byte) msg.optInt(SteamConstant.setOrderMinutesKey);
                        buf.put(OrderTime_key);
                        byte OrderTime_length = (byte) msg.optInt(SteamConstant.setOrderMinutesLength);
                        buf.put(OrderTime_length);
                        byte OrderTime = (byte) msg.optInt(SteamConstant.setOrderMinutes01);
                        buf.put(OrderTime);
                        if (OrderTime_length>1){
                            buf.put((byte) msg.optInt(SteamConstant.setOrderMinutes02));
                        }
                        if (OrderTime_length>2){
                            buf.put((byte) msg.optInt(SteamConstant.setOrderMinutes03));
                            buf.put((byte)0);
                        }

                        //总段数
                        byte steameOvenTotalNumberOfSegments_Key = (byte) msg.optInt(SteamConstant.sectionNumberKey);
                        buf.put(steameOvenTotalNumberOfSegments_Key);
                        byte steameOvenTotalNumberOfSegments_Length = (byte) msg.optInt(SteamConstant.sectionNumberLength);
                        buf.put(steameOvenTotalNumberOfSegments_Length);
                        byte steameOvenTotalNumberOfSegments_Value = (byte) msg.optInt(SteamConstant.sectionNumber);
                        buf.put(steameOvenTotalNumberOfSegments_Value);



                        //旋转烤
                        if (msg.has(SteamConstant.rotateSwitchKey)) {
                            byte rotateSwitchKey = (byte) msg.optInt(SteamConstant.rotateSwitchKey);
                            buf.put(rotateSwitchKey);
                            byte rotateSwitchLength = (byte) msg.optInt(SteamConstant.rotateSwitchLength);
                            buf.put(rotateSwitchLength);
                            byte rotateSwitch = (byte) msg.optInt(SteamConstant.rotateSwitch);
                            buf.put(rotateSwitch);
                        }

                        //模式
                        byte SteameOvenMode_Key = (byte) msg.optInt(SteamConstant.modeKey);
                        buf.put(SteameOvenMode_Key);
                        byte SteameOvenMode_Length = (byte) msg.optInt(SteamConstant.modeLength);
                        buf.put(SteameOvenMode_Length);
                        byte SteameOvenMode = (byte) msg.optInt(SteamConstant.mode);
                        buf.put(SteameOvenMode);
                        //上温度

                        if (msg.has(SteamConstant.setUpTempKey)) {
                            byte SteameOvenSetTemp_Key = (byte) msg.optInt(SteamConstant.setUpTempKey);
                            buf.put(SteameOvenSetTemp_Key);
                            byte SteameOvenSetTemp_Length = (byte) msg.optInt(SteamConstant.setUpTempLength);
                            buf.put(SteameOvenSetTemp_Length);
                            byte SteameOvenSetTemp = (byte) msg.optInt(SteamConstant.setUpTemp);
                            buf.put(SteameOvenSetTemp);
                        }

                        if (msg.has(SteamConstant.setDownTempKey)){
                            byte SteameOvenSetDownTemp_Key = (byte) msg.optInt(SteamConstant.setDownTempKey);
                            buf.put(SteameOvenSetDownTemp_Key);
                            byte SteameOvenSetDownTemp_Length = (byte) msg.optInt(SteamConstant.setDownTempLength);
                            buf.put(SteameOvenSetDownTemp_Length);
                            byte SteameOvenSetDownTemp = (byte) msg.optInt(SteamConstant.setDownTemp);
                            buf.put(SteameOvenSetDownTemp);
                        }
                        //时间
                        byte SteameOvenSetTime_Key = (byte) msg.optInt(SteamConstant.setTimeKey);
                        buf.put(SteameOvenSetTime_Key);
                        byte SteameOvenSetTime_Length = (byte) msg.optInt(SteamConstant.setTimeLength);
                        buf.put(SteameOvenSetTime_Length);
                        byte SteameOvenSetTime = (byte) msg.optInt(SteamConstant.setTime0b);
                        buf.put(SteameOvenSetTime);
                        if (SteameOvenSetTime_Length>1)
                            buf.put((byte) msg.optInt(SteamConstant.setTime1b));

                        //蒸汽量
                        if ( msg.has(SteamConstant.steam)) {
                            byte steamKey = (byte) msg.optInt(SteamConstant.steamKey);
                            buf.put(steamKey);
                            byte steamLength = (byte) msg.optInt(SteamConstant.steamLength);
                            buf.put(steamLength);
                            byte steam = (byte) msg.optInt(SteamConstant.steam);
                            buf.put(steam);
                        }
                        break;
                    //单属性设置
                    case SteamConstant.BS_TYPE_1:
                        buf.put((byte) msg.optInt(SteamConstant.ARGUMENT_NUMBER));
                        //一体机工作控制
                        buf.put((byte) msg.optInt(SteamConstant.workCtrlKey));
                        buf.put((byte) msg.optInt(SteamConstant.workCtrlLength));
                        buf.put((byte) msg.optInt(SteamConstant.workCtrl));
                        break;
                    //多段模式设置
                    case SteamConstant.BS_TYPE_2:
//                           byte numberOfCategory2 = (byte) msg.optInt(SteamConstant.categoryCode);
//                           buf.put(numberOfCategory2);
                        byte number2 = (byte) msg.optInt(SteamConstant.ARGUMENT_NUMBER);
                        buf.put(number2);
                        //一体机电源控制
                        byte powerCtrlKey2 = (byte) msg.optInt(SteamConstant.powerCtrlKey);
                        buf.put(powerCtrlKey2);
                        byte powerCtrlLength2 = (byte) msg.optInt(SteamConstant.powerCtrlLength);
                        buf.put(powerCtrlLength2);
                        byte powerCtrlKeyValue2 = (byte) msg.optInt(SteamConstant.powerCtrl);
                        buf.put(powerCtrlKeyValue2);
                        //一体机工作控制
                        byte SteameOvenStatus_Key2 = (byte) msg.optInt(SteamConstant.workCtrlKey);
                        buf.put(SteameOvenStatus_Key2);
                        byte SteameOvenStatus_Length2 = (byte) msg.optInt(SteamConstant.workCtrlLength);
                        buf.put(SteameOvenStatus_Length2);
                        byte SteameOvenStatus2 = (byte) msg.optInt(SteamConstant.workCtrl);
                        buf.put(SteameOvenStatus2);

                        //总段数
                        byte steameOvenTotalNumberOfSegments_Key2 = (byte) msg.optInt(SteamConstant.sectionNumberKey);
                        buf.put(steameOvenTotalNumberOfSegments_Key2);
                        byte steameOvenTotalNumberOfSegments_Length2 = (byte) msg.optInt(SteamConstant.sectionNumberLength);
                        buf.put(steameOvenTotalNumberOfSegments_Length2);
                        int sectionNumberMulti = msg.optInt(SteamConstant.sectionNumber);

                        byte steameOvenTotalNumberOfSegments_Value2 = (byte) sectionNumberMulti == 1 ?  (byte) 0 :(byte) sectionNumberMulti;
                        buf.put(steameOvenTotalNumberOfSegments_Value2);

                        if (sectionNumberMulti > 0) {
                            for (int i = 0; i < sectionNumberMulti; i++) {
                                //模式
                                byte modeKey = (byte) msg.optInt(SteamConstant.modeKey + i);
                                buf.put(modeKey);
                                byte modeLength = (byte) msg.optInt(SteamConstant.modeLength + i);
                                buf.put(modeLength);
                                byte mode = (byte) msg.optInt(SteamConstant.mode + i);
                                buf.put(mode);
                                //上温度
                                byte setUpTempKey = (byte) msg.optInt(SteamConstant.setUpTempKey + i);
                                buf.put(setUpTempKey);
                                byte setUpTempLength = (byte) msg.optInt(SteamConstant.setUpTempLength + i);
                                buf.put(setUpTempLength);
                                byte setUpTemp = (byte) msg.optInt(SteamConstant.setUpTemp + i);
                                buf.put(setUpTemp);

                                //下温度
                                byte SteameOvenSetDownTemp_Key = (byte) msg.optInt(SteamConstant.setDownTempKey+ i);
                                buf.put(SteameOvenSetDownTemp_Key);
                                byte SteameOvenSetDownTemp_Length = (byte) msg.optInt(SteamConstant.setDownTempLength+ i);
                                buf.put(SteameOvenSetDownTemp_Length);
                                byte SteameOvenSetDownTemp = (byte) msg.optInt(SteamConstant.setDownTemp+ i);
                                buf.put(SteameOvenSetDownTemp);


                                //时间
                                byte setTimeKey = (byte) msg.optInt(SteamConstant.setTimeKey + i);
                                buf.put(setTimeKey);
                                byte setTimeLength = (byte) msg.optInt(SteamConstant.setTimeLength + i);
                                buf.put(setTimeLength);
                                byte setTime = (byte) msg.optInt(SteamConstant.setTime0b + i);
                                buf.put(setTime);
                                if (setTimeLength>1){
                                    buf.put((byte) msg.optInt(SteamConstant.setTime1b + i));
                                }
                                //蒸汽量

                                byte steamKey2 = (byte) msg.optInt(SteamConstant.steamKey + i);
                                buf.put(steamKey2);
                                byte steamLength2 = (byte) msg.optInt(SteamConstant.steamLength + i);
                                buf.put(steamLength2);
                                byte steam2 = (byte) msg.optInt(SteamConstant.steam + i);
                                buf.put(steam2);
                                //Log.e("结果蒸汽",steam2+"----");


                            }
                        }

                        break;
                    case SteamConstant.BS_TYPE_3:
//                           byte numberOfCategory3 = (byte) msg.optInt(SteamConstant.categoryCode);
//                           buf.put(numberOfCategory3);
                        byte number3 = (byte) msg.optInt(SteamConstant.ARGUMENT_NUMBER);
                        buf.put(number3);
                        //一体机电源控制
                        byte powerCtrlKey3 = (byte) msg.optInt(SteamConstant.powerCtrlKey);
                        buf.put(powerCtrlKey3);
                        byte powerCtrlLength3 = (byte) msg.optInt(SteamConstant.powerCtrlLength);
                        buf.put(powerCtrlLength3);
                        byte powerCtrlKeyValue3 = (byte) msg.optInt(SteamConstant.powerCtrl);
                        buf.put(powerCtrlKeyValue3);
                        //一体机工作控制
                        byte SteameOvenStatus_Key3 = (byte) msg.optInt(SteamConstant.workCtrlKey);
                        buf.put(SteameOvenStatus_Key3);
                        byte SteameOvenStatus_Length3 = (byte) msg.optInt(SteamConstant.workCtrlLength);
                        buf.put(SteameOvenStatus_Length3);
                        byte SteameOvenStatus3 = (byte) msg.optInt(SteamConstant.workCtrl);
                        buf.put(SteameOvenStatus3);

                        byte recipeIdKey = (byte) msg.optInt(SteamConstant.recipeIdKey);
                        buf.put(recipeIdKey);
                        byte recipeIdLength = (byte) msg.optInt(SteamConstant.recipeIdLength);
                        buf.put(recipeIdLength);
                        byte recipeId = (byte) msg.optInt(SteamConstant.recipeId);
                        buf.put(recipeId);
                        if (recipeIdLength>1){
                            buf.put((byte) msg.optInt(SteamConstant.recipeId01));
                        }

                        byte recipeSetMinutesKey = (byte) msg.optInt(SteamConstant.recipeSetMinutesKey);
                        buf.put(recipeSetMinutesKey);
                        byte recipeSetMinutesLength = (byte) msg.optInt(SteamConstant.recipeSetMinutesLength);
                        buf.put(recipeSetMinutesLength);
                        byte recipeSetMinutes = (byte) msg.optInt(SteamConstant.recipeSetMinutes);
                        buf.put(recipeSetMinutes);
                        if (msg.optInt(SteamConstant.recipeSetMinutesLength) > 1){
                            byte recipeSetMinutesH = (byte) msg.optInt(SteamConstant.recipeSetMinutesH);
                            buf.put(recipeSetMinutesH);
                        }

//                           byte sectionNumberKey = (byte) msg.optInt(SteamConstant.sectionNumberKey);
//                           buf.put(sectionNumberKey);
//                           byte sectionNumberLength = (byte) msg.optInt(SteamConstant.sectionNumberLength);
//                           buf.put(sectionNumberLength);
//                           byte sectionNumber = (byte) msg.optInt(SteamConstant.sectionNumber);
//                           buf.put(sectionNumber);
                        break;

                    case SteamConstant.BS_TYPE_4:
//                           byte categoryCode4 = (byte) msg.optInt(SteamConstant.categoryCode);
//                           buf.put(categoryCode4);
                        byte argumentNumber4 = (byte) msg.optInt(SteamConstant.ARGUMENT_NUMBER);
                        buf.put(argumentNumber4);
                        //烟机电源
                        byte fan_powerCtrlKey = (byte) msg.optInt(SteamConstant.fan_powerCtrlKey);
                        buf.put(fan_powerCtrlKey);
                        byte fan_powerCtrlLength = (byte) msg.optInt(SteamConstant.fan_powerCtrlLength);
                        buf.put(fan_powerCtrlLength);
                        byte fan_powerCtrl = (byte) msg.optInt(SteamConstant.fan_powerCtrl);
                        buf.put(fan_powerCtrl);

                        byte fan_gearKey = (byte) msg.optInt(SteamConstant.fan_gearKey);
                        buf.put(fan_gearKey);
                        byte fan_gearLength = (byte) msg.optInt(SteamConstant.fan_gearLength);
                        buf.put(fan_gearLength);
                        byte fan_gear = (byte) msg.optInt(SteamConstant.fan_gear);
                        buf.put(fan_gear);
                    case SteamConstant.BS_TYPE_5:
                        byte argumentNumber5 = (byte) msg.optInt(SteamConstant.ARGUMENT_NUMBER);
                        buf.put(argumentNumber5);


                        byte powerCtrlKey5 = (byte) msg.optInt(SteamConstant.powerCtrlKey);
                        buf.put(powerCtrlKey5);
                        byte powerCtrlLength5 = (byte) msg.optInt(SteamConstant.powerCtrlLength);
                        buf.put(powerCtrlLength5);
                        byte powerCtrlKeyValue5 = (byte) msg.optInt(SteamConstant.powerCtrl);
                        buf.put(powerCtrlKeyValue5);
                        //一体机工作控制
                        byte SteameOvenStatus_Key5 = (byte) msg.optInt(SteamConstant.workCtrlKey);
                        buf.put(SteameOvenStatus_Key5);
                        byte SteameOvenStatus_Length5 = (byte) msg.optInt(SteamConstant.workCtrlLength);
                        buf.put(SteameOvenStatus_Length5);
                        byte SteameOvenStatus5 = (byte) msg.optInt(SteamConstant.workCtrl);
                        buf.put(SteameOvenStatus5);

                        //段数
                        byte sectionNumberKey5 = (byte) msg.optInt(SteamConstant.sectionNumberKey);
                        buf.put(sectionNumberKey5);
                        byte sectionNumberLength5 = (byte) msg.optInt(SteamConstant.sectionNumberLength);
                        buf.put(sectionNumberLength5);
                        byte sectionNumber5 = (byte) msg.optInt(SteamConstant.sectionNumber);
                        buf.put(sectionNumber5);

                        //微波等级

                        byte microWaveLevelCtrlKey = (byte) msg.optInt(SteamConstant.microWaveLevelCtrlKey);
                        buf.put(microWaveLevelCtrlKey);
                        byte microWaveLevelLength = (byte) msg.optInt(SteamConstant.microWaveLevelLength);
                        buf.put(microWaveLevelLength);
                        byte microWaveLevelCtrl = (byte) msg.optInt(SteamConstant.microWaveLevelCtrl);
                        buf.put(microWaveLevelCtrl);
                        if (microWaveLevelLength>1){
                            buf.put((byte) msg.optInt(SteamConstant.microWaveLevelCtrl01));
                        }


//                           //微波重量
//                           byte microWaveWeightCtrlKey = (byte) msg.optInt(SteamConstant.microWaveWeightCtrlKey);
//                           buf.put(microWaveWeightCtrlKey);
//                           byte microWaveWeightLength = (byte) msg.optInt(SteamConstant.microWaveWeightLength);
//                           buf.put(microWaveWeightLength);
//                           byte microWaveWeightCtrl = (byte) msg.optInt(SteamConstant.microWaveWeightCtrl);
//                           buf.put(microWaveWeightCtrl);

                        //预定时间
                        byte orderTime5 = (byte) msg.optInt(SteamConstant.setOrderMinutesKey);
                        buf.put(orderTime5);
                        byte orderTime_Length5 = (byte) msg.optInt(SteamConstant.setOrderMinutesLength);
                        buf.put(orderTime_Length5);
                        buf.put((byte) msg.optInt(SteamConstant.setOrderMinutes01));
                        if (orderTime_Length5>1) {
                            buf.put((byte) msg.optInt(SteamConstant.setOrderMinutes02));
                        }
                        if (orderTime_Length5>2) {
                            buf.put((byte) msg.optInt(SteamConstant.setOrderMinutes03));
                        }

                        //时间
                        byte SteameOvenSetTime_Key5 = (byte) msg.optInt(SteamConstant.setTimeKey);
                        buf.put(SteameOvenSetTime_Key5);
                        byte SteameOvenSetTime_Length5 = (byte) msg.optInt(SteamConstant.setTimeLength);
                        buf.put(SteameOvenSetTime_Length5);
                        byte SteameOvenSetTime5 = (byte) msg.optInt(SteamConstant.setTime0b);
                        buf.put(SteameOvenSetTime5);
                        if (SteameOvenSetTime_Length5>1)
                            buf.put((byte) msg.optInt(SteamConstant.setTime1b));

                        break;
                    case SteamConstant.BS_TYPE_6:
                        byte argumentNumber6 = (byte) msg.optInt(SteamConstant.ARGUMENT_NUMBER);
                        buf.put(argumentNumber6);

                        byte powerCtrlKey6 = (byte) msg.optInt(SteamConstant.powerCtrlKey);
                        buf.put(powerCtrlKey6);
                        byte powerCtrlLength6 = (byte) msg.optInt(SteamConstant.powerCtrlLength);
                        buf.put(powerCtrlLength6);
                        byte powerCtrlKeyValue6 = (byte) msg.optInt(SteamConstant.powerCtrl);
                        buf.put(powerCtrlKeyValue6);

                        //一体机工作控制
                        byte SteameOvenStatus_Key6 = (byte) msg.optInt(SteamConstant.workCtrlKey);
                        buf.put(SteameOvenStatus_Key6);
                        byte SteameOvenStatus_Length6 = (byte) msg.optInt(SteamConstant.workCtrlLength);
                        buf.put(SteameOvenStatus_Length6);
                        byte SteameOvenStatus6 = (byte) msg.optInt(SteamConstant.workCtrl);
                        buf.put(SteameOvenStatus6);

                        //模式
                        byte mode6 = (byte) msg.optInt(SteamConstant.mode);
                        buf.put(mode6);
                        byte modeLength6 = (byte) msg.optInt(SteamConstant.modeLength);
                        buf.put(modeLength6);
                        byte modeLengthValue6 = (byte) msg.optInt(SteamConstant.modeKey);
                        buf.put(modeLengthValue6);
                        //段数
                        byte sectionNumberKey6 = (byte) msg.optInt(SteamConstant.sectionNumberKey);
                        buf.put(sectionNumberKey6);
                        byte sectionNumberLength6 = (byte) msg.optInt(SteamConstant.sectionNumberLength);
                        buf.put(sectionNumberLength6);
                        byte sectionNumber6 = (byte) msg.optInt(SteamConstant.sectionNumber);
                        buf.put(sectionNumber6);

                        //微波等级

                        byte microWaveLevelCtrlKey6 = (byte) msg.optInt(SteamConstant.microWaveLevelCtrlKey);
                        buf.put(microWaveLevelCtrlKey6);
                        byte microWaveLevelLength6 = (byte) msg.optInt(SteamConstant.microWaveLevelLength);
                        buf.put(microWaveLevelLength6);
                        byte microWaveLevelCtrl6 = (byte) msg.optInt(SteamConstant.microWaveLevelCtrl);
                        buf.put(microWaveLevelCtrl6);
                        if (microWaveLevelLength6>1){
                            buf.put( (byte) msg.optInt(SteamConstant.microWaveLevelCtrl01));
                        }

                        //微波重量
                        byte microWaveWeightCtrlKey6 = (byte) msg.optInt(SteamConstant.microWaveWeightCtrlKey);
                        buf.put(microWaveWeightCtrlKey6);
                        byte microWaveWeightLength6 = (byte) msg.optInt(SteamConstant.microWaveWeightLength);
                        buf.put(microWaveWeightLength6);
                        byte microWaveWeightCtrl6 = (byte) msg.optInt(SteamConstant.microWaveWeightCtrl);
                        buf.put(microWaveWeightCtrl6);


                        //预约时间
                        byte SteamOvenSetTime_Key6 = (byte) msg.optInt(SteamConstant.setOrderMinutesKey);
                        buf.put(SteamOvenSetTime_Key6);
                        byte SteamOvenSetTime_Length6 = (byte) msg.optInt(SteamConstant.setOrderMinutesLength);
                        buf.put(SteamOvenSetTime_Length6);
                        buf.put((byte) msg.optInt(SteamConstant.setOrderMinutes01));
                        if (SteamOvenSetTime_Length6>1) {
                            buf.put((byte) msg.optInt(SteamConstant.setOrderMinutes02));
                        }
                        if (SteamOvenSetTime_Length6>2) {
                            buf.put((byte) msg.optInt(SteamConstant.setOrderMinutes03));
                        }

                        break;

                    //加蒸汽 或者 旋转
                    case SteamConstant.BS_TYPE_7:
                        byte argumentNumber7 = (byte) msg.optInt(SteamConstant.ARGUMENT_NUMBER);
                        buf.put(argumentNumber7);
                        buf.put( (byte) msg.optInt(SteamConstant.steamKey));
                        buf.put((byte) msg.optInt(SteamConstant.steamLength));
                        buf.put((byte) msg.optInt(SteamConstant.steam));
                        break;
                    case SteamConstant.BS_TYPE_8:
                        byte number8 = (byte) msg.optInt(SteamConstant.ARGUMENT_NUMBER);
                        buf.put(number8);
                        //一体机电源控制
                        byte powerCtrlKey8 = (byte) msg.optInt(SteamConstant.powerCtrlKey);
                        buf.put(powerCtrlKey8);
                        byte powerCtrlLength8 = (byte) msg.optInt(SteamConstant.powerCtrlLength);
                        buf.put(powerCtrlLength8);
                        byte powerCtrlKeyValue8 = (byte) msg.optInt(SteamConstant.powerCtrl);
                        buf.put(powerCtrlKeyValue8);
                        //一体机工作控制
                        byte SteameOvenStatus_Key8 = (byte) msg.optInt(SteamConstant.workCtrlKey);
                        buf.put(SteameOvenStatus_Key8);
                        byte SteameOvenStatus_Length8 = (byte) msg.optInt(SteamConstant.workCtrlLength);
                        buf.put(SteameOvenStatus_Length8);
                        byte SteameOvenStatus8 = (byte) msg.optInt(SteamConstant.workCtrl);
                        buf.put(SteameOvenStatus8);


                        byte addTimeKey = (byte) msg.optInt(SteamConstant.addExtraTimeCtrlKey);
                        buf.put(addTimeKey);
                        byte addTimeKey8 = (byte) msg.optInt(SteamConstant.addExtraTimeCtrlLength);
                        buf.put(addTimeKey8);
                        byte addExtraTimeCtrl8 = (byte) msg.optInt(SteamConstant.addExtraTimeCtrl);
                        buf.put(addExtraTimeCtrl8);
                        if (msg.has(SteamConstant.addExtraTimeCtrl1)) {
                            byte addExtraTimeCtrl18 = (byte) msg.optInt(SteamConstant.addExtraTimeCtrl1);
                            buf.put(addExtraTimeCtrl18);
                        }
                        break;
                    default:
                        break;

                }

                break;
            case MsgKeys.setDeviceAttribute_Rep:
                buf.put((byte) 1);
                buf.put((byte) 0);
                break;
            case MsgKeys.getSteameOvenStatus_Req:
                break;
            case MsgKeys.getDeviceAttribute_Req: //属性查询
                buf.put((byte) 0x00);
                break;
        }
    }


    @Override
    protected void onDecodeMsg(MqttMsg msg, byte[] payload, int offset) throws Exception {
        //从payload中取值角标
        switch (msg.getID()) {
            case MsgKeys.getDeviceAttribute_Req:
                break;
            case MsgKeys.getDeviceAttribute_Rep: { //属性查询响应
                //属性个数
                int attributeNum = MsgUtils.getByte(payload[offset]);
                offset ++;
                while (attributeNum > 0) {
                    attributeNum--;
                    int key = MsgUtils.getByte(payload[offset]);
                    offset++;
                    int length = MsgUtils.getByte(payload[offset]);
                    offset++;
                    int value = 0; //支持1 2 4字节值
                    if (length == 1) {
                        value = MsgUtils.getByte(payload[offset]);
                        offset++;
                    } else if (length == 2) {
                        value = MsgUtils.bytes2ShortLittle(payload, offset);
                        offset += 2;
                    } else if (length == 4) {
                        value = MsgUtils.bytes2IntLittle(payload, offset);
                        offset += 4;
                    } else {
                        offset += length;
                        continue;  //错误的属性
                    }
                    switch (key) {
                        case QualityKeys.powerState:
                            msg.putOpt(SteamConstant.powerState, value);
                            break;
                        case QualityKeys.powerCtrl:
                            msg.putOpt(SteamConstant.powerCtrl, value);
                            break;
                        case QualityKeys.workState: //工作状态
                            msg.putOpt(SteamConstant.SteameOvenStatus, value);
                            break;
                        case QualityKeys.faultCode: //故障码
                            msg.putOpt(SteamConstant.SteamFaultCode,value);
                            break;
                        case QualityKeys.totalRemainSeconds: //总剩余时间
                            msg.putOpt(SteamConstant.SteameOvenLeftTime, value);
                            break;
                        case QualityKeys.rotateSwitch:
                            msg.putOpt(SteamConstant.rotateSwitch, value);
                            break;
                        case QualityKeys.waterBoxState:
                            msg.putOpt(SteamConstant.waterBoxState, value);
                            break;
                        case QualityKeys.waterLevelState:
                            msg.putOpt(SteamConstant.waterLevelState, value);
                            break;
                        case QualityKeys.doorState:
                            msg.putOpt(SteamConstant.doorState, value);
                            break;
                        case QualityKeys.steamState:
                            msg.putOpt(SteamConstant.steamState, value);
                            break;
                        case QualityKeys.recipeId:
                            msg.putOpt(SteamConstant.recipeId, value);
                            break;
                        case QualityKeys.recipeSetSecs:
                            msg.putOpt(SteamConstant.recipeSetMinutes, value);
                            break;
                        case QualityKeys.curTemp:
                            msg.putOpt(SteamConstant.curTemp, value);
                            break;
                        case QualityKeys.curTemp2:
                            msg.putOpt(SteamConstant.curTemp2, value);
                            break;

//                        case QualityKeys.totalRemainSeconds:
//                            short totalRemainSeconds = ByteUtils.toShort(payload[offset]);
//                            msg.putOpt(SteamConstant.totalRemainSeconds, totalRemainSeconds);
//                            short totalRemainSeconds2 = ByteUtils.toShort(payload[offset+1]);
//                            msg.putOpt(SteamConstant.totalRemainSeconds2, totalRemainSeconds2);
//                            //获取总剩余时间
//                            byte[]  totalRemains = new byte[length];
//                            for (int i = 0 ; i < length ; i ++ ){
//                                short recipeTimeMinu = ByteUtils.toShort(payload[offset]);
//                                totalRemains[i] = (byte) recipeTimeMinu ;
//                                offset ++ ;
//                            }
//                            int totalRemain = ByteUtils.byteToInt2(totalRemains);
//                            msg.putOpt(SteamConstant.totalRemain, totalRemain);
//                            break;
                        case QualityKeys.descaleFlag:
                            msg.putOpt(SteamConstant.descaleFlag, value);
                            break;
                        case QualityKeys.curSteamTotalHours:
                            msg.putOpt(SteamConstant.curSteamTotalHours, value);
                            break;
                        case QualityKeys.curSteamTotalNeedHours:
                            msg.putOpt(SteamConstant.curSteamTotalNeedHours, value);
                            break;
                        case QualityKeys.cookedTime:
                            msg.putOpt(SteamConstant.cookedTime, value);
                            break;
                        case QualityKeys.descaleIndex:
                            msg.putOpt(SteamConstant.chugouType, value);
                            break;
                        case QualityKeys.curSectionNbr:
                            msg.putOpt(SteamConstant.curSectionNbr, value);
                            break;
                        case QualityKeys.sectionNumber:
                            msg.putOpt(SteamConstant.sectionNumber, value);
                            break;
                        case QualityKeys.mode:
                            msg.putOpt(SteamConstant.mode, value);
                            break;
                        case QualityKeys.setUpTemp:
                            msg.putOpt(SteamConstant.setUpTemp, value);
                            break;
                        case QualityKeys.setDownTemp:
                            msg.putOpt(SteamConstant.setDownTemp, value);
                            break;
                        case QualityKeys.setTime:
//                            short setTime = ByteUtils.toShort(payload[offset]);
//                            msg.putOpt(SteamConstant.setTime, setTime);
//                            short setTimeH = ByteUtils.toShort(payload[offset+1]);
//                            msg.putOpt(SteamConstant.setTimeH, setTimeH);
                            msg.putOpt(SteamConstant.setTime, value);
                            break;
                        case QualityKeys.restTime:
//                            short restTime = ByteUtils.toShort(payload[offset]);
//                            msg.putOpt(SteamConstant.restTime, restTime);
//                            short restTimeH = ByteUtils.toShort(payload[offset+1]);
//                            msg.putOpt(SteamConstant.restTimeH, restTimeH);
                            msg.putOpt(SteamConstant.restTime, value);
                            break;
                        case QualityKeys.steam:
                            msg.putOpt(SteamConstant.steam, value);
                            break;
                        case QualityKeys.mode2:
                            msg.putOpt(SteamConstant.mode2, value);
                            break;
                        case QualityKeys.setUpTemp2:
                            msg.putOpt(SteamConstant.setUpTemp2, value);
                            break;
                        case QualityKeys.setDownTemp2:
                            msg.putOpt(SteamConstant.setDownTemp2, value);
                            break;
                        case QualityKeys.setTime2:
//                            short setTime1 = ByteUtils.toShort(payload[offset]);
//                            msg.putOpt(SteamConstant.setTime2, setTime1);
//                            short setTimeH2 = ByteUtils.toShort(payload[offset+1]);
//                            msg.putOpt(SteamConstant.setTimeH2, setTimeH2);
                            msg.putOpt(SteamConstant.setTime2, value);
                            break;
                        case QualityKeys.restTime2:
//                            short restTime1 = ByteUtils.toShort(payload[offset]);
//                            msg.putOpt(SteamConstant.restTime2, restTime1);
//                            short restTimeH2 = ByteUtils.toShort(payload[offset+1]);
//                            msg.putOpt(SteamConstant.restTimeH2, restTimeH2);
//                            Log.e("时间--",restTime1+"---"+restTimeH2);
                            msg.putOpt(SteamConstant.restTime2, value);
                            break;
                        case QualityKeys.steam2:
                            msg.putOpt(SteamConstant.steam2, value);
                            break;
                        case QualityKeys.mode3:
                            msg.putOpt(SteamConstant.mode3, value);
                            break;
                        case QualityKeys.setUpTemp3:
                            msg.putOpt(SteamConstant.setUpTemp3, value);
                            break;
                        case QualityKeys.setDownTemp3:
                            msg.putOpt(SteamConstant.setDownTemp3, value);
                            break;
                        case QualityKeys.setTime3:
//                            short setTime2 = ByteUtils.toShort(payload[offset]);
//                            msg.putOpt(SteamConstant.setTime3, setTime2);
//                            short setTimeH3 = ByteUtils.toShort(payload[offset+1]);
//                            msg.putOpt(SteamConstant.setTimeH3, setTimeH3);
                            msg.putOpt(SteamConstant.setTime3, value);
                            break;
                        case QualityKeys.restTime3:
//                            short restTime2 = ByteUtils.toShort(payload[offset]);
//                            msg.putOpt(SteamConstant.restTime3, restTime2);
//                            short restTimeH3 = ByteUtils.toShort(payload[offset+1]);
//                            msg.putOpt(SteamConstant.restTimeH3, restTimeH3);
                            msg.putOpt(SteamConstant.restTime3, value);
                            break;
                        case QualityKeys.steam3:
                            msg.putOpt(SteamConstant.steam3, value);
                            break;
                    }
                }
            }
                break;
            case MsgKeys.setDeviceAttribute_Rep://属性设置响应
                //设置完成立即查询
                SteamAbstractControl.getInstance().queryAttribute(msg.getGuid());
                break;
            case MsgKeys.getDeviceEventReport://事件上报
                //属性个数
                int attributeNum = MsgUtils.getByte(payload[offset]);
                offset++;
                break;
            case MsgKeys.getDeviceAlarmEventReport:
                short code = ByteUtils.toShort(payload[offset++]);
                msg.putOpt(SteamConstant.faultCode, code);
                break;
            case MsgKeys.setSteameOvenStatusControl_Rep:
                msg.putOpt(SteamConstant.RC,
                        ByteUtils.toShort(payload[offset++]));
                break;
            case MsgKeys.SteameOvenAlarm_Noti:
                msg.putOpt(SteamConstant.SteameOvenAlarm, ByteUtils.toShort(payload[offset++]));
                msg.putOpt(SteamConstant.ArgumentNumber, ByteUtils.toShort(payload[offset++]));
                break;
            case MsgKeys.setSteameOvenBasicMode_Rep://一体机基本模式回应
                msg.putOpt(SteamConstant.RC, ByteUtils.toShort(payload[offset++]));
                break;
            case MsgKeys.setTheRecipe_Rep://一体机菜谱设置回
                msg.putOpt(SteamConstant.RC, ByteUtils.toShort(payload[offset++]));
                break;
            case MsgKeys.setSteameOvenFloodlight_Rep://一体机照明灯回应
                msg.putOpt(SteamConstant.RC, ByteUtils.toShort(payload[offset++]));
                break;
            case MsgKeys.setSteameOvenWater_Rep://水箱回应
                msg.putOpt(SteamConstant.RC, ByteUtils.toShort(payload[offset++]));
                break;
            case MsgKeys.setSteameOvensteam_Rep://一体机加蒸汽回应
                msg.putOpt(SteamConstant.RC, ByteUtils.toShort(payload[offset++]));
                break;
            case MsgKeys.setSteameOvenMultistageCooking_Rep://一体机多段烹饪回应
                msg.putOpt(SteamConstant.RC, ByteUtils.toShort(payload[offset++]));
                break;
            case MsgKeys.setSteameOvenAutoRecipeMode610_Rep://一体机610多段烹饪回应
                msg.putOpt(SteamConstant.RC, ByteUtils.toShort(payload[offset++]));
                break;
            case MsgKeys.setSteameOvenAutoRecipeMode_Rep:
                msg.putOpt(SteamConstant.RC, ByteUtils.toShort(payload[offset++]));
                break;
        }

    }

    /**
     * 命令备份方法
     * @param msg
     * @param payload
     * @param offset
     * @throws Exception
     */
    protected void onDecodeMsgBack(MqttMsg msg, byte[] payload, int offset) throws Exception {
        switch (msg.getID()) {
            /**
             * 属性查询响应
             */
            case MsgKeys.getDeviceAttribute_Rep:
                short arg = ByteUtils.toShort(payload[offset++]);
                msg.putOpt(SteamConstant.ArgumentNumber, arg);
                while (arg > 0) {
                    short key620 = ByteUtils.toShort(payload[offset++]);
                    short steamOvenHeader_Length = ByteUtils.toShort(payload[offset++]);
                    switch (key620){
                        case 1:
                            short powerState = ByteUtils.toShort(payload[offset]);
                            msg.putOpt(SteamConstant.powerState, powerState);
                            break;
                        case 2:
                            short powerCtrl = ByteUtils.toShort(payload[offset]);
                            msg.putOpt(SteamConstant.powerCtrl, powerCtrl);
                            break;
                        case 3:
                            short workState = ByteUtils.toShort(payload[offset]);
                            msg.putOpt(SteamConstant.workState, workState);
                            msg.putOpt(SteamConstant.SteameOvenStatus, workState);
                            break;
                        case 6:
                            short orderLeftMinutes = ByteUtils.toShort(payload[offset]);
                            msg.putOpt(SteamConstant.orderLeftMinutes, orderLeftMinutes);
                            msg.putOpt(SteamConstant.orderMinutesLength,steamOvenHeader_Length);

                            byte[]  orderTimes = new byte[steamOvenHeader_Length];
                            for (int i = 0 ; i < steamOvenHeader_Length ; i ++ ){
                                short orderLeftMinute = ByteUtils.toShort(payload[offset]);
                                orderTimes[i] = (byte) orderLeftMinute ;
                                offset ++ ;
                            }
                            int orderLeftTime = ByteUtils.byteToInt2(orderTimes);
                            msg.putOpt(SteamConstant.orderLeftTime, orderLeftTime);
                            offset -= steamOvenHeader_Length ;
                            break;
                        case 7:
                            short faultCode = ByteUtils.toShort(payload[offset]);
                            msg.putOpt(SteamConstant.faultCode, faultCode);
                            break;
                        case 9:
                            short rotateSwitch = ByteUtils.toShort(payload[offset]);
                            msg.putOpt(SteamConstant.rotateSwitch, rotateSwitch);
                            break;
                        case 10:
                            short waterBoxState = ByteUtils.toShort(payload[offset]);
                            msg.putOpt(SteamConstant.waterBoxState, waterBoxState);
                            break;
                        case 12:
                            short waterLevelState = ByteUtils.toShort(payload[offset]);
                            msg.putOpt(SteamConstant.waterLevelState, waterLevelState);
                            break;
                        case 13:
                            short doorState = ByteUtils.toShort(payload[offset]);
                            msg.putOpt(SteamConstant.doorState, doorState);
                            break;
                        case 15:
                            short steamState = ByteUtils.toShort(payload[offset]);
                            msg.putOpt(SteamConstant.steamState, steamState);
                            break;
                        case 17:
                            short recipeId = ByteUtils.toShort(payload[offset]);
                            msg.putOpt(SteamConstant.recipeId, recipeId);
                            break;
                        case 18:

//                            short recipeSetMinutes = ByteUtils.toShort(payload[offset]);
//                            msg.putOpt(SteamConstant.recipeSetMinutes, recipeSetMinutes);
                            byte[]  recipeTimes = new byte[steamOvenHeader_Length];
                            for (int i = 0 ; i < steamOvenHeader_Length ; i ++ ){
                                short recipeTimeMinu = ByteUtils.toShort(payload[offset]);
                                recipeTimes[i] = (byte) recipeTimeMinu ;
                                offset ++ ;
                            }
                            int recipeTime = ByteUtils.byteToInt2(recipeTimes);
                            msg.putOpt(SteamConstant.recipeSetMinutes, recipeTime);
                            offset -= steamOvenHeader_Length ;
                            break;
                        case 19:
                            short curTemp = ByteUtils.toShort(payload[offset]);
                            msg.putOpt(SteamConstant.curTemp, curTemp);
                            break;
                        case 20:
                            short curTemp2 = ByteUtils.toShort(payload[offset]);
                            msg.putOpt(SteamConstant.curTemp2, curTemp2);
                            break;

                        case 21:
                            short totalRemainSeconds = ByteUtils.toShort(payload[offset]);
                            msg.putOpt(SteamConstant.totalRemainSeconds, totalRemainSeconds);
                            short totalRemainSeconds2 = ByteUtils.toShort(payload[offset+1]);
                            msg.putOpt(SteamConstant.totalRemainSeconds2, totalRemainSeconds2);
                            //LogUtils.i("testTime", "--------" + totalRemainSeconds + "-----" + totalRemainSeconds2);

                            //获取总剩余时间
                            byte[]  totalRemains = new byte[steamOvenHeader_Length];
                            for (int i = 0 ; i < steamOvenHeader_Length ; i ++ ){
                                short recipeTimeMinu = ByteUtils.toShort(payload[offset]);
                                totalRemains[i] = (byte) recipeTimeMinu ;
                                offset ++ ;
                            }
                            int totalRemain = ByteUtils.byteToInt2(totalRemains);
                            msg.putOpt(SteamConstant.totalRemain, totalRemain);
                            offset -= steamOvenHeader_Length ;
                            break;
                        case 22:
                            short descaleFlag = ByteUtils.toShort(payload[offset]);
                            msg.putOpt(SteamConstant.descaleFlag, descaleFlag);
                            break;
                        case 23:
                            short curSteamTotalHours = ByteUtils.toShort(payload[offset]);
                            msg.putOpt(SteamConstant.curSteamTotalHours, curSteamTotalHours);

                            break;
                        case 24:
                            short curSteamTotalNeedHours = ByteUtils.toShort(payload[offset]);
                            msg.putOpt(SteamConstant.curSteamTotalNeedHours, curSteamTotalNeedHours);

                            break;
                        case 25:
                            short cookedTime = ByteUtils.toShort(payload[offset]);
                            msg.putOpt(SteamConstant.cookedTime, cookedTime);
                            break;
                        case 26:
                            short chugouType = ByteUtils.toShort(payload[offset]);
                            msg.putOpt(SteamConstant.chugouType, chugouType);
                            break;
                        case 99:
                            short curSectionNbr = ByteUtils.toShort(payload[offset]);
                            msg.putOpt(SteamConstant.curSectionNbr, curSectionNbr);
                            break;
                        case 100:
                            short sectionNumber = ByteUtils.toShort(payload[offset]);
                            msg.putOpt(SteamConstant.sectionNumber, sectionNumber);
                            break;
                        case 101:
                            short mode = ByteUtils.toShort(payload[offset]);
                            msg.putOpt(SteamConstant.mode, mode);
                            Log.e("模式11",mode+"--MsgMar");
                            break;
                        case 102:
                            short setUpTemp = ByteUtils.toShort(payload[offset]);
                            msg.putOpt(SteamConstant.setUpTemp, setUpTemp);
                            break;
                        case 103:
                            short setDownTemp = ByteUtils.toShort(payload[offset]);
                            msg.putOpt(SteamConstant.setDownTemp, setDownTemp);
                            break;
                        case 104:
                            short setTime = ByteUtils.toShort(payload[offset]);
                            msg.putOpt(SteamConstant.setTime, setTime);
                            short setTimeH = ByteUtils.toShort(payload[offset+1]);
                            msg.putOpt(SteamConstant.setTimeH, setTimeH);
                            break;
                        case 105:
                            short restTime = ByteUtils.toShort(payload[offset]);
                            msg.putOpt(SteamConstant.restTime, restTime);
                            short restTimeH = ByteUtils.toShort(payload[offset+1]);
                            msg.putOpt(SteamConstant.restTimeH, restTimeH);
                            break;
                        case 106:
                            short steam = ByteUtils.toShort(payload[offset]);
                            msg.putOpt(SteamConstant.steam, steam);
                            break;
                        case 111:
                            short mode1 = ByteUtils.toShort(payload[offset]);
                            msg.putOpt(SteamConstant.mode2, mode1);
                            break;
                        case 112:
                            short setUpTemp1 = ByteUtils.toShort(payload[offset]);
                            msg.putOpt(SteamConstant.setUpTemp2, setUpTemp1);
                            break;
                        case 113:
                            short setDownTemp1 = ByteUtils.toShort(payload[offset]);
                            msg.putOpt(SteamConstant.setDownTemp2, setDownTemp1);
                            break;
                        case 114:
                            short setTime1 = ByteUtils.toShort(payload[offset]);
                            msg.putOpt(SteamConstant.setTime2, setTime1);
                            short setTimeH2 = ByteUtils.toShort(payload[offset+1]);
                            msg.putOpt(SteamConstant.setTimeH2, setTimeH2);
                            break;
                        case 115:
                            short restTime1 = ByteUtils.toShort(payload[offset]);
                            msg.putOpt(SteamConstant.restTime2, restTime1);
                            short restTimeH2 = ByteUtils.toShort(payload[offset+1]);
                            msg.putOpt(SteamConstant.restTimeH2, restTimeH2);
                            Log.e("时间--",restTime1+"---"+restTimeH2);
                            break;
                        case 116:
                            short steam1 = ByteUtils.toShort(payload[offset]);
                            msg.putOpt(SteamConstant.steam2, steam1);
                            break;
                        case 121:
                            short mode2 = ByteUtils.toShort(payload[offset]);
                            msg.putOpt(SteamConstant.mode3, mode2);
                            break;
                        case 122:
                            short setUpTemp2 = ByteUtils.toShort(payload[offset]);
                            msg.putOpt(SteamConstant.setUpTemp3, setUpTemp2);
                            break;
                        case 123:
                            short setDownTemp2 = ByteUtils.toShort(payload[offset]);
                            msg.putOpt(SteamConstant.setDownTemp3, setDownTemp2);

                            break;
                        case 124:
                            short setTime2 = ByteUtils.toShort(payload[offset]);
                            msg.putOpt(SteamConstant.setTime3, setTime2);
                            short setTimeH3 = ByteUtils.toShort(payload[offset+1]);
                            msg.putOpt(SteamConstant.setTimeH3, setTimeH3);
                            break;
                        case 125:
                            short restTime2 = ByteUtils.toShort(payload[offset]);
                            msg.putOpt(SteamConstant.restTime3, restTime2);
                            short restTimeH3 = ByteUtils.toShort(payload[offset+1]);
                            msg.putOpt(SteamConstant.restTimeH3, restTimeH3);
                            break;
                        case 126:
                            short steam2 = ByteUtils.toShort(payload[offset]);
                            msg.putOpt(SteamConstant.steam3, steam2);
                            break;
                        default:
//                            offset += steamOvenHeader_Length;
                            break;
                    }
                    offset += steamOvenHeader_Length;
                    arg--;
                }
                break;
            case MsgKeys.setDeviceAttribute_Rep:
                short arg1 = ByteUtils.toShort(payload[offset++]);
                break;

            case MsgKeys.getDeviceAlarmEventReport:
                short code = ByteUtils.toShort(payload[offset++]);

                msg.putOpt(SteamConstant.faultCode, code);
                break;


            case MsgKeys.getDeviceEventReport:
                break;
            case MsgKeys.setSteameOvenStatusControl_Rep:
                msg.putOpt(SteamConstant.RC,
                        ByteUtils.toShort(payload[offset++]));
                break;
            case MsgKeys.getSteameOvenStatus_Rep://一体机状态查询回应

                msg.putOpt(SteamConstant.SteameOvenStatus,
                        ByteUtils.toShort(payload[offset++]));
                msg.putOpt(SteamConstant.SteameOvenPowerOnStatus,
                        ByteUtils.toShort(payload[offset++]));
                msg.putOpt(SteamConstant.SteameOvenWorknStatus,
                        ByteUtils.toShort(payload[offset++]));
                msg.putOpt(SteamConstant.SteameOvenAlarm,
                        ByteUtils.toShort(payload[offset++]));
                msg.putOpt(SteamConstant.SteameOvenMode,
                        ByteUtils.toShort(payload[offset++]));
                msg.putOpt(SteamConstant.SteameOvenTemp,
                        ByteUtils.toShort(payload[offset++]));
                msg.putOpt(SteamConstant.SteameOvenLeftTime,
                        ByteUtils.toInt16(payload, offset++, BYTE_ORDER));
                offset++ ;
                msg.putOpt(SteamConstant.SteameOvenLight,
                        ByteUtils.toShort(payload[offset++]));
                msg.putOpt(SteamConstant.SteameOvenWaterStatus,
                        ByteUtils.toShort(payload[offset++]));

                msg.putOpt(SteamConstant.SteameOvenSetTemp,
                        ByteUtils.toShort(payload[offset++]));

                msg.putOpt(SteamConstant.SteameOvenSetTime,
                        ByteUtils.toShort(payload[offset++]));

                msg.putOpt(SteamConstant.SteameOvenOrderTime_min,
                        ByteUtils.toShort(payload[offset++]));
                msg.putOpt(SteamConstant.SteameOvenOrderTime_hour,
                        ByteUtils.toShort(payload[offset++]));

                msg.putOpt(SteamConstant.SteameOvenRecipeId,
                        ByteUtils.toShort(payload[offset++]));
                offset++;

                msg.putOpt(SteamConstant.SteameOvenRecipesteps,
                        ByteUtils.toShort(payload[offset++]));

                msg.putOpt(SteamConstant.SteameOvenSetDownTemp,
                        ByteUtils.toShort(payload[offset++]));
                msg.putOpt(SteamConstant.SteameOvenDownTemp,
                        ByteUtils.toShort(payload[offset++]));
                msg.putOpt(SteamConstant.SteameOvenSteam,
                        ByteUtils.toShort(payload[offset++]));

                msg.putOpt(SteamConstant.steameOvenTotalNumberOfSegments_Key,
                        ByteUtils.toShort(payload[offset++]));
                msg.putOpt(SteamConstant.SteameOvenSectionOfTheStep_Key,
                        ByteUtils.toShort(payload[offset++]));
                msg.putOpt(SteamConstant.SteameOvenPreFlag,
                        ByteUtils.toShort(payload[offset++]));
                msg.putOpt(SteamConstant.SteameOvenModelType,//菜谱种类
                        ByteUtils.toShort(payload[offset++]));

                short argument = ByteUtils.toShort(payload[offset++]);
                msg.putOpt(SteamConstant.ArgumentNumber, argument);

                while (argument > 0) {
                    short argumentKey = ByteUtils.toShort(payload[offset++]);
                    switch (argumentKey) {
                        case 3:
                            msg.putOpt(SteamConstant.CpStepKey,
                                    argumentKey);
                            msg.putOpt(SteamConstant.CpStepLength,
                                    ByteUtils.toShort(payload[offset++]));
                            msg.putOpt(SteamConstant.CpStepValue,
                                    ByteUtils.toShort(payload[offset++]));
                            break;
                        case 4:
                            msg.putOpt(SteamConstant.SteamKey,
                                    argumentKey);
                            msg.putOpt(SteamConstant.SteamLength,
                                    ByteUtils.toShort(payload[offset++]));
                            msg.putOpt(SteamConstant.SteamValue,
                                    ByteUtils.toShort(payload[offset++]));
                            break;
                        case 5:
                            msg.putOpt(SteamConstant.MultiStepCookingStepsKey,
                                    argumentKey);
                            msg.putOpt(SteamConstant.MultiStepCookingStepsLength,
                                    ByteUtils.toShort(payload[offset++]));
                            msg.putOpt(SteamConstant.MultiStepCookingStepsValue,
                                    ByteUtils.toShort(payload[offset++]));
                            break;
                        case 6:
                            msg.putOpt(SteamConstant.SteamOvenAutoRecipeMode,
                                    argumentKey);
                            msg.putOpt(SteamConstant.SteamOvenAutoRecipeModeLength,
                                    ByteUtils.toShort(payload[offset++]));
                            msg.putOpt(SteamConstant.AutoRecipeModeValue,
                                    ByteUtils.toShort(payload[offset++]));
                            break;
                        case 7:
                            msg.putOpt(SteamConstant.MultiStepCurrentStepsKey,
                                    argumentKey);
                            msg.putOpt(SteamConstant.MultiStepCurrentStepsLength,
                                    ByteUtils.toShort(payload[offset++]));
                            msg.putOpt(SteamConstant.MultiStepCurrentStepsValue,
                                    ByteUtils.toShort(payload[offset++]));
                            break;
                        case 8:
                            msg.putOpt(SteamConstant.SteameOvenPreFlagKey,
                                    argumentKey);
                            msg.putOpt(SteamConstant.SteameOvenPreFlagLength,
                                    ByteUtils.toShort(payload[offset++]));
                            msg.putOpt(SteamConstant.SteameOvenPreFlagValue,
                                    ByteUtils.toShort(payload[offset++]));
                            break;
                        case 9:
                            msg.putOpt(SteamConstant.weatherDescalingKey,
                                    argumentKey);
                            msg.putOpt(SteamConstant.weatherDescalingLength,
                                    ByteUtils.toShort(payload[offset++]));
                            msg.putOpt(SteamConstant.weatherDescalingValue,
                                    ByteUtils.toShort(payload[offset++]));
                            break;
                        case 10:
                            msg.putOpt(SteamConstant.doorStatusKey,
                                    argumentKey);
                            msg.putOpt(SteamConstant.doorStatusLength,
                                    ByteUtils.toShort(payload[offset++]));
                            msg.putOpt(SteamConstant.doorStatusValue,
                                    ByteUtils.toShort(payload[offset++]));
                            break;
                        case 11:
                            msg.putOpt(SteamConstant.time_H_key,
                                    argumentKey);
                            msg.putOpt(SteamConstant.time_H_length,
                                    ByteUtils.toShort(payload[offset++]));
                            msg.putOpt(SteamConstant.time_H_Value,
                                    ByteUtils.toShort(payload[offset++]));
                            break;
                        case 12:
//                            msg.putOpt(SteamConstant.time_H_key,
//                                    argumentKey);
//                            msg.putOpt(SteamConstant.time_H_length,
//                                    ByteUtils.toShort(payload[offset++]));
                            offset++ ;
                            msg.putOpt(SteamConstant.SteameOvenLeftMin,
                                    ByteUtils.toShort(payload[offset++]));
                            msg.putOpt(SteamConstant.SteameOvenLeftHours,
                                    ByteUtils.toShort(payload[offset++]));
                            break;
                    }
                    argument--;
                }


                break;
            case MsgKeys.SteameOvenAlarm_Noti:
                msg.putOpt(SteamConstant.SteameOvenAlarm,
                        ByteUtils.toShort(payload[offset++]));
                msg.putOpt(SteamConstant.ArgumentNumber,
                        ByteUtils.toShort(payload[offset++]));
                break;
            case MsgKeys.SteameOven_Noti://工作事件上报
                short eventId = ByteUtils.toShort(payload[offset++]);
                msg.putOpt(SteamConstant.EventId, eventId);
                msg.putOpt(SteamConstant.UserId, MsgUtils.getString(payload, offset, 10));
                offset += 10;
                arg = ByteUtils.toShort(payload[offset++]);
                msg.putOpt(SteamConstant.ArgumentNumber, arg);
                while (arg > 0) {
                    short arg_key = ByteUtils.toShort(payload[offset++]);
                    switch (arg_key) {
                        //设置的基本模式
                        case 1:
                            msg.putOpt(SteamConstant.setSteameOvenBasicMode_Key,
                                    arg_key);
                            msg.putOpt(SteamConstant.setSteameOvenBasicMode_Length,
                                    ByteUtils.toShort(payload[offset++]));
                            msg.putOpt(SteamConstant.setSteameOvenBasicMode_value,
                                    ByteUtils.toShort(payload[offset++]));
                            break;
                        //设置的温度
                        case 2:
                            msg.putOpt(SteamConstant.SteameOvenSetTemp,
                                    arg_key);
                            msg.putOpt(SteamConstant.SteameOvenSetTemp_Length,
                                    ByteUtils.toShort(payload[offset++]));
//                            offset++;
                            msg.putOpt(SteamConstant.SteameOvenSetTemp_Value,
                                    ByteUtils.toShort(payload[offset++]));
//                            offset++;
                            break;
                        //设置的时间
                        case 3:
                            msg.putOpt(SteamConstant.SteameOvenSetTime,
                                    arg_key);
                            msg.putOpt(SteamConstant.SteameOvenSetTime_Length,
                                    ByteUtils.toShort(payload[offset++]));
//                            offset++;
                            msg.putOpt(SteamConstant.SteameOvenSetTime_Value,
                                    ByteUtils.toShort(payload[offset++]));
//                            offset++;
                            break;
                        //设置的下温度
                        case 4:
                            msg.putOpt(SteamConstant.SteameOvenSetDownTemp,
                                    arg_key);
                            msg.putOpt(SteamConstant.SteameOvenSetDownTemp_Lenght,
                                    ByteUtils.toShort(payload[offset++]));
//                            offset++;
                            msg.putOpt(SteamConstant.SteameOvenSetDownTemp_Vaue,
                                    ByteUtils.toShort(payload[offset++]));
//                            offset++;
                            break;
                        //自动模式
                        case 5:
                            msg.putOpt(SteamConstant.SteameOvenCpMode,
                                    arg_key);
                            msg.putOpt(SteamConstant.SteameOvenCpMode_Length,
                                    ByteUtils.toShort(payload[offset++]));
                            msg.putOpt(SteamConstant.SteameOvenCpMode_Value,
                                    ByteUtils.toShort(payload[offset++]));
                            break;
                        //烤叉旋转
                        case 6:
                            msg.putOpt(SteamConstant.SteameOvenRevolve,
                                    arg_key);
                            msg.putOpt(SteamConstant.SteameOvenRevolve_Length,
                                    ByteUtils.toShort(payload[offset++]));
                            msg.putOpt(SteamConstant.SteameOvenRevolve_Value,
                                    ByteUtils.toShort(payload[offset++]));
                            break;
                        //水箱更改
                        case 7:
                            msg.putOpt(SteamConstant.SteameOvenWaterChanges,
                                    arg_key);
                            msg.putOpt(SteamConstant.SteameOvenWaterChanges_Length,
                                    ByteUtils.toShort(payload[offset++]));
                            msg.putOpt(SteamConstant.SteameOvenWaterChanges_Value,
                                    ByteUtils.toShort(payload[offset++]));
                            break;
                        //照明灯开关
                        case 8:
                            msg.putOpt(SteamConstant.SteameOvenLight, arg_key);
                            msg.putOpt(SteamConstant.SteameOvenLight_Length, ByteUtils.toShort(payload[offset++]));
                            msg.putOpt(SteamConstant.SteameOvenLight_Value, ByteUtils.toShort(payload[offset++]));
                            break;
                        //工作完成参数
                        case 9:

                            msg.putOpt(SteamConstant.SteameOvenWorkComplete, arg_key);
                            msg.putOpt(SteamConstant.SteameOvenWorkComplete_Length, ByteUtils.toShort(payload[offset++]));
                            short s = ByteUtils.toShort(payload[offset++]);
                            msg.putOpt(SteamConstant.SteameOvenWorkComplete_Value, s);
                            break;
                        //加蒸汽
                        case 10:
                            msg.putOpt(SteamConstant.SteameOvenSteam,
                                    arg_key);
                            msg.putOpt(SteamConstant.SteameOvenSteam_Length,
                                    ByteUtils.toShort(payload[offset++]));
                            msg.putOpt(SteamConstant.SteameOvenSteam_Value,
                                    ByteUtils.toShort(payload[offset++]));
                            break;
                        //开关事件参数
                        case 11:
                            msg.putOpt(SteamConstant.setSteameOvenSwitchControl,
                                    arg_key);
                            msg.putOpt(SteamConstant.setSteameOvenSwitchControl_Length,
                                    ByteUtils.toShort(payload[offset++]));
                            msg.putOpt(SteamConstant.setSteameOvenSwitchControl_Value,
                                    ByteUtils.toShort(payload[offset++]));
                            break;
                        //新增一体机自动菜谱上报
                        case 12:
                            msg.putOpt(SteamConstant.SteamOvenAutoRecipeMode,
                                    arg_key);
                            msg.putOpt(SteamConstant.SteamOvenAutoRecipeModeLength,
                                    ByteUtils.toShort(payload[offset++]));
                            msg.putOpt(SteamConstant.SteamOvenAutoRecipeModeValue,
                                    ByteUtils.toShort(payload[offset++]));
                            break;
                    }
                    arg--;
                }

                break;
            case MsgKeys.setSteameOvenBasicMode_Rep://一体机基本模式回应
                msg.putOpt(SteamConstant.RC,
                        ByteUtils.toShort(payload[offset++]));
                break;
            case MsgKeys.setTheRecipe_Rep://一体机菜谱设置回
                msg.putOpt(SteamConstant.RC,
                        ByteUtils.toShort(payload[offset++]));
                break;
            case MsgKeys.setSteameOvenFloodlight_Rep://一体机照明灯回应
                msg.putOpt(SteamConstant.RC,
                        ByteUtils.toShort(payload[offset++]));
                break;
            case MsgKeys.setSteameOvenWater_Rep://水箱回应
                msg.putOpt(SteamConstant.RC,
                        ByteUtils.toShort(payload[offset++]));
                break;
            case MsgKeys.setSteameOvensteam_Rep://一体机加蒸汽回应
                msg.putOpt(SteamConstant.RC,
                        ByteUtils.toShort(payload[offset++]));
                break;
            case MsgKeys.setSteameOvenMultistageCooking_Rep://一体机多段烹饪回应
                msg.putOpt(SteamConstant.RC,
                        ByteUtils.toShort(payload[offset++]));
                break;
            case MsgKeys.setSteameOvenAutoRecipeMode610_Rep://一体机610多段烹饪回应
                msg.putOpt(SteamConstant.RC,
                        ByteUtils.toShort(payload[offset++]));
                break;
            case MsgKeys.setSteameOvenAutoRecipeMode_Rep:
                msg.putOpt(SteamConstant.RC,
                        ByteUtils.toShort(payload[offset++]));
                break;
            default:

                break;
        }
    }
}

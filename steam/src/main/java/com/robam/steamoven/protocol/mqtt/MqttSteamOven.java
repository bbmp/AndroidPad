package com.robam.steamoven.protocol.mqtt;

import android.util.Log;

import com.robam.common.bean.MqttDirective;
import com.robam.common.mqtt.MqttMsg;
import com.robam.common.mqtt.MqttPublic;
import com.robam.common.mqtt.MsgKeys;
import com.robam.common.utils.ByteUtils;
import com.robam.common.utils.MsgUtils;
import com.robam.steamoven.constant.QualityKeys;
import com.robam.steamoven.constant.SteamConstant;
import com.robam.steamoven.constant.SteamStateConstant;
import com.robam.steamoven.device.SteamAbstractControl;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class MqttSteamOven extends MqttPublic {
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
                            msg.putOpt(SteamConstant.workState, value);
                            break;
                        case QualityKeys.orderLeftSecs:
                            msg.putOpt(SteamConstant.orderLeftTime, value);
                            break;
                        case QualityKeys.faultCode: //故障码
                            msg.putOpt(SteamConstant.faultCode,value);
                            break;
                        case QualityKeys.totalRemainSeconds: //总剩余时间
                            msg.putOpt(SteamConstant.totalRemainSeconds, value);
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
                        case QualityKeys.wasteWaterBox:
                            msg.putOpt(SteamConstant.wasteWaterBox, value);
                            break;
                        case QualityKeys.wasteWaterLevel:
                            msg.putOpt(SteamConstant.wasteWaterLevel, value);
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
                            msg.putOpt(SteamConstant.setTime, value);
                            break;
                        case QualityKeys.restTime:
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
                            msg.putOpt(SteamConstant.setTime2, value);
                            break;
                        case QualityKeys.restTime2:
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
                            msg.putOpt(SteamConstant.setTime3, value);
                            break;
                        case QualityKeys.restTime3:
                            msg.putOpt(SteamConstant.restTime3, value);
                            break;
                        case QualityKeys.steam3:
                            msg.putOpt(SteamConstant.steam3, value);
                            break;
                    }
                }
                if(msg.getInt(SteamConstant.workState) == SteamStateConstant.WORK_STATE_WORKING_FINISH){
                    MqttDirective.getInstance().finishWorkModelState(msg.getGuid());
                }else{
                    int recipeId = msg.optInt(SteamConstant.recipeId);
                    if(recipeId != 0){
                        int anInt = msg.getInt(SteamConstant.restTime);
                        if(anInt > 0){
                            MqttDirective.getInstance().updateModelWorkState(msg.getGuid(),0,recipeId);
                        }
                    }else{
                        int mode = msg.optInt(SteamConstant.mode);
                        if(mode != 0){
                            int anInt = msg.optInt(SteamConstant.restTime);
                            int anInt2 = msg.optInt(SteamConstant.restTime2);
                            int anInt3 = msg.optInt(SteamConstant.restTime3);
                            if((anInt + anInt2 + anInt3) > 0){
                                MqttDirective.getInstance().updateModelWorkState(msg.getGuid(),mode,0);
                            }
                        }
                    }
                }

            }
                break;
            case MsgKeys.setDeviceAttribute_Rep:
                 //属性设置响应
                 SteamAbstractControl.getInstance().queryAttribute(msg.getGuid());
                 break;
            case MsgKeys.getDeviceEventReport://事件上报
                //属性个数
                int attributeNum = MsgUtils.getByte(payload[offset]);
                offset++;
                break;
            case MsgKeys.getDeviceAlarmEventReport:{
                short code = ByteUtils.toShort(payload[offset++]);
                //msg.putOpt(SteamConstant.faultCode, code);
            }
            break;
            case MsgKeys.setSteameOvenStatusControl_Rep:
                msg.putOpt(SteamConstant.RC, ByteUtils.toShort(payload[offset++]));
                break;
            case MsgKeys.SteameOvenAlarm_Noti:
                msg.putOpt(SteamConstant.SteameOvenAlarm, ByteUtils.toShort(payload[offset++]));
                msg.putOpt(SteamConstant.ArgumentNumber, ByteUtils.toShort(payload[offset++]));
                break;

        }

    }
}

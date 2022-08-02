package com.robam.steamoven.protocol.mqtt;

import com.robam.common.mqtt.MqttMsg;
import com.robam.common.mqtt.MqttPublic;
import com.robam.common.mqtt.MsgKeys;
import com.robam.common.utils.ByteUtils;
import com.robam.steamoven.bean.SteamOven;
import com.robam.steamoven.constant.QualityKeys;
import com.robam.steamoven.device.SteamAbstractControl;
import com.robam.steamoven.device.SteamFactory;
import com.robam.steamoven.protocol.SerialToMqttHelper;

import java.nio.ByteBuffer;

public class MqttSteamOven extends MqttPublic {
    @Override
    protected void onDecodeMsg(int msgId, byte[] payload, int offset) {
//        Cq926Control cq926Control = new Cq926Control();
        //从payload中取值角标
        switch (msgId) {
            case MsgKeys.getDeviceAttribute_Req:

                break;
            case MsgKeys.setDeviceAttribute_Req:
                //属性个数
                short number = ByteUtils.toShort(payload[offset]);
                offset++;
                while (number > 0) {
                    short key =  ByteUtils.toShort(payload[offset]);
                    offset++;
                    short length =  ByteUtils.toShort(payload[offset]);
                    offset++;
                    switch (key) {
                        case QualityKeys.powerCtrl:
                            short powerCtrl =  ByteUtils.toShort(payload[offset]);
                            SteamOven.getInstance().powerCtrl = powerCtrl;
                            offset++;
                            if (powerCtrl == 0){
//                                cq926Control.shutDown();
                                //调用串口控制
                                SteamAbstractControl.getInstance().shutDown();
                                return;
                            }else {
//                                DeviceFactory.getInstance().getControlAction().powerOn();
                            }
                            break;
                        case QualityKeys.workCtrl:
                            short workCtrl =  ByteUtils.toShort(payload[offset]);
                            SteamOven.getInstance().workCtrl = workCtrl;
                            offset++;
                            break;
                        case QualityKeys.setOrderSecs:
                            byte[]  setOrderSecs = new byte[length];
                            for (int i = 0 ; i < length ; i ++ ){
                                short setOrderSec =  ByteUtils.toShort(payload[offset]);
                                setOrderSecs[i] = (byte) setOrderSec ;
                                offset ++ ;
                            }
                            int orderTime = ByteUtils.byteToInt2(setOrderSecs);
                            SteamOven.getInstance().orderTime = orderTime;
                            break;
                        case QualityKeys.lightSwitch:
                            short lightSwitch =  ByteUtils.toShort(payload[offset]);

                            offset++;
                            break;
                        case QualityKeys.rotateSwitch:
                            offset++;
                            break;
                        case QualityKeys.waterBoxCtrl:
                            short waterBoxCtrl =  ByteUtils.toShort(payload[offset]);

                            offset++;
                            break;
                        case QualityKeys.steamCtrl:
                            short steamCtrl =  ByteUtils.toShort(payload[offset]);
//                            SteamOven.getInstance().steamCtrl = steamCtrl;
                            offset++;
                            break;
                        case QualityKeys.recipeId:
                            short recipeId =  ByteUtils.toShort(payload[offset]);
//                            cq926Control.recipeId = recipeId;
                            offset++;
                            break;
                        case QualityKeys.recipeSetSecs:
                            byte[]  recipeSetSecs = new byte[length];
                            for (int i = 0 ; i < length ; i ++ ){
                                short recipeSetSec =  ByteUtils.toShort(payload[offset]);
                                recipeSetSecs[i] = (byte) recipeSetSec ;
                                offset ++ ;
                            }
                            int time = ByteUtils.byteToInt2(recipeSetSecs);
//                            cq926Control.recipeSetSecs = time;
                            break;
                        case QualityKeys.sectionNumber:
                            short sectionNumber =  ByteUtils.toShort(payload[offset]);
//                            cq926Control.sectionNumber = sectionNumber;
                            offset++;
                            break;
                        case QualityKeys.mode:
                            short mode =  ByteUtils.toShort(payload[offset]);
                            SteamOven.getInstance().mode = mode;
                            offset++;
                            break;
                        case QualityKeys.setUpTemp:
                            int setUpTemp =  ByteUtils.toShort(payload[offset]);
                            SteamOven.getInstance().setUpTemp = setUpTemp;
                            offset++;
                            break;
                        case QualityKeys.setDownTemp:
                            int setDownTemp =  ByteUtils.toShort(payload[offset]);
                            SteamOven.getInstance().setDownTemp = setDownTemp;
                            offset++;
                            break;
                        case QualityKeys.setTime:
                            byte[]  setTimeByte = new byte[length];
                            for (int i = 0 ; i < length ; i ++ ){
                                short setTime =  ByteUtils.toShort(payload[offset]);
                                setTimeByte[i] = (byte) setTime ;
                                offset ++ ;
                            }
                            int setTime = ByteUtils.byteToInt2(setTimeByte);
                            SteamOven.getInstance().setTime = setTime / 60;
                            break;
                        case QualityKeys.restTime:
                            break;
                        case QualityKeys.steam:
                            int steam = ByteUtils.toShort(payload[offset]);
                            SteamOven.getInstance().steam = steam;
                            offset++;
                            break;
                        case QualityKeys.mode2:
                            short mode2 = ByteUtils.toShort(payload[offset]);
                            SteamOven.getInstance().mode2 = mode2;
                            offset++;
                            break;
                        case QualityKeys.setUpTemp2:
                            int setUpTemp2 = ByteUtils.toShort(payload[offset]);
                            SteamOven.getInstance().setUpTemp2 = setUpTemp2;
                            offset++;
                            break;
                        case QualityKeys.setDownTemp2:
                            int setDownTemp2 = ByteUtils.toShort(payload[offset]);
                            SteamOven.getInstance().setDownTemp2 = setDownTemp2;
                            offset++;
                            break;
                        case QualityKeys.setTime2:
                            byte[]  setTime2Byte = new byte[length];
                            for (int i = 0 ; i < length ; i ++ ){
                                short setTime2 = ByteUtils.toShort(payload[offset]);
                                setTime2Byte[i] = (byte) setTime2 ;
                                offset ++ ;
                            }
                            int setTime2 = ByteUtils.byteToInt2(setTime2Byte);
                            SteamOven.getInstance().setTime2 = setTime2 / 60;
                            break;
                        case QualityKeys.restTime2:
                            break;
                        case QualityKeys.steam2:
                            int steam2 = ByteUtils.toShort(payload[offset]);
                            SteamOven.getInstance().steam2 = steam2;
                            offset++;
                            break;
                        case QualityKeys.mode3:
                            short mode3 = ByteUtils.toShort(payload[offset]);
                            SteamOven.getInstance().mode3 = mode3;
                            offset++;
                            break;
                        case QualityKeys.setUpTemp3:
                            int setUpTemp3 = ByteUtils.toShort(payload[offset]);
                            SteamOven.getInstance().setUpTemp3 = setUpTemp3;
                            offset++;
                            break;
                        case QualityKeys.setDownTemp3:
                            int setDownTemp3 = ByteUtils.toShort(payload[offset]);
                            SteamOven.getInstance().setDownTemp3 = setDownTemp3;
                            offset++;
                            break;
                        case QualityKeys.setTime3:
                            byte[]  setTime3Byte = new byte[length];
                            for (int i = 0 ; i < length ; i ++ ){
                                short setTime3 = ByteUtils.toShort(payload[offset]);
                                setTime3Byte[i] = (byte) setTime3 ;
                                offset ++ ;
                            }
                            int setTime3 = ByteUtils.byteToInt2(setTime3Byte);
                            SteamOven.getInstance().setTime3 = setTime3 / 60;
                            break;
                        case QualityKeys.restTime3:
                            break;
                        case QualityKeys.steam3:
                            int steam3 = ByteUtils.toShort(payload[offset]);
                            SteamOven.getInstance().steam3 = steam3;
                            offset++;
                            break;
                        default:
                            offset += length;
                            break;
                    }
                    number -- ;
                }
                //被控制,一体机应用时会被控制

//                SteamControl.onMqttControl();
                break;
            default:
                break;
        }
    }

    @Override
    protected void onEncodeMsg(ByteBuffer buf, MqttMsg msg) {
        int msgId = msg.getID();
        switch (msgId) {
            case MsgKeys.DeviceConnected_Noti:
                buf.put((byte) 1);
                buf.put("0000000000".getBytes());
                buf.put(SteamFactory.getPlatform().getMac().getBytes());
                buf.put(msg.getGuid().getBytes());
                buf.put((byte) SteamFactory.getPlatform().getMac().length());
                buf.put(SteamFactory.getPlatform().getMac().getBytes());
                buf.put((byte) 1);
                buf.put((byte) 4);
                buf.put((byte) 1);
                break;
            case MsgKeys.setDeviceAttribute_Rep:
                buf.put((byte) 1);
                buf.put((byte) 0);
                break;
            case MsgKeys.getDeviceAttribute_Rep:
                buf.put((byte) 38);
                //电源状态
                buf.put((byte) QualityKeys.powerState);
                buf.put((byte) 1);
                buf.put(SerialToMqttHelper.getPowerState());
                //工作状态
                buf.put((byte) QualityKeys.workState);
                buf.put((byte) 1);
                buf.put(SerialToMqttHelper.getWorkState());
                //剩余预约时间
                buf.put((byte) QualityKeys.orderLeftSecs);
                byte[] orderLeftSecs = SerialToMqttHelper.getOrderLeftSecs();
                buf.put((byte) orderLeftSecs.length);
                buf.put(SerialToMqttHelper.getOrderLeftSecs());
                //故障码
                buf.put((byte) QualityKeys.faultCode);
                buf.put((byte) 1);
                buf.put(SerialToMqttHelper.getFaultCode());
                //灯状态
                buf.put((byte) QualityKeys.lightSwitch);
                buf.put((byte) 1);
                buf.put(SerialToMqttHelper.getLampState());
                //旋转烤开关
                buf.put((byte) QualityKeys.rotateSwitch);
                buf.put((byte) 1);
                buf.put(SerialToMqttHelper.getRotateSwitch());
                //水箱状态
                buf.put((byte) QualityKeys.waterBoxState);
                buf.put((byte) 1);
                buf.put(SerialToMqttHelper.getWaterBoxState());
                //水位状态
                buf.put((byte) QualityKeys.waterLevelState);
                buf.put((byte) 1);
                buf.put(SerialToMqttHelper.getWaterLevelState());
                //门状态 13
                buf.put((byte) QualityKeys.doorState);
                buf.put((byte) 1);
                buf.put(SerialToMqttHelper.getDoorState());
                //手动加蒸汽状态 15
                buf.put((byte) QualityKeys.steamState);
                buf.put((byte) 1);
                buf.put(SerialToMqttHelper.getSteamState());
                //菜谱编号 17
                buf.put((byte) QualityKeys.recipeId);
                buf.put((byte) 1);
                buf.put(SerialToMqttHelper.getRecipeId());
                //菜谱设置总时间 18
                buf.put((byte) QualityKeys.recipeSetSecs);
                byte[] recipeSetSecs = SerialToMqttHelper.getRecipeSetSecs();
                buf.put((byte) recipeSetSecs.length);
                buf.put(recipeSetSecs);
                //当前上温度 19
                buf.put((byte) QualityKeys.curTemp);
                buf.put((byte) 1);
                buf.put(SerialToMqttHelper.getTempUp());
                //当前下温度 20
                buf.put((byte) QualityKeys.curTemp2);
                buf.put((byte) 1);
                buf.put(SerialToMqttHelper.getTempDown());
                //总剩余时间 21
                buf.put((byte) QualityKeys.totalRemainSeconds);
                byte[] totalRemainSeconds = SerialToMqttHelper.getTotalRemainSeconds();
                buf.put((byte) totalRemainSeconds.length);
                buf.put(totalRemainSeconds);
                //除垢请求标志 22
                buf.put((byte) QualityKeys.descaleFlag);
                buf.put((byte) 1);
                buf.put(SerialToMqttHelper.getDescaleFlag());
                //除垢当前段数 26
                buf.put((byte) QualityKeys.descaleIndex);
                buf.put((byte) 1);
                buf.put(SerialToMqttHelper.getLampState());
                //除垢总段数 27
                buf.put((byte) QualityKeys.descaleNum);
                buf.put((byte) 1);
                buf.put(SerialToMqttHelper.getLampState());
                //当前段数 99
                buf.put((byte) QualityKeys.curSectionNbr);
                buf.put((byte) 1);
                buf.put(SerialToMqttHelper.getCurSectionNbr());
                //设置的总段数 100
                buf.put((byte) QualityKeys.sectionNumber);
                buf.put((byte) 1);
                buf.put(SerialToMqttHelper.getSectionNumber());
                //首段模式 101
                buf.put((byte) QualityKeys.mode);
                buf.put((byte) 1);
                buf.put(SerialToMqttHelper.getMode());
                //上温度 102
                buf.put((byte) QualityKeys.setUpTemp);
                buf.put((byte) 1);
                buf.put(SerialToMqttHelper.getSetUpTemp());
                //下温度 103
                buf.put((byte) QualityKeys.setDownTemp);
                buf.put((byte) 1);
                buf.put(SerialToMqttHelper.getSetDownTemp());
                //设置时间
                buf.put((byte) QualityKeys.setTime);
                byte[] setTime = SerialToMqttHelper.getSetTime();
                buf.put((byte) setTime.length);
                buf.put(setTime);
                //剩余时间 105
                buf.put((byte) QualityKeys.restTime);
                byte[] restTime = SerialToMqttHelper.getRestTime();
                buf.put((byte) restTime.length);
                buf.put(restTime);
                //蒸汽量 106
                buf.put((byte) QualityKeys.steam);
                buf.put((byte) 1);
                buf.put(SerialToMqttHelper.getSteam());

                //二段模式 111
                buf.put((byte) QualityKeys.mode2);
                buf.put((byte) 1);
                buf.put(SerialToMqttHelper.getMode2());
                //上温度 112
                buf.put((byte) QualityKeys.setUpTemp2);
                buf.put((byte) 1);
                buf.put(SerialToMqttHelper.getSetUpTemp2());
                //下温度 113
                buf.put((byte) QualityKeys.setDownTemp2);
                buf.put((byte) 1);
                buf.put(SerialToMqttHelper.getSetDownTemp2());
                //下温度 114
                buf.put((byte) QualityKeys.setTime2);
                buf.put((byte) 1);
                buf.put(SerialToMqttHelper.getSetTime2());
                //剩余时间 115
                buf.put((byte) QualityKeys.restTime2);
                byte[] restTime2 = SerialToMqttHelper.getRestTime2();
                buf.put((byte) restTime2.length);
                buf.put(restTime2);
                //蒸汽量 116
                buf.put((byte) QualityKeys.steam2);
                buf.put((byte) 1);
                buf.put(SerialToMqttHelper.getSteam2());

                //三段模式 121
                buf.put((byte) QualityKeys.mode3);
                buf.put((byte) 1);
                buf.put(SerialToMqttHelper.getMode3());
                //上温度 122
                buf.put((byte) QualityKeys.setUpTemp3);
                buf.put((byte) 1);
                buf.put(SerialToMqttHelper.getSetUpTemp3());
                //下温度 123
                buf.put((byte) QualityKeys.setDownTemp3);
                buf.put((byte) 1);
                buf.put(SerialToMqttHelper.getSetDownTemp3());
                //下温度 124
                buf.put((byte) QualityKeys.setTime3);
                buf.put((byte) 1);
                buf.put(SerialToMqttHelper.getSetTime3());
                //剩余时间 125
                buf.put((byte) QualityKeys.restTime3);
                byte[] restTime3 = SerialToMqttHelper.getRestTime3();
                buf.put((byte) restTime3.length);
                buf.put(restTime3);
                //蒸汽量 126
                buf.put((byte) QualityKeys.steam3);
                buf.put((byte) 1);
                buf.put(SerialToMqttHelper.getSteam3());
                break;
        }
    }
}

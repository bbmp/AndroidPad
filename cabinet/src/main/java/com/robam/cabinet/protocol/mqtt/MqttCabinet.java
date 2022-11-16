package com.robam.cabinet.protocol.mqtt;

import android.util.Log;

import com.robam.cabinet.constant.CabinetConstant;
import com.robam.cabinet.constant.EventConstant;
import com.robam.cabinet.device.CabinetAbstractControl;
import com.robam.common.bean.MqttDirective;
import com.robam.common.mqtt.MqttMsg;
import com.robam.common.mqtt.MqttPublic;
import com.robam.common.mqtt.MsgKeys;
import com.robam.common.utils.ByteUtils;
import com.robam.common.utils.LogUtils;
import com.robam.common.utils.MsgUtils;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

//消毒柜mqtt
public class MqttCabinet extends MqttPublic {

    @Override
    protected void onDecodeMsg(MqttMsg msg, byte[] payload, int offset) throws Exception {
        switch (msg.getID()) {
            case MsgKeys.SetSteriLock_Rep:
            case MsgKeys.SetSteriPowerOnOff_Rep:
                msg.putOpt(CabinetConstant.RC, ByteUtils.toShort(payload[offset++]));
                CabinetAbstractControl.getInstance().queryAttribute(msg.getGuid());
                break;
            case MsgKeys.GetSteriStatus_Rep:
                short mode = ByteUtils.toShort(payload[offset++]);
                msg.putOpt(CabinetConstant.SteriStatus, mode);
                short doorState = ByteUtils.toShort(payload[offset++]);
                msg.putOpt(CabinetConstant.SteriLock, doorState);
                int workTime = ByteUtils.toInt32(payload, offset++, ByteOrder.LITTLE_ENDIAN);
                msg.putOpt(CabinetConstant.SteriWorkLeftTimeL, workTime);
                offset++;
                offset++;
                offset++;
                msg.putOpt(CabinetConstant.SteriDoorLock, ByteUtils.toShort(payload[offset++]));
                short waringCode = ByteUtils.toShort(payload[offset++]);
                msg.putOpt(CabinetConstant.SteriAlarmStatus, waringCode);
                msg.putOpt(CabinetConstant.SteriParaTem, ByteUtils.toShort(payload[offset++]));
                msg.putOpt(CabinetConstant.SteriParaHum, ByteUtils.toShort(payload[offset++]));
                msg.putOpt(CabinetConstant.SteriParaGerm, ByteUtils.toShort(payload[offset++]));
                msg.putOpt(CabinetConstant.SteriParaOzone, ByteUtils.toShort(payload[offset++]));
                short argument = ByteUtils.toShort(payload[offset++]);
                msg.putOpt(CabinetConstant.ArgumentNumber, argument);
                while (argument > 0) {
                    short argument_key = ByteUtils.toShort(payload[offset++]);
                    switch (argument_key) {
                        case 1:
                            msg.putOpt(CabinetConstant.Key, argument_key);
                            short aShort = ByteUtils.toShort(payload[offset++]);
                            msg.putOpt(CabinetConstant.Length,aShort);
                            int anInt = ByteUtils.toInt16(payload, offset++,ByteOrder.LITTLE_ENDIAN);
                            //int anInt = ByteUtils.toInt16(payload, offset++, ByteUtils.BYTE_ORDER);
                            msg.putOpt(CabinetConstant.REMAINING_APPOINT_TIME,anInt);
                            offset++;
                            break;
                        case 2:
                            msg.putOpt(CabinetConstant.Key, argument_key);
                            msg.putOpt(CabinetConstant.Length,ByteUtils.toShort(payload[offset++]));
                            msg.putOpt(CabinetConstant.SteriSecurityLock,ByteUtils.toShort(payload[offset++]));
                            break;
                        default:
                            break;

                    }
                    argument--;
                }
                if(mode != 0 && workTime != 0){//记录最新工作模式
                    LogUtils.e("MqttCabinet updateModelWorkState");
                    MqttDirective.getInstance().updateModelWorkState(msg.getGuid(),mode,0);
                }
                break;

            // 通知类
            case MsgKeys.SteriAlarm_Noti:
                short waringCodeNoti = ByteUtils.toShort(payload[offset++]);
                msg.putOpt(CabinetConstant.AlarmId, waringCodeNoti);
                MqttDirective.getInstance().getDirective().setValue((int)waringCodeNoti);
                break;
            case MsgKeys.SteriEvent_Noti:
                short eventId = ByteUtils.toShort(payload[offset++]);
                short eventParam = ByteUtils.toShort(payload[offset++]);
                msg.putOpt(CabinetConstant.EventId, eventId);
                msg.putOpt(CabinetConstant.EventParam, eventParam);
                msg.putOpt(CabinetConstant.ArgumentNumber, ByteUtils.toShort(payload[offset++]));
                msg.putOpt(CabinetConstant.UserId, MsgUtils.getString(payload, offset++, 10));
                if(eventId == EventConstant.WORK_FINISH && eventParam == 0){
                    LogUtils.e("MqttCabinet finishWorkModelState");
                    MqttDirective.getInstance().finishWorkModelState(msg.getGuid());
                    //MqttDirective.getInstance().getDirective().setValue((int) eventId);
                }
                break;
        }
    }

    @Override
    protected void onEncodeMsg(ByteBuffer buf, MqttMsg msg) {
        switch (msg.getID()) {
            case MsgKeys.SetSteriPowerOnOff_Req:
                buf.put(msg.optString(CabinetConstant.UserId).getBytes());
                buf.put((byte) msg.optInt(CabinetConstant.SteriStatus));
                short setTime = (short) msg.optInt(CabinetConstant.SteriTime);
                buf.put((byte) (setTime & 0xFF));
                buf.put((byte) ((setTime >> 8) & 0xFF));
                buf.put((byte) msg.optInt(CabinetConstant.ArgumentNumber));
                if (msg.optInt(CabinetConstant.ArgumentNumber) > 0) {
                    if (msg.optInt(CabinetConstant.warmDishKey) == 1) {
                        buf.put((byte) msg.optInt(CabinetConstant.warmDishKey));
                        buf.put((byte) msg.optInt(CabinetConstant.warmDishLength));
                        buf.put((byte) msg.optInt(CabinetConstant.warmDishTempValue));
                    }
                    if (msg.optInt(CabinetConstant.Key) == 2) {
                        buf.put((byte) msg.optInt(CabinetConstant.Key));
                        buf.put((byte) msg.optInt(CabinetConstant.Length));
                        int appointmentTime = msg.optInt(CabinetConstant.SteriReserveTime);
                        byte time1 = (byte) (appointmentTime & 0xFF);
                        byte time2 = (byte) ((appointmentTime >> 8) & 0xFF);
                        buf.put(time1);
                        buf.put(time2);
                    }
                }
                break;
            case MsgKeys.GetSteriStatus_Req:
                buf.put(msg.optString(CabinetConstant.UserId).getBytes());
                break;
            case MsgKeys.SetSteriPVConfig_Req:
                buf.put(msg.optString(CabinetConstant.UserId).getBytes());
                boolean bool = msg.optBoolean(CabinetConstant.SteriSwitchDisinfect);
                buf.put(bool ? (byte) 1 : (byte) 0);
                buf.put((byte) msg.optInt(CabinetConstant.SteriInternalDisinfect));
                bool = msg.optBoolean(CabinetConstant.SteriSwitchWeekDisinfect);
                buf.put(bool ? (byte) 1 : (byte) 0);
                buf.put((byte) msg.optInt(CabinetConstant.SteriWeekInternalDisinfect));
                buf.put((byte) msg.optInt(CabinetConstant.SteriPVDisinfectTime));
                buf.put((byte) msg.optInt(CabinetConstant.ArgumentNumber));
                break;
            case MsgKeys.SetSteriLock_Req:
                //buf.put(msg.optString(CabinetConstant.UserId).getBytes());
                byte value = (byte) msg.optInt(CabinetConstant.SteriLock);
                buf.put(value);
                break;

        }
    }

}

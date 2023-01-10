package com.robam.dishwasher.protocol.mqtt;

import android.util.Log;

import com.robam.common.ITerminalType;
import com.robam.common.bean.MqttDirective;
import com.robam.common.mqtt.MqttMsg;
import com.robam.common.mqtt.MqttPublic;
import com.robam.common.mqtt.MsgKeys;
import com.robam.common.utils.ByteUtils;
import com.robam.common.utils.LogUtils;
import com.robam.common.utils.MsgUtils;
import com.robam.dishwasher.constant.DishWasherConstant;
import com.robam.dishwasher.constant.DishWasherEvent;
import com.robam.dishwasher.device.DishWasherAbstractControl;
import java.nio.ByteBuffer;

//mqtt洗碗机
public class MqttDishWasher extends MqttPublic {

    @Override
    protected void onDecodeMsg(MqttMsg msg, byte[] payload, int offset) throws Exception{
        //解析需要的字段存放
        switch (msg.getID()) {
            case MsgKeys.getDishWasherWorkMode:
            case MsgKeys.getDishWasherPower:
            case MsgKeys.getDishWasherChildLock:
                msg.putOpt(DishWasherConstant.RC, ByteUtils.toShort(payload[offset++]));
                //MqttDirective.getInstance().getDirective().setValue(msg.getID());
                //LogUtils.i("MqttDishWasher onDecodeMsg  guid : "+msg.getGuid());
                MqttDirective.getInstance().setStrLiveDataValue(msg.getGuid(),msg.getID());
                DishWasherAbstractControl.getInstance().queryAttribute(msg.getGuid());
                //Log.i("startWork"," ReQuery" );
                break;
            case MsgKeys.getDishWasherStatus:
                short powerStatus =  ByteUtils.toShort(payload[offset++]);
                short stoveLock = ByteUtils.toShort(payload[offset++]);
                short dishWasherWorkMode = ByteUtils.toShort(payload[offset++]);
                //LogUtils.i("MqttDishWasher workMode "+dishWasherWorkMode);
                int remainingWorkingTime = ByteUtils.toInt32(payload, offset++, ByteUtils.BYTE_ORDER);
                offset++;
                short lowerLayerWasher = ByteUtils.toShort(payload[offset++]);
                short enhancedDryStatus = ByteUtils.toShort(payload[offset++]);
                short appointmentSwitchStatus = ByteUtils.toShort(payload[offset++]);
                short autoVentilation = ByteUtils.toShort(payload[offset++]);
                int appointmentTime = ByteUtils.toInt32(payload, offset++, ByteUtils.BYTE_ORDER);
                offset++;
                int appointmentRemainingTime = ByteUtils.toInt32(payload, offset++, ByteUtils.BYTE_ORDER);

                offset++;
                short rinseAgentPositionKey = ByteUtils.toShort(payload[offset++]);
                short saltFlushValue = ByteUtils.toShort(payload[offset++]);
                short dishWasherFanSwitch = ByteUtils.toShort(payload[offset++]);
                short doorOpenState = ByteUtils.toShort(payload[offset++]);
                short lackRinseStatus = ByteUtils.toShort(payload[offset++]);
                short lackSaltStatus = ByteUtils.toShort(payload[offset++]);
                short abnormalAlarmStatus = ByteUtils.toShort(payload[offset++]);
                //short ADD_AUX = ByteUtils.toShort(payload[payload.length - 1]);
                short argument = ByteUtils.toShort(payload[offset++]);
                while (argument > 0) {
                    argument--;
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
                        case 1:
                            msg.putOpt(DishWasherConstant.CurrentWaterTemperatureValue, value);
                            break;
                        case 2:
                            msg.putOpt(DishWasherConstant.SetWorkTimeValue, value);
                            break;
                        case 3:
                            msg.putOpt(DishWasherConstant.ADD_AUX,value);
                            break;
                        case 10:
                            msg.putOpt(DishWasherConstant.WORK_MODE_STATE, value);
                            break;
                        default:
                            break;
                    }
                }

                msg.putOpt(DishWasherConstant.powerStatus, powerStatus);
                msg.putOpt(DishWasherConstant.StoveLock, stoveLock);
                msg.putOpt(DishWasherConstant.DishWasherWorkMode, dishWasherWorkMode);
                msg.putOpt(DishWasherConstant.REMAINING_WORKING_TIME, remainingWorkingTime);
                msg.putOpt(DishWasherConstant.LowerLayerWasher, lowerLayerWasher);
                msg.putOpt(DishWasherConstant.AppointmentSwitchStatus, appointmentSwitchStatus);
                msg.putOpt(DishWasherConstant.AutoVentilation, autoVentilation);
                msg.putOpt(DishWasherConstant.AppointmentTime, appointmentTime);
                msg.putOpt(DishWasherConstant.AppointmentRemainingTime, appointmentRemainingTime);
                msg.putOpt(DishWasherConstant.SaltFlushValue, saltFlushValue);
                msg.putOpt(DishWasherConstant.DishWasherFanSwitch, dishWasherFanSwitch);
                msg.putOpt(DishWasherConstant.DoorOpenState, doorOpenState);
                msg.putOpt(DishWasherConstant.LackRinseStatus, lackRinseStatus);
                msg.putOpt(DishWasherConstant.LackSaltStatus, lackSaltStatus);
                msg.putOpt(DishWasherConstant.AbnormalAlarmStatus, abnormalAlarmStatus);
                //msg.putOpt(DishWasherConstant.ADD_AUX, ADD_AUX);
                if(dishWasherWorkMode != 0 && remainingWorkingTime > 0){//记录最新工作模式
                    MqttDirective.getInstance().updateModelWorkState(msg.getGuid(),dishWasherWorkMode,0);
                }
                break;
            case MsgKeys.getEventReport:
                short aShort = ByteUtils.toShort(payload[offset++]);
                msg.putOpt(DishWasherConstant.EventId,aShort);
                if (aShort == DishWasherEvent.EVENT_WORK_COMPLETE_RESET) {
                    msg.putOpt(DishWasherConstant.WATER_CONSUMPTION, ByteUtils.toShort(payload[offset++]));
                    offset++;
                    msg.putOpt(DishWasherConstant.POWER_CONSUMPTION, ByteUtils.toShort(payload[offset++]));
                    //记录工作完成结束事件
                    MqttDirective.getInstance().finishWorkModelState(msg.getGuid());
                    MqttDirective.getInstance().setStrLiveDataValue(msg.getGuid(),aShort);
                }
                //MqttDirective.getInstance().getDirective().setValue((int)aShort);
                break;
        }
    }


    @Override
    protected void onEncodeMsg(ByteBuffer buf, MqttMsg msg) {
        switch (msg.getID()) {
            case MsgKeys.setDishWasherPower:
                buf.put(msg.optString(DishWasherConstant.UserId).getBytes());
                buf.put((byte) msg.optInt(DishWasherConstant.PowerMode));
                break;
            case MsgKeys.setDishWasherChildLock:
                buf.put(msg.optString(DishWasherConstant.UserId).getBytes());
                buf.put((byte) msg.optInt(DishWasherConstant.StoveLock));
                break;
            case MsgKeys.setDishWasherWorkMode:
                buf.put(msg.optString(DishWasherConstant.UserId).getBytes());
                buf.put((byte) msg.optInt(DishWasherConstant.DishWasherWorkMode));
                buf.put((byte) msg.optInt(DishWasherConstant.LowerLayerWasher));
                buf.put((byte) msg.optInt(DishWasherConstant.AutoVentilation));
                buf.put((byte) msg.optInt(DishWasherConstant.EnhancedDrySwitch));
                buf.put((byte) msg.optInt(DishWasherConstant.AppointmentSwitch));
                int appointTime = msg.optInt(DishWasherConstant.AppointmentTime);
                buf.put((byte) (appointTime & 0Xff));
                buf.put((byte) ((appointTime >> 8) & 0Xff));
                //参数个数
                int argumentNum = msg.optInt(DishWasherConstant.ArgumentNumber);
                if (argumentNum != 0){
                    buf.put((byte) argumentNum);
                    //附加功能
                    int addAux = msg.optInt(DishWasherConstant.ADD_AUX);
                    buf.put((byte) 1);
                    buf.put((byte) 1);
                    buf.put((byte) addAux);
                }
                break;
            case MsgKeys.setDishWasherStatus:
                buf.put((byte) ITerminalType.PAD);
                //str = msg.optString(DishWasherConstant.UserId);
                //buf.put(str.getBytes());
                break;
            case MsgKeys.setDishWasherUserOperate:
                buf.put(msg.optString(DishWasherConstant.UserId).getBytes());
                buf.put(msg.optString(DishWasherConstant.ArgumentNumber).getBytes());
                if (msg.optInt(DishWasherConstant.ArgumentNumber) > 0) {
                    if (msg.optInt(DishWasherConstant.SaltFlushKey) == 1) {
                        buf.put((byte) msg.optInt(DishWasherConstant.SaltFlushKey));
                        buf.put((byte) msg.optInt(DishWasherConstant.SaltFlushLength));
                        buf.put((byte) msg.optInt(DishWasherConstant.SaltFlushValue));
                    }
                    if (msg.optInt(DishWasherConstant.RinseAgentPositionKey) == 2) {
                        buf.put((byte) msg.optInt(DishWasherConstant.RinseAgentPositionKey));
                        buf.put((byte) msg.optInt(DishWasherConstant.RinseAgentPositionLength));
                        buf.put((byte) msg.optInt(DishWasherConstant.RinseAgentPositionValue));
                    }
                }
                break;
            case MsgKeys.getDeviceAttribute_Req:
                    buf.put((byte) 0x00);
                    break;
        }
    }
}

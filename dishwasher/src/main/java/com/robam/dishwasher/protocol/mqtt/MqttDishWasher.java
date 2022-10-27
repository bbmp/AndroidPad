package com.robam.dishwasher.protocol.mqtt;

import com.robam.common.ITerminalType;
import com.robam.common.mqtt.MqttMsg;
import com.robam.common.mqtt.MqttPublic;
import com.robam.common.mqtt.MsgKeys;
import com.robam.common.utils.ByteUtils;
import com.robam.common.utils.MsgUtils;
import com.robam.dishwasher.constant.DishWasherConstant;

import java.nio.ByteBuffer;

//mqtt洗碗机
public class MqttDishWasher extends MqttPublic {

    @Override
    protected void onDecodeMsg(MqttMsg msg, byte[] payload, int offset) throws Exception{
        //解析需要的字段存放
        switch (msg.getID()) {
            case MsgKeys.getDishWasherWorkMode:
                msg.putOpt(DishWasherConstant.RC, ByteUtils.toShort(payload[offset++]));
                break;
            case MsgKeys.getDishWasherStatus:
                short powerStatus =  ByteUtils.toShort(payload[offset++]);
                short stoveLock = ByteUtils.toShort(payload[offset++]);
                short dishWasherWorkMode = ByteUtils.toShort(payload[offset++]);
                int dishWasherRemainingWorkingTime = ByteUtils.toInt32(payload, offset++, ByteUtils.BYTE_ORDER);
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
                short ADD_AUX = ByteUtils.toShort(payload[payload.length - 1]);
                short argument = ByteUtils.toShort(payload[offset++]);

                msg.putOpt(DishWasherConstant.powerStatus, powerStatus);
                msg.putOpt(DishWasherConstant.DishWasherWorkMode, dishWasherWorkMode);
                msg.putOpt(DishWasherConstant.DishWasherRemainingWorkingTime, dishWasherRemainingWorkingTime);
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
                msg.putOpt(DishWasherConstant.ADD_AUX, ADD_AUX);

                break;
        }
    }

    @Override
    protected void onEncodeMsg(ByteBuffer buf, MqttMsg msg) {
//        switch (msg.getID()) {
//            case MsgKeys.setDishWasherStatus: //洗碗机状态查询
//                buf.put((byte) ITerminalType.PAD);
//                break;
//        }
        //ByteBuffer buf = ByteBuffer.allocate(BufferSize).order(BYTE_ORDER);
        byte b;
        String str;
        switch (msg.getID()) {
            case MsgKeys.setDishWasherPower:
                str = msg.optString(DishWasherConstant.UserId);
                buf.put(str.getBytes());
                b = (byte) msg.optInt(DishWasherConstant.PowerMode);
                buf.put(b);
                break;
            case MsgKeys.setDishWasherChildLock:
                str = msg.optString(DishWasherConstant.UserId);
                buf.put(str.getBytes());
                b = (byte) msg.optInt(DishWasherConstant.StoveLock);
                buf.put(b);
                break;
            case MsgKeys.setDishWasherWorkMode:
                str = msg.optString(DishWasherConstant.UserId);
                buf.put(str.getBytes());
                b = (byte) msg.optInt(DishWasherConstant.DishWasherWorkMode);
                buf.put(b);
                b = (byte) msg.optInt(DishWasherConstant.LowerLayerWasher);
                buf.put(b);
                b = (byte) msg.optInt(DishWasherConstant.AutoVentilation);
                buf.put(b);
                b = (byte) msg.optInt(DishWasherConstant.EnhancedDrySwitch);
                buf.put(b);
                b = (byte) msg.optInt(DishWasherConstant.AppointmentSwitch);
                buf.put(b);
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
                str = msg.optString(DishWasherConstant.UserId);
                buf.put(str.getBytes());
                str = msg.optString(DishWasherConstant.ArgumentNumber);
                buf.put(str.getBytes());
                if (msg.optInt(DishWasherConstant.ArgumentNumber) > 0) {
                    if (msg.optInt(DishWasherConstant.SaltFlushKey) == 1) {
                        b = (byte) msg.optInt(DishWasherConstant.SaltFlushKey);
                        buf.put(b);
                        b = (byte) msg.optInt(DishWasherConstant.SaltFlushLength);
                        buf.put(b);
                        b = (byte) msg.optInt(DishWasherConstant.SaltFlushValue);
                        buf.put(b);

                    }
                    if (msg.optInt(DishWasherConstant.RinseAgentPositionKey) == 2) {
                        b = (byte) msg.optInt(DishWasherConstant.RinseAgentPositionKey);
                        buf.put(b);
                        b = (byte) msg.optInt(DishWasherConstant.RinseAgentPositionLength);
                        buf.put(b);
                        b = (byte) msg.optInt(DishWasherConstant.RinseAgentPositionValue);
                        buf.put(b);

                    }
                }
                break;
            case MsgKeys.getDeviceAttribute_Req:
                    buf.put((byte) 0x00);
                    break;
        }
        /*byte[] data = new byte[buf.position()];
        System.arraycopy(buf.array(), 0, data, 0, data.length);
        bufT.put(data);*/
        //将所有数据放入 新创建的byte[] 最后设置给buf？
    }


}

package com.robam.dishwasher.protocol.mqtt;

import com.robam.common.ITerminalType;
import com.robam.common.mqtt.IProtocol;
import com.robam.common.mqtt.MqttMsg;
import com.robam.common.mqtt.MqttPublic;
import com.robam.common.mqtt.MsgKeys;
import com.robam.common.utils.ByteUtils;
import com.robam.dishwasher.constant.DishWasherConstant;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

//mqtt洗碗机
public class MqttDishWasher extends MqttPublic {

    @Override
    protected Map onDecodeMsg(int msgId, String srcGuid, byte[] payload, int offset) {
        //解析需要的字段存放
        Map map = new HashMap();
        switch (msgId) {
            case MsgKeys.getDishWasherStatus:
                short powerStatus =
                        ByteUtils.toShort(payload[offset++]);
                map.put(DishWasherConstant.powerStatus, powerStatus);
                short stoveLock =
                        ByteUtils.toShort(payload[offset++]);
                short dishWasherWorkMode =
                        ByteUtils.toShort(payload[offset++]);
                map.put(DishWasherConstant.DishWasherWorkMode, dishWasherWorkMode);
                int dishWasherRemainingWorkingTime =
                        ByteUtils.toInt32(payload, offset++, ByteUtils.BYTE_ORDER);
                map.put(DishWasherConstant.DishWasherRemainingWorkingTime, dishWasherRemainingWorkingTime);
                offset++;
                short lowerLayerWasher =
                        ByteUtils.toShort(payload[offset++]);
                short enhancedDryStatus =
                        ByteUtils.toShort(payload[offset++]);
                short appointmentSwitchStatus =
                        ByteUtils.toShort(payload[offset++]);
                short autoVentilation =
                        ByteUtils.toShort(payload[offset++]);
                int appointmentTime =
                        ByteUtils.toInt32(payload, offset++, ByteUtils.BYTE_ORDER);
                offset++;
                int appointmentRemainingTime =
                        ByteUtils.toInt32(payload, offset++, ByteUtils.BYTE_ORDER);
                offset++;
                short rinseAgentPositionKey =
                        ByteUtils.toShort(payload[offset++]);
                short saltFlushValue =
                        ByteUtils.toShort(payload[offset++]);
                short dishWasherFanSwitch =
                        ByteUtils.toShort(payload[offset++]);
                short doorOpenState =
                        ByteUtils.toShort(payload[offset++]);
                short lackRinseStatus =
                        ByteUtils.toShort(payload[offset++]);
                short lackSaltStatus =
                        ByteUtils.toShort(payload[offset++]);
                short abnormalAlarmStatus =
                        ByteUtils.toShort(payload[offset++]);

                short ADD_AUX =
                        ByteUtils.toShort(payload[payload.length - 1]);

                short argument = ByteUtils.toShort(payload[offset++]);
                break;
        }
        return map;
    }

    @Override
    protected void onEncodeMsg(ByteBuffer buf, MqttMsg msg) {
        switch (msg.getID()) {
            case MsgKeys.setDishWasherStatus:
                buf.put((byte) ITerminalType.PAD);
                break;
        }
    }
}

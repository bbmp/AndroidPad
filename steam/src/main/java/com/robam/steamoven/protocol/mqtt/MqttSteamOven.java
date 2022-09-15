package com.robam.steamoven.protocol.mqtt;

import android.annotation.SuppressLint;
import android.text.TextUtils;

import androidx.core.util.Preconditions;

import com.robam.common.bean.RTopic;
import com.robam.common.mqtt.IProtocol;
import com.robam.common.mqtt.MqttMsg;
import com.robam.common.mqtt.MqttPublic;
import com.robam.common.mqtt.MsgKeys;
import com.robam.common.mqtt.RTopicParser;
import com.robam.common.utils.ByteUtils;
import com.robam.common.utils.LogUtils;
import com.robam.common.utils.MsgUtils;
import com.robam.common.utils.StringUtils;
import com.robam.steamoven.bean.SteamOven;
import com.robam.steamoven.constant.QualityKeys;
import com.robam.steamoven.constant.SteamConstant;
import com.robam.steamoven.device.SteamAbstractControl;
import com.robam.steamoven.device.SteamFactory;
import com.robam.steamoven.protocol.SerialToMqttHelper;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.HashMap;
import java.util.Map;

public class MqttSteamOven extends MqttPublic {
    private final int BufferSize = 1024 * 2;
    private static final int GUID_SIZE = 17;
    private final int CMD_CODE_SIZE = 1;
    public static final ByteOrder BYTE_ORDER = ByteOrder.LITTLE_ENDIAN;

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
            case MsgKeys.getDeviceAttribute_Rep: {
            }
            break;
            case MsgKeys.getSteameOvenStatus_Rep:
                //状态
                short status = ByteUtils.toShort(payload[offset++]);
                msg.putOpt(SteamConstant.SteameOvenStatus, status);
                short powerOnStatus = ByteUtils.toShort(payload[offset++]);
                short workOnStatus = ByteUtils.toShort(payload[offset++]);
                msg.putOpt(SteamConstant.SteameOvenWorknStatus, workOnStatus);
                short alarm = ByteUtils.toShort(payload[offset++]);
                short mode = ByteUtils.toShort(payload[offset++]);
                msg.putOpt(SteamConstant.SteameOvenMode, mode);
                short temp = ByteUtils.toShort(payload[offset++]);
                short leftTime = ByteUtils.toShort(payload[offset++]);
                msg.putOpt(SteamConstant.SteameOvenLeftTime, leftTime); //工作剩余时间
                offset++;
                short light = ByteUtils.toShort(payload[offset++]);
                short waterStatus = ByteUtils.toShort(payload[offset++]);
                short setTemp = ByteUtils.toShort(payload[offset++]);
                short setTime = ByteUtils.toShort(payload[offset++]);
                short min = ByteUtils.toShort(payload[offset++]);
                short hour = ByteUtils.toShort(payload[offset++]);
                short recipeId = ByteUtils.toShort(payload[offset++]);
                offset++;
                short recipeSteps = ByteUtils.toShort(payload[offset++]);
                short setDownTemp = ByteUtils.toShort(payload[offset++]);
                short downTemp = ByteUtils.toShort(payload[offset++]);
                short steam = ByteUtils.toShort(payload[offset++]);
                short segments_Key = ByteUtils.toShort(payload[offset++]);
                short step_Key = ByteUtils.toShort(payload[offset++]);
                short preFalg = ByteUtils.toShort(payload[offset++]);
                short modelType = ByteUtils.toShort(payload[offset++]);

                short argument = ByteUtils.toShort(payload[offset++]);

                while (argument > 0) {
                    short argumentKey = ByteUtils.toShort(payload[offset++]);
                    switch (argumentKey) {
                        case 3:

                            short cpStepLength = ByteUtils.toShort(payload[offset++]);
                            short cpStepValue = ByteUtils.toShort(payload[offset++]);
                            break;
                        case 4:
                            short steamLength = ByteUtils.toShort(payload[offset++]);
                            short steamValue = ByteUtils.toShort(payload[offset++]);
                            break;
                        case 5:
                            short MultiStepCookingStepsLength = ByteUtils.toShort(payload[offset++]);
                            short MultiStepCookingStepsValue = ByteUtils.toShort(payload[offset++]);
                            break;
                        case 6:
                            short SteamOvenAutoRecipeModeLength = ByteUtils.toShort(payload[offset++]);
                            short AutoRecipeModeValue = ByteUtils.toShort(payload[offset++]);
                            break;
                        case 7:
                            short MultiStepCurrentStepsLength = ByteUtils.toShort(payload[offset++]);
                            short MultiStepCurrentStepsValue = ByteUtils.toShort(payload[offset++]);
                            break;
                        case 8:
                            short SteameOvenPreFlagLength = ByteUtils.toShort(payload[offset++]);
                            short SteameOvenPreFlagValue = ByteUtils.toShort(payload[offset++]);
                            break;
                        case 9:
                            short weatherDescalingLength = ByteUtils.toShort(payload[offset++]);
                            short weatherDescalingValue = ByteUtils.toShort(payload[offset++]);
                            break;
                        case 10:
                            short doorStatusLength = ByteUtils.toShort(payload[offset++]);
                            short doorStatusValue = ByteUtils.toShort(payload[offset++]);
                            break;
                        case 11:
                            short time_H_length = ByteUtils.toShort(payload[offset++]);
                            short time_H_Value = ByteUtils.toShort(payload[offset++]);
                            break;
                        case 12:
                            offset++ ;
                            short SteameOvenLeftMin = ByteUtils.toShort(payload[offset++]);
                            short SteameOvenLeftHours = ByteUtils.toShort(payload[offset++]);
                            break;
                    }
                    argument--;
                }
                break;
            case MsgKeys.getDeviceEventReport: //事件上报
                //设备型号
                short categoryCodeEvent = ByteUtils.toShort(payload[offset++]);
                short event = ByteUtils.toShort(payload[offset++]);
                LogUtils.e("categoryCodeEvent " + categoryCodeEvent + " event=" + event);
                break;
        }
    }
}

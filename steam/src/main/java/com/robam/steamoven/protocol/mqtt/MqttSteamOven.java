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

                break;
            case MsgKeys.setDeviceAttribute_Req: //属性设置
                if (null != msg.getBytes())
                    buf.put(msg.getBytes());
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
                        case QualityKeys.workState: //工作状态
                            msg.putOpt(SteamConstant.SteameOvenStatus, value);
                            break;
                        case QualityKeys.faultCode: //故障码
                            msg.putOpt(SteamConstant.SteamFaultCode, value);
                            break;
                        case QualityKeys.totalRemainSeconds: //总剩余时间
                            msg.putOpt(SteamConstant.SteameOvenLeftTime, value);
                            break;
                    }
                }
            }
                break;
            case MsgKeys.setDeviceAttribute_Rep: { //属性设置响应
                //设置完成立即查询
                SteamAbstractControl.getInstance().queryAttribute(msg.getGuid());
            }
                break;
            case MsgKeys.getDeviceEventReport: { //事件上报
                //属性个数
                int attributeNum = MsgUtils.getByte(payload[offset]);
                    offset++;

            }
                break;
        }
    }
}

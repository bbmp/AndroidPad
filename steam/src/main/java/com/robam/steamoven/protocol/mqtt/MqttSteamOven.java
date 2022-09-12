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
import com.robam.steamoven.device.SteamAbstractControl;
import com.robam.steamoven.device.SteamFactory;
import com.robam.steamoven.protocol.SerialToMqttHelper;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class MqttSteamOven implements IProtocol {
    private final int BufferSize = 1024 * 2;
    private static final int GUID_SIZE = 17;
    private final int CMD_CODE_SIZE = 1;
    public static final ByteOrder BYTE_ORDER = ByteOrder.LITTLE_ENDIAN;

    private void onDecodeMsg(int msgId, byte[] payload, int offset) {
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

                            break;
                        case QualityKeys.workCtrl:
                            short workCtrl =  ByteUtils.toShort(payload[offset]);

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
                            offset++;
                            break;
                        case QualityKeys.setUpTemp:
                            int setUpTemp =  ByteUtils.toShort(payload[offset]);
                            offset++;
                            break;
                        case QualityKeys.setDownTemp:
                            int setDownTemp =  ByteUtils.toShort(payload[offset]);
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
                            break;
                        case QualityKeys.restTime:
                            break;
                        case QualityKeys.steam:
                            int steam = ByteUtils.toShort(payload[offset]);
                            offset++;
                            break;
                        case QualityKeys.mode2:
                            short mode2 = ByteUtils.toShort(payload[offset]);
                            offset++;
                            break;
                        case QualityKeys.setUpTemp2:
                            int setUpTemp2 = ByteUtils.toShort(payload[offset]);
                            offset++;
                            break;
                        case QualityKeys.setDownTemp2:
                            int setDownTemp2 = ByteUtils.toShort(payload[offset]);
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
                            break;
                        case QualityKeys.restTime2:
                            break;
                        case QualityKeys.steam2:
                            int steam2 = ByteUtils.toShort(payload[offset]);
                            offset++;
                            break;
                        case QualityKeys.mode3:
                            short mode3 = ByteUtils.toShort(payload[offset]);
                            offset++;
                            break;
                        case QualityKeys.setUpTemp3:
                            int setUpTemp3 = ByteUtils.toShort(payload[offset]);
                            offset++;
                            break;
                        case QualityKeys.setDownTemp3:
                            int setDownTemp3 = ByteUtils.toShort(payload[offset]);
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
                            break;
                        case QualityKeys.restTime3:
                            break;
                        case QualityKeys.steam3:
                            int steam3 = ByteUtils.toShort(payload[offset]);
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

    private void onEncodeMsg(ByteBuffer buf, MqttMsg msg) {
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
    public byte[] encode(MqttMsg msg) {
        try {

            ByteBuffer buf = ByteBuffer.allocate(BufferSize).order(BYTE_ORDER);
            byte[] tmp = null;
            // guid
            String guid = msg.getGuid();
            //guid id null
            if (TextUtils.isEmpty(guid))
                return null;
            //guid length error
            if (guid.length() != GUID_SIZE)
                return null;

            tmp = guid.getBytes();
            buf.put(tmp);

            // cmdCode
            buf.put(MsgUtils.toByte(msg.getID()));

            // data params
            onEncodeMsg(buf, msg);

            // buf to byte[]
            byte[] data = new byte[buf.position()];
            System.arraycopy(buf.array(), 0, data, 0, data.length);
            buf.clear();
            return data;
        } catch (Exception e) {
            String log = String.format(
                    "mqtt encode error. topic:%s\nerror:%s",
                    msg.getrTopic().getTopic(), e.getMessage());
            LogUtils.e(log);
        }
        return null;
    }

    @Override
    public int decode(String topic, byte[] payload) {
        try {
            if (null == payload)
                return -1;

            RTopic rTopic = RTopicParser.parse(topic);
            if (null == rTopic)
                return -1;

            //数据长度不符
            if (payload.length >= GUID_SIZE + CMD_CODE_SIZE)
                return -1;

            int offset = 0;
            // guid
            String srcGuid = MsgUtils.getString(payload, offset, GUID_SIZE);
            offset += GUID_SIZE;

            // cmd id
            String dt = srcGuid.substring(0 , 5) ;
            String signNum = srcGuid.substring(5 , 17) ;
            short msgId = ByteUtils.toShort(payload[offset++]);

            // paser payload
            onDecodeMsg(msgId, payload, offset);

            return msgId;
        } catch (Exception e) {
            String log = String.format(
                    "mqtt decode error. topic:%s\nerror:%s\nbyte[]:%s",
                    topic, e.getMessage(), StringUtils.bytes2Hex(payload));
            LogUtils.e(log);
            e.printStackTrace();
        }
        return -1;
    }
}

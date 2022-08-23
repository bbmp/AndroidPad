package com.robam.ventilator.protocol.mqtt;

import android.text.TextUtils;

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
import com.robam.steamoven.device.SteamFactory;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

//烟机mqtt私有协议
public class MqttVentilator implements IProtocol {
    private final int BufferSize = 1024 * 2;
    private static final int GUID_SIZE = 17;
    private final int CMD_CODE_SIZE = 1;
    public static final ByteOrder BYTE_ORDER = ByteOrder.LITTLE_ENDIAN;


    private void onDecodeMsg(int msgId, byte[] payload, int offset) {
//从payload中取值角标
        //远程被控制
        switch (msgId) {
            case MsgKeys.getDeviceAttribute_Req:

                break;
            case MsgKeys.setDeviceAttribute_Req:
                //属性个数
                short number = ByteUtils.toShort(payload[offset]);
                break;
        }
    }
    //烟机端的协议
    private void onEncodeMsg(ByteBuffer buf, MqttMsg msg) {
        //远程控制其他设备或通知上报

        int msgId = msg.getID();
        switch (msgId) {
            case MsgKeys.DeviceConnected_Noti:
                break;
            case MsgKeys.setDeviceAttribute_Rep:
                buf.put((byte) 1);
                buf.put((byte) 0);
                break;
            case MsgKeys.getDeviceAttribute_Rep:
                break;
        }
    }

    @Override
    public byte[] encode(MqttMsg msg) {
        //烟机比较特殊，会调用其他设备协议打包
        //如果是一体机
//        return SteamFactory.getProtocol().encode(msg);

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

package com.robam.common.mqtt;

import android.annotation.SuppressLint;

import androidx.core.util.Preconditions;


import com.robam.common.bean.RTopic;
import com.robam.common.utils.ByteUtils;
import com.robam.common.utils.LogUtils;
import com.robam.common.utils.MsgUtils;
import com.robam.common.utils.StringUtils;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Map;

/**
 *  mqtt协议公共打包和解析
 */
public abstract class MqttPublic implements IProtocol{
    protected final int BufferSize = 1024 * 2;
    protected static final int GUID_SIZE = 17;
    protected final int CMD_CODE_SIZE = 1;
    public static final ByteOrder BYTE_ORDER = ByteOrder.LITTLE_ENDIAN;

    @SuppressLint("RestrictedApi")
    public byte[] encode(MqttMsg msg) {
        try {
            Preconditions.checkNotNull(msg);

            ByteBuffer buf = ByteBuffer.allocate(BufferSize).order(BYTE_ORDER);
            byte[] tmp = null;
            // guid
            String guid = msg.getGuid();
            Preconditions.checkNotNull(guid, "guid is null");
            Preconditions.checkState(guid.length() == GUID_SIZE,
                    "guid length error");
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

        }
        return null;
    }

    @SuppressLint("RestrictedApi")
    @Override
    public MqttMsg decode(String topic, byte[] payload) {

        try {
            Preconditions.checkNotNull(payload);

            RTopic rTopic = RTopicParser.parse(topic);
            Preconditions.checkNotNull(rTopic);


            Preconditions.checkState(payload.length >= GUID_SIZE + CMD_CODE_SIZE,
                    "数据长度不符");

            int offset = 0;
            // guid
            String srcGuid = MsgUtils.getString(payload, offset, GUID_SIZE);
            offset += GUID_SIZE;

            // cmd id
            String dt = srcGuid.substring(0 , 5) ;
            String signNum = srcGuid.substring(5 , 17) ;
            short msgId = ByteUtils.toShort(payload[offset++]);
            MqttMsg msg = new MqttMsg.Builder()
                    .setMsgId(msgId)
                    .setGuid(srcGuid)
                    .setDt(dt)
                    .setTopic(rTopic)
                    .build();

            // paser payload
            onDecodeMsg(msg, payload, offset);

            return msg;
        } catch (Exception e) {
            String log = String.format(
                    "mqtt decode error. topic:%s\nerror:%s\nbyte[]:%s",
                    topic, e.getMessage(), StringUtils.bytes2Hex(payload));
            LogUtils.e(log);
            e.printStackTrace();
        }
        return null;
    }

    protected abstract void onDecodeMsg(MqttMsg msg, byte[] payload, int offset) throws Exception;

    protected abstract void onEncodeMsg(ByteBuffer buf, MqttMsg msg);
}

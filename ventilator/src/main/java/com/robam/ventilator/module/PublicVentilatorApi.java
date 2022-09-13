package com.robam.ventilator.module;

import android.annotation.SuppressLint;

import androidx.core.util.Preconditions;

import com.robam.common.bean.AccountInfo;
import com.robam.common.bean.Device;
import com.robam.common.bean.RTopic;
import com.robam.common.module.IPublicVentilatorApi;
import com.robam.common.mqtt.MqttMsg;
import com.robam.common.mqtt.RTopicParser;
import com.robam.common.utils.ByteUtils;
import com.robam.common.utils.LogUtils;
import com.robam.common.utils.MsgUtils;
import com.robam.common.utils.StringUtils;
import com.robam.steamoven.bean.SteamOven;
import com.robam.steamoven.device.SteamFactory;
import com.robam.stove.bean.Stove;
import com.robam.stove.device.StoveFactory;
import com.robam.ventilator.bean.Ventilator;
import com.robam.ventilator.device.VentilatorFactory;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class PublicVentilatorApi implements IPublicVentilatorApi {
    protected final int BufferSize = 1024 * 2;
    protected static final int GUID_SIZE = 17;
    protected final int CMD_CODE_SIZE = 1;
    public static final ByteOrder BYTE_ORDER = ByteOrder.LITTLE_ENDIAN;

    @SuppressLint("RestrictedApi")
    @Override
    public byte[] encode(MqttMsg msg) {
        try {
            // guid
            String targetGuid = msg.getrTopic().getSignNum();

            for (Device device: AccountInfo.getInstance().deviceList) {
                // data params
                if (device.guid.equals(targetGuid)) {
                    if (device instanceof SteamOven)
                        return SteamFactory.getProtocol().encode(msg);
                    else if (device instanceof Stove)
                        return StoveFactory.getProtocol().encode(msg);
                    else if (device instanceof Ventilator)
                        return VentilatorFactory.getProtocol().encode(msg);
                }
            }

            return null;
        } catch (Exception e) {

        }
        return null;
    }

    @SuppressLint("RestrictedApi")
    @Override
    public int decode(String topic, byte[] payload) {
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

            short msgId = ByteUtils.toShort(payload[offset++]);
            LogUtils.e( "收到消息： " + "topic = " + topic + " ,msgId = " + msgId);

            //分发到各设备
            for (Device device: AccountInfo.getInstance().deviceList) {
                if (srcGuid.equals(device.guid)) {
                    if (device instanceof SteamOven)
                        return SteamFactory.getProtocol().decode(topic, payload);
                    else if (device instanceof Stove)
                        return StoveFactory.getProtocol().decode(topic, payload);
                    else if (device instanceof Ventilator)
                        return VentilatorFactory.getProtocol().decode(topic, payload);
                }
            }

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

package com.robam.ventilator.module;

import android.annotation.SuppressLint;

import androidx.core.util.Preconditions;

import com.robam.cabinet.bean.Cabinet;
import com.robam.cabinet.device.CabinetFactory;
import com.robam.common.bean.AccountInfo;
import com.robam.common.bean.Device;
import com.robam.common.mqtt.IProtocol;
import com.robam.common.mqtt.MqttMsg;
import com.robam.common.utils.ByteUtils;
import com.robam.common.utils.LogUtils;
import com.robam.common.utils.MsgUtils;
import com.robam.common.utils.StringUtils;
import com.robam.dishwasher.bean.DishWasher;
import com.robam.dishwasher.device.DishWasherFactory;
import com.robam.common.device.subdevice.Pan;
import com.robam.pan.device.PanFactory;
import com.robam.steamoven.bean.SteamOven;
import com.robam.steamoven.device.SteamFactory;
import com.robam.common.device.subdevice.Stove;
import com.robam.stove.device.StoveFactory;
import com.robam.ventilator.device.VentilatorFactory;

import java.nio.ByteOrder;

//协议转发api
public class TransmitApi implements IProtocol {
    protected final int BufferSize = 1024 * 2;
    protected static final int GUID_SIZE = 17;
    protected final int CMD_CODE_SIZE = 1;
    public static final ByteOrder BYTE_ORDER = ByteOrder.LITTLE_ENDIAN;

    @SuppressLint("RestrictedApi")
    @Override
    public byte[] encode(MqttMsg msg) {
        try {
            // guid
            String targetGuid = msg.getrTopic().getDeviceType() + msg.getrTopic().getSignNum();
            LogUtils.e("encode targetGuid " + targetGuid);

            //拦截转发
            for (Device device: AccountInfo.getInstance().deviceList) {
                if (device.guid.equals(targetGuid)) {
                    // data params
                    if (device instanceof SteamOven)
                        return SteamFactory.getProtocol().encode(msg);
                    else if (device instanceof Stove)
                        return StoveFactory.getProtocol().encode(msg);
                    else if (device instanceof Pan)
                        return PanFactory.getProtocol().encode(msg);
                    else if (device instanceof DishWasher)
                        return DishWasherFactory.getProtocol().encode(msg);
                    else if (device instanceof Cabinet)
                        return CabinetFactory.getProtocol().encode(msg);
                }
            }

            return VentilatorFactory.getProtocol().encode(msg);
        } catch (Exception e) {

        }
        return null;
    }

    @SuppressLint("RestrictedApi")
    @Override
    public MqttMsg decode(String topic, byte[] payload) {
        try {
            Preconditions.checkNotNull(payload);

//            RTopic rTopic = RTopicParser.parse(topic);
//            Preconditions.checkNotNull(rTopic);

            Preconditions.checkState(payload.length >= GUID_SIZE + CMD_CODE_SIZE,
                    "数据长度不符");

            int offset = 0;
            // guid 哪个设备发过来
            String srcGuid = MsgUtils.getString(payload, offset, GUID_SIZE);
            offset += GUID_SIZE;

            short msgId = ByteUtils.toShort(payload[offset++]);
            LogUtils.e("收到消息： " + "topic = " + topic + " ,msgId = " + msgId + " srcguid = " + srcGuid);
            LogUtils.e("收到消息： " + StringUtils.bytes2Hex(payload));
            MqttMsg msg = null;
            //分发到各设备
            for (Device device: AccountInfo.getInstance().deviceList) {
                if (device.guid.equals(srcGuid)) {
                    if (device instanceof SteamOven) {
                        msg = SteamFactory.getProtocol().decode(topic, payload);
                        //是否更新设备状态
                        if (device.onMsgReceived(msg))
                            AccountInfo.getInstance().getGuid().setValue(device.guid);  //更新设备状态

                        return msg;
                    } else if (device instanceof Stove) {
                        msg = StoveFactory.getProtocol().decode(topic, payload);
                        //
                        device.onMsgReceived(msg);
                        return msg;
                    } else if (device instanceof Pan) {
                        msg = PanFactory.getProtocol().decode(topic, payload);
                        device.onMsgReceived(msg);
                        return msg;
                    } else if (device instanceof DishWasher) {
                        msg = DishWasherFactory.getProtocol().decode(topic, payload);
                        if (device.onMsgReceived(msg))
                            AccountInfo.getInstance().getGuid().setValue(device.guid);  //更新设备状态

                        return msg;
                    } else if (device instanceof Cabinet) {
                        msg = CabinetFactory.getProtocol().decode(topic, payload);
                        if (device.onMsgReceived(msg))
                            AccountInfo.getInstance().getGuid().setValue(device.guid);  //更新设备状态

                        return msg;
                    }
                }
            }

            return VentilatorFactory.getProtocol().decode(topic, payload);
        } catch (Exception e) {
            String log = String.format(
                    "mqtt decode error. topic:%s\nerror:%s\nbyte[]:%s",
                    topic, e.getMessage(), StringUtils.bytes2Hex(payload));
            LogUtils.e(log);
            e.printStackTrace();
        }
        return null;
    }
}

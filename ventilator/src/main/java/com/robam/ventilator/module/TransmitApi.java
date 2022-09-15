package com.robam.ventilator.module;

import android.annotation.SuppressLint;

import androidx.core.util.Preconditions;

import com.robam.cabinet.bean.Cabinet;
import com.robam.cabinet.constant.CabinetConstant;
import com.robam.cabinet.device.CabinetFactory;
import com.robam.common.bean.AccountInfo;
import com.robam.common.bean.Device;
import com.robam.common.bean.RTopic;
import com.robam.common.module.IPublicVentilatorApi;
import com.robam.common.mqtt.IProtocol;
import com.robam.common.mqtt.MqttMsg;
import com.robam.common.mqtt.RTopicParser;
import com.robam.common.utils.ByteUtils;
import com.robam.common.utils.LogUtils;
import com.robam.common.utils.MsgUtils;
import com.robam.common.utils.StringUtils;
import com.robam.dishwasher.bean.DishWasher;
import com.robam.dishwasher.constant.DishWasherConstant;
import com.robam.dishwasher.device.DishWasherFactory;
import com.robam.pan.bean.Pan;
import com.robam.pan.device.PanFactory;
import com.robam.steamoven.bean.SteamOven;
import com.robam.steamoven.constant.SteamConstant;
import com.robam.steamoven.device.SteamFactory;
import com.robam.stove.bean.Stove;
import com.robam.stove.device.StoveFactory;
import com.robam.ventilator.bean.Ventilator;
import com.robam.ventilator.constant.VentilatorConstant;
import com.robam.ventilator.device.VentilatorFactory;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Map;

//转发api
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
            LogUtils.e("targetGuid " + targetGuid);

            //拦截转发
            for (Device device: AccountInfo.getInstance().deviceList) {
                if (device.guid.equals(targetGuid)) {
                    // data params
                    if (device instanceof SteamOven)
                        return SteamFactory.getProtocol().encode(msg);
                    else if (device instanceof Stove)
                        return StoveFactory.getProtocol().encode(msg);
                    else if (device instanceof Ventilator)
                        return VentilatorFactory.getProtocol().encode(msg);
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

            RTopic rTopic = RTopicParser.parse(topic);
            Preconditions.checkNotNull(rTopic);

            Preconditions.checkState(payload.length >= GUID_SIZE + CMD_CODE_SIZE,
                    "数据长度不符");

            int offset = 0;
            // guid
            String srcGuid = MsgUtils.getString(payload, offset, GUID_SIZE);

            MqttMsg msg = null;
            //分发到各设备
            for (Device device: AccountInfo.getInstance().deviceList) {
                if (device.guid.equals(srcGuid)) {
                    if (device instanceof SteamOven) {
                        msg = SteamFactory.getProtocol().decode(topic, payload);
                        if (null != msg && null != msg.opt(SteamConstant.SteameOvenStatus)) { //有响应
                            device.queryNum = 0; //查询超过一次无响应离线
                            device.status = Device.ONLINE;
                            ((SteamOven) device).workMode = (short) msg.opt(SteamConstant.SteameOvenMode);
                            AccountInfo.getInstance().getGuid().setValue(device.guid);  //更新设备状态
                        }
                        return msg;
                    } else if (device instanceof Stove)
                        return StoveFactory.getProtocol().decode(topic, payload);
                    else if (device instanceof Ventilator) {
                        msg = VentilatorFactory.getProtocol().decode(topic, payload);
                        if (null != msg && null != msg.opt(VentilatorConstant.FanStatus)) {
                            device.queryNum = 0;
                            device.status = Device.ONLINE;
                            AccountInfo.getInstance().getGuid().setValue(device.guid);  //更新设备状态
                        }
                        return msg;
                    } else if (device instanceof Pan)
                        return PanFactory.getProtocol().decode(topic, payload);
                    else if (device instanceof DishWasher) {
                        msg = DishWasherFactory.getProtocol().decode(topic, payload);
                        if (msg != null && null != msg.opt(DishWasherConstant.powerStatus)) {
                            device.queryNum = 0;
                            device.status = Device.ONLINE;
                            AccountInfo.getInstance().getGuid().setValue(device.guid);  //更新设备状态
                        }
                        return msg;
                    } else if (device instanceof Cabinet) {
                        msg = CabinetFactory.getProtocol().decode(topic, payload);
                        if (null != msg && null != msg.opt(CabinetConstant.SteriStatus)) {
                            device.queryNum = 0;
                            device.status = Device.ONLINE;
                            AccountInfo.getInstance().getGuid().setValue(device.guid);  //更新设备状态
                        }
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

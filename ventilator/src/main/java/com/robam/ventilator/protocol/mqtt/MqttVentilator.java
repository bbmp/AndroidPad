package com.robam.ventilator.protocol.mqtt;

import android.text.TextUtils;

import com.robam.cabinet.bean.Cabinet;
import com.robam.cabinet.device.CabinetFactory;
import com.robam.common.ITerminalType;
import com.robam.common.bean.AccountInfo;
import com.robam.common.bean.Device;
import com.robam.common.bean.RTopic;
import com.robam.common.mqtt.IProtocol;
import com.robam.common.mqtt.MqttMsg;
import com.robam.common.mqtt.MqttPublic;
import com.robam.common.mqtt.MsgKeys;
import com.robam.common.mqtt.RTopicParser;
import com.robam.common.utils.ByteUtils;
import com.robam.common.utils.DeviceUtils;
import com.robam.common.utils.LogUtils;
import com.robam.common.utils.MsgUtils;
import com.robam.common.utils.StringUtils;
import com.robam.dishwasher.bean.DishWasher;
import com.robam.dishwasher.device.DishWasherFactory;
import com.robam.pan.bean.Pan;
import com.robam.pan.device.PanFactory;
import com.robam.steamoven.bean.SteamOven;
import com.robam.steamoven.device.SteamFactory;
import com.robam.stove.bean.Stove;
import com.robam.stove.device.StoveFactory;
import com.robam.stove.protocol.mqtt.MqttStove;
import com.robam.ventilator.device.VentilatorFactory;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;

//烟机mqtt私有协议
public class MqttVentilator extends MqttPublic {
    private final int BufferSize = 1024 * 2;
    private static final int GUID_SIZE = 17;
    private final int CMD_CODE_SIZE = 1;
    public static final ByteOrder BYTE_ORDER = ByteOrder.LITTLE_ENDIAN;


    private void decodeMsg(int msgId, String guid, byte[] payload, int offset) {
//从payload中取值角标
        //远程被控制
        switch (msgId) {
            case MsgKeys.getDeviceAttribute_Req:

                break;
            case MsgKeys.setDeviceAttribute_Req:
                //属性个数
                short number = ByteUtils.toShort(payload[offset]);
                break;
            default:

                break;
        }
    }
    //烟机端的协议
    private void encodeMsg(ByteBuffer buf, MqttMsg msg) {
        //远程控制其他设备或通知上报

        int msgId = msg.getID();
        switch (msgId) {
            case MsgKeys.DeviceConnected_Noti:
                buf.put((byte) 1);
                buf.put("0000000000".getBytes());
                buf.put(VentilatorFactory.getPlatform().getMac().getBytes());
                buf.put(msg.getGuid().getBytes());
                buf.put((byte) VentilatorFactory.getPlatform().getMac().length());
                buf.put(VentilatorFactory.getPlatform().getMac().getBytes());
                buf.put((byte) 1);
                buf.put((byte) 4);
                buf.put((byte) 1);
                break;
            case MsgKeys.setDeviceAttribute_Rep:
                buf.put((byte) 1);
                buf.put((byte) 0);
                break;
            case MsgKeys.getDeviceAttribute_Req:  //属性查询
                buf.put((byte) 0x00);
                break;
            case MsgKeys.GetFanStatus_Req: //烟机状态查询
                buf.put((byte) ITerminalType.PAD);
                break;
        }
    }

    @Override
    protected void onDecodeMsg(int msgId, String srcGuid, byte[] payload, int offset) {
        //分发到各设备
        for (Device device: AccountInfo.getInstance().deviceList) {
            if (srcGuid.equals(device.guid)) {
                LogUtils.e("srcGuid " + srcGuid);
                device.onReceivedMsg(msgId, srcGuid, payload, offset);
                break;
            }
        }
    }

    @Override
    protected void onEncodeMsg(ByteBuffer buf, MqttMsg msg) {
        encodeMsg(buf, msg);
    }
}

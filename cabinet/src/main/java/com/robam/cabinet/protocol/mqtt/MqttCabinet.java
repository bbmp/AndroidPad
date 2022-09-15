package com.robam.cabinet.protocol.mqtt;

import com.robam.cabinet.bean.Cabinet;
import com.robam.cabinet.constant.CabinetConstant;
import com.robam.common.ITerminalType;
import com.robam.common.mqtt.IProtocol;
import com.robam.common.mqtt.MqttMsg;
import com.robam.common.mqtt.MqttPublic;
import com.robam.common.mqtt.MsgKeys;
import com.robam.common.utils.ByteUtils;

import org.json.JSONException;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

//消毒柜mqtt
public class MqttCabinet extends MqttPublic {

    @Override
    protected void onDecodeMsg(MqttMsg msg, byte[] payload, int offset) throws Exception {
        switch (msg.getID()) {
            case MsgKeys.GetSteriStatus_Rep: //消毒柜状态响应
                short steriStatus =
                        ByteUtils.toShort(payload[offset++]);
                msg.putOpt(CabinetConstant.SteriStatus, steriStatus);
                short steriLock =
                        ByteUtils.toShort(payload[offset++]);
                short steriWorkLeftTimeL =
                        ByteUtils.toShort(payload[offset++]);
                short steriWorkLeftTimeH =
                        ByteUtils.toShort(payload[offset++]);
                short ateriAlarmStatus =
                        ByteUtils.toShort(payload[offset++]);
                break;
        }
    }

    @Override
    protected void onEncodeMsg(ByteBuffer buf, MqttMsg msg) {
        switch (msg.getID()) {
            case MsgKeys.GetSteriStatus_Req: //消毒柜状态查询
                buf.put((byte) ITerminalType.PAD);
                break;
        }
    }

}

package com.robam.pan.protocol.mqtt;

import android.text.TextUtils;

import com.robam.common.ITerminalType;
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
import com.robam.pan.constant.PanConstant;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.HashMap;
import java.util.Map;

//锅mqtt
public class MqttPan extends MqttPublic {
    private final int BufferSize = 1024 * 2;
    private static final int GUID_SIZE = 17;
    private final int CMD_CODE_SIZE = 1;
    public static final ByteOrder BYTE_ORDER = ByteOrder.LITTLE_ENDIAN;

    private void encodeMsg(ByteBuffer buf, MqttMsg msg) {
        switch (msg.getID()) {
            case MsgKeys.FanGetPanStatus_Req: //属性查询
                buf.put((byte) ITerminalType.PAD);
                break;
        }
    }

    private void decodeMsg(short msgId, String srcGuid, byte[] payload, int offset) {
    }

    @Override
    protected void onDecodeMsg(MqttMsg msg, byte[] payload, int offset) throws Exception{

        switch (msg.getID()) {
            case MsgKeys.SetPotTemp_Rep: //查询返回
                //属性个数
                short attributeNum = ByteUtils.toShort(payload[offset]);
                break;
        }
    }

    @Override
    protected void onEncodeMsg(ByteBuffer buf, MqttMsg msg) {
        encodeMsg(buf, msg);
    }
}

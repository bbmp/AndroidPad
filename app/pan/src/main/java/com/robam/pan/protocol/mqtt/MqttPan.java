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

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

//锅mqtt
public class MqttPan extends MqttPublic {
    private final int BufferSize = 1024 * 2;
    private static final int GUID_SIZE = 17;
    private final int CMD_CODE_SIZE = 1;
    public static final ByteOrder BYTE_ORDER = ByteOrder.LITTLE_ENDIAN;

    private void encodeMsg(ByteBuffer buf, MqttMsg msg) {
        switch (msg.getID()) {
            case MsgKeys.GetPotTemp_Req: //属性查询
                buf.put((byte) ITerminalType.PAD);
                break;
        }
    }

    private void decodeMsg(short msgId, String srcGuid, byte[] payload, int offset) {
    }

    @Override
    protected void onDecodeMsg(int msgId, String srcGuid, byte[] payload, int offset) {
        switch (msgId) {
            case MsgKeys.SetPotTemp_Rep: //查询返回
                float temp = ByteUtils.toFloat(payload, offset++, ByteUtils.BYTE_ORDER);
                offset++;
                offset++;
                offset++;
                short status = ByteUtils.toShort(payload[offset++]);
                //参数个数
                short count = ByteUtils.toShort(payload[offset++]);
                while (count >= 0) {
                    short valueKey = ByteUtils.toShort(payload[offset++]);
                    short valueLength = ByteUtils.toShort(payload[offset++]);
                    switch (valueKey) {
                        case 1: //无人锅电机模式
                            short Pot_ESPMode = ByteUtils.toShort(payload[offset++]);
                            break;
                        case 2: //无人锅锅盖状态
                            short Pot_LisState = ByteUtils.toShort(payload[offset++]);
                            break;
                        case 3: //P档菜谱值
                            short Pot_PMenuValue = ByteUtils.toShort(payload[offset++]);
                            break;
                        case 4: //无人锅平台菜谱/曲线还原模式值  len 4
                            short Pot_PlatformMenuValue = ByteUtils.toShort(payload[offset++]);
                            offset++;
                            offset++;
                            offset++;
                            break;
                        case 5: //无人锅电量
                            short Pot_ElectricValue = ByteUtils.toShort(payload[offset++]);
                            break;
                        case 6: //无人锅模式状态
                            short Pot_ModelState = ByteUtils.toShort(payload[offset++]);
                            break;
                        case 7: //无人锅本地记录状态
                            short Pot_LocalRecordState = ByteUtils.toShort(payload[offset++]);
                            break;
                        case 8: //菜谱/曲线还原运行秒数
                            short Pot_MenuRestoreSecond = ByteUtils.toShort(payload[offset++]);
                            offset++;
                            break;
                        case 9: //无人锅绑定炉头
                            short Pot_BindHead = ByteUtils.toShort(payload[offset++]);
//                            offset++;
                            break;
                        default:
                            offset += valueLength;
                            break;
                    }
                    count--;
                }
                break;
        }
    }

    @Override
    protected void onEncodeMsg(ByteBuffer buf, MqttMsg msg) {
        encodeMsg(buf, msg);
    }
}

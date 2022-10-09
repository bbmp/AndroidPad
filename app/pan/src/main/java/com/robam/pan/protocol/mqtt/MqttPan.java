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
import com.robam.pan.constant.QualityKeys;

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
            case MsgKeys.GetPotTemp_Req: //属性查询
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
                float temp = MsgUtils.bytes2FloatLittle(payload, offset);//锅温
                offset += 4;
                int status = MsgUtils.getByte(payload[offset++]);//状态
                int attributeNum = MsgUtils.getByte(payload[offset++]);//属性个数
                while (attributeNum > 0) {
                    attributeNum--;
                    int key = MsgUtils.getByte(payload[offset++]);
                    int length = MsgUtils.getByte(payload[offset++]);
                    switch (key) {
                        case QualityKeys.key1:
                            MsgUtils.getString(payload, offset, 5);
                            offset += 5;
                            break;
                        case QualityKeys.key2:
                            int lidStatus = MsgUtils.getByte(payload[offset++]);//锅盖状态
                            break;
                        case QualityKeys.key3:
                            int pValue = MsgUtils.getByte(payload[offset++]);//p档菜谱值
                            break;
                        case QualityKeys.key4:
                            int recipeud = MsgUtils.bytes2IntLittle(payload, offset);
                            offset += 4;
                            break;
                        case QualityKeys.key5:
                            int battery = MsgUtils.getByte(payload[offset++]);//电量
                            break;
                        case QualityKeys.key6:
                            int mode = MsgUtils.getByte(payload[offset++]);//模式
                            break;
                        case QualityKeys.key7:
                            int localStatus = MsgUtils.getByte(payload[offset++]);//本地记录状态
                            break;
                        case QualityKeys.key8:
                            int runSeconds = MsgUtils.bytes2ShortLittle(payload, offset);//运行秒数
                            offset += 2;
                            break;
                        case QualityKeys.key9:
                            int recipeStoveid = MsgUtils.getByte(payload[offset++]);//菜谱炉头id
                            break;
                        case  QualityKeys.key10:
                            int setSeconds = MsgUtils.getByte(payload[offset++]);//设置秒数
                            break;
                    }
                }
                break;
        }
    }

    @Override
    protected void onEncodeMsg(ByteBuffer buf, MqttMsg msg) {
        encodeMsg(buf, msg);
    }
}

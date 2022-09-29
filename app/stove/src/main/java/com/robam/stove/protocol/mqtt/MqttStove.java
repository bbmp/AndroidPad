package com.robam.stove.protocol.mqtt;

import com.robam.common.ITerminalType;
import com.robam.common.bean.AccountInfo;
import com.robam.common.bean.RTopic;
import com.robam.common.device.Plat;
import com.robam.common.mqtt.IProtocol;
import com.robam.common.mqtt.MqttManager;
import com.robam.common.mqtt.MqttMsg;
import com.robam.common.mqtt.MqttPublic;
import com.robam.common.mqtt.MsgKeys;
import com.robam.common.utils.ByteUtils;
import com.robam.common.utils.DeviceUtils;
import com.robam.common.utils.MsgUtils;
import com.robam.stove.constant.StoveConstant;
import com.robam.stove.device.HomeStove;
import com.robam.stove.device.StoveAbstractControl;
import com.robam.stove.device.StoveFactory;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

//灶具mqtt实现
public class MqttStove extends MqttPublic {


    @Override
    protected void onDecodeMsg(MqttMsg msg, byte[] payload, int offset) throws Exception {
        switch (msg.getID()) {
            case MsgKeys.DeviceConnected_Noti: //设备上线通知
                AccountInfo.getInstance().getGuid().setValue(msg.getGuid());
                break;
            case MsgKeys.GetStoveStatus_Req: //查询灶状态
                //答复
//                String curGuid = msg.getrTopic().getDeviceType() + msg.getrTopic().getSignNum(); //当前设备guid
//                MqttMsg newMsg = new MqttMsg.Builder()
//                        .setMsgId(MsgKeys.GetStoveStatus_Rep)
//                        .setGuid(curGuid)
//                        .setDt(Plat.getPlatform().getDt())
//                        .setTopic(new RTopic(RTopic.TOPIC_UNICAST, DeviceUtils.getDeviceTypeId(msg.getGuid()), DeviceUtils.getDeviceNumber(msg.getGuid())))
//                        .build();


                break;
            case MsgKeys.SetStoveStatus_Req: //设置灶状态
                short terminalType = ByteUtils.toShort(payload[offset++]); //控制端类型
                String user = MsgUtils.getString(payload, offset, 10); //user
                offset += 10;
                short isCook = ByteUtils.toShort(payload[offset++]); //是否菜谱做菜
                msg.putOpt(StoveConstant.isCook, isCook);
                short id = ByteUtils.toShort(payload[offset++]); //炉头id
                msg.putOpt(StoveConstant.stoveId, id);
                short workStatus = ByteUtils.toShort(payload[offset++]);//工作状态
                msg.putOpt(StoveConstant.workStatus, workStatus);

                break;
        }
    }

    @Override
    protected void onEncodeMsg(ByteBuffer buf, MqttMsg msg) {
        switch (msg.getID()) {
            case MsgKeys.DeviceConnected_Noti://上线通知
                buf.put((byte) 1);
                buf.put("0000000000".getBytes());
                buf.put(DeviceUtils.getDeviceNumber(msg.getGuid()).getBytes()); //mac
                buf.put(msg.getGuid().getBytes());
                buf.put((byte) DeviceUtils.getDeviceNumber(msg.getGuid()).length());
                buf.put(DeviceUtils.getDeviceNumber(msg.getGuid()).getBytes());
                buf.put((byte) 1);
                buf.put((byte) 4);
                buf.put((byte) 1);
                break;
            case MsgKeys.GetStoveStatus_Req: //查询状态
                //控制端类型
                buf.put((byte) ITerminalType.PAD);
                break;
            case MsgKeys.GetStoveStatus_Rep:
                buf.put((byte) 0x02); //炉头个数
                buf.put((byte) 0x00);//童锁
                buf.put((byte) 0x00);// 工作状态
                buf.put((byte) 0x00);// 功率等级
                buf.putShort((short) 0); //定时剩余秒数
                buf.put((byte) 0x00);// 报警状态
                buf.put((byte) 0x00);// 功率等级
                buf.put((byte) 0x00); //参数个数

                break;
            case MsgKeys.SetStoveStatus_Req: //设置灶具状态
                //控制端类型
                buf.put((byte) ITerminalType.PAD);
                buf.put(AccountInfo.getInstance().getUserString().getBytes());
                buf.put((byte) msg.opt(StoveConstant.isCook));
                buf.put((byte) msg.opt(StoveConstant.stoveId));
                buf.put((Byte) msg.opt(StoveConstant.workStatus));
                break;
            case MsgKeys.SetStoveLevel_Req: //设置挡位
                buf.put((byte) ITerminalType.PAD);
                buf.put(AccountInfo.getInstance().getUserString().getBytes());
                buf.put((byte) msg.opt(StoveConstant.isCook));
                buf.put((byte) msg.opt(StoveConstant.stoveId));
                buf.put((byte) msg.opt(StoveConstant.level));
                break;
            case MsgKeys.SetStoveShutdown_Req: //设置灶定时关火
                buf.put((byte) ITerminalType.PAD);
                buf.put((byte) msg.opt(StoveConstant.stoveId));
                buf.put((byte) msg.opt(StoveConstant.timingtime));
                break;
        }
    }
}

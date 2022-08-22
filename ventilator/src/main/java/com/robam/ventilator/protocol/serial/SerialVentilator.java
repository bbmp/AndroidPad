package com.robam.ventilator.protocol.serial;

import com.robam.common.utils.Crc16Utils;

//烟机串口协议部分
public class SerialVentilator {
    private static final byte FRAME_HEAD = (byte)0x55;//帧头
    private static final byte FRAME_HEAD_SEND = (byte)0xAA;//帧头
    public static final byte MSG_TYPE_CMD = 0x01;//消息类型:功能选择
    public static final byte MSG_TYPE_POOL = 0x0F;//消息类型:功能查询
    private static final byte VERSION = 1;//程序版本号

    //功能选择打包命令 1.2协议对应长度
    private static byte[] packCtrlCmd(byte[] payload) {  //payload
        byte[] result = new byte[29];
        result[0] = FRAME_HEAD_SEND;
        result[1] = 28;//len
        result[2] = MSG_TYPE_CMD;
        result[3] = VERSION;      //公共部分
        System.arraycopy(payload, 0, result, 4, payload.length);
        short crc = Crc16Utils.calcCrc16(result, 1, result.length - 3);
        result[result.length - 2] = (byte)(crc & 0xff);
        result[result.length - 1] = (byte)((crc >> 8) & 0xff);
        return result;
    }
    public static byte[] powerOn(){

        byte[] payload = new byte[]{
        };
        byte[] data = packCtrlCmd(payload);
        return data ;
    }
}

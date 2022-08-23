package com.robam.ventilator.protocol.serial;

import com.robam.common.utils.Crc16Utils;
import com.robam.common.utils.LogUtils;
import com.robam.common.utils.StringUtils;

import java.util.Arrays;

//烟机串口协议部分
public class SerialVentilator {
    private static final byte FRAME_HEAD = (byte)0x55;//帧头
    private static final byte FRAME_HEAD_SEND = (byte)0xAA;//帧头
    public static final byte u8ID_Number_HIGHBYTE = 0x50;//
    public static final byte MSG_TYPE_QUERY = 0x01;//查询
    public static final byte MSG_TYPE_CONTROL = 0x02;//控制
    private static final byte u8ID_Number_LOWBYTE = 0x68;
    private static final int u8FirmwareVersion = 0;//程序版本号
    /**
     * byte6 开机/关机
     */
    public static byte startup = 0x00 ;
    public static int startupIndex = 6 ;

    /**
     * byte7 开灯/关灯
     */
    public static byte lightOn = (byte) 0xA0;
    public static int lightOnIndex = 7;
    /**
     * byte8 风机挡位
     */
    public static byte gear = (byte) 0xA0;
    public static int gearIndex = 8;
    /**
     * byte9 蜂鸣器
     */
    public static byte beep = 0x00;
    public static int beepIndex = 9;
    /**
     * byte10 风门挡板
     */
    public static byte baffle = (byte) 0xA0;
    public static int baffleIndex = 10;
    /**
     * byte11 参数1 风门电流反馈值
     */
    public static byte param1 = 0x00;
    public static int param1Index = 11;
    /**
     * byte12 参数2 风门状态反馈值
     */
    public static byte param2 = 0x00;
    public static int param2Index = 12;
    /**
     * byte13 参数3 风机功率/2反馈值
     */
    public static byte param3 = 0x00;
    public static int param3Index = 13;
    /**
     * byte14 参数4（风机转速/10反馈值
     */
    public static byte param4 = 0x00;
    public static int param4Index = 14;
    /**
     * byte15 参数5预留（可以用来传递温度值
     */
    public static byte param5 = 0x00;
    public static int param5Index = 15;
    /**
     * byte16 风门挡板2
     */
    public static byte baffle2 = (byte) 0x00;
    public static int baffle2Index = 16;
    /**
     * byte17 参数6 设置可调色温灯颜色	0）-100，0表示全冷100全
     */
    public static byte param6 = 0;
    public static int param6Index = 17;
    /**
     * byte18  参数7 设置智感恒吸模式
     */
    public static byte param7 = (byte) 0x00;
    public static int param7Index = 18;
    /**
     * byte19  参数8 智感恒吸阻力值
     */
    public static byte param8 = (byte) 0x00;
    public static int param8Index = 19;
    /**
     * byte20  参数9 预留
     */
    public static byte param9 = (byte) 0x00;
    public static int param9Index = 20;

    //功能选择打包命令 1.2协议对应长度
    private static byte[] packCtrlCmd(byte[] payload) {  //payload
        byte[] result = new byte[23];
        result[0] = FRAME_HEAD_SEND;
        result[1] = 22;//len
        result[2] = u8ID_Number_HIGHBYTE;
        result[3] = u8ID_Number_LOWBYTE;
        result[4] = u8FirmwareVersion;  //公共部分
        result[5] = MSG_TYPE_CONTROL;
        System.arraycopy(payload, 0, result, 6, payload.length);
        short crc = Crc16Utils.calcCrc16(result, 1, result.length - 3);
        result[result.length - 2] = (byte)(crc & 0xff);
        result[result.length - 1] = (byte)((crc >> 8) & 0xff);
        return result;
    }

    //功能查询打包命令
    public static byte[] packQueryCmd() {
        byte[] result = new byte[10];
        result[0] = FRAME_HEAD_SEND;
        result[1] = 9;//len
        result[2] = u8ID_Number_HIGHBYTE;
        result[3] = u8ID_Number_LOWBYTE;
        result[4] = u8FirmwareVersion;  //公共部分
        result[5] = MSG_TYPE_QUERY;
        result[6] = 0x00;
        result[7] = 0x00;
        short crc = Crc16Utils.calcCrc16(result, 1, result.length - 3);
        result[result.length - 2] = (byte)(crc & 0xff);
        result[result.length - 1] = (byte)((crc >> 8) & 0xff);
        return result;
    }
    //开机
    public static byte[] powerOn(){
        startup = 0x01;
        byte[] payload = new byte[]{
                startup,
                lightOn,
                gear,
                beep,
                baffle,
                param1,
                param2,
                param3,
                param4,
                param5,
                baffle2,
                param6,
                param7,
                param8,
                param9
        };
        byte[] data = packCtrlCmd(payload);
        LogUtils.i(StringUtils.bytes2Hex(data));
        return data ;
    }

    /**
     * 开灯 bit0置为1
     * @return
     */
    public static byte[] onLamp(){
        lightOn = (byte) 0xA1;
        byte[] payload = new byte[]{
                startup,
                lightOn,
                gear,
                beep,
                baffle,
                param1,
                param2,
                param3,
                param4,
                param5,
                baffle2,
                param6,
                param7,
                param8,
                param9
        };
        byte[] data = packCtrlCmd(payload);
        LogUtils.i(StringUtils.bytes2Hex(data));
        return data ;
    }
    /**
     * 关灯 bit0置为0
     * @return
     */
    public static byte[] offLamp(){
        lightOn = (byte) 0xA0;
        byte[] payload = new byte[]{
                startup,
                lightOn,
                gear,
                beep,
                baffle,
                param1,
                param2,
                param3,
                param4,
                param5,
                baffle2,
                param6,
                param7,
                param8,
                param9
        };
        byte[] data = packCtrlCmd(payload);
        LogUtils.i(StringUtils.bytes2Hex(data));
        return data ;
    }
    /**
     * 风机挡位
     */
    public static byte[] setGear(byte curgear) {
        gear = curgear;
        byte[] payload = new byte[]{
                startup,
                lightOn,
                gear,
                beep,
                baffle,
                param1,
                param2,
                param3,
                param4,
                param5,
                baffle2,
                param6,
                param7,
                param8,
                param9
        };
        byte[] data = packCtrlCmd(payload);
        LogUtils.i(StringUtils.bytes2Hex(data));
        return data ;
    }
    /**
     * 设置可调色温灯
     */
    public static byte[] setColorLamp(byte color) { //0-100 0全冷 100 全暖
        param6 = color;
        byte[] payload = new byte[]{
                startup,
                lightOn,
                gear,
                beep,
                baffle,
                param1,
                param2,
                param3,
                param4,
                param5,
                baffle2,
                param6,
                param7,
                param8,
                param9
        };
        byte[] data = packCtrlCmd(payload);
        LogUtils.i(StringUtils.bytes2Hex(data));
        return data ;
    }
    /**
     * 设置智感恒吸
     */
    public static byte[] setSmart(byte smart) {
        param7 = smart;
        byte[] payload = new byte[]{
                startup,
                lightOn,
                gear,
                beep,
                baffle,
                param1,
                param2,
                param3,
                param4,
                param5,
                baffle2,
                param6,
                param7,
                param8,
                param9
        };
        byte[] data = packCtrlCmd(payload);
        LogUtils.i(StringUtils.bytes2Hex(data));
        return data ;
    }


    //包解析,解析串口收到的数据
    public static byte[] parseSerial(byte[] data) {
        //数据为空
        if (null == data || data.length <= 8)
            return null;
        //check head
        if (data[0] != 0xBA)
            return null;
        //device error
        if (data[2] != 0x50 || data[3] != 0x68)
            return null;
        int length = data[1];
        //length error
        if (length != data.length -1)
            return null;
        short crc = Crc16Utils.calcCrc16(data, 1, length - 2);
        //crc error
        if ((byte)(crc & 0xff) != data[length - 2] || (byte)((crc >> 8) & 0xff) != data[length - 1])
            return null;

        int msgType = data[5];
        switch (msgType) {
            //查询返回
            case MSG_TYPE_QUERY:
                startup = data[startupIndex];
                lightOn = data[lightOnIndex];
                gear    = data[gearIndex   ];
                beep    = data[beepIndex   ];
                baffle  = data[baffleIndex ];
                param1  = data[param1Index ];
                param2  = data[param2Index ];
                param3  = data[param3Index ];
                param4  = data[param4Index ];
                param5  = data[param5Index ];
                baffle2 = data[baffle2Index];
                param6  = data[param6Index ];
                param7  = data[param7Index ];
                param8  = data[param8Index ];
                param9  = data[param9Index ];
                break;
            //控制返回
            case MSG_TYPE_CONTROL:
                //0x5A：成功；         0xA5：失败；
                byte result = data[6];
                break;
        }
        return null;
    }
}

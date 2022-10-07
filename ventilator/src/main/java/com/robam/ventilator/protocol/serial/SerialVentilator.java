package com.robam.ventilator.protocol.serial;

import android.serialport.helper.SerialPortHelper;

import com.robam.common.utils.ByteUtils;
import com.robam.common.utils.Crc16Utils;
import com.robam.common.utils.LogUtils;
import com.robam.common.utils.StringUtils;
import com.robam.ventilator.device.HomeVentilator;

import java.util.Arrays;

//烟机串口协议部分
public class SerialVentilator {
    private static final byte FRAME_HEAD = (byte)0x55;//帧头
    private static final byte FRAME_HEAD_SEND = (byte)0xAA;//帧头
    private static final byte FRAME_HEAD_RES = (byte) 0xBA; //
    public static final byte u8ID_Number_HIGHBYTE = (byte)0x50;//
    public static final byte MSG_TYPE_QUERY = 0x01;//查询
    public static final byte MSG_TYPE_CONTROL = 0x02;//控制
    private static final byte u8ID_Number_LOWBYTE = (byte)0x68;
    private static final int u8FirmwareVersion = 0;//程序版本号
    private static final byte MSG_SUCCESS = (byte) 0x5A;
    private static final byte MSG_FAILED = (byte) 0xA5;

    /**
     * byte6 开机/关机
     */
    public static int startupIndex = 6 ;

    /**
     * byte7 开灯/关灯
     */
    public static int lightOnIndex = 7;
    /**
     * byte8 风机挡位
     */
    public static int gearIndex = 8;
    /**
     * byte9 蜂鸣器
     */
    public static int beepIndex = 9;
    /**
     * byte10 风门挡板
     */
    public static int baffleIndex = 10;
    /**
     * byte11 参数1 风门电流反馈值
     */
    public static int param1Index = 11;
    /**
     * byte12 参数2 风门状态反馈值
     */
    public static int param2Index = 12;
    /**
     * byte13 参数3 风机功率/2反馈值
     */
    public static int param3Index = 13;
    /**
     * byte14 参数4（风机转速/10反馈值
     */
    public static int param4Index = 14;
    /**
     * byte15 参数5预留（可以用来传递温度值
     */
    public static int param5Index = 15;
    /**
     * byte16 风门挡板2
     */
    public static int baffle2Index = 16;
    /**
     * byte17 参数6 设置可调色温灯颜色	0）-100，0表示全冷100全
     */
    public static int param6Index = 17;
    /**
     * byte18  参数7 设置智感恒吸模式
     */
    public static int param7Index = 18;
    /**
     * byte19  参数8 智感恒吸阻力值
     */
    public static int param8Index = 19;
    /**
     * byte20  参数9 预留
     */
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
        result[result.length - 1] = (byte)(crc & 0xff);
        result[result.length - 2] = (byte)((crc >> 8) & 0xff);
        LogUtils.e(StringUtils.bytes2Hex(result));
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
        result[result.length - 1] = (byte)(crc & 0xff);
        result[result.length - 2] = (byte)((crc >> 8) & 0xff);
        LogUtils.e(StringUtils.bytes2Hex(result));
        return result;
    }
    //开机
    public static byte[] powerOn(){
        byte startup = (byte) 0x01;  //开机
        byte beep = (byte) 0x02;     //蜂鸣
        byte[] payload = new byte[]{
                startup,
                HomeVentilator.getInstance().lightOn,
                HomeVentilator.getInstance().gear,
                beep,
                HomeVentilator.getInstance().baffle,
                HomeVentilator.getInstance().param1,
                HomeVentilator.getInstance().param2,
                HomeVentilator.getInstance().param3,
                HomeVentilator.getInstance().param4,
                HomeVentilator.getInstance().param5,
                HomeVentilator.getInstance().baffle2,
                HomeVentilator.getInstance().param6,
                HomeVentilator.getInstance().param7,
                HomeVentilator.getInstance().param8,
                HomeVentilator.getInstance().param9
        };
        byte[] data = packCtrlCmd(payload);
        LogUtils.i(StringUtils.bytes2Hex(data));
        return data ;
    }

    //关机机
    public static byte[] shutDown(){
        byte startup = (byte) 0x00;
        byte beep = (byte) 0x03;
        byte[] payload = new byte[]{
                startup,
                HomeVentilator.getInstance().lightOn,
                HomeVentilator.getInstance().gear,
                beep,
                HomeVentilator.getInstance().baffle,
                HomeVentilator.getInstance().param1,
                HomeVentilator.getInstance().param2,
                HomeVentilator.getInstance().param3,
                HomeVentilator.getInstance().param4,
                HomeVentilator.getInstance().param5,
                HomeVentilator.getInstance().baffle2,
                HomeVentilator.getInstance().param6,
                HomeVentilator.getInstance().param7,
                HomeVentilator.getInstance().param8,
                HomeVentilator.getInstance().param9
        };
        byte[] data = packCtrlCmd(payload);
        LogUtils.i(StringUtils.bytes2Hex(data));
        return data ;
    }

    //设置烟机状态
    public static byte[] setFanStatus(byte status) {
        byte startup = status;
        byte[] payload = new byte[]{
                startup,
                HomeVentilator.getInstance().lightOn,
                HomeVentilator.getInstance().gear,
                HomeVentilator.getInstance().beep,
                HomeVentilator.getInstance().baffle,
                HomeVentilator.getInstance().param1,
                HomeVentilator.getInstance().param2,
                HomeVentilator.getInstance().param3,
                HomeVentilator.getInstance().param4,
                HomeVentilator.getInstance().param5,
                HomeVentilator.getInstance().baffle2,
                HomeVentilator.getInstance().param6,
                HomeVentilator.getInstance().param7,
                HomeVentilator.getInstance().param8,
                HomeVentilator.getInstance().param9
        };
        byte[] data = packCtrlCmd(payload);
        LogUtils.i(StringUtils.bytes2Hex(data));
        return data ;
    }
    /**
     * 灯控制
     * @return
     */
    public static byte[] setLight(byte light){
        byte lightOn = light;
        byte[] payload = new byte[]{
                HomeVentilator.getInstance().startup,
                lightOn,
                HomeVentilator.getInstance().gear,
                HomeVentilator.getInstance().beep,
                HomeVentilator.getInstance().baffle,
                HomeVentilator.getInstance().param1,
                HomeVentilator.getInstance().param2,
                HomeVentilator.getInstance().param3,
                HomeVentilator.getInstance().param4,
                HomeVentilator.getInstance().param5,
                HomeVentilator.getInstance().baffle2,
                HomeVentilator.getInstance().param6,
                HomeVentilator.getInstance().param7,
                HomeVentilator.getInstance().param8,
                HomeVentilator.getInstance().param9
        };
        byte[] data = packCtrlCmd(payload);
        LogUtils.i(StringUtils.bytes2Hex(data));
        return data ;
    }

    /**
     * 风机挡位
     */
    public static byte[] setGear(byte curgear) {
        byte gear = curgear;
        byte beep = (byte) 0x02;     //蜂鸣
        byte[] payload = new byte[]{
                HomeVentilator.getInstance().startup,
                HomeVentilator.getInstance().lightOn,
                gear,
                beep,
                HomeVentilator.getInstance().baffle,
                HomeVentilator.getInstance().param1,
                HomeVentilator.getInstance().param2,
                HomeVentilator.getInstance().param3,
                HomeVentilator.getInstance().param4,
                HomeVentilator.getInstance().param5,
                HomeVentilator.getInstance().baffle2,
                HomeVentilator.getInstance().param6,
                HomeVentilator.getInstance().param7,
                HomeVentilator.getInstance().param8,
                HomeVentilator.getInstance().param9
        };
        byte[] data = packCtrlCmd(payload);
        LogUtils.i(StringUtils.bytes2Hex(data));
        return data ;
    }

    /**
     * 设置风门挡板
     * @param door
     * @return
     */
    public static byte[] setDoor(byte door) {
        byte baffle = door;
        byte[] payload = new byte[]{
                HomeVentilator.getInstance().startup,
                HomeVentilator.getInstance().lightOn,
                HomeVentilator.getInstance().gear,
                HomeVentilator.getInstance().beep,
                baffle,
                HomeVentilator.getInstance().param1,
                HomeVentilator.getInstance().param2,
                HomeVentilator.getInstance().param3,
                HomeVentilator.getInstance().param4,
                HomeVentilator.getInstance().param5,
                HomeVentilator.getInstance().baffle2,
                HomeVentilator.getInstance().param6,
                HomeVentilator.getInstance().param7,
                HomeVentilator.getInstance().param8,
                HomeVentilator.getInstance().param9
        };
        byte[] data = packCtrlCmd(payload);
        LogUtils.i(StringUtils.bytes2Hex(data));
        return data ;
    }
    /**
     * 设置可调色温灯
     */
    public static byte[] setColorLamp(byte color) { //0-100 0全冷 100 全暖
        byte param6 = color;
        byte[] payload = new byte[]{
                HomeVentilator.getInstance().startup,
                HomeVentilator.getInstance().lightOn,
                HomeVentilator.getInstance().gear,
                HomeVentilator.getInstance().beep,
                HomeVentilator.getInstance().baffle,
                HomeVentilator.getInstance().param1,
                HomeVentilator.getInstance().param2,
                HomeVentilator.getInstance().param3,
                HomeVentilator.getInstance().param4,
                HomeVentilator.getInstance().param5,
                HomeVentilator.getInstance().baffle2,
                param6,
                HomeVentilator.getInstance().param7,
                HomeVentilator.getInstance().param8,
                HomeVentilator.getInstance().param9
        };
        byte[] data = packCtrlCmd(payload);
        LogUtils.i(StringUtils.bytes2Hex(data));
        return data ;
    }
    /**
     * 设置智感恒吸
     */
    public static byte[] setSmart(byte smart) {
        byte param7 = smart;
        byte[] payload = new byte[]{
                HomeVentilator.getInstance().startup,
                HomeVentilator.getInstance().lightOn,
                HomeVentilator.getInstance().gear,
                HomeVentilator.getInstance().beep,
                HomeVentilator.getInstance().baffle,
                HomeVentilator.getInstance().param1,
                HomeVentilator.getInstance().param2,
                HomeVentilator.getInstance().param3,
                HomeVentilator.getInstance().param4,
                HomeVentilator.getInstance().param5,
                HomeVentilator.getInstance(). baffle2,
                HomeVentilator.getInstance().param6,
                param7,
                HomeVentilator.getInstance().param8,
                HomeVentilator.getInstance().param9
        };
        byte[] data = packCtrlCmd(payload);
        LogUtils.i(StringUtils.bytes2Hex(data));
        return data ;
    }


    //包解析,解析串口收到的数据
    public static byte[] parseSerial(byte[] data, int recLen) {
        LogUtils.e(StringUtils.bytes2Hex(data));
        //数据为空
        if (null == data)
            return null;
        //check head
        if (data[0] != FRAME_HEAD_RES)
            return null;
        //device error
        if (data[2] != u8ID_Number_HIGHBYTE || data[3] != u8ID_Number_LOWBYTE)
            return null;
        int length = ByteUtils.toInt(data[1]);
        //length error
        if (length > data.length -1)
            return null;
        short crc = Crc16Utils.calcCrc16(data, 1, length - 2);
        byte low = (byte)(crc & 0xff);
        byte high = (byte)((crc >> 8) & 0xff);
        //crc error
        if ((byte)(crc & 0xff) != data[length] || (byte)((crc >> 8) & 0xff) != data[length - 1])
            return null;

        int msgType = ByteUtils.toInt(data[5]);
        switch (msgType) {
            //查询返回
            case MSG_TYPE_QUERY:
                HomeVentilator.getInstance().startup = data[startupIndex];
                HomeVentilator.getInstance().lightOn = data[lightOnIndex];
                HomeVentilator.getInstance().gear    = data[gearIndex   ];
                HomeVentilator.getInstance().beep    = data[beepIndex   ];
                HomeVentilator.getInstance().baffle  = data[baffleIndex ];
                HomeVentilator.getInstance().param1  = data[param1Index ];
                HomeVentilator.getInstance().param2  = data[param2Index ];
                HomeVentilator.getInstance().param3  = data[param3Index ];
                HomeVentilator.getInstance().param4  = data[param4Index ];
                HomeVentilator.getInstance().param5  = data[param5Index ];
                HomeVentilator.getInstance().baffle2 = data[baffle2Index];
                HomeVentilator.getInstance().param6  = data[param6Index ];
                HomeVentilator.getInstance().param7  = data[param7Index ];
                HomeVentilator.getInstance().param8  = data[param8Index ];
                HomeVentilator.getInstance().param9  = data[param9Index ];
                break;
            //控制返回
            case MSG_TYPE_CONTROL:
                //0x5A：成功；         0xA5：失败；
                if (data[6] == MSG_SUCCESS) {
                    SerialPortHelper.getInstance().addCommands(packQueryCmd()); //查询状态
                } else
                    LogUtils.e("failed");
                break;
        }
        return null;
    }
}

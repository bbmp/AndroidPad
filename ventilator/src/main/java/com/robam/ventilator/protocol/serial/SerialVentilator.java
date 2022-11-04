package com.robam.ventilator.protocol.serial;

import android.serialport.helper.SerialPortHelper;

import com.robam.common.bean.AccountInfo;
import com.robam.common.ble.BleDecoder;
import com.robam.common.device.Plat;
import com.robam.common.utils.ByteUtils;
import com.robam.common.utils.Crc16Utils;
import com.robam.common.utils.LogUtils;
import com.robam.common.utils.StringUtils;
import com.robam.ventilator.device.HomeVentilator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;

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

    private static final int MCU_UART_MIN_PAYLOAD_LEN = 1;
    private static final int MCU_UART_TX_MAX_PAYLOAD_LEN = 240;
    private static final int MCU_UART_RX_MAX_PAYLOAD_LEN = 240;
    private static final int MCU_UART_STATUS_START = 0;
    private static final int MCU_UART_STATUS_PARSE_SYNC1 = 1;//解析BA
    private static final int MCU_UART_STATUS_PARSE_LEN = 2;//解析LEN
    private static final int MCU_UART_STATUS_PARSE_HIGH = 3;//解析0x50
    private static final int MCU_UART_STATUS_PARSE_LOW = 4;//解析0x68
    private static final int MCU_UART_STATUS_PARSE_DATA = 5;//解析DATA
    private static final LinkedList<Byte> input_buf = new LinkedList<>();//待解析数据
    private static final ArrayList<Byte> decode_buf = new ArrayList<>();//解析后数据
    private static int decode_status = MCU_UART_STATUS_START;//解析状态机

    private static int cnt = 0;//应该payload接收长度(LEN字段值)
    private static int peek_idx = 0;//peek索引

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
        byte baffle = (byte) 0xA1; //风门挡板
        byte[] payload = new byte[]{
                startup,
                HomeVentilator.getInstance().lightOn,
                HomeVentilator.getInstance().gear,
                beep,
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
    //开机并设置挡位
    public static byte[] powerOnGear(byte curgear) {
        byte startup = (byte) 0x01;  //开机
        byte beep = (byte) 0x02;     //蜂鸣
        byte baffle = (byte) 0xA1; //风门挡板
        byte[] payload = new byte[]{
                startup,
                HomeVentilator.getInstance().lightOn,
                curgear,
                beep,
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

    //关机
    public static byte[] shutDown(){
        byte startup = (byte) 0x00;
        byte lightOn = (byte) 0xA0;
        byte gear = (byte) 0xA0;
        byte beep = (byte) 0x03;
        byte baffle = (byte) 0xA0;
        byte[] payload = new byte[]{
                startup,
                lightOn,
                gear,
                beep,
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
    //油网清洗
    public static byte[] openOilClean() {
        byte startup = (byte) 0x01;
        byte lightOn = (byte) 0xA1; //开灯
        byte gear = (byte) 0xA0;
        byte beep = (byte) 0x04;     //蜂鸣
        byte baffle = (byte) 0xA1; //打开风门挡板
        byte[] payload = new byte[]{
                startup,
                lightOn,
                gear,
                beep,
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
    //关闭油网清洗
    public static byte[] closeOilClean() {
        byte startup = (byte) 0x01;
        byte lightOn = (byte) 0xA0;
        byte gear = (byte) 0xA0;
        byte beep = (byte) 0x04;     //蜂鸣
        byte baffle = (byte) 0xA1; //风门挡板
        byte[] payload = new byte[]{
                startup,
                lightOn,
                gear,
                beep,
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
    public static byte[] setLight(byte light, byte baffle){
        byte lightOn = light;
        byte beep = (byte) 0x04;
        byte[] payload = new byte[]{
                HomeVentilator.getInstance().startup,
                lightOn,
                HomeVentilator.getInstance().gear,
                beep,
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
    public static byte[] setColorLamp() { //0-100 0全冷 100 全暖
        byte lightOn = (byte) 0xA1;
        byte beep = (byte) 0x04;
        byte param6 = 0;
        if (HomeVentilator.getInstance().param6 == 0)
            param6 = 100;
        else
            param6 = 0;
        byte baffle = (byte) 0xA1; //打开风门挡板
        byte[] payload = new byte[]{
                HomeVentilator.getInstance().startup,
                lightOn,
                HomeVentilator.getInstance().gear,
                beep,
                baffle,
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
        byte beep = (byte) 0x04;
        byte[] payload = new byte[]{
                HomeVentilator.getInstance().startup,
                HomeVentilator.getInstance().lightOn,
                HomeVentilator.getInstance().gear,
                beep,
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

    public static void init_decoder() {
        input_buf.clear();
        decode_buf.clear();
        decode_status = MCU_UART_STATUS_START;
        cnt = 0;
        peek_idx = 0;

    }

    public static void push_raw_data(Byte []data_array) {
        input_buf.addAll(Arrays.asList(data_array));
    }

    public static Byte [] decode_data() {
        Byte[] ret;
        byte val;

        while(input_buf.size() > 0 && peek_idx < input_buf.size()) {
            val = input_buf.get(peek_idx);
//            LogUtils.e(ByteUtils.toHex(val));
            peek_idx++;
            switch (decode_status) {
                case MCU_UART_STATUS_START:
                    cnt = 0;
                    decode_status = MCU_UART_STATUS_PARSE_SYNC1;
                    //break; //force to skip break;
                case MCU_UART_STATUS_PARSE_SYNC1:
                    pop_and_repeek_buf();
                    if(val == FRAME_HEAD_RES) {
                        decode_status = MCU_UART_STATUS_PARSE_LEN;
                    } else {
                        decode_status = MCU_UART_STATUS_START;
                        //Log.d(TAG, "sync1 error " + val);
                    }
                    break;
                case MCU_UART_STATUS_PARSE_LEN:
                   if(ByteUtils.toInt(val) >= MCU_UART_MIN_PAYLOAD_LEN &&
                            ByteUtils.toInt(val) <= MCU_UART_RX_MAX_PAYLOAD_LEN){
                        cnt = val;
                        decode_buf.clear();
                        decode_buf.add(val);
                        decode_status = MCU_UART_STATUS_PARSE_DATA;
                    } else {
                        pop_and_repeek_buf();
                        decode_status = MCU_UART_STATUS_START;
                        //Log.d(TAG, "len1 error " + val);
                    }
                    break;
                case MCU_UART_STATUS_PARSE_DATA:
                    decode_buf.add(val);
                    if(decode_buf.size() >= cnt) {
                        ret = mcu_uart_unpack(decode_buf);
                        if(ret != null) {
                            while(peek_idx > 0) {
                                input_buf.pop();
                                peek_idx--;
                            }
                        } else {
                            //Log.d(TAG, "mcu_uart_unpack error " + cnt);
                            pop_and_repeek_buf();
                        }
                        decode_status = MCU_UART_STATUS_START;
                        return ret;
                    }
                    break;
            }
        }
        return null;
    }

    private static void pop_and_repeek_buf() {
        input_buf.pop();
        peek_idx = 0;
    }

    private static Byte [] mcu_uart_unpack(ArrayList<Byte> src) {
        Byte [] dst;
        int dst_len = src.size();

        dst = src.toArray(new Byte[0]);

        if(dst == null) {
            return null;
        }
        byte data[] = BleDecoder.ByteArraysTobyteArrays(dst);
        //device error
        if (data[1] != u8ID_Number_HIGHBYTE || data[2] != u8ID_Number_LOWBYTE)
            return null;
        int length = ByteUtils.toInt(data[0]);
        //length error
        if (length > data.length)
            return null;
        short crc = Crc16Utils.calcCrc16(data, 0, dst_len - 2);
        byte low = (byte)(crc & 0xff);
        byte high = (byte)((crc >> 8) & 0xff);
        //crc error
        if ((byte)(crc & 0xff) != data[dst_len - 1] || (byte)((crc >> 8) & 0xff) != data[dst_len - 2])
            return null;

        int msgType = ByteUtils.toInt(data[4]);
        switch (msgType) {
            //查询返回
            case MSG_TYPE_QUERY:
                HomeVentilator.getInstance().startup = data[startupIndex - 1];
                HomeVentilator.getInstance().status  = data[startupIndex - 1]; //工作状态
                HomeVentilator.getInstance().lightOn = data[lightOnIndex - 1];
                HomeVentilator.getInstance().gear    = data[gearIndex    - 1];
                HomeVentilator.getInstance().beep    = data[beepIndex    - 1];
                HomeVentilator.getInstance().baffle  = data[baffleIndex  - 1];
                HomeVentilator.getInstance().param1  = data[param1Index  - 1];
                HomeVentilator.getInstance().param2  = data[param2Index  - 1];
                HomeVentilator.getInstance().param3  = data[param3Index  - 1];
                HomeVentilator.getInstance().param4  = data[param4Index  - 1];
                HomeVentilator.getInstance().param5  = data[param5Index  - 1];
                HomeVentilator.getInstance().baffle2 = data[baffle2Index - 1];
                HomeVentilator.getInstance().param6  = data[param6Index  - 1];
                HomeVentilator.getInstance().param7  = data[param7Index  - 1];
                HomeVentilator.getInstance().param8  = data[param8Index  - 1];
                HomeVentilator.getInstance().param9  = data[param9Index  - 1];
                AccountInfo.getInstance().getGuid().setValue(Plat.getPlatform().getDeviceOnlySign());  //烟机更新
                break;
            //控制返回
            case MSG_TYPE_CONTROL:
                //0x5A：成功；         0xA5：失败；
                if (data[5] == MSG_SUCCESS) {
                    SerialPortHelper.getInstance().addCommands(packQueryCmd()); //查询状态
                } else
                    LogUtils.e("failed");
                break;
        }

        return Arrays.copyOf(dst, dst.length - 2);
    }

    //包解析,解析串口收到的数据
    public static void parseSerial(byte[] data, int recLen) {
        LogUtils.i(StringUtils.bytes2Hex(data));
        //数据为空
        if (null == data)
            return ;

        push_raw_data(BleDecoder.byteArraysToByteArrays(data, recLen));
        Byte[] ret;
        do {
            ret = decode_data();
        } while (ret != null);
    }
}

package com.robam.common.utils;


import com.robam.common.mqtt.MqttPublic;

import java.nio.ByteOrder;

public class MsgUtils {
    public final static ByteOrder BYTE_ORDER = MqttPublic.BYTE_ORDER;

    public static int getByte(byte b) {
        return ByteUtils.toInt(b);
    }

    /**
     *  读取小端byte数组为short
     * @param bytes
     * @return
     */
    public static short bytes2ShortLittle(byte[] bytes, int startIndex) {
        return (short) (((bytes[startIndex + 1] << 8) | bytes[startIndex] & 0xff));
    }
    /**
     * byte数组到int的转换(小端)
     * @param bytes
     * @return
     */
    public static int bytes2IntLittle(byte[] bytes, int startIndex)
    {
        int int1=bytes[startIndex     ]&0xff;
        int int2=(bytes[startIndex + 1]&0xff)<<8;
        int int3=(bytes[startIndex + 2]&0xff)<<16;
        int int4=(bytes[startIndex + 3]&0xff)<<24;

        return int1|int2|int3|int4;
    }

    /**
     * 将byte数组数据转换成float
     * @param arr
     * @return
     */
    public static float bytes2FloatLittle(byte[] arr, int startIndex) {
        int accum = 0;
        accum = accum|(arr[startIndex    ] & 0xff) << 0;
        accum = accum|(arr[startIndex + 1] & 0xff) << 8;
        accum = accum|(arr[startIndex + 2] & 0xff) << 16;
        accum = accum|(arr[startIndex + 3] & 0xff) << 24;
        return Float.intBitsToFloat(accum);
    }

    /**
     * 将byte转换成double
     * @param arr
     * @return
     */
    public static double bytes2DoubleLittle(byte[] arr, int startIndex) {
        long value = 0;
        for (int i = 0; i < 8; i++) {
            value |= ((long) (arr[i + startIndex] & 0xff)) << (8 * i);
        }
        return Double.longBitsToDouble(value);
    }

    public static String getString(byte[] data, int startIndex, int length) {
        return ByteUtils.toString(data, startIndex, length);
    }

    public static byte toByte(short s) {
        return (byte) s;
    }

    public static byte toByte(int s) {
        return (byte) s;
    }

//    public static short calcCrc(byte[] data) {
//
//        short res = Crc16Utils.calcCrc16(data);
//        return res;
//    }
//
//    public static short calcCrc2(byte[] data) {
//
//        short res = Crc16Utils.calcCrc16(data);
//
//        ByteBuffer buf = ByteBuffer.allocate(2).order(BYTE_ORDER);
//        buf.putShort(res);
//
//        byte[] bytes = new byte[2];
//        bytes[0] = buf.get(1);
//        bytes[1] = buf.get(0);
//
//        res = ByteBuffer.wrap(bytes).order(BYTE_ORDER).getShort();
//
//        return res;
//    }
}

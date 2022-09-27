package com.robam.common.utils;

import android.util.Base64;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.Charset;
import java.util.Arrays;


public class ByteUtils {
    public final static ByteOrder BYTE_ORDER = ByteOrder.LITTLE_ENDIAN;
    public static final Charset CHARSET = Charset.forName("utf-8");

    // byte 与 int 的相互转换
    public static byte toByte(int x) {
        return (byte) x;
    }

    public static short toShort(byte b) {
        return (short) toInt(b);
    }

    public static int toInt(byte b) {
        // Java 总是把 byte 当做有符处理；我们可以通过将其和 0xFF 进行二进制与得到它的无符值
        return b & 0xFF;
    }

    public static int toInt(short x) {
        return ((int) x) & 0xffff;
    }

    /**
     * 转化无符号二进制
     */
    public static String toBin(byte b) {
        return Integer.toBinaryString(toInt(b));
    }

    /**
     * 转化无符号十六进制
     */
    public static String toHex(byte b) {
        return Integer.toHexString(toInt(b));
    }

    public static short toInt16(byte[] value, int startIndex,
                                ByteOrder byteOrder) {
        ByteBuffer byteBuffer = allocate(2, byteOrder);
        byteBuffer.put(value, startIndex, 2);
        return byteBuffer.getShort(0);
    }

    public static int toInt32(byte[] value, int startIndex, ByteOrder byteOrder) {
        ByteBuffer byteBuffer = allocate(4, byteOrder);
        byteBuffer.put(value, startIndex, 4);
        return byteBuffer.getInt(0);

    }

    public static long toInt64(byte[] value, int startIndex, ByteOrder byteOrder) {
        ByteBuffer byteBuffer = allocate(8, byteOrder);
        byteBuffer.put(value, startIndex, 8);
        return byteBuffer.getLong(0);
    }

    public static float toFloat(byte[] value, int startIndex,
                                ByteOrder byteOrder) {
        ByteBuffer byteBuffer = allocate(4, byteOrder);
        byteBuffer.put(value, startIndex, 4);
        return byteBuffer.getFloat(0);
    }

    public static double toDouble(byte[] value, int startIndex,
                                  ByteOrder byteOrder) {
        ByteBuffer byteBuffer = allocate(8, byteOrder);
        byteBuffer.put(value, startIndex, 8);
        return byteBuffer.getDouble(0);
    }

    public static String toString(byte[] value, int startIndex, int length) {
        try {
            return new String(value, startIndex,
                    Math.min(value.length, length), CHARSET).trim();
        } catch (Exception e) {
            return null;
        }
    }

    public static String toBase64(byte[] value) {
        return Base64.encodeToString(value, Base64.DEFAULT);
    }



    public static byte[] getBytes(short value, ByteOrder byteOrder) {
        ByteBuffer byteBuffer = allocate(2, byteOrder);
        byteBuffer.putShort(value);
        return byteBuffer.array();
    }

    public static byte[] getBytes(int value, ByteOrder byteOrder,int num) {
        ByteBuffer byteBuffer = allocate(num, byteOrder);
        byteBuffer.putInt(value);
        return byteBuffer.array();
    }


    public static byte[] getBytes(int value, ByteOrder byteOrder) {
        ByteBuffer byteBuffer = allocate(4, byteOrder);
        byteBuffer.putInt(value);
        return byteBuffer.array();
    }

    public static byte[] getBytes(long value, ByteOrder byteOrder) {
        ByteBuffer byteBuffer = allocate(8, byteOrder);
        byteBuffer.putLong(value);
        return byteBuffer.array();
    }

    public static byte[] getBytes(float value, ByteOrder byteOrder) {
        ByteBuffer byteBuffer = allocate(4, byteOrder);
        byteBuffer.putFloat(value);
        return byteBuffer.array();
    }

    public static byte[] getBytes(double value, ByteOrder byteOrder) {
        ByteBuffer byteBuffer = allocate(8, byteOrder);
        byteBuffer.putDouble(value);
        return byteBuffer.array();
    }

    public static byte[] getBase64(String base64String) {
        return Base64.decode(base64String, Base64.DEFAULT);
    }


    public static byte[] reversionByte(byte[] value) {
        int length = value.length;
        byte[] resultValue = new byte[length];
        int j = 0;
        for (int i = length - 1; i >= 0; i--) {
            resultValue[j++] = value[i];
        }
        return resultValue;
    }

    public static ByteBuffer allocate(int capacity, ByteOrder byteOrder) {
        return ByteBuffer.allocate(capacity).order(byteOrder);
    }

    public static String println(ByteBuffer buf) {
        Byte[] bytes = new Byte[buf.position()];
        System.arraycopy(buf.array(), 0, bytes, 0, bytes.length);
        return Arrays.toString(bytes);
    }


    /**
     * int转byte数组
     * @param n
     * @return
     */
    public static byte[] intToBytes(int n){
        byte[] b = new byte[4];
        for(int i = 0;i < 4;i++)
        {
            b[i]=(byte)(n>>(24-i*8));
        }
        return b;
    }
    /**
     * int转byte数组
     * @param n
     * @return
     */
    public static byte[] intToBytes2(int n){
        byte[] b = new byte[4];
        for(int i = 3; i >=  0 ; i--)
        {
            b[i]=(byte)(n>>(24 - (3-i)*8));
        }
        return b;
    }
    /**
     * byte数组转int
     * @param b
     * @return
     */
    public static int byteToInt(byte[] b)
    {
        int mask=0xff;
        int temp=0;
        int n=0;
        for(int i = 0 ; i < b.length ; i++){
            n<<=8;
            temp=b[i]&mask;
            n|=temp;
        }
        return n;
    }

    /**
     * byte数组转int（高低八位相反）
     * @param b
     * @return
     */
    public static int byteToInt2(byte[] b)
    {
        int mask=0xff;
        int temp=0;
        int n=0;
        for(int i = b.length - 1 ; i >= 0 ; i--){
            n<<=8;
            temp=b[i]&mask;
            n|=temp;
        }
        return n;
    }
}


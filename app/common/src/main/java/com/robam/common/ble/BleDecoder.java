package com.robam.common.ble;

import com.robam.common.utils.ByteUtils;
import com.robam.common.utils.LogUtils;
import com.robam.common.utils.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class BleDecoder {
    public static final int GUID_LEN = 17;

    public static final int DECODE_CMD_KEY_OFFSET = 0;
    public static final int DECODE_CMD_ID_OFFSET = 1;
    public static final int DECODE_PAYLOAD_OFFSET = 2;

    //OPTION字段
    public static final byte OPTION_ENCRYPT_BIT = 1;
    public static final byte OPTION_CRC_BIT = 2;
    public static final byte OPTION_CHECKSUM_BIT = 8;

    //CMD KEY字段
    public static final int ROKI_UART_CMD_KEY_INTERNAL = 1;
    public static final int ROKI_UART_CMD_KEY_BROADCAST = 3;
    public static final int ROKI_UART_CMD_KEY_DYNAMIC = 32;
    public static final int CMD_DISCONNECT_BLE_PRIOR_NOTICE = 94;
    public static final int RESP_DISCONNECT_BLE_PRIOR_NOTICE = 95;

    public static final int RC_SUCCESS = 0;
    public static final int RC_FAIL = 1;

    //CMD ID--公共内部命令
    public static final int CMD_PAIRING_REQUEST_INT = 1;
    public static final int RESP_PAIRING_REQUEST_INT = 2;
    public static final int CMD_DEVICE_ONLINE_INT = 3;
    public static final int RESP_DEVICE_ONLINE_INT = 4;
    //CMD ID--烟灶锅内部指令
    public static final int EVENT_IH_POWER_CHANGED_INT = 96;//灶具档位变化通知烟机
    public static final int RSP_EVENT_IH_POWER_CHANGED_INT = 97;//灶具档位通知回复
    public static final int CMD_GET_POT_STATUS_INT = 106;//烟机查询锅
    public static final int RESP_GET_POT_STATUS_INT = 107;
    public static final int CMD_GET_IH_STATUS_INT = 108;//烟机查询灶
    public static final int RESP_GET_IH_STATUS_INT = 109;
    public static final int EVENT_POT_TEMPERATURE_DROP = 110;//锅温度骤变通知烟机
    public static final int EVENT_POT_TEMPERATURE_OV = 111;//锅干烧预警通知烟机
    public static final int EVENT_POT_LINK_2_RH = 112;//锅联动烟机(烟锅联动)
    public static final int CMD_RH_SET_INT = 115;//内部远程烟机交互
    public static final int RSP_RH_SET_INT = 116;
    public static final int CMD_COOKER_SET_INT = 117;//内部远程灶具交互
    public static final int RSP_COOKER_SET_INT = 118;
    public static final int CMD_POT_SET_INT = 119;//内部远程无人锅交互
    public static final int RSP_POT_SET_INT = 120;
    public static final int CMD_COOKER_RECIPE_SET_INT = 121;//内部远程灶具菜谱步骤设置
    public static final int RSP_COOKER_RECIPE_SET_INT = 122;
    //CMD ID--灶具外部指令
    public static final int CMD_COOKER_STATUS_RES = 129;//查询灶返回
    public static final int CMD_COOKER_SET = 130;//设置灶具状态
    public static final int CMD_COOKER_SET_RES = 131;//设置灶具返回
    public static final int CMD_COOKER_TIME_RES = 135; //定时设置返回
    public static final int CMD_COOKER_LOCK_RES = 137;//设置童锁返回
    //CMD ID--无人锅外部指令
    public static final int CMD_POT_INTERACTION_RES = 154;//智能锅智能互动返回

    private static final int MCU_UART_MIN_PAYLOAD_LEN = 1;
    private static final int MCU_UART_TX_MAX_PAYLOAD_LEN = 240;
    private static final int MCU_UART_RX_MAX_PAYLOAD_LEN = 240;
    private static final int ENCRYPT_RAND_CODE_LEN = 1;

    private static final byte SYNC1 = (byte)0xfe;//同步字
    private static final byte SYNC2 = (byte)0x5c;

    private static final int MCU_UART_STATUS_START = 0;
    private static final int MCU_UART_STATUS_PARSE_SYNC1 = 1;//解析FE
    private static final int MCU_UART_STATUS_PARSE_SYNC2 = 2;//解析5C
    private static final int MCU_UART_STATUS_PARSE_OPTION = 3;//解析OPTION
    private static final int MCU_UART_STATUS_PARSE_LEN1 = 4;//解析LEN
    private static final int MCU_UART_STATUS_PARSE_LEN2 = 5;
    private static final int MCU_UART_STATUS_PARSE_DATA = 6;//解析DATA

    private static byte g_rand_value = 0;//随机数
    private static int cmd_key = ROKI_UART_CMD_KEY_DYNAMIC;
    private static final Lock lock = new ReentrantLock();

    private final LinkedList<Byte> input_buf = new LinkedList<>();//待解析数据
    private final ArrayList<Byte>  decode_buf = new ArrayList<>();//解析后数据
    private int decode_status = MCU_UART_STATUS_START;//解析状态机
    private int option = 0;//option字段
    private int cnt = 0;//应该payload接收长度(LEN字段值)
    private int peek_idx = 0;//peek索引
    private long last_tick = 0;//上次解析时间戳

    public BleDecoder(int rand_value) {
        init_decoder(rand_value);
    }

    //!初始化解码器
    public void init_decoder(int rand_value) {
        input_buf.clear();
        decode_buf.clear();
        decode_status = MCU_UART_STATUS_START;
        option = 0;
        cnt = 0;
        peek_idx = 0;
        last_tick = 0;

        lock.lock();
        g_rand_value = (byte)rand_value;
        lock.unlock();
    }

    //将接收数据推入解码器(多线程需自行加锁)
    public void push_raw_data(Byte []data_array) {
        input_buf.addAll(Arrays.asList(data_array));
    }

    //解码解码器中的数据(多线程需自行加锁)
    public Byte [] decode_data(long timeout_ms) {
        Byte[] ret;
        byte val;

        long cur_tick = System.currentTimeMillis();
        if(decode_status > MCU_UART_STATUS_PARSE_SYNC1 && cur_tick - last_tick >= timeout_ms) {
            pop_and_repeek_buf();
            decode_status = MCU_UART_STATUS_START;
        }

        while(input_buf.size() > 0 && peek_idx < input_buf.size()) {
            val = input_buf.get(peek_idx);
//            LogUtils.e(ByteUtils.toHex(val));
            peek_idx++;
            switch (decode_status) {
                case MCU_UART_STATUS_START:
                    cnt = 0;
                    decode_status = MCU_UART_STATUS_PARSE_SYNC1;
                    last_tick = cur_tick;
                    //break; //force to skip break;
                case MCU_UART_STATUS_PARSE_SYNC1:
                    pop_and_repeek_buf();
                    if(val == SYNC1) {
                        decode_status = MCU_UART_STATUS_PARSE_SYNC2;
                    } else {
                        decode_status = MCU_UART_STATUS_START;
                        //Log.d(TAG, "sync1 error " + val);
                    }
                    break;
                case MCU_UART_STATUS_PARSE_SYNC2:
                    pop_and_repeek_buf();
                    if(val == SYNC2) {
                        decode_status = MCU_UART_STATUS_PARSE_OPTION;
                    } else if(val != SYNC1){
                        decode_status = MCU_UART_STATUS_START;
                        //Log.d(TAG, "sync2 error " + val);
                    }
                    break;
                case MCU_UART_STATUS_PARSE_OPTION:
                    if(((val) & (OPTION_CRC_BIT | OPTION_CHECKSUM_BIT)) == (OPTION_CRC_BIT | OPTION_CHECKSUM_BIT)) { // invalid
                        pop_and_repeek_buf();
                        if(val == SYNC1) {
                            decode_status = MCU_UART_STATUS_PARSE_SYNC2;
                        } else {
                            decode_status = MCU_UART_STATUS_START;
                        }
                        //Log.d(TAG, "option error " + val);
                    } else {
                        option = val;
                        cnt = 0;
                        decode_status = MCU_UART_STATUS_PARSE_LEN1;
                    }
                    break;
                case MCU_UART_STATUS_PARSE_LEN1:
                    if(ByteUtils.toInt(val) > 0x7f) {
                        cnt = val & 0x7f;
                        decode_status = MCU_UART_STATUS_PARSE_LEN2;
                    } else if(ByteUtils.toInt(val) >= MCU_UART_MIN_PAYLOAD_LEN &&
                            ByteUtils.toInt(val) <= MCU_UART_RX_MAX_PAYLOAD_LEN){
                        cnt = val;
                        decode_buf.clear();
                        decode_status = MCU_UART_STATUS_PARSE_DATA;
                    } else {
                        pop_and_repeek_buf();
                        decode_status = MCU_UART_STATUS_START;
                        //Log.d(TAG, "len1 error " + val);
                    }
                    break;
                case MCU_UART_STATUS_PARSE_LEN2:
                    if(ByteUtils.toInt(val) > 0x7f) {
                        pop_and_repeek_buf();
                        decode_status = MCU_UART_STATUS_START;
                        //Log.d(TAG, "len2 error " + val);
                    } else {
                        cnt |= (ByteUtils.toInt(val) << 7);
                        if(cnt >= MCU_UART_MIN_PAYLOAD_LEN && cnt <= MCU_UART_RX_MAX_PAYLOAD_LEN) {
                            decode_buf.clear();
                            decode_status = MCU_UART_STATUS_PARSE_DATA;
                        } else {
                            pop_and_repeek_buf();
                            decode_status = MCU_UART_STATUS_START;
                            //Log.d(TAG, "len3 error " + val);
                        }
                    }
                    break;
                case MCU_UART_STATUS_PARSE_DATA:
                    decode_buf.add(val);
                    if(decode_buf.size() >= cnt) {
                        ret = mcu_uart_unpack(option, decode_buf);
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

    private void pop_and_repeek_buf() {
        input_buf.pop();
        peek_idx = 0;
    }

    //!封装内部命令
    public static Byte [] make_internal_send_packet(int cmd_id, Byte[] payload) {
        Byte [] final_payload;
        if(payload == null) {
            final_payload = new Byte[2];
        } else {
            final_payload = new Byte[payload.length + 2];
        }
        final_payload[0] = ROKI_UART_CMD_KEY_INTERNAL;//CMD KEY=1表示内部指令
        final_payload[1] = (byte)cmd_id;
        if(payload != null) {
            System.arraycopy(payload, 0, final_payload, 2, payload.length);
        }
        return mcu_uart_pack(OPTION_CRC_BIT | OPTION_ENCRYPT_BIT, final_payload);
    }

    //封装外部指令
    public static ExternBleData make_external_send_packet(int cmd_id, Byte[] payload) {
        ExternBleData ext_data = new ExternBleData();
        Byte [] final_payload;
        if(payload == null) {
            final_payload = new Byte[2];
        } else {
            final_payload = new Byte[payload.length + 2];
        }
        lock.lock();
        ext_data.cmd_key = cmd_key;
        final_payload[0] = (byte)ext_data.cmd_key;//CMD KEY=1表示内部指令
        cmd_key++;
        if(cmd_key >= 250) {
            cmd_key = ROKI_UART_CMD_KEY_DYNAMIC;
        }
        lock.unlock();
        final_payload[1] = (byte)cmd_id;
        if(payload != null) {
            System.arraycopy(payload, 0, final_payload, 2, payload.length);
        }
        ext_data.payload = mcu_uart_pack(OPTION_CRC_BIT | OPTION_ENCRYPT_BIT, final_payload);
        return ext_data;
    }
    //加密数据
    public static Byte[] mcu_uart_pack(Byte[] src) {
        return mcu_uart_pack(OPTION_CRC_BIT | OPTION_ENCRYPT_BIT, src);
    }

    private static Byte [] mcu_uart_pack(int option, Byte[] src) {
        int payload_len = src.length;
        int offset;
        short check;

        if(src.length == 0) {
            return null;
        }

        //calculate payload length
        if((option & OPTION_ENCRYPT_BIT) != 0) {
            payload_len += ENCRYPT_RAND_CODE_LEN;
        }
        if((option & OPTION_CRC_BIT) != 0) {
            payload_len += 2;
        } else if((option & OPTION_CHECKSUM_BIT) != 0) {
            payload_len++;
        }

        if(payload_len > MCU_UART_TX_MAX_PAYLOAD_LEN || payload_len < MCU_UART_MIN_PAYLOAD_LEN) {
            return null;
        }

        Byte[] dst;
        if(payload_len > 0x7f) {
            offset = 5;
        } else {
            offset = 4;
        }
        dst = new Byte[payload_len + offset];
        dst[0] = SYNC1;
        dst[1] = SYNC2;
        dst[2] = (byte)option;
        //fill length
        if(payload_len > 0x7f) {
            dst[3] = (byte)((payload_len & 0x7f) | 0x80);
            dst[4] = (byte)(payload_len >> 7);
        } else {
            dst[3] = (byte)payload_len;
        }

        //fill payload
        if((option & OPTION_ENCRYPT_BIT) == 0) {
            System.arraycopy(src, 0, dst, offset, src.length);
        } else {
            dst[offset] = 0;
            System.arraycopy(src, 0, dst, offset + ENCRYPT_RAND_CODE_LEN, src.length);
        }

        if((option & OPTION_CRC_BIT) != 0) {
            check = crc16(src);
            dst[dst.length - 2] = (byte)check;//check
            dst[dst.length - 1] = (byte)(check >> 8);//check
        } else if((option & OPTION_CHECKSUM_BIT) != 0) {
            dst[dst.length - 1] = cs8(src);//check
        }
        if((option & OPTION_ENCRYPT_BIT) != 0) {
            Byte [] before_encrypt = new Byte[payload_len];
            System.arraycopy(dst, offset + ENCRYPT_RAND_CODE_LEN, before_encrypt, 1, payload_len - 1);
            Byte [] mid = encrypt(before_encrypt);//encrypt
            if(mid != null) {
                System.arraycopy(mid, 0, dst, offset, payload_len);
            }
        }
        return dst;
    }

    private static Byte [] mcu_uart_unpack(int option, ArrayList<Byte> src) {
        Byte [] dst;
        int dst_len = src.size();
        int calc_check = 1;
        int get_check = 0;

        if(src.size() > MCU_UART_RX_MAX_PAYLOAD_LEN) {
            return null;
        }

        //calculate dst length
        if((option & OPTION_ENCRYPT_BIT) != 0) {
            dst_len -= ENCRYPT_RAND_CODE_LEN;
        }

        if((option & OPTION_CRC_BIT) != 0) {
            if(dst_len >= 2) {
                dst_len -= 2;
            } else {
                return null;
            }
        } else if((option & OPTION_CHECKSUM_BIT) != 0) {
            dst_len--;
        }

        if((option & OPTION_ENCRYPT_BIT) != 0) {
            dst = decrypt(src.toArray(new Byte[0]));
        } else {
            dst = src.toArray(new Byte[0]);
        }
        if(dst == null) {
            return null;
        }
        if((option & OPTION_CRC_BIT) != 0) {
            calc_check = ByteUtils.toInt(crc16(Arrays.copyOf(dst, dst.length - 2)));
            LogUtils.e("calc_check= " + calc_check);
            get_check = ByteUtils.toInt(dst[dst.length - 2]) | (ByteUtils.toInt(dst[dst.length - 1]) << 8);
            LogUtils.e("get_check= " + get_check);
        } else if((option & OPTION_CHECKSUM_BIT) != 0) {
            calc_check = ByteUtils.toInt(cs8(Arrays.copyOf(dst, dst.length - 1)));
            get_check = ByteUtils.toInt(dst[dst_len]);
        }
        if(calc_check != get_check) {
            return null;
        }
        return Arrays.copyOf(dst, dst.length - 2);
    }

    //Byte数组转byte数组
    public static byte [] ByteArraysTobyteArrays(Byte [] input) {
        if(input == null) {
            return null;
        }
        byte [] output = new byte[input.length];
        for(int i = 0; i < input.length; i++) {
            output[i] = input[i];
        }
        return output;
    }
    //byte转Byte数组
    public static Byte [] byteArraysToByteArrays(byte [] input) {
        if(input == null) {
            return null;
        }
        Byte [] output = new Byte[input.length];
        for(int i = 0; i < input.length; i++) {
            output[i] = input[i];
        }
        return output;
    }

    public static Byte [] byteArraysToByteArrays(byte [] input, int recLen) {
        if(input == null) {
            return null;
        }
        Byte [] output = new Byte[recLen];
        for(int i = 0; i < recLen; i++) {
            output[i] = input[i];
        }
        return output;
    }

    private static byte InvertUint8(byte src) {
        int i;
        byte tmp = 0;

        for(i = 0; i < 8; i++) {
            if((src & (1 << i)) != 0) {
                tmp |= 1 << (7 - i);
            }
        }
        return tmp;
    }

    private static short InvertUint16(short src) {
        int i;
        short tmp = 0;

        for(i = 0; i < 16; i++) {
            if((src & (1 << i)) != 0) {
                tmp |= 1 << (15 - i);
            }
        }
        return tmp;
    }

    private static short crc16(Byte [] array) {
        final int CRC_16_POLYNOMIALS = 0x8005;
        int i;
        short wCRC = (short)0xFFFF;

        if(array == null) {
            return 0;
        }

        for(byte chChar : array) {
            chChar = InvertUint8(chChar);
            wCRC ^= (short)(ByteUtils.toInt(chChar) << 8);
            for (i = 0; i < 8; i++) {
                if ((wCRC & 0x8000) != 0) {
                    wCRC = (short) ((ByteUtils.toInt(wCRC) << 1) ^ CRC_16_POLYNOMIALS);
                } else {
                    wCRC = (short) (ByteUtils.toInt(wCRC) << 1);
                }
            }
        }
        wCRC = InvertUint16(wCRC);
        return wCRC;
    }

    private static Byte cs8(Byte [] array) {
        byte sum = 0;

        if(array == null) {
            return 0;
        }

        for(byte item : array) {
            sum += item;
        }
        return sum;
    }

    private static Byte [] encrypt(Byte [] input) {
        byte rand_code;
        if(input.length == 0) {
            return null;
        }

        Byte [] output = new Byte[input.length];
        lock.lock();
        rand_code = g_rand_value++;
        lock.unlock();
        output[0] = rand_code;

        for(int i = ENCRYPT_RAND_CODE_LEN; i < output.length; i++) {
            output[i] = (byte)(input[i] ^ rand_code); //异或
        }
        for(int i = 0; i < output.length; i++) {
            output[i] = (byte) gc_encrypt_table[ByteUtils.toInt(output[i])];    //加密
        }
        return output;
    }

    private static Byte [] decrypt(Byte [] input) {
        byte  rand_code;
        if(input.length == 0) {
            return null;
        }
        for(int i = 0; i < input.length; i++) {
            input[i] = (byte) gc_decrypt_table[ByteUtils.toInt(input[i])];    //解密
        }
        rand_code = input[0];
        Byte [] output = new Byte[input.length - ENCRYPT_RAND_CODE_LEN];
        for(int i = 0;i < output.length; i++) {
            output[i] = (byte) (input[i + ENCRYPT_RAND_CODE_LEN] ^ rand_code);  //异或
        }
        return output;
    }

    public static final short [] gc_encrypt_table = {
        0x56, 0x80, 0x42, 0x84, 0x45, 0x87, 0x47, 0x86, 0x44, 0x85, 0x82, 0x46, 0x43, 0x83, 0x41, 0x81,
        0x98, 0x58, 0x9A, 0x5C, 0x9D, 0x5F, 0x9F, 0x5E, 0x9C, 0x5D, 0x5A, 0x9E, 0x9B, 0x5B, 0x99, 0x59,
        0x8C, 0x4C, 0x8E, 0x48, 0x89, 0x4B, 0x8B, 0x4A, 0x88, 0x49, 0x4E, 0x8A, 0x8F, 0x4F, 0x8D, 0x4D,
        0x54, 0x40, 0x94, 0x90, 0x51, 0x93, 0x53, 0x92, 0x50, 0x91, 0x96, 0x52, 0x57, 0x97, 0x55, 0x95,
        0x04, 0xC4, 0x06, 0xC0, 0x01, 0xC3, 0x03, 0xC2, 0x00, 0xC1, 0xC6, 0x02, 0x07, 0xC7, 0x05, 0xC5,
        0x10, 0xD0, 0x12, 0xD4, 0x15, 0xD7, 0x17, 0xD6, 0x14, 0xD5, 0xD2, 0x16, 0x13, 0xD3, 0x11, 0xD1,
        0x38, 0xF8, 0x3A, 0xFC, 0x3D, 0xFF, 0x3F, 0xFE, 0x3C, 0xFD, 0xFA, 0x3E, 0x3B, 0xFB, 0x39, 0xF9,
        0x7C, 0xBC, 0x7E, 0xB8, 0x79, 0xBB, 0x7B, 0xBA, 0x78, 0xB9, 0xBE, 0x7A, 0x7F, 0xBF, 0x7D, 0xBD,
        0xA4, 0x64, 0xA6, 0x60, 0xA1, 0x63, 0xA3, 0x62, 0xA0, 0x61, 0x66, 0xA2, 0xA7, 0x67, 0xA5, 0x65,
        0xE0, 0x20, 0xE2, 0x24, 0xE5, 0x27, 0xE7, 0x26, 0xE4, 0x25, 0x22, 0xE6, 0xE3, 0x23, 0xE1, 0x21,
        0xF4, 0x34, 0xF6, 0x30, 0xF1, 0x33, 0xF3, 0x32, 0xF0, 0x31, 0x36, 0xF2, 0xF7, 0x37, 0xF5, 0x35,
        0xDC, 0x1C, 0xDE, 0x18, 0xD9, 0x1B, 0xDB, 0x1A, 0xD8, 0x19, 0x1E, 0xDA, 0xDF, 0x1F, 0xDD, 0x1D,
        0xC8, 0x08, 0xCA, 0x0C, 0xCD, 0x0F, 0xCF, 0x0E, 0xCC, 0x0D, 0x0A, 0xCE, 0xCB, 0x0B, 0xC9, 0x09,
        0xB0, 0x70, 0xB2, 0x74, 0xB5, 0x77, 0xB7, 0x76, 0xB4, 0x75, 0x72, 0xB6, 0xB3, 0x73, 0xB1, 0x71,
        0x68, 0xA8, 0x6A, 0xAC, 0x6D, 0xAF, 0x6F, 0xAE, 0x6C, 0xAD, 0xAA, 0x6E, 0x6B, 0xAB, 0x69, 0xA9,
        0x2C, 0xEC, 0x2E, 0xE8, 0x29, 0xEB, 0x2B, 0xEA, 0x28, 0xE9, 0xEE, 0x2A, 0x2F, 0xEF, 0x2D, 0xED,
    };

    public static final short [] gc_decrypt_table = {
        0x48, 0x44, 0x4b, 0x46, 0x40, 0x4e, 0x42, 0x4c, 0xc1, 0xcf, 0xca, 0xcd, 0xc3, 0xc9, 0xc7, 0xc5,
        0x50, 0x5e, 0x52, 0x5c, 0x58, 0x54, 0x5b, 0x56, 0xb3, 0xb9, 0xb7, 0xb5, 0xb1, 0xbf, 0xba, 0xbd,
        0x91, 0x9f, 0x9a, 0x9d, 0x93, 0x99, 0x97, 0x95, 0xf8, 0xf4, 0xfb, 0xf6, 0xf0, 0xfe, 0xf2, 0xfc,
        0xa3, 0xa9, 0xa7, 0xa5, 0xa1, 0xaf, 0xaa, 0xad, 0x60, 0x6e, 0x62, 0x6c, 0x68, 0x64, 0x6b, 0x66,
        0x31, 0x0e, 0x02, 0x0c, 0x08, 0x04, 0x0b, 0x06, 0x23, 0x29, 0x27, 0x25, 0x21, 0x2f, 0x2a, 0x2d,
        0x38, 0x34, 0x3b, 0x36, 0x30, 0x3e, 0x00, 0x3c, 0x11, 0x1f, 0x1a, 0x1d, 0x13, 0x19, 0x17, 0x15,
        0x83, 0x89, 0x87, 0x85, 0x81, 0x8f, 0x8a, 0x8d, 0xe0, 0xee, 0xe2, 0xec, 0xe8, 0xe4, 0xeb, 0xe6,
        0xd1, 0xdf, 0xda, 0xdd, 0xd3, 0xd9, 0xd7, 0xd5, 0x78, 0x74, 0x7b, 0x76, 0x70, 0x7e, 0x72, 0x7c,
        0x01, 0x0f, 0x0a, 0x0d, 0x03, 0x09, 0x07, 0x05, 0x28, 0x24, 0x2b, 0x26, 0x20, 0x2e, 0x22, 0x2c,
        0x33, 0x39, 0x37, 0x35, 0x32, 0x3f, 0x3a, 0x3d, 0x10, 0x1e, 0x12, 0x1c, 0x18, 0x14, 0x1b, 0x16,
        0x88, 0x84, 0x8b, 0x86, 0x80, 0x8e, 0x82, 0x8c, 0xe1, 0xef, 0xea, 0xed, 0xe3, 0xe9, 0xe7, 0xe5,
        0xd0, 0xde, 0xd2, 0xdc, 0xd8, 0xd4, 0xdb, 0xd6, 0x73, 0x79, 0x77, 0x75, 0x71, 0x7f, 0x7a, 0x7d,
        0x43, 0x49, 0x47, 0x45, 0x41, 0x4f, 0x4a, 0x4d, 0xc0, 0xce, 0xc2, 0xcc, 0xc8, 0xc4, 0xcb, 0xc6,
        0x51, 0x5f, 0x5a, 0x5d, 0x53, 0x59, 0x57, 0x55, 0xb8, 0xb4, 0xbb, 0xb6, 0xb0, 0xbe, 0xb2, 0xbc,
        0x90, 0x9e, 0x92, 0x9c, 0x98, 0x94, 0x9b, 0x96, 0xf3, 0xf9, 0xf7, 0xf5, 0xf1, 0xff, 0xfa, 0xfd,
        0xa8, 0xa4, 0xab, 0xa6, 0xa0, 0xae, 0xa2, 0xac, 0x61, 0x6f, 0x6a, 0x6d, 0x63, 0x69, 0x67, 0x65,
    };
    public static class ExternBleData {
        public Byte [] payload;
        public int cmd_key;
    }
}
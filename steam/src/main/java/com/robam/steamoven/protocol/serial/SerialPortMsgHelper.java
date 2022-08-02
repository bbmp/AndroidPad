package com.robam.steamoven.protocol.serial;




/**
 * 串口查询控制数据payload管理类
 */
public class SerialPortMsgHelper {
    // 协议表对应  ， payload对应  ，功能
    /**
     * 4 - 0 系统状态
     */
    public static byte sysState = 3 ;
    public static int sysStateIndex = 0 ;
    /**
     * 5 - 1 上位机故障
     */
    public static byte controlFault = 0 ;
    public static int controlFaultIndex = 1 ;
    /**
     * 6 - 2 beep蜂鸣器
     */
    public static byte beep = 4 ;
    public static int beepIndex = 2 ;

    /**
     * 7 - 3 蜂鸣器计数器
     */
    public  static byte beepCount = 0 ;
    public static int beepCountIndex = 3 ;

    /**
     * 8 - 4 工作状态
     */
    public static byte workState = 0 ;
    public static int workStateIndex = 4 ;

    /**
     * 9 - 5 工作状态 （下层）
     */
    public static byte workStateDown = 0 ;
    public static int workStateDownIndex = 5 ;

    /**
     * 10 - 6 工作标志位 1
     */
    public static byte workSign = 0 ;
    public static int workSignIndex = 6 ;
    /**
     * 11 - 7 工作标志位 2
     */
    public static byte workSign2 = 0 ;
    public static int workSign2Index = 7 ;
    /**
     * 12 - 8 工作标志位 3
     */
    public static byte workSign3 = 0 ;
    public static int workSign3Index = 8 ;

    /**
     * 13 - 9 工作类型
     */
    public  static byte workType = 0 ;
    public static int workTypeIndex = 9 ;
    /**
     * 14 - 10 工作模式
     */
    public static byte workMode = 0 ;
    public static int workModeIndex = 10 ;

    /**
     * 15 - 11 设置上温度
     * 16 - 12
     */
    public static byte setUpTemp1 = 0 ;
    public static byte setUpTemp2 = 0 ;
    public static int setUpTemp1Index = 11 ;
    public static int setUpTemp2Index = 12 ;

    /**
     * 17 - 13 设置下温度
     * 18 - 14
     */
    public static byte setDownTemp1 = 0 ;
    public static byte setDownTemp2 = 0 ;
    public static int setDownTemp1Index = 13 ;
    public static int setDownTemp2Index = 14 ;

    /**
     * 19 - 15 水质调节
     */
    public static byte waterQuality = 0 ;
    public static int waterQualityIndex = 15 ;

    /**
     * 20 - 16 蒸汽量
     */
    public static byte steam = 0 ;
    public static int steamIndex = 16 ;

    /**
     * 21 - 17 微波
     */
    public static byte ripple = 0 ;
    public static int rippleIndex = 17 ;

    /**
     * 22 - 18 风机转速
     */
    public static byte fanSpeed = 0 ;
    public static int fanSpeedIndex = 18 ;

    //    public static byte[] payload =  new byte[]{sysState
//                , controlFault
//                , beep
//                , beepCount
//                , workState, workStateDown
//                , workSign ,workSign2 , workSign3
//                , workType
//                , workMode
//                , setUpTemp1,setUpTemp2
//                , setDownTemp1, setDownTemp2
//                , waterQuality
//                , steam
//                , ripple
//                , fanSpeed
//                };
    public static byte[] payload ;

    /**
     *  开机上电
     * @return
     */
    public static byte[] powerOn(){
        sysState =(byte) 3 ;
        beep = 2 ;
        beepCount = 1 ;
        //默认开机下发关闭水箱指令
        workSign2 = (byte)16 ;
        payload = new byte[]{sysState
                , controlFault
                , beep
                , beepCount
                , workState, workStateDown
                , workSign ,workSign2 , workSign3
                , workType
                , workMode
                , setUpTemp1,setUpTemp2
                , setDownTemp1, setDownTemp2
                , waterQuality
                , steam
                , ripple
                , fanSpeed
        };
        return payload ;
    }

    /**
     *  关闭
     * @return
     */
    public static byte[] powerOff(){
        sysState =(byte) 0 ;
        beep = 3 ;
        beepCount = (byte) getBeepCount() ;
        payload = new byte[]{sysState
                , controlFault
                , beep
                , beepCount
                , workState, workStateDown
                , workSign ,workSign2 , workSign3
                , workType
                , workMode
                , setUpTemp1,setUpTemp2
                , setDownTemp1, setDownTemp2
                , waterQuality
                , steam
                , ripple
                , fanSpeed
        };
        return payload ;
    }

    /**
     *  按键音
     * @return
     */
    public static byte[] keyTone(){
        if (payload != null
        ){
            payload[beepIndex] = (byte) 4 ;
            payload[beepCountIndex] = (byte) getBeepCount() ;
        }else {

        }
        return payload ;
    }

    /**
     * 开灯
     * @return
     */
    public static byte[] onLamp(){
        if (payload != null
        ){
            if ((payload[workSignIndex] & 0x01) == 0){ //关灯状态
                payload[beepIndex] = (byte) 4 ;
                payload[beepCountIndex] = (byte) getBeepCount() ;
                payload[workSignIndex] = (byte) (payload[workSignIndex] + 1) ;
            }

        }else {

        }
        return payload ;
    }

    /**
     * 关灯
     * @return
     */
    public static byte[] offLamp(){
        if (payload != null
        ){
            if ((payload[workSignIndex] & 0x01) == 1){ //开灯状态
                payload[beepIndex] = (byte) 4 ;
                payload[beepCountIndex] = (byte) getBeepCount() ;
                payload[workSignIndex] = (byte) (payload[workSignIndex] - 1) ;
            }
        }else {

        }
        return payload ;
    }

    /**
     *  开始工作
     * @param work_state  8 工作状态
     * @param work_type  12 工作类型
     * @param work_mode 13 工作模式
     * @param up_temp  14 15 上层温度
     * @param down_temp  16 17 下层温度
     * @param _steam  19 蒸汽量
     * @return
     */
    public static byte[] startWork(short work_state
            ,short work_type
            ,short work_mode
            , int up_temp
            , int down_temp
            , short _steam){
        if(payload != null){
            sysState = (byte)6 ;
            beep = (byte) 4 ;
            beepCount = (byte) getBeepCount() ;
            workState = (byte) work_state ;
            //门锁
            workSign2 = (byte) 1 ;
            workType = (byte) work_type ;
            workMode = (byte) work_mode ;
            setUpTemp1 = (byte) up_temp ;
            setDownTemp1 = (byte) down_temp ;
            steam = (byte) _steam ;
            payload[sysStateIndex] = sysState ;
            payload[beepIndex] = beep ;
            payload[beepCountIndex] = beepCount ;
            payload[workStateIndex]  = workState ;
            payload[workSign2Index]  = workSign2 ;
            payload[workTypeIndex]  = workType ;
            payload[workModeIndex]  = workMode ;
            payload[setUpTemp1Index]  = setUpTemp1 ;
            payload[setDownTemp1Index]  = setDownTemp1 ;
            payload[steamIndex]  = steam ;
        }
//        payload = new byte[]{3, 0, 4, (byte) getBeepCount(), (byte)workState, 0, 1 ,0 , (byte) workType ,(byte) workMode ,
//                (byte) upTemp , 0  ,(byte) downTemp , 0 ,
//                0 , (byte) steam };
        return payload ;
    }

    /**
     * 退出工作/关闭当次工作

     * @return
     */
    public static byte[] stopWork(){
        sysState = 3 ;
        controlFault = 0 ;
        beep = 7 ;
        beepCount = (byte) getBeepCount() ;
        workState = 0 ;
        workStateDown = 0 ;
        workSign = 0 ;
        workSign2 = 0 ;
        workSign3 = 0 ;
        workType = 0 ;
        workMode = (byte) 0xFF ;
        setUpTemp1 = 0 ;
        setUpTemp2 = 0 ;
        setDownTemp1 = 0 ;
        setDownTemp2 = 0 ;
        waterQuality = 0 ;
        steam = 0 ;
        ripple = 0 ;
        fanSpeed = 0 ;
        payload = new byte[]{sysState
                , controlFault
                , beep
                , beepCount
                , workState, workStateDown
                , workSign ,workSign2 , workSign3
                , workType
                , workMode
                , setUpTemp1,setUpTemp2
                , setDownTemp1, setDownTemp2
                , waterQuality
                , steam
                , ripple
                , fanSpeed
        };
//        payload = new byte[]{3, 0, 7, (byte) getBeepCount(), 0, 0, 1 ,0 , (byte) 255 ,(byte) 0 ,
//                0 , 0  ,0 , 0 ,
//                0 , 0 };
        return payload ;
    }

    /**
     *
     * @param workState 2：预热暂停 4：工作暂停
     * @return
     */
    public static byte[] pauseWork(short workState){
        if (payload != null){
            payload[sysStateIndex] = (byte) 6 ;
            if ((payload[workSignIndex] & 0x80) != 0){ // 暂停需要关闭或者加蒸汽重新计时当前在加蒸汽状态
                payload[workSignIndex] = (byte) (payload[workSignIndex] - 128) ;
            }
            //暂停需要开锁
            if (((payload[workSign2Index] & 0x01)) != 0){
                payload[workSign2Index] = (byte) (payload[workSign2Index] - 1) ;
            }
            payload[beepIndex] = (byte) 4 ;
            payload[beepCountIndex] = (byte) getBeepCount() ;
            payload[workStateIndex] = (byte) workState ;
        }
        return payload ;
    }

    /**
     * 继续工作
     * @param workState
     * @return
     */
    private static byte[] continueWork(short workState){
        if (payload != null){
            payload[sysStateIndex] = (byte) 6 ;
            //开始工作需要关锁
            if (((payload[workSign2Index] & 0x01)) == 0){
                payload[workSign2Index] = (byte) (payload[workSign2Index] + 1) ;
            }
            payload[beepIndex] = (byte) 4 ;
            payload[beepCountIndex] = (byte) getBeepCount() ;
            payload[workStateIndex] = (byte) workState ;
        }
        return payload ;
    }


    /**
     * 完成工作 倒计时结束
     * @return
     */
    public static byte[] completeWork(){
        if (payload != null){
            payload[sysStateIndex] = (byte) 3 ;
            payload[beepIndex] = (byte) 7 ;
            payload[beepCountIndex] = (byte) getBeepCount() ;
            payload[workStateIndex] = (byte) 0 ;
        }
        return payload ;
    }


    /**
     * 预热结束转工作状态
     * @return
     */
    public static byte[] preHeatToWork(){
        if (payload != null){
            //预热结束声音
            payload[beepIndex] = (byte) 8 ;
            payload[beepCountIndex] = (byte) getBeepCount() ;
            payload[workStateIndex] = (byte) SteamOvenStatusEnum.WORK_STATE_WORK.getCode() ;
        }
        return payload ;
    }


    /**
     * 继续工作 预约
     * @return
     */
    public static byte[] orderWork(){
        if (payload != null){
            payload[sysStateIndex] = (byte) 3 ;
            payload[beepIndex] = (byte) 4 ;
            payload[beepCountIndex] = (byte) getBeepCount() ;
            payload[workStateIndex] = (byte) 0 ;
        }
        return payload ;
    }

    /**
     * 加蒸汽
     * @param
     * @return
     */
    public static byte[] addSteam(){
        if (payload != null){
            if ((payload[workSignIndex] & 0x80) == 0){ // 当前不在加蒸汽状态
                payload[beepCountIndex] = (byte) getBeepCount() ;
                payload[workSignIndex] = (byte) (payload[workSignIndex] + 128) ;
            }
        }
        return payload ;
    }

    /**
     * 关闭加蒸汽
     * @param
     * @return
     */
    public static byte[] closeAddSteam(){
        if (payload != null){
            if ((payload[workSignIndex] & 0x80) != 0){ // 当前在加蒸汽状态
                payload[beepCountIndex] = (byte) getBeepCount() ;
                payload[workSignIndex] = (byte) (payload[workSignIndex] - 128) ;
            }
        }
        return payload ;
    }


    /**
     * 升降电机(上升) 面板926L60和水箱一起
     * @param
     * @return
     */
    public static byte[] uppdem(){
        if (payload != null){
            //工作标志位11
            byte b7 = payload[workSign2Index];
            //开门标志位
            int i = b7 & 0x08;
            if (i == 0){
                payload[workSign2Index] = (byte) (b7 + 8) ;
            }
            b7 = payload[workSign2Index];
            int i2 = b7 & 0x10;
            if (i2 != 0){
                payload[workSign2Index] = (byte) (b7 -16 ) ;
            }
            payload[beepIndex] = (byte) 4 ;
            payload[beepCountIndex] = (byte) getBeepCount();
        }
        return payload ;
    }

    /**
     * 升降电机(下降) 面板 926L60和水箱一起
     * @param
     * @return
     */
    public static byte[] downpdem(){
        if (payload != null){
            //工作标志位11
            byte b7 = payload[workSign2Index];
            //开门标志位
            int i = b7 & 0x08;
            if (i !=  0){
                payload[workSign2Index] = (byte) (b7 - 8) ;
            }
            b7 = payload[workSign2Index];
            int i2 = b7 & 0x10;
            if (i2 == 0){
                payload[workSign2Index] = (byte) (b7 + 16 ) ;
            }
            payload[beepIndex] = (byte) 4 ;
            payload[beepCountIndex] = (byte) getBeepCount();
        }
        return payload ;
    }

    /**
     * 开门
     * @param
     * @return
     */
    public static byte[] openDoor(){
        if (payload != null){
            //工作标志位10
            byte b6 = payload[6];
            //开门标志位
            int i = b6 & 0x10;
            if (i == 0){
                payload[6] = (byte) (b6 + 16) ;
            }
            payload[2] = (byte) 4 ;
            payload[3] = (byte) getBeepCount();
        }
        return payload ;
    }

    /**
     * 关门
     * @param
     * @return
     */
    public static byte[] shutDoor(){
        if (payload != null){
            //工作标志位10
            byte b6 = payload[6];
            //开门标志位
            int i = b6 & 0x10;
            if (i != 0){
                payload[6] = (byte) (b6 - 16) ;
            }
            payload[2] = (byte) 4 ;
            payload[3] = (byte) getBeepCount();
        }
        return payload ;
    }

    /**
     * 旋转烤盘
     * @param panState
     * @return
     */
    private static byte[] rotatePan(short panState){
//        if (payload != null){
//            bit_11[6] = 1 ;
//        }
//        String s = String.copyValueOf(bit_11);
//        int value = Integer.parseInt(s, 2);
//        payload[7] = (byte) value ;
        return payload ;
    }
    /**
     * 获取计数器
     * @return
     */
    public static int getBeepCount() {
        if (beepCount < 255){
            beepCount += 1 ;
        }else {
            beepCount = 0 ;
        }
        return beepCount;
    }
    /**
     * 设置水质
     * @return
     */
    public static byte[] setWaterQuality(int water_quality) {
        if (payload != null){
            payload[beepCountIndex] = (byte) getBeepCount() ;
            payload[waterQualityIndex] = (byte) water_quality ;
        }
        return payload;
    }
}



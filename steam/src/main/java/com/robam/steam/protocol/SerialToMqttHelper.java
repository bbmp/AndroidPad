package com.robam.steam.protocol;

import com.robam.common.utils.ByteUtils;
import com.robam.steam.bean.SteamOven;

public class SerialToMqttHelper {
    /**
     * 获取一体机对应MQTT协议中的电源状态
     */

    public static byte getPowerState() {
        short sysState = SteamOven.getInstance().sysState;
        if (sysState == 0) {
            return (byte) 0;
        } else if (sysState == 1 || sysState == 2 || sysState == 3) {
            return (byte) 2;
        } else {
            return (byte) 3;
        }
    }

    /**
     * 获取一体机对应MQTT协议中的工作状态
     */

    public static byte getWorkState() {
        int workState = SteamOven.getInstance().workState;
        if (workState == 1) { //预热
            return (byte) 2;
        } else if (workState == 2) { //预热暂停
            return (byte) 3;
        } else if (workState == 3) { //工作
            return (byte) 4;
        } else if (workState == 4) { //工作暂停
            return (byte) 5;
        } else {
            //设置过预约时间 设置为当前工作再预约中
            if (SteamOven.getInstance().orderTime != 0) {
                return (byte) 1;
            }
            return (byte) 0;
        }
    }

    /**
     * 获取一体机对应MQTT协议中的预约剩余时间
     */

    public static byte[] getOrderLeftSecs() {
        int orderLeftTime = SteamOven.getInstance().orderLeftTime;
        byte[] bytes = ByteUtils.intToBytes2(orderLeftTime);
        return bytes;
    }
    /**
     * 获取一体机故障码
     */

    public static byte getFaultCode() {
        if (SteamOven.getInstance().fault_temp_up) { //上温度故障
            return (byte) 2;
        } else if (SteamOven.getInstance().fault_temp_down) { //下温度故障
            return (byte) 3;
        } else if (SteamOven.getInstance().fault_disp_comm) { //显示板通信故障
            return (byte) 0;
        } else if (SteamOven.getInstance().fault_fan_up) { //上风机故障
            return (byte) 4;
        } else if (SteamOven.getInstance().fault_heat) { //加热故障
            return (byte) 9;
        } else if (SteamOven.getInstance().fault_water_level) { //水位检测故障
            return (byte) 6;
        } else if (SteamOven.getInstance().fault_heater_fan) { //加热风机故障
            return (byte) 12;
        } else if (SteamOven.getInstance().fault_steam_temp) { //底部温度故障
            return (byte) 9;
        } else if (SteamOven.getInstance().fault_up_and_down_motor) { //升降电机堵转
            return (byte) 0;
        } else {
            return 0;
        }
    }
    /**
     * 获取灯状态
     */

    public static byte getLampState() {
        return (byte) SteamOven.getInstance().upLampState;
    }
    /*
     *  获取旋转按钮状态
     */
    public static byte getRotateSwitch() {
        return (byte) SteamOven.getInstance().rotateSwitch;
    }

    /*
     ** 水箱状态
     */
    public static byte getWaterBoxState() {
        return (byte) (SteamOven.getInstance().waterBoxState == 0 ? 1 : 0);
    }
    /*
     * 水位状态
     */
    public static byte getWaterLevelState() {
        return (byte) SteamOven.getInstance().waterLevelState;
    }

    /*
     * 门状态
     */
    public static byte getDoorState() {
        return (byte) SteamOven.getInstance().doorState;
    }
    /*
     * 蒸汽状态
     */
    public static byte getSteamState() {
        return (byte) (SteamOven.getInstance().steamState ? 0 : 2);
    }

    /*
     * 菜谱编号
     */
    public static byte getRecipeId() {
        return (byte) SteamOven.getInstance().recipeId;
    }
    /*
     *菜谱设置时间
     */
    public static byte[] getRecipeSetSecs() {
        int recipeSetMinutes = SteamOven.getInstance().recipeSetMinutes;
        byte[] bytes = ByteUtils.intToBytes2(recipeSetMinutes);
        return bytes;
    }

    /*
     * 当前上温度
     */
    public static byte getTempUp() {
        return (byte) SteamOven.getInstance().upTemp;
    }

    /*
     * 当前下温度
     */
    public static byte getTempDown() {
        return (byte) SteamOven.getInstance().downTemp;
    }
    /*
     * 剩余总时间
     */
    public static byte[] getTotalRemainSeconds() {
        int totalRemainSeconds = SteamOven.getInstance().totalRemainSeconds;
        byte[] bytes = ByteUtils.intToBytes2(totalRemainSeconds);
        return bytes;
    }

    /*
     * 除垢请求标志
     */
    public static byte getDescaleFlag() {
        return (byte) SteamOven.getInstance().descaleNum;
    }
    /*
     * 首段模式
     */
    public static byte getMode() {
        return (byte) SteamOven.getInstance().mode;
    }

    /*
     * 设置上温度
     */
    public static byte getSetUpTemp() {
        return (byte) SteamOven.getInstance().setUpTemp;
    }

    /*
     * 设置下温度
     */
    public static byte getSetDownTemp() {
        return (byte) SteamOven.getInstance().setDownTemp;
    }



    /**
     * 获取总段数
     */
    public static byte getSectionNumber() {
        return (byte) SteamOven.getInstance().getSectionNumber();
    }

    /**
     * 获取当前段数
     */
    public static byte getCurSectionNbr() {
        return (byte) SteamOven.getInstance().curSectionNbr;
    }
    /*
     * 设置时间 设备端需要的是秒
     */
    public static byte[] getSetTime() {
        int setTime = SteamOven.getInstance().setTime * 60;
        byte[] bytes = ByteUtils.intToBytes2(setTime);
        return bytes;
    }

    /*
     * 剩余时间
     */
    public static byte[] getRestTime() {
        byte[] bytes = ByteUtils.intToBytes2(SteamOven.getInstance().restTime);
        return bytes;
    }
    /*
     *蒸汽量
     */
    public static byte getSteam() {
        return (byte) SteamOven.getInstance().steam;
    }

    /*
     * 二段模式
     */
    public static byte getMode2() {
        return (byte) SteamOven.getInstance().mode2;
    }

    /*
     * 二段模式设置上温度
     */
    public static byte getSetUpTemp2() {
        return (byte) SteamOven.getInstance().setUpTemp2;
    }

    /*
     * 二段模式设置下温度
     */
    public static byte getSetDownTemp2() {
        return (byte) SteamOven.getInstance().setDownTemp2;
    }

    /*
     * 二段模式设置时间
     */
    public static byte getSetTime2() {
        return (byte) SteamOven.getInstance().setTime2;
    }

    /*
     * 二段模式剩余时间
     */
    public static byte[] getRestTime2() {
        byte[] bytes = ByteUtils.intToBytes2(SteamOven.getInstance().restTime2);
        return bytes;
    }

    /*
     *二段模式蒸汽量
     */
    public static byte getSteam2() {
        return (byte) SteamOven.getInstance().steam2;
    }

    /*
     * 三段模式
     */
    public static byte getMode3() {
        return (byte) SteamOven.getInstance().mode3;
    }

    /*
     * 三段模式设置上温度
     */
    public static byte getSetUpTemp3() {
        return (byte) SteamOven.getInstance().setUpTemp3;
    }

    /*
     * 三段模式设置下温度
     */
    public static byte getSetDownTemp3() {
        return (byte) SteamOven.getInstance().setDownTemp3;
    }

    /*
     * 三段模式设置时间
     */
    public static byte getSetTime3() {
        return (byte) SteamOven.getInstance().setTime3;
    }

    /*
     * 三段模式剩余时间
     */
    public static byte[] getRestTime3() {
        byte[] bytes = ByteUtils.intToBytes2(SteamOven.getInstance().restTime3);
        return bytes;
    }

    /*
     *三段模式蒸汽量
     */
    public static byte getSteam3() {
        return (byte) SteamOven.getInstance().steam3;
    }
}

package com.robam.steamoven.protocol;

import com.robam.common.utils.ByteUtils;
import com.robam.steamoven.bean.SteamOven;

public class SerialToMqttHelper {
    /**
     * 获取一体机对应MQTT协议中的电源状态
     */

    public static byte getPowerState(SteamOven steamOven) {
        short sysState = steamOven.sysState;
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

    public static byte getWorkState(SteamOven steamOven) {
        int workState = steamOven.workState;
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
            if (steamOven.orderTime != 0) {
                return (byte) 1;
            }
            return (byte) 0;
        }
    }

    /**
     * 获取一体机对应MQTT协议中的预约剩余时间
     */

    public static byte[] getOrderLeftSecs(SteamOven steamOven) {
        int orderLeftTime = steamOven.orderLeftTime;
        byte[] bytes = ByteUtils.intToBytes2(orderLeftTime);
        return bytes;
    }
    /**
     * 获取一体机故障码
     */

    public static byte getFaultCode(SteamOven steamOven) {
        if (steamOven.fault_temp_up) { //上温度故障
            return (byte) 2;
        } else if (steamOven.fault_temp_down) { //下温度故障
            return (byte) 3;
        } else if (steamOven.fault_disp_comm) { //显示板通信故障
            return (byte) 0;
        } else if (steamOven.fault_fan_up) { //上风机故障
            return (byte) 4;
        } else if (steamOven.fault_heat) { //加热故障
            return (byte) 9;
        } else if (steamOven.fault_water_level) { //水位检测故障
            return (byte) 6;
        } else if (steamOven.fault_heater_fan) { //加热风机故障
            return (byte) 12;
        } else if (steamOven.fault_steam_temp) { //底部温度故障
            return (byte) 9;
        } else if (steamOven.fault_up_and_down_motor) { //升降电机堵转
            return (byte) 0;
        } else {
            return 0;
        }
    }
    /**
     * 获取灯状态
     */

    public static byte getLampState(SteamOven steamOven) {
        return (byte) steamOven.upLampState;
    }
    /*
     *  获取旋转按钮状态
     */
    public static byte getRotateSwitch(SteamOven steamOven) {
        return (byte) steamOven.rotateSwitch;
    }

    /*
     ** 水箱状态
     */
    public static byte getWaterBoxState(SteamOven steamOven) {
        return (byte) (steamOven.waterBoxState == 0 ? 1 : 0);
    }
    /*
     * 水位状态
     */
    public static byte getWaterLevelState(SteamOven steamOven) {
        return (byte) steamOven.waterLevelState;
    }

    /*
     * 门状态
     */
    public static byte getDoorState(SteamOven steamOven) {
        return (byte) steamOven.doorState;
    }
    /*
     * 蒸汽状态
     */
    public static byte getSteamState(SteamOven steamOven) {
        return (byte) (steamOven.steamState ? 0 : 2);
    }

    /*
     * 菜谱编号
     */
    public static byte getRecipeId(SteamOven steamOven) {
        return (byte) steamOven.recipeId;
    }
    /*
     *菜谱设置时间
     */
    public static byte[] getRecipeSetSecs(SteamOven steamOven) {
        int recipeSetMinutes = steamOven.recipeSetMinutes;
        byte[] bytes = ByteUtils.intToBytes2(recipeSetMinutes);
        return bytes;
    }

    /*
     * 当前上温度
     */
    public static byte getTempUp(SteamOven steamOven) {
        return (byte) steamOven.upTemp;
    }

    /*
     * 当前下温度
     */
    public static byte getTempDown(SteamOven steamOven) {
        return (byte) steamOven.downTemp;
    }
    /*
     * 剩余总时间
     */
    public static byte[] getTotalRemainSeconds(SteamOven steamOven) {
        int totalRemainSeconds = steamOven.totalRemainSeconds;
        byte[] bytes = ByteUtils.intToBytes2(totalRemainSeconds);
        return bytes;
    }

    /*
     * 除垢请求标志
     */
    public static byte getDescaleFlag(SteamOven steamOven) {
        return (byte) steamOven.descaleNum;
    }
    /*
     * 首段模式
     */
    public static byte getMode(SteamOven steamOven) {
        return (byte) steamOven.mode;
    }

    /*
     * 设置上温度
     */
    public static byte getSetUpTemp(SteamOven steamOven) {
        return (byte) steamOven.setUpTemp;
    }

    /*
     * 设置下温度
     */
    public static byte getSetDownTemp(SteamOven steamOven) {
        return (byte) steamOven.setDownTemp;
    }



    /**
     * 获取总段数
     */
    public static byte getSectionNumber(SteamOven steamOven) {
        return (byte) steamOven.getSectionNumber();
    }

    /**
     * 获取当前段数
     */
    public static byte getCurSectionNbr(SteamOven steamOven) {
        return (byte) steamOven.curSectionNbr;
    }
    /*
     * 设置时间 设备端需要的是秒
     */
    public static byte[] getSetTime(SteamOven steamOven) {
        int setTime = steamOven.setTime * 60;
        byte[] bytes = ByteUtils.intToBytes2(setTime);
        return bytes;
    }

    /*
     * 剩余时间
     */
    public static byte[] getRestTime(SteamOven steamOven) {
        byte[] bytes = ByteUtils.intToBytes2(steamOven.restTime);
        return bytes;
    }
    /*
     *蒸汽量
     */
    public static byte getSteam(SteamOven steamOven) {
        return (byte) steamOven.steam;
    }

    /*
     * 二段模式
     */
    public static byte getMode2(SteamOven steamOven) {
        return (byte) steamOven.mode2;
    }

    /*
     * 二段模式设置上温度
     */
    public static byte getSetUpTemp2(SteamOven steamOven) {
        return (byte) steamOven.setUpTemp2;
    }

    /*
     * 二段模式设置下温度
     */
    public static byte getSetDownTemp2(SteamOven steamOven) {
        return (byte) steamOven.setDownTemp2;
    }

    /*
     * 二段模式设置时间
     */
    public static byte getSetTime2(SteamOven steamOven) {
        return (byte) steamOven.setTime2;
    }

    /*
     * 二段模式剩余时间
     */
    public static byte[] getRestTime2(SteamOven steamOven) {
        byte[] bytes = ByteUtils.intToBytes2(steamOven.restTime2);
        return bytes;
    }

    /*
     *二段模式蒸汽量
     */
    public static byte getSteam2(SteamOven steamOven) {
        return (byte) steamOven.steam2;
    }

    /*
     * 三段模式
     */
    public static byte getMode3(SteamOven steamOven) {
        return (byte) steamOven.mode3;
    }

    /*
     * 三段模式设置上温度
     */
    public static byte getSetUpTemp3(SteamOven steamOven) {
        return (byte) steamOven.setUpTemp3;
    }

    /*
     * 三段模式设置下温度
     */
    public static byte getSetDownTemp3(SteamOven steamOven) {
        return (byte) steamOven.setDownTemp3;
    }

    /*
     * 三段模式设置时间
     */
    public static byte getSetTime3(SteamOven steamOven) {
        return (byte) steamOven.setTime3;
    }

    /*
     * 三段模式剩余时间
     */
    public static byte[] getRestTime3(SteamOven steamOven) {
        byte[] bytes = ByteUtils.intToBytes2(steamOven.restTime3);
        return bytes;
    }

    /*
     *三段模式蒸汽量
     */
    public static byte getSteam3(SteamOven steamOven) {
        return (byte) steamOven.steam3;
    }
}

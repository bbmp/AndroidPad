package com.robam.steamoven.device;

import com.robam.common.IDeviceType;
import com.robam.common.bean.AccountInfo;
import com.robam.common.bean.Device;
import com.robam.common.utils.ToastUtils;

public class HomeSteamOven {
    //当前进入的一体机
    public static HomeSteamOven getInstance() {
        return HomeSteamOven.SteanOvenHolder.instance;
    }
    private static class SteanOvenHolder {
        private static final HomeSteamOven instance = new HomeSteamOven();
    }
    //当前guid
    public String guid;

    /**
     * 工作状态
     */
    public int workState;
    /**
     * 工作类型 普通模式 多段 菜谱 蒸模式 烤模式
     */
    public short funCode;
    /**
     * 工作模式
     */
    public short workMode;

    //设备类型
    public String dt;

    //设备分类
    public String dc;
    /**
     * 剩余总时间
     */
    public int totalRemainSeconds;
    //工作温度
    public int workTemp;



    /**
     * 预约开始时间
     */
    public String orderTime;
    /**
     * 工作模式
     */
    //public int workMode;
    /**
     * 工作时长
     */
    public int workHours;

    /**
     * 是否离线
     * @return
     */
    public boolean isPanOffline() {
        for (Device device : AccountInfo.getInstance().deviceList) {
            if (device.dc.equals(IDeviceType.RZNG) && device.status == Device.ONLINE) {

                return false;
            }
        }
        return true;
    }
}

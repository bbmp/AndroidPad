package com.robam.stove.device;

import com.robam.common.IDeviceType;
import com.robam.common.bean.AccountInfo;
import com.robam.common.bean.Device;
import com.robam.common.device.Plat;
import com.robam.common.module.IPublicVentilatorApi;
import com.robam.common.module.ModulePubliclHelper;
import com.robam.common.utils.LogUtils;

import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class HomeStove {

    //当前进入的灶具锅首页
    public static HomeStove getInstance() {
        return HomeStove.StoveHolder.instance;
    }
    private static class StoveHolder {
        private static final HomeStove instance = new HomeStove();
    }
    //当前guid
    public String guid;
    /**
     * 当前功能
     */
    public int funCode;
//
//    /**
//     * 左灶工作模式
//     */
//    public int leftWorkMode;
//    /**
//     * 左灶工作时长
//     */
//    public String leftWorkHours;
//    //左灶工作温度
//    public String leftWorkTemp;
//    //左灶
//    public MutableLiveData<Boolean> leftStove = new MutableLiveData<>(false);
//    /**
//     * 右灶工作模式
//     */
//    public int rightWorkMode;
//    /**
//     * 右灶工作时长
//     */
//    public String rightWorkHours;
//    //右灶工作温度
//    public String rightWorkTemp;
//    //右灶
//    public MutableLiveData<Boolean> rightStove = new MutableLiveData<>(false);
    public boolean isStoveOffline() {
        for (Device device: AccountInfo.getInstance().deviceList) {
            if (device.dc.equals(IDeviceType.RRQZ) && device.status == Device.ONLINE) {

                return false;
            }
        }
        return true;
    }
}

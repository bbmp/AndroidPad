package com.robam.stove.device;

import androidx.lifecycle.MutableLiveData;

public class HomeStove {


    //当前进入的灶具锅首页
    public static HomeStove getInstance() {
        return HomeStove.StoveHolder.instance;
    }
    private static class StoveHolder {
        private static final HomeStove instance = new HomeStove();
    }
    /**
     * 当前功能
     */
    public int funCode;

    /**
     * 左灶工作模式
     */
    public int leftWorkMode;
    /**
     * 左灶工作时长
     */
    public String leftWorkHours;
    //左灶工作温度
    public String leftWorkTemp;
    //左灶
    public MutableLiveData<Boolean> leftStove = new MutableLiveData<>(false);
    /**
     * 右灶工作模式
     */
    public int rightWorkMode;
    /**
     * 右灶工作时长
     */
    public String rightWorkHours;
    //右灶工作温度
    public String rightWorkTemp;
    //右灶
    public MutableLiveData<Boolean> rightStove = new MutableLiveData<>(false);
}

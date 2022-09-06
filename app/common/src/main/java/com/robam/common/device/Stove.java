package com.robam.common.device;

import androidx.lifecycle.MutableLiveData;


/**
 * 灶具
 */
public class Stove {
    public static Stove getInstance() {
        return StoveHolder.instance;
    }

    //
    public final static int STOVE_LEFT = 0;
    public final static int STOVE_RIGHT = 1;
    private static class StoveHolder {
        private static final Stove instance = new Stove();
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

package com.robam.pan.device;


import androidx.lifecycle.MutableLiveData;

public class HomePan {
    //当前进入的锅首页
    public static HomePan getInstance() {
        return HomePan.PanHolder.instance;
    }

    private static class PanHolder {
        private static final HomePan instance = new HomePan();
    }

    //当前guid
    public String guid;
    //锅温度
    public MutableLiveData<Integer> panTemp = new MutableLiveData<Integer>(0);
}

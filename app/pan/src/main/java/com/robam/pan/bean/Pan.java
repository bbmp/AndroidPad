package com.robam.pan.bean;

import androidx.lifecycle.MutableLiveData;

public class Pan {
    public static Pan getInstance() {
        return Pan.PanHolder.instance;
    }

    private static class PanHolder {
        private static final Pan instance = new Pan();
    }
    //锅温度
    public MutableLiveData<Integer> panTemp = new MutableLiveData<Integer>(0);
}

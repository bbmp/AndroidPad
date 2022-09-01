package com.robam.stove.bean;

import android.content.Context;

import androidx.lifecycle.MutableLiveData;

import com.robam.common.manager.FunctionManager;
import com.robam.stove.R;

import java.util.List;

/**
 * 灶具
 */
public class Stove {
    public static Stove getInstance() {
        return StoveHolder.instance;
    }


    private static class StoveHolder {
        private static final Stove instance = new Stove();
    }

    private List<StoveFunBean> stoveFunBeans;
    /**
     * 当前功能
     */
    public int funCode;
    /**
     * 工作模式
     */
    public int workMode;
    /**
     * 工作时长
     */
    public String workHours;
    //工作温度
    public String workTemp;
    //左灶
    public MutableLiveData<Boolean> leftStove = new MutableLiveData<>(false);
    //右灶
    public MutableLiveData<Boolean> rightStove = new MutableLiveData<>(false);

    public void init(Context context) {
        if (null == stoveFunBeans)
            stoveFunBeans = FunctionManager.getFuntionList(context, StoveFunBean.class, R.raw.stove);
    }

    public List<StoveFunBean> getStoveFunBeans() {
        return stoveFunBeans;
    }
    //当前功能下的模式
    public List<ModeBean> getModeBeans(int funCode) {
        if (null != stoveFunBeans) {
            for (int i=0; i<stoveFunBeans.size(); i++) {
                if (stoveFunBeans.get(i).funtionCode == funCode)
                    return stoveFunBeans.get(i).mode;
            }
        }
        return null;
    }
}

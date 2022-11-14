package com.robam.common.ui.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import androidx.annotation.Nullable;
import androidx.navigation.NavController;

import com.robam.common.R;
import com.robam.common.device.Plat;
import com.robam.common.http.ILife;
import com.robam.common.module.IPublicVentilatorApi;
import com.robam.common.module.ModulePubliclHelper;
import com.robam.common.skin.SkinStatusBarUtils;
import com.robam.common.ui.action.ActivityAction;
import com.robam.common.ui.action.ClickAction;
import com.robam.common.utils.ToastUtils;

public abstract class BaseActivity extends AbsActivity implements ActivityAction, ClickAction, ILife {
    protected NavController navController;
    private boolean isDestroyed;
    private IPublicVentilatorApi iPublicVentilatorApi = ModulePubliclHelper.getModulePublic(IPublicVentilatorApi.class, IPublicVentilatorApi.VENTILATOR_PUBLIC);

    private boolean lockLongPressKey;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
//        //沉浸式
//        SkinStatusBarUtils.translucent(this);
//        //字体
//        SkinStatusBarUtils.setStatusBarLightMode(this);

        setContentView(getLayoutId());
        //占位状态栏
//        setStateBarFixer();
        initView();
        initData();
    }

    @Override
    public Context getContext() {
        return this;
    }

    /**
     * 获取布局 ID
     */
    protected abstract int getLayoutId();

    /**
     * 初始化控件
     */
    protected abstract void initView();

    /**
     * 初始化数据
     */
    protected abstract void initData();

    /**
     * 设置状态栏占位
     */
    protected void setStateBarFixer(){
        View mStateBarFixer = findViewById(R.id.status_bar_fix);
        if (mStateBarFixer != null){
            ViewGroup.LayoutParams layoutParams = mStateBarFixer.getLayoutParams();
            layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
            layoutParams.height = getStatusBarHeight(this);
            mStateBarFixer.setLayoutParams(layoutParams);

        }
    }

    private int getStatusBarHeight(Activity a) {
        int result = 0;
        int resourceId = a.getResources().getIdentifier("status_bar_height",
                "dimen", "android");
        if (resourceId > 0) {
            result = a.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    @Override
    public void onBackPressed() {


        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        isDestroyed = true;
        //关闭延时关机
        IPublicVentilatorApi iPublicVentilatorApi = ModulePubliclHelper.getModulePublic(IPublicVentilatorApi.class, IPublicVentilatorApi.VENTILATOR_PUBLIC);
        if (null != iPublicVentilatorApi)
            iPublicVentilatorApi.closeDelayDialog();
    }

    @Override
    public boolean onKeyLongPress(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_F2) {
            if (!iPublicVentilatorApi.isStartUp()) {//关机状态
                lockLongPressKey = true;
                iPublicVentilatorApi.setColorLamp();
                Plat.getPlatform().openWaterLamp();
            }
            return true;
        }
        return super.onKeyLongPress(keyCode, event);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_F2) {
            if (event.getRepeatCount() == 0) {
                lockLongPressKey = false;
                event.startTracking();
            } else
                lockLongPressKey = true;
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_F2) {
            if (lockLongPressKey) {
                lockLongPressKey = false;
                return true;
            }
            if (null != iPublicVentilatorApi) {
//                ToastUtils.showShort(this, "light " + iPublicVentilatorApi.getFanLight());
                if (iPublicVentilatorApi.getFanLight() == 0) {
                    Plat.getPlatform().openWaterLamp();
                    iPublicVentilatorApi.setFanLight(1);
                } else {
                    Plat.getPlatform().closeWaterLamp();
                    iPublicVentilatorApi.setFanLight(0);
                }
                return true;
            }
        } else if (keyCode == KeyEvent.KEYCODE_F1) {
            if (null != iPublicVentilatorApi) {
                if (iPublicVentilatorApi.isStartUp()) {
                    iPublicVentilatorApi.beep();
                    //延时关机
                   iPublicVentilatorApi.delayShutDown();
                } else {
                    iPublicVentilatorApi.powerOn(); //开机
                    Plat.getPlatform().screenOn();
                    Plat.getPlatform().openPowerLamp();
//                    iPublicVentilatorApi.startService(this);
                }
                return true;
            }
        }
        return super.onKeyUp(keyCode, event);
    }

    @Override
    public boolean isDestroyed() {
        return isDestroyed;
    }
}

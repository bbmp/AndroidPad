package com.robam.common.ui.activity;

import android.app.Activity;
import android.content.Context;
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
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        ToastUtils.showShort(this, "keycode=" + event.getKeyCode());
        if (event.getKeyCode() == KeyEvent.KEYCODE_VOLUME_UP && event.getAction() == KeyEvent.ACTION_UP) { //左键

            if (null != iPublicVentilatorApi) {
                ToastUtils.showShort(this, "light " + iPublicVentilatorApi.getFanLight());
                if (iPublicVentilatorApi.getFanLight() == 0) {
                    Plat.getPlatform().openPowerLamp();
                    iPublicVentilatorApi.setFanLight(1);
                } else {
                    Plat.getPlatform().closePowerLamp();
                    iPublicVentilatorApi.setFanLight(0);
                }
                return true;
            }
        } else if (event.getKeyCode() == KeyEvent.KEYCODE_F1 && event.getAction() == KeyEvent.ACTION_UP) { //右键
            if (null != iPublicVentilatorApi) {
                if (iPublicVentilatorApi.isScreenOn(this)) {
                    Plat.getPlatform().screenOff();
                    Plat.getPlatform().closePowerLamp();
//                iPublicVentilatorApi.shutDown();
                } else {
                    Plat.getPlatform().screenOn();
                    Plat.getPlatform().openPowerLamp();
                }
            }

            return true;
        }
        return super.dispatchKeyEvent(event);
    }

    @Override
    public boolean isDestroyed() {
        return isDestroyed;
    }
}

package com.robam.common.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;

import com.robam.common.R;
import com.robam.common.skin.SkinStatusBarUtils;
import com.robam.common.ui.action.ClickAction;

public abstract class BaseActivity extends AbsActivity implements ClickAction {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //沉浸式
        SkinStatusBarUtils.translucent(this);
        //字体
        SkinStatusBarUtils.setStatusBarLightMode(this);
    }

}

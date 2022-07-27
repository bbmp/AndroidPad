package com.robam.common.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import androidx.annotation.Nullable;

import com.robam.common.R;
import com.robam.common.skin.SkinStatusBarUtils;
import com.robam.common.ui.action.ClickAction;

public abstract class BaseActivity extends AbsActivity implements ClickAction {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        //沉浸式
        SkinStatusBarUtils.translucent(this);
        //字体
        SkinStatusBarUtils.setStatusBarLightMode(this);
    }

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
//        super.onBackPressed();
        finishAfterTransition();
    }
}

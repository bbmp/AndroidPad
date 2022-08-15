package com.robam.dishwasher.base;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.robam.common.ui.activity.BaseActivity;
import com.robam.common.utils.LogUtils;
import com.robam.dishwasher.R;
import com.robam.dishwasher.manager.AppManager;

public abstract class DishWasherBaseActivity extends BaseActivity {

    public void showFloat() {
        findViewById(R.id.iv_float).setVisibility(View.VISIBLE);
    }

    public void showLeft() {
        findViewById(R.id.ll_left).setVisibility(View.VISIBLE);
    }

    public void showCenter() {
        findViewById(R.id.ll_center).setVisibility(View.VISIBLE);
    }

    public void showRight() {
        findViewById(R.id.ll_right).setVisibility(View.VISIBLE);
    }

    public void setRight(int res) {
        findViewById(R.id.ll_right).setVisibility(View.VISIBLE);
        TextView textView = findViewById(R.id.tv_right);
        textView.setText(res);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppManager.getInstance().addActivity(this);
        LogUtils.i("washer onCreate stack size = " + AppManager.getInstance().getActivityStackSize());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        AppManager.getInstance().removeActivity(this);
        LogUtils.i("washer onDestroy stack size = " + AppManager.getInstance().getActivityStackSize());
    }
}

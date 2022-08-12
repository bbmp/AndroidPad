package com.robam.dishwasher.base;

import android.view.View;

import com.robam.common.ui.activity.BaseActivity;
import com.robam.dishwasher.R;

public abstract class DishWasherBaseActivity extends BaseActivity {

    public void showLeft() {
        findViewById(R.id.ll_left).setVisibility(View.VISIBLE);
    }

    public void showCenter() {
        findViewById(R.id.ll_center).setVisibility(View.VISIBLE);
    }

    public void showRight() {
        findViewById(R.id.ll_right).setVisibility(View.VISIBLE);
    }
}

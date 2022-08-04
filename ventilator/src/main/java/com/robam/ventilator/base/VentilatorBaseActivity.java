package com.robam.ventilator.base;

import android.view.View;

import com.robam.common.ui.activity.BaseActivity;
import com.robam.ventilator.R;

public abstract class VentilatorBaseActivity extends BaseActivity {

    public void showLeft() {
        findViewById(R.id.ll_left).setVisibility(View.VISIBLE);
    }

    public void showCenter() {
        findViewById(R.id.ll_center).setVisibility(View.VISIBLE);
    }
}

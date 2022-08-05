package com.robam.ventilator.base;

import android.view.View;
import android.widget.TextView;

import com.robam.common.ui.activity.BaseActivity;
import com.robam.ventilator.R;

public abstract class VentilatorBaseActivity extends BaseActivity {

    public void showLeft() {
        findViewById(R.id.ll_left).setVisibility(View.VISIBLE);
    }

    public void showCenter() {
        findViewById(R.id.ll_center).setVisibility(View.VISIBLE);
    }

    public void setCenter(int res) {
        findViewById(R.id.ll_center).setVisibility(View.VISIBLE);
        findViewById(R.id.iv_center).setVisibility(View.GONE);
        findViewById(R.id.tc_center).setVisibility(View.GONE);
        TextView textView = findViewById(R.id.tv_center);
        textView.setVisibility(View.VISIBLE);
        textView.setText(res);
    }

}

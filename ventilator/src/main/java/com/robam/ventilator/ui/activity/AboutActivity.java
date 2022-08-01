package com.robam.ventilator.ui.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.robam.common.ui.activity.BaseActivity;
import com.robam.ventilator.R;

public class AboutActivity extends BaseActivity {

    @Override
    protected int getLayoutId() {
        return R.layout.ventilator_activity_layout_about;
    }

    @Override
    protected void initView() {
        findViewById(R.id.iv_left).setVisibility(View.VISIBLE);
        findViewById(R.id.tv_left).setVisibility(View.VISIBLE);
        TextView tvCenter = findViewById(R.id.tv_center);
        tvCenter.setVisibility(View.VISIBLE);
        tvCenter.setText(R.string.ventilator_about_product);
    }

    @Override
    protected void initData() {

    }
}
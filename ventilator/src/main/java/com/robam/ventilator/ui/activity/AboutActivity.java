package com.robam.ventilator.ui.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.robam.common.ui.activity.BaseActivity;
import com.robam.ventilator.BuildConfig;
import com.robam.ventilator.R;
import com.robam.ventilator.base.VentilatorBaseActivity;

public class AboutActivity extends VentilatorBaseActivity {
    private TextView tvSysV;
    private TextView tvModelV;

    @Override
    protected int getLayoutId() {
        return R.layout.ventilator_activity_layout_about;
    }

    @Override
    protected void initView() {

        showLeft();
        showCenter();
        findViewById(R.id.tc_center).setVisibility(View.GONE);
        tvSysV = findViewById(R.id.tv_sys_v);
        tvModelV = findViewById(R.id.tv_model_v);
        TextView tvCenter = findViewById(R.id.tv_center);
        tvCenter.setVisibility(View.VISIBLE);
        tvCenter.setText(R.string.ventilator_about_product);
        setOnClickListener(R.id.ll_left);
    }

    @Override
    protected void initData() {
        tvSysV.setText(BuildConfig.VERSION_NAME);
        tvModelV.setText(BuildConfig.MODEL);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.ll_left)
            finish();
    }
}
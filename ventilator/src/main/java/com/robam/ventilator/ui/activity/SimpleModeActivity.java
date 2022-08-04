package com.robam.ventilator.ui.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;

import com.robam.common.ui.activity.BaseActivity;
import com.robam.ventilator.R;
import com.robam.ventilator.base.VentilatorBaseActivity;

public class SimpleModeActivity extends VentilatorBaseActivity {


    @Override
    protected int getLayoutId() {
        return R.layout.ventilator_activity_layout_simple_mode;
    }

    @Override
    protected void initView() {

        showCenter();
        setOnClickListener(R.id.tv_exit_simple_mode);
    }

    @Override
    protected void initData() {

    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.tv_exit_simple_mode)
            finish();
    }
}
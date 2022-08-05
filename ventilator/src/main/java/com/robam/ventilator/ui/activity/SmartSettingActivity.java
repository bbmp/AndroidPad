package com.robam.ventilator.ui.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;

import com.robam.ventilator.R;
import com.robam.ventilator.base.VentilatorBaseActivity;

public class SmartSettingActivity extends VentilatorBaseActivity {


    @Override
    protected int getLayoutId() {
        return R.layout.ventilator_activity_layout_smart_setting;
    }

    @Override
    protected void initView() {
        showLeft();
        setCenter(R.string.ventilator_smart_setting);

        setOnClickListener(R.id.ll_left);
    }

    @Override
    protected void initData() {

    }
    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.ll_left)
            finish();
    }
}
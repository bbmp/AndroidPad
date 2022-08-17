package com.robam.ventilator.ui.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.robam.common.ui.activity.BaseActivity;
import com.robam.common.ui.view.SwitchButton;
import com.robam.ventilator.R;
import com.robam.ventilator.base.VentilatorBaseActivity;

public class DateSettingActivity extends VentilatorBaseActivity {
    private SwitchButton switchButton;
    private LinearLayout llSelect;

    @Override
    protected int getLayoutId() {
        return R.layout.ventilator_activity_layout_date_setting;
    }

    @Override
    protected void initView() {
        showLeft();
        setCenter(R.string.ventilator_setting_time);

        switchButton = findViewById(R.id.sb_auto_set);
        llSelect = findViewById(R.id.ll_date_select);
        switchButton.setOnCheckedChangeListener(new SwitchButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(SwitchButton button, boolean checked) {
                llSelect.setVisibility(checked? View.GONE: View.VISIBLE);
            }
        });
    }

    @Override
    protected void initData() {

    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        int id = view.getId();

    }
}
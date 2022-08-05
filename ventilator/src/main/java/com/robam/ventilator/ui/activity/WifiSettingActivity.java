package com.robam.ventilator.ui.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.robam.common.ui.activity.BaseActivity;
import com.robam.ventilator.R;
import com.robam.ventilator.base.VentilatorBaseActivity;

public class WifiSettingActivity extends VentilatorBaseActivity {
    private RecyclerView rvWifi;

    @Override
    protected int getLayoutId() {
        return R.layout.ventilator_activity_layout_wifi_setting;
    }

    @Override
    protected void initView() {
        showLeft();
        setCenter(R.string.ventilator_net_set);
        //首次进入
        setRight();
        setOnClickListener(R.id.ll_left);
    }

    @Override
    protected void initData() {

    }

    public void setRight() {
        findViewById(R.id.ll_right).setVisibility(View.VISIBLE);
        findViewById(R.id.iv_right1).setVisibility(View.GONE);
        findViewById(R.id.view_right1).setVisibility(View.GONE);
        findViewById(R.id.view_right2).setVisibility(View.VISIBLE);
        findViewById(R.id.iv_right2).setVisibility(View.VISIBLE);
        TextView textView = findViewById(R.id.tv_right);
        textView.setText(R.string.ventilator_skip);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.ll_left)
            finish();
    }
}
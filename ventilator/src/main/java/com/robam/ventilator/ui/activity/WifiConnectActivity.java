package com.robam.ventilator.ui.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.robam.common.ui.activity.BaseActivity;
import com.robam.common.ui.view.PasswordEditText;
import com.robam.ventilator.R;
import com.robam.ventilator.base.VentilatorBaseActivity;
import com.robam.ventilator.constant.VentilatorConstant;
import com.robam.ventilator.manager.VenWifiManager;

public class WifiConnectActivity extends VentilatorBaseActivity {
    private TextView tvName;
    private PasswordEditText etPassword;

    @Override
    protected int getLayoutId() {
        return R.layout.ventilator_activity_layout_wifi_connect;
    }

    @Override
    protected void initView() {
        showLeft();
        setCenter(R.string.ventilator_input_password);

        tvName = findViewById(R.id.tv_wifi_name);
        etPassword = findViewById(R.id.et_password);
        Bundle bundle = getIntent().getExtras();
        if (null != bundle) {
            String ssid = bundle.getString(VentilatorConstant.EXTRA_WIFI_SSID);
            tvName.setText(ssid);
        }

        setOnClickListener(R.id.btn_join);
    }

    @Override
    protected void initData() {

    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        int id = view.getId();
        if (id == R.id.btn_join) {
            //连接网络
            VenWifiManager.connectWifiPws(getApplicationContext()
                    , tvName.getText().toString()
                    ,etPassword.getText().toString()
            );
            connecting();
        }
    }

    //连接网络
    private void connecting() {

    }
}
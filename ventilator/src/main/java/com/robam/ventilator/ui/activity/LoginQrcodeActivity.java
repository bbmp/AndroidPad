package com.robam.ventilator.ui.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;

import com.robam.ventilator.R;
import com.robam.ventilator.base.VentilatorBaseActivity;

public class LoginQrcodeActivity extends VentilatorBaseActivity {

    @Override
    protected int getLayoutId() {
        return R.layout.ventilator_activity_layout_login_qrcode;
    }

    @Override
    protected void initView() {
        showLeft();
        setCenter(R.string.ventilator_login_qrcode);
        setOnClickListener(R.id.tv_login_phone, R.id.tv_login_password);
    }

    @Override
    protected void initData() {

    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        int id = view.getId();
        if (id == R.id.tv_login_phone) {
            startActivity(LoginPhoneActivity.class);
            finish();
        } else if (id == R.id.tv_login_password) {
            startActivity(LoginPasswordActivity.class);
            finish();
        }
    }
}
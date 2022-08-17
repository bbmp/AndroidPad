package com.robam.ventilator.ui.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;

import com.robam.ventilator.R;
import com.robam.ventilator.base.VentilatorBaseActivity;

public class LoginPasswordActivity extends VentilatorBaseActivity {


    @Override
    protected int getLayoutId() {
        return R.layout.ventilator_activity_layout_login_password;
    }

    @Override
    protected void initView() {
        showLeft();
        setCenter(R.string.ventilator_login_password);

        setOnClickListener(R.id.tv_login_qrcode, R.id.tv_login_phone);
    }

    @Override
    protected void initData() {

    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        int id = view.getId();
        if (id == R.id.tv_login_qrcode) {
            startActivity(LoginQrcodeActivity.class);
            finish();
        } else if (id == R.id.tv_login_phone) {
            startActivity(LoginPhoneActivity.class);
            finish();
        }
    }
}
package com.robam.ventilator.ui.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.robam.common.ui.activity.BaseActivity;
import com.robam.common.ui.view.MCountdownView;
import com.robam.common.utils.LogUtils;
import com.robam.common.utils.ToastUtils;
import com.robam.ventilator.R;
import com.robam.ventilator.base.VentilatorBaseActivity;

public class LoginPhoneActivity extends VentilatorBaseActivity {
    private MCountdownView mCountdownView;
    private EditText etPhone, etVerify;


    @Override
    protected int getLayoutId() {
        return R.layout.ventilator_activity_layout_login_phone;
    }

    @Override
    protected void initView() {

        showLeft();
        setCenter(R.string.ventilator_login_phone);

        mCountdownView = findViewById(R.id.tv_getverifycode);
        etPhone = findViewById(R.id.et_phone);
        etVerify = findViewById(R.id.et_verifycode);
        setOnClickListener(R.id.tv_login_qrcode, R.id.tv_login_password, R.id.bt_login, R.id.tv_getverifycode);
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
        } else if (id == R.id.tv_login_password) {
            startActivity(LoginPasswordActivity.class);
            finish();
        } else if (id == R.id.tv_login) {
            //login
            String phone = etPhone.getText().toString();
            if (TextUtils.isEmpty(phone)) {
                ToastUtils.showShort(this, R.string.ventilator_input_phone_hint);
                return;
            }
            String verifyCode = etVerify.getText().toString();
            if (TextUtils.isEmpty(verifyCode)) {
                ToastUtils.showShort(this, R.string.ventilator_input_verifycode_hint);
                return;
            }

        } else if (id == R.id.tv_getverifycode) {
            //获取验证码
            mCountdownView.addOnCountDownListener(new MCountdownView.OnCountDownListener() {
                @Override
                public void onCountDown(int currentSecond) {
                    if (currentSecond <= 0)
                        mCountdownView.setText(R.string.ventilator_get_verifycode);
                    else
                        mCountdownView.setText(currentSecond + "s");
                }
            });
            mCountdownView.start();
        }
    }
}
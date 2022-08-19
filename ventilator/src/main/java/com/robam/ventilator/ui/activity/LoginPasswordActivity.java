package com.robam.ventilator.ui.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import com.google.gson.Gson;
import com.robam.common.http.RetrofitCallback;
import com.robam.common.utils.LogUtils;
import com.robam.common.utils.MD5Utils;
import com.robam.common.utils.MMKVUtils;
import com.robam.common.utils.ToastUtils;
import com.robam.ventilator.R;
import com.robam.ventilator.base.VentilatorBaseActivity;
import com.robam.ventilator.bean.AccountInfo;
import com.robam.ventilator.http.CloudHelper;
import com.robam.ventilator.response.GetTokenRes;
import com.robam.ventilator.response.GetUserInfoRes;

public class LoginPasswordActivity extends VentilatorBaseActivity {
    private static final String PASSWORD_LOGIN = "mobilePassword";

    //输入手机号和密码
    private EditText etPhone, etPwd;

    @Override
    protected int getLayoutId() {
        return R.layout.ventilator_activity_layout_login_password;
    }

    @Override
    protected void initView() {
        showLeft();
        setCenter(R.string.ventilator_login_password);
        etPhone = findViewById(R.id.et_phone);
        etPwd = findViewById(R.id.et_password);

        setOnClickListener(R.id.bt_login, R.id.tv_login_qrcode, R.id.tv_login_phone);
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
        } else if (id == R.id.bt_login) { //登录
            String phone = etPhone.getText().toString();
            if (TextUtils.isEmpty(phone)) {
                ToastUtils.showShort(this, R.string.ventilator_input_phone_hint);
                return;
            }
            String pwd = etPwd.getText().toString();
            if (TextUtils.isEmpty(pwd)) {
                ToastUtils.showShort(this, R.string.ventilator_input_password_hint);
                return;
            }
            //获取token
            getToken(phone, pwd);
        }
    }
    //获取token
    private void getToken(String phone, String pwd) {
        CloudHelper.getToken(this, PASSWORD_LOGIN, phone, "", MD5Utils.digest(pwd), "roki_client", "test", "RKDRD",
                GetTokenRes.class, new RetrofitCallback<GetTokenRes>() {
                    @Override
                    public void onSuccess(GetTokenRes getTokenRes) {
                        if (null != getTokenRes) {
                            getUserInfo(getTokenRes.getAccess_token());
                        } else {
                            ToastUtils.showShort(LoginPasswordActivity.this, R.string.ventilator_request_failed);
                        }
                    }

                    @Override
                    public void onFaild(String err) {
                        LogUtils.e("getToken" + err);
                    }
                });
    }
    //获取用户信息
    private void getUserInfo(String access_token) {
        CloudHelper.getUserInfo(this, access_token, GetUserInfoRes.class, new RetrofitCallback<GetUserInfoRes>() {
            @Override
            public void onSuccess(GetUserInfoRes getUserInfoRes) {
                if (null != getUserInfoRes && null != getUserInfoRes.getUser()) {
                    String userJson = new Gson().toJson(getUserInfoRes.getUser());
                    //保存用户信息及登录状态
                    MMKVUtils.login(true);
                    MMKVUtils.setUser(userJson);
                    //登录成功
                    AccountInfo.getInstance().getUser().setValue(getUserInfoRes.getUser());
                    finish();
                    //绑定设备
//                    bindDevice(getUserInfoRes.getUser().id);
                } else {
                    ToastUtils.showShort(LoginPasswordActivity.this, R.string.ventilator_request_failed);
                }
            }

            @Override
            public void onFaild(String err) {
                LogUtils.e("getUserInfo" + err);
            }
        });
    }
}
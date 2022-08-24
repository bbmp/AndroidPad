package com.robam.ventilator.ui.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.google.gson.Gson;
import com.robam.common.bean.BaseResponse;
import com.robam.common.http.RetrofitCallback;
import com.robam.common.utils.LogUtils;
import com.robam.common.utils.MMKVUtils;
import com.robam.common.utils.QrUtils;
import com.robam.common.utils.ToastUtils;
import com.robam.ventilator.R;
import com.robam.ventilator.base.VentilatorBaseActivity;
import com.robam.ventilator.bean.AccountInfo;
import com.robam.ventilator.bean.UserInfo;
import com.robam.ventilator.http.CloudHelper;
import com.robam.ventilator.response.GetLoginStatusRes;
import com.robam.ventilator.response.GetUserInfoRes;

import java.lang.ref.WeakReference;
import java.util.Random;
import java.util.UUID;

public class LoginQrcodeActivity extends VentilatorBaseActivity {
    private ImageView ivQrcode;
    private static final String QRCODE_LOGIN = "qrcode";

    @Override
    protected int getLayoutId() {
        return R.layout.ventilator_activity_layout_login_qrcode;
    }

    @Override
    protected void initView() {
        showLeft();
        setCenter(R.string.ventilator_login_qrcode);
        ivQrcode = findViewById(R.id.iv_qrcode);

        setOnClickListener(R.id.tv_login_phone, R.id.tv_login_password);
    }

    @Override
    protected void initData() {
        getQrCode();
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

    /**
     * 获取二维码接口
     */
    private void getQrCode() {
        String preUuid = UUID.randomUUID().toString();

        final Bitmap imgBit = QrUtils.create2DCode("UUID-LOGIN" + preUuid);
        if (ivQrcode != null) {
            ivQrcode.setImageBitmap(imgBit);
        }
        getLoginStatus(preUuid);
    }

    /**
     * 登录接口
     *
     */
    private void getLoginStatus(String key) {
        CloudHelper.getLoginStatus(this, key, GetLoginStatusRes.class, new RetrofitCallback<GetLoginStatusRes>() {
            @Override
            public void onSuccess(GetLoginStatusRes getLoginStatusRes) {
                if (null != getLoginStatusRes) {
                    GetLoginStatusRes.Payload payLoad = getLoginStatusRes.payload;
                    if (null != payLoad) {
                        String account = payLoad.account;
                        String password = payLoad.password;
                        toLogin(account, password);
                    }
                }
            }

            @Override
            public void onFaild(String err) {
                LogUtils.e("getLoginStatus" + err);
            }
        });

    }
    //获取用户信息
    private void toLogin(String account, String password) {
        CloudHelper.loginQrcode(this, account, password, GetUserInfoRes.class, new RetrofitCallback<GetUserInfoRes>() {
            @Override
            public void onSuccess(GetUserInfoRes getUserInfoRes) {
                if (null != getUserInfoRes && null != getUserInfoRes.getUser()) {
                    UserInfo info = getUserInfoRes.getUser();
                    info.loginType = QRCODE_LOGIN;
                    info.password = password;
                    String userJson = new Gson().toJson(info);
                    //保存用户信息及登录状态
                    MMKVUtils.login(true);
                    MMKVUtils.setUser(userJson);
                    //登录成功
                    AccountInfo.getInstance().getUser().setValue(info);
                    finish();
                    //绑定设备
//                    bindDevice(getUserInfoRes.getUser().id);
                } else {
                    ToastUtils.showShort(LoginQrcodeActivity.this, R.string.ventilator_request_failed);
                }
            }

            @Override
            public void onFaild(String err) {
                LogUtils.e("toLogin" + err);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
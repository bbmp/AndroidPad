package com.robam.ventilator.ui.activity;

import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import com.google.gson.Gson;
import com.robam.common.http.RetrofitCallback;
import com.robam.common.ui.dialog.IDialog;
import com.robam.common.utils.LogUtils;
import com.robam.common.utils.MD5Utils;
import com.robam.common.utils.MMKVUtils;
import com.robam.common.utils.ToastUtils;
import com.robam.ventilator.R;
import com.robam.ventilator.base.VentilatorBaseActivity;
import com.robam.common.bean.AccountInfo;
import com.robam.common.bean.UserInfo;
import com.robam.ventilator.constant.DialogConstant;
import com.robam.ventilator.constant.VentilatorConstant;
import com.robam.ventilator.factory.VentilatorDialogFactory;
import com.robam.ventilator.http.CloudHelper;
import com.robam.ventilator.response.GetTokenRes;
import com.robam.ventilator.response.GetUserInfoRes;

public class LoginPasswordActivity extends VentilatorBaseActivity {
    private static final String PASSWORD_LOGIN = "mobilePassword";

    //输入手机号和密码
    private EditText etPhone, etPwd;
    private IDialog waitDialog;
    private boolean first;

    @Override
    protected int getLayoutId() {
        return R.layout.ventilator_activity_layout_login_password;
    }

    @Override
    protected void initView() {
        if (null != getIntent()) {
            first = getIntent().getBooleanExtra(VentilatorConstant.EXTRA_FIRST, false);
            if (first) { //首次登录
                //跳过
                setRight();
            } else
                showLeft();
        } else
            showLeft();
        setCenter(R.string.ventilator_login_password);
        etPhone = findViewById(R.id.et_phone);
        etPwd = findViewById(R.id.et_password);

        setOnClickListener(R.id.bt_login, R.id.tv_login_qrcode, R.id.tv_login_phone, R.id.ll_right);
    }

    @Override
    protected void initData() {

    }

    //登录等待中
    private void showWaitDialog() {
        if (null == waitDialog) {
            waitDialog = VentilatorDialogFactory.createDialogByType(this, DialogConstant.DIALOG_TYPE_WAITING);
            waitDialog.setCancelable(false);
        }
        waitDialog.show();
    }
    //关闭
    private void cancelWaitDialog() {
        if (null != waitDialog)
            waitDialog.dismiss();
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        int id = view.getId();
        if (id == R.id.tv_login_qrcode) {
            Intent intent = new Intent(LoginPasswordActivity.this, LoginQrcodeActivity.class);
            intent.putExtra(VentilatorConstant.EXTRA_FIRST, first);
            startActivity(intent);
            finish();
        } else if (id == R.id.tv_login_phone) {
            Intent intent = new Intent(LoginPasswordActivity.this, LoginPhoneActivity.class);
            intent.putExtra(VentilatorConstant.EXTRA_FIRST, first);
            startActivity(intent);
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

            showWaitDialog();
            //获取token
            getToken(phone, pwd);
        } else if (id == R.id.ll_right) {
            //跳转到首页
            if (first)
                HomeActivity.start(this);
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
                            cancelWaitDialog();
                            ToastUtils.showShort(LoginPasswordActivity.this, R.string.ventilator_request_failed);
                        }
                    }

                    @Override
                    public void onFaild(String err) {
                        cancelWaitDialog();
                        ToastUtils.showShort(LoginPasswordActivity.this, R.string.ventilator_net_err);
                        LogUtils.e("getToken" + err);
                    }
                });
    }
    //获取用户信息
    private void getUserInfo(String access_token) {
        CloudHelper.getUserInfo(this, access_token, GetUserInfoRes.class, new RetrofitCallback<GetUserInfoRes>() {
            @Override
            public void onSuccess(GetUserInfoRes getUserInfoRes) {
                cancelWaitDialog();
                if (null != getUserInfoRes && null != getUserInfoRes.getUser()) {
                    UserInfo info = getUserInfoRes.getUser();
                    info.loginType = PASSWORD_LOGIN;
                    String userJson = new Gson().toJson(info);
                    //保存用户信息及登录状态
                    MMKVUtils.login(true);
                    MMKVUtils.setUser(userJson);
                    //登录成功
                    AccountInfo.getInstance().getUser().setValue(info);
                    //跳转到首页
                    if (first)
                        HomeActivity.start(LoginPasswordActivity.this);
                    finish();
                    //绑定设备
//                    bindDevice(getUserInfoRes.getUser().id);
                } else {
                    ToastUtils.showShort(LoginPasswordActivity.this, R.string.ventilator_request_failed);
                }
            }

            @Override
            public void onFaild(String err) {
                cancelWaitDialog();
                ToastUtils.showShort(LoginPasswordActivity.this, R.string.ventilator_net_err);
                LogUtils.e("getUserInfo" + err);
            }
        });
    }
}
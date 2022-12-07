package com.robam.ventilator.ui.activity;

import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import com.google.gson.Gson;
import com.robam.common.bean.BaseResponse;
import com.robam.common.device.Plat;
import com.robam.common.http.RetrofitCallback;
import com.robam.common.ui.dialog.IDialog;
import com.robam.common.ui.view.MCountdownView;
import com.robam.common.utils.LogUtils;
import com.robam.common.utils.MMKVUtils;
import com.robam.common.utils.ToastUtils;
import com.robam.ventilator.R;
import com.robam.ventilator.base.VentilatorBaseActivity;
import com.robam.common.bean.AccountInfo;
import com.robam.common.bean.UserInfo;
import com.robam.ventilator.constant.DialogConstant;
import com.robam.ventilator.constant.VentilatorConstant;
import com.robam.ventilator.device.VentilatorFactory;
import com.robam.ventilator.factory.VentilatorDialogFactory;
import com.robam.ventilator.http.CloudHelper;
import com.robam.ventilator.response.GetTokenRes;
import com.robam.ventilator.response.GetUserInfoRes;
import com.robam.ventilator.response.GetVerifyCodeRes;

public class LoginPhoneActivity extends VentilatorBaseActivity {
    private MCountdownView mCountdownView;
    private EditText etPhone, etVerify;
    private static final String CODE_LOGIN = "mobileSmsCode";
    private IDialog waitDialog;
    private boolean first;

    @Override
    protected int getLayoutId() {
        return R.layout.ventilator_activity_layout_login_phone;
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
        setCenter(R.string.ventilator_login_phone);

        mCountdownView = findViewById(R.id.tv_getverifycode);
        etPhone = findViewById(R.id.et_phone);
        etVerify = findViewById(R.id.et_verifycode);
        setOnClickListener(R.id.tv_login_qrcode, R.id.tv_login_password, R.id.bt_login, R.id.tv_getverifycode, R.id.ll_right);
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
            Intent intent = new Intent(LoginPhoneActivity.this, LoginQrcodeActivity.class);
            intent.putExtra(VentilatorConstant.EXTRA_FIRST, first);
            startActivity(intent);
            finish();
        } else if (id == R.id.tv_login_password) {
            Intent intent = new Intent(LoginPhoneActivity.this, LoginPasswordActivity.class);
            intent.putExtra(VentilatorConstant.EXTRA_FIRST, first);
            startActivity(intent);
            finish();
        } else if (id == R.id.bt_login) {
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

            showWaitDialog();
            getToken(phone, verifyCode);
        } else if (id == R.id.tv_getverifycode) {
            String phone = etPhone.getText().toString();
            if (TextUtils.isEmpty(phone)) {
                ToastUtils.showShort(this, R.string.ventilator_input_phone_hint);
                return;
            }
            //获取验证码
            getVerifyCode(phone);
        } else if (id == R.id.ll_right) {
            //跳转到首页
            if (first)
                HomeActivity.start(this);
        }
    }

    //获取token
    private void getToken(String phone, String verifyCode) {
        CloudHelper.getToken(this, CODE_LOGIN, phone, verifyCode, "", "roki_client", "test", "RKDRD",
                GetTokenRes.class, new RetrofitCallback<GetTokenRes>() {
                    @Override
                    public void onSuccess(GetTokenRes getTokenRes) {
                        if (null != getTokenRes) {
                            getUserInfo(getTokenRes.getAccess_token());
                        } else {
                            cancelWaitDialog();
                            ToastUtils.showShort(LoginPhoneActivity.this, R.string.ventilator_request_failed);
                        }
                    }

                    @Override
                    public void onFaild(String err) {
                        cancelWaitDialog();
                        if (TextUtils.isEmpty(err))
                            ToastUtils.showShort(LoginPhoneActivity.this, R.string.ventilator_net_err);
                        else
                            ToastUtils.showShort(LoginPhoneActivity.this, err);
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
                    info.loginType = CODE_LOGIN;
                    String userJson = new Gson().toJson(info);
                    //保存用户信息及登录状态
                    MMKVUtils.login(true);
                    MMKVUtils.setUser(userJson);
                    //登录成功
                    AccountInfo.getInstance().getUser().setValue(info);
                    if (first)
                        //跳转到首页
                        HomeActivity.start(LoginPhoneActivity.this);
                    finish();
                    //绑定设备
//                    bindDevice(getUserInfoRes.getUser().id);
                } else {
                    ToastUtils.showShort(LoginPhoneActivity.this, R.string.ventilator_request_failed);
                }
            }

            @Override
            public void onFaild(String err) {
                cancelWaitDialog();
                ToastUtils.showShort(LoginPhoneActivity.this, R.string.ventilator_net_err);
                LogUtils.e("getUserInfo" + err);
            }
        });
    }

    //获取验证码
    private void getVerifyCode(String phone) {
        CloudHelper.getVerifyCode(this, phone, GetVerifyCodeRes.class, new RetrofitCallback<GetVerifyCodeRes>() {
            @Override
            public void onSuccess(GetVerifyCodeRes getVerifyCodeRes) {
                if (null != getVerifyCodeRes) {
                    LogUtils.e(getVerifyCodeRes.toString());
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
                } else {
                    ToastUtils.showShort(LoginPhoneActivity.this, R.string.ventilator_request_failed);
                }
            }

            @Override
            public void onFaild(String err) {
                ToastUtils.showShort(LoginPhoneActivity.this, R.string.ventilator_net_err);
                LogUtils.e("getVerifyCode" + err);
            }
        });
    }
    /**
     * 登陆后绑定设备
     * @param userId
     */
    private void bindDevice(long userId){
        CloudHelper.bindDevice(this, userId, Plat.getPlatform().getDeviceOnlySign(),
                Plat.getPlatform().getDt(), true, BaseResponse.class, new RetrofitCallback<BaseResponse>() {
                    @Override
                    public void onSuccess(BaseResponse baseResponse) {
                        if (null != baseResponse) {
                            //登录成功
                            finish();
                        }
                    }

                    @Override
                    public void onFaild(String err) {
                        LogUtils.e("bindDevice" + err);
                    }
                });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mCountdownView.stop();
    }
}
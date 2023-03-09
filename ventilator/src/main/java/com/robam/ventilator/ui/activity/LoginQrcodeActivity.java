package com.robam.ventilator.ui.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Handler;
import android.util.Base64;
import android.view.View;
import android.widget.ImageView;

import com.google.gson.Gson;
import com.robam.common.http.RetrofitCallback;
import com.robam.common.utils.LogUtils;
import com.robam.common.utils.MMKVUtils;
import com.robam.common.utils.QrUtils;
import com.robam.common.utils.ToastUtils;
import com.robam.ventilator.R;
import com.robam.ventilator.base.VentilatorBaseActivity;
import com.robam.common.bean.AccountInfo;
import com.robam.common.bean.UserInfo;
import com.robam.ventilator.constant.VentilatorConstant;
import com.robam.ventilator.http.CloudHelper;
import com.robam.ventilator.response.GetLoginStatusRes;
import com.robam.ventilator.response.GetUserInfoRes;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

public class LoginQrcodeActivity extends VentilatorBaseActivity {
    private ImageView ivQrcode;
    private static final String QRCODE_LOGIN = "qrcode";
    private boolean first;
    private Handler mHandler = new Handler();
    private Runnable runnable;

    @Override
    protected int getLayoutId() {
        return R.layout.ventilator_activity_layout_login_qrcode;
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

        setCenter(R.string.ventilator_login_qrcode);
        ivQrcode = findViewById(R.id.iv_qrcode);

        setOnClickListener(R.id.tv_login_phone, R.id.tv_login_password, R.id.ll_right);
    }

    @Override
    protected void initData() {
        runnable = new Runnable() {
            @Override
            public void run() {
                getQrCode();

                mHandler.postDelayed(runnable, 60000);
            }
        };
        mHandler.post(runnable);
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        int id = view.getId();
        if (id == R.id.tv_login_phone) {
            Intent intent = new Intent(LoginQrcodeActivity.this, LoginPhoneActivity.class);
            intent.putExtra(VentilatorConstant.EXTRA_FIRST, first);
            startActivity(intent);
            finish();
        } else if (id == R.id.tv_login_password) {
            Intent intent = new Intent(LoginQrcodeActivity.this, LoginPasswordActivity.class);
            intent.putExtra(VentilatorConstant.EXTRA_FIRST, first);
            startActivity(intent);
            finish();
        } else if (id == R.id.ll_right) {
            //跳转到首页
            if (first)
                HomeActivity.start(this);
        }
    }

    /**
     * 获取二维码接口
     */
    private void getQrCode() {
        String preUuid = UUID.randomUUID().toString();

        final Bitmap imgBit = QrUtils.create2DCode("UUID-LOGINtoken" + preUuid, (int)getResources().getDimension(com.robam.common.R.dimen.dp_194),
                (int)getResources().getDimension(com.robam.common.R.dimen.dp_194), Color.WHITE);
        if (ivQrcode != null) {
            ivQrcode.setImageBitmap(imgBit);
        }
        getLoginStatus2("token" + preUuid);
    }
    /**
     * 扫码登录
     */
    private void getLoginStatus2(String key) {
        long timeStamp = System.currentTimeMillis(); //时间戳
        // 表单请求参数
        Map<String, String> requestParameters = new HashMap();
        requestParameters.put("key", key);
        requestParameters.put("appType", "RKPAD");
        requestParameters.put("loginType", "9");

        CloudHelper.getCode(this, "roki_yyj_client", "y37vjf992ym0od06ud8nnhmq", timeStamp + "", timeStamp + "",
                signString(timeStamp, requestParameters), key, "RKPAD", "9", GetLoginStatusRes.class, new RetrofitCallback<GetLoginStatusRes>() {
            @Override
            public void onSuccess(GetLoginStatusRes getLoginStatusRes) {
                if (null != getLoginStatusRes) {
                    GetLoginStatusRes.Payload payLoad = getLoginStatusRes.payload;
                    if (null != payLoad) {
                        String access_token = payLoad.access_token;
                        toLoginCode(access_token);
                    }
                }
            }

            @Override
            public void onFaild(String err) {
                LogUtils.e("getLoginStatus" + err);
            }
        });
    }

    /**
     * 登录接口
     *
     */
    private void getLoginStatus(String key) {
        LogUtils.e("getLoginStatus");
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
    //签名逻辑
    private String signString(long timeStamp, Map<String, String> requestParameters) {
        try {
            String appId = "roki_yyj_client"; //由系统分配
            String appKey = "y37vjf992ym0od06ud8nnhmq"; //由系统分配

            String nonce = timeStamp + ""; // 随即数
            String appSecret = "1761771d5d80ceaf0d19df6b12ccc23cefff91e0"; // 有系统分配与appKey成对出现

            Map<String, String> treeMap = new TreeMap<>(requestParameters);
            treeMap.put("appId", appId);
            treeMap.put("appKey", appKey);
            treeMap.put("timestamp", timeStamp + "");
            treeMap.put("nonce", nonce);

            Iterator<Map.Entry<String, String>> iterator = treeMap.entrySet().iterator();
            StringBuilder stringToSign = new StringBuilder();
            while (iterator.hasNext()) {
                Map.Entry<String, String> next = iterator.next();
                stringToSign.append(next.getKey()).append(next.getValue());
            }
            stringToSign.append(appSecret);
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(appSecret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
            byte[] signData = mac.doFinal(stringToSign.toString().getBytes(StandardCharsets.UTF_8));
            // 最终签名结果，后续请求需放⼊请求头中(sign)
            String signD = URLEncoder.encode(Base64.encodeToString(signData, Base64.NO_WRAP), "UTF-8");
            return signD;
        } catch (Exception e) {}
        return "";
    }
    //获取用户信息
    private void toLoginCode(String access_token) {
        long timeStamp = System.currentTimeMillis(); //时间戳
        // 表单请求参数
        Map<String, String> requestParameters = new HashMap();
//        requestParameters.put("access_token", access_token);

        CloudHelper.loginCode(this, "roki_yyj_client", "y37vjf992ym0od06ud8nnhmq", timeStamp+"", timeStamp +"", signString(timeStamp, requestParameters),
                access_token, GetUserInfoRes.class, new RetrofitCallback<GetUserInfoRes>() {
            @Override
            public void onSuccess(GetUserInfoRes getUserInfoRes) {
                if (null != getUserInfoRes && null != getUserInfoRes.getUser()) {
                    UserInfo info = getUserInfoRes.getUser();
                    info.loginType = QRCODE_LOGIN;
//                    info.password = password;
                    String userJson = new Gson().toJson(info);
                    //保存用户信息及登录状态
                    MMKVUtils.login(true);
                    MMKVUtils.setUser(userJson);
                    //登录成功
                    AccountInfo.getInstance().getUser().setValue(info);
                    //跳转到首页
                    if (first)
                        HomeActivity.start(LoginQrcodeActivity.this);
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
                    //跳转到首页
                    if (first)
                        HomeActivity.start(LoginQrcodeActivity.this);
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
        mHandler.removeCallbacks(runnable);

        mHandler.removeCallbacksAndMessages(null);
    }
}
package com.robam.ventilator.ui.activity;

import android.net.ConnectivityManager;
import android.net.Network;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.robam.common.ui.dialog.IDialog;
import com.robam.common.ui.view.PasswordEditText;
import com.robam.common.utils.MMKVUtils;
import com.robam.common.utils.ToastUtils;
import com.robam.ventilator.R;
import com.robam.ventilator.base.VentilatorBaseActivity;
import com.robam.ventilator.constant.DialogConstant;
import com.robam.ventilator.constant.VentilatorConstant;
import com.robam.ventilator.factory.VentilatorDialogFactory;
import com.robam.ventilator.manager.VenWifiManager;

public class WifiConnectActivity extends VentilatorBaseActivity {
    private TextView tvName;
    private PasswordEditText etPassword;
    private IDialog waitingDialog;

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
            etPassword.setText(MMKVUtils.getWifiPwd(ssid));
            etPassword.requestFocus();
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
            connecting();
            final int sdkVersion = getApplicationInfo().targetSdkVersion;
            if (sdkVersion > Build.VERSION_CODES.P) {
                VenWifiManager.connectWifiPws(getApplicationContext()
                        , tvName.getText().toString()
                        , etPassword.getText().toString(), new ConnectivityManager.NetworkCallback() {
                            @Override
                            public void onAvailable(Network network) {
                                Log.i("onAvailable", "success");
                                if (null != waitingDialog)
                                    waitingDialog.dismiss();
                                //connect success
                                finish();
                            }

                            @Override
                            public void onUnavailable() {
                                Log.i("onUnavailable", "failed");
                                if (null != waitingDialog)
                                    waitingDialog.dismiss();
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        ToastUtils.showShort(WifiConnectActivity.this, R.string.ventilator_connect_failed);
                                    }
                                });
                            }
                        });
            } else {
                if (VenWifiManager.connectWifiPws(getApplicationContext()
                        , tvName.getText().toString()
                        , etPassword.getText().toString())) {
                    if (null != waitingDialog)
                        waitingDialog.dismiss();
                    //connect success
                    MMKVUtils.setWifi(tvName.getText().toString(), etPassword.getText().toString());
                    finish();
                } else {
                    if (null != waitingDialog)
                        waitingDialog.dismiss();
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ToastUtils.showShort(WifiConnectActivity.this, R.string.ventilator_connect_failed);
                        }
                    });
                }
            }

        }
    }

    //连接网络
    private void connecting() {
        if (null == waitingDialog) {
            waitingDialog = VentilatorDialogFactory.createDialogByType(getContext(), DialogConstant.DIALOG_TYPE_WAITING);
            waitingDialog.setCancelable(false);
        }
        waitingDialog.show();
    }
}
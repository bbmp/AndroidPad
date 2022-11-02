package com.robam.ventilator.ui.activity;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.Group;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.robam.common.ui.view.SwitchButton;
import com.robam.common.utils.PermissionUtils;
import com.robam.ventilator.R;
import com.robam.ventilator.base.VentilatorBaseActivity;
import com.robam.common.bean.AccountInfo;
import com.robam.ventilator.constant.VentilatorConstant;
import com.robam.ventilator.manager.VenWifiManager;
import com.robam.ventilator.ui.adapter.RvWifiAdapter;
import com.robam.ventilator.ui.receiver.VentilatorReceiver;

import java.util.List;

public class WifiSettingActivity extends VentilatorBaseActivity {
    private RecyclerView rvWifi;
    private WifiManager mWifiManager;
    private RvWifiAdapter rvWifiAdapter;
//    private VentilatorReceiver ventilatorReceiver;
    private Group group;
    private SwitchButton switchButton;

    @Override
    protected int getLayoutId() {
        return R.layout.ventilator_activity_layout_wifi_setting;
    }

    @Override
    protected void initView() {
        setCenter(R.string.ventilator_net_set);
        boolean first = false;
        group = findViewById(R.id.ventilator_group2);
        Bundle bundle = getIntent().getExtras();
        if (null != bundle) {
            first = bundle.getBoolean(VentilatorConstant.EXTRA_FIRST, false);
            if (first) {
                group.setVisibility(View.GONE);
                //首次进入
                setRight();
            }
        }
        if (!first)
            showLeft();

        rvWifi = findViewById(R.id.rv_wifi);
        switchButton = findViewById(R.id.sb_auto_set);
        rvWifi.setLayoutManager(new GridLayoutManager(this , 2 ));
        setOnClickListener(R.id.ll_right);
        switchButton.setOnCheckedChangeListener(new SwitchButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(SwitchButton button, boolean checked) {
                if (!checked)
                    VenWifiManager.closeWifi(mWifiManager);
                else
                    VenWifiManager.openWifi(mWifiManager);
                setWifiData();
            }
        });
    }

    @Override
    protected void initData() {
        rvWifiAdapter = new RvWifiAdapter();
        rvWifi.setAdapter(rvWifiAdapter);
        PermissionUtils.requestPermission(this, new PermissionUtils.OnPermissionListener() {
            @Override
            public void onSucceed() {
                onPermissionGranted();
            }

            @Override
            public void onFailed() {
                //权限未给
            }
        }, Manifest.permission.ACCESS_FINE_LOCATION , Manifest.permission.ACCESS_COARSE_LOCATION);

        rvWifiAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
                ScanResult result = (ScanResult) adapter.getItem(position);
                Intent intent = new Intent();
                intent.putExtra(VentilatorConstant.EXTRA_WIFI_SSID, result.SSID);
                intent.setClass(WifiSettingActivity.this, WifiConnectActivity.class);
                startActivity(intent);
            }
        });
    }

    //已授权
    private void onPermissionGranted() {
        mWifiManager = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
        //监听联网状态
        AccountInfo.getInstance().getConnect().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                //连接成功
                if (aBoolean) {
                    TextView textView = findViewById(R.id.tv_right);
                    textView.setText(R.string.ventilator_next_step);
                    //当前连接wifi
                    WifiInfo info = mWifiManager.getConnectionInfo();
                    rvWifiAdapter.setInfo(info);
                }
                //获取wifi
                setWifiData();
            }
        });
        //注册广播
//        registerWifiReceiver();

    }

    private void registerWifiReceiver() {
//        ventilatorReceiver = new VentilatorReceiver();
//        IntentFilter filter =new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
//        filter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);//监听wifi是开关变化的状态
//        filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);//监听wifi连接状态
//        filter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);//监听wifi列表变化（开启一个热点或者关闭一个热点）
//        registerReceiver(ventilatorReceiver, filter);
//        mWifiManager = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
    }

    private void setWifiData(){
        if (mWifiManager != null){
            List<ScanResult> wifiList = VenWifiManager.getWifiListBy(mWifiManager);

            rvWifiAdapter.setList(wifiList);
            //当前连接wifi
            WifiInfo info = mWifiManager.getConnectionInfo();
            rvWifiAdapter.setInfo(info);
        }
    }

    public void setRight() {
        findViewById(R.id.ll_right).setVisibility(View.VISIBLE);
        findViewById(R.id.iv_right1).setVisibility(View.GONE);
        findViewById(R.id.view_right1).setVisibility(View.GONE);
        findViewById(R.id.view_right2).setVisibility(View.VISIBLE);
        findViewById(R.id.iv_right2).setVisibility(View.VISIBLE);
        TextView textView = findViewById(R.id.tv_right);
        textView.setText(R.string.ventilator_skip);
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        int id = view.getId();
        if (id == R.id.ll_right) {
            //跳过
            if (!AccountInfo.getInstance().getConnect().getValue())
                HomeActivity.start(this);
            else {
                //login
                startActivity(LoginPhoneActivity.class);
                finish();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        if (null != ventilatorReceiver) {
//            unregisterReceiver(ventilatorReceiver);
//            ventilatorReceiver = null;
//        }
    }
}
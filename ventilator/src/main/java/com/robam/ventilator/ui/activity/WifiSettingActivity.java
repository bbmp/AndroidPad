package com.robam.ventilator.ui.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.robam.common.ui.activity.BaseActivity;
import com.robam.common.utils.PermissionUtils;
import com.robam.ventilator.R;
import com.robam.ventilator.base.VentilatorBaseActivity;
import com.robam.ventilator.manager.VenWifiManager;
import com.robam.ventilator.ui.adapter.RvWifiAdapter;
import com.robam.ventilator.ui.receiver.VentilatorReceiver;

import java.util.List;

public class WifiSettingActivity extends VentilatorBaseActivity {
    private RecyclerView rvWifi;
    private WifiManager mWifiManager;
    private RvWifiAdapter rvWifiAdapter;
    private VentilatorReceiver ventilatorReceiver;

    @Override
    protected int getLayoutId() {
        return R.layout.ventilator_activity_layout_wifi_setting;
    }

    @Override
    protected void initView() {
        showLeft();
        setCenter(R.string.ventilator_net_set);
        //首次进入
        setRight();
        setOnClickListener(R.id.ll_left);

        rvWifi = findViewById(R.id.rv_wifi);
        rvWifi.setLayoutManager(new GridLayoutManager(this , 2 ));
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

    }

    //已授权
    private void onPermissionGranted() {
        //注册广播
        registerWifiReceiver();
        //获取wifi
        setWifiData();
    }

    private void registerWifiReceiver() {
        ventilatorReceiver = new VentilatorReceiver();
        IntentFilter filter =new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
        filter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);//监听wifi是开关变化的状态
        filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);//监听wifi连接状态
        filter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);//监听wifi列表变化（开启一个热点或者关闭一个热点）
        registerReceiver(ventilatorReceiver, filter);
        mWifiManager = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
    }

    private void setWifiData(){
        if (mWifiManager != null){
            List<ScanResult> wifiList = VenWifiManager.getWifiListBy(mWifiManager);

            //当前连接wifi
            WifiInfo info = mWifiManager.getConnectionInfo();
            rvWifiAdapter.setInfo(info);
            rvWifiAdapter.setList(wifiList);
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
        int id = view.getId();
        if (id == R.id.ll_left)
            finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (null != ventilatorReceiver) {
            unregisterReceiver(ventilatorReceiver);
            ventilatorReceiver = null;
        }
    }
}
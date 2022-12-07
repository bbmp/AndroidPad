package com.robam.ventilator.ui.adapter;

import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.text.TextUtils;
import android.view.View;

import androidx.annotation.NonNull;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.robam.ventilator.R;

public class RvWifiAdapter extends BaseQuickAdapter<ScanResult, BaseViewHolder> {
    private WifiInfo info;
    private static final String WIFI_AUTH_OPEN = "";
    private static final String WIFI_AUTH_ROAM = "[ESS]";


    public void setInfo(WifiInfo info) {
        this.info = info;
        notifyDataSetChanged();
    }

    public RvWifiAdapter() {
        super(R.layout.ventilator_item_layout_wifi);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder baseViewHolder, ScanResult scanResult) {
        baseViewHolder.setText(R.id.tv_wifi_name, TextUtils.isEmpty(scanResult.SSID) ? scanResult.BSSID : scanResult.SSID);
        if (info != null ){
            if (scanResult.BSSID.equals(info.getBSSID())){
                baseViewHolder.setVisible(R.id.iv_select, true);
            }else {
                baseViewHolder.setVisible(R.id.iv_select, false);
            }
        }else {
            baseViewHolder.setVisible(R.id.iv_select, false);
        }
        if (scanResult.capabilities != null) {
            String capabilities = scanResult.capabilities.trim();
            if (capabilities != null && (capabilities.equals(WIFI_AUTH_OPEN) || capabilities.equals(WIFI_AUTH_ROAM))) {
                baseViewHolder.setVisible(R.id.iv_lock, false); //无密码
                return;
            }
        }
        baseViewHolder.setVisible(R.id.iv_lock, true);
    }
}

package com.robam.ventilator.ui.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

import com.robam.common.utils.LogUtils;

public class VentilatorReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (WifiManager.WIFI_STATE_CHANGED_ACTION.equals(intent.getAction())) {
            //wifi开关变化
            int state = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, 0);
            switch (state) {
                case WifiManager.WIFI_STATE_DISABLED: {
                    //wifi关闭
                    LogUtils.i("已经关闭");

                    break;
                }
                case WifiManager.WIFI_STATE_DISABLING: {
                    //wifi正在关闭
                    LogUtils.i("正在关闭");
                    //tv_wifiState.append("\n 打开变化：wifi正在关闭");
                    break;
                }
                case WifiManager.WIFI_STATE_ENABLED: {
                    //wifi已经打开
                    LogUtils.i("已经打开");
                    // tv_wifiState.append("\n 打开变化：wifi已经打开");
                    break;
                }
                case WifiManager.WIFI_STATE_ENABLING: {
                    //wifi正在打开
                    LogUtils.i("正在打开");
                    // tv_wifiState.append("\n 打开变化：wifi正在打开");
                    break;
                }
                case WifiManager.WIFI_STATE_UNKNOWN: {
                    //未知
                    LogUtils.i("未知状态");
                    // tv_wifiState.append("\n 打开变化：wifi未知状态");
                    break;
                }
            }
        }else if (WifiManager.NETWORK_STATE_CHANGED_ACTION.equals(intent.getAction())) {
            //监听wifi连接状态
            NetworkInfo info = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
            LogUtils.i( "--NetworkInfo--" + info.toString());
            if (NetworkInfo.State.DISCONNECTED == info.getState()) {
                LogUtils.i("wifi没连接上");
            } else if (NetworkInfo.State.CONNECTED == info.getState()) {//wifi连接上了
                WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
                WifiInfo wifiInfo = wifiManager.getConnectionInfo();
                //获取当前wifi名称
                LogUtils.i( "连接到网络 " + wifiInfo.getSSID());
                LogUtils.i("wifi已连接");
            } else if (NetworkInfo.State.CONNECTING == info.getState()) {//正在连接
                LogUtils.i("wifi正在连接");
            }
        } else if (WifiManager.SCAN_RESULTS_AVAILABLE_ACTION.equals(intent.getAction())) {
            LogUtils.i("wifi列表发生变化");
        }
    }
}
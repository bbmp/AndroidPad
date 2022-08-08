package com.robam.ventilator.manager;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkRequest;
import android.net.NetworkSpecifier;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiNetworkSpecifier;
import android.os.PatternMatcher;
import android.text.TextUtils;
import android.util.Log;


import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class VenWifiManager {
    /**
     * 开始扫描wifi
     */
    public static void startScanWifi(WifiManager manager) {
        if (manager != null) {
            manager.startScan();
        }
    }

    /**
     * 获取wifi列表
     */
    public static List<ScanResult> getWifiList(WifiManager mWifiManager) {
        if (mWifiManager != null) {
            return mWifiManager.getScanResults();
        }
        return null;
    }

    /**
     * 获取wifi列表 排除重复的 筛选信号最强的
     */
    public static List<ScanResult> getWifiListBy(WifiManager mWifiManager) {
        if (mWifiManager != null) {
            List<ScanResult> olist = mWifiManager.getScanResults();
            if (olist != null) {
                List<ScanResult> nlist = new ArrayList<>();
                WifiInfo info = mWifiManager.getConnectionInfo();
                for (int i = 0; i < olist.size(); i++) {
//                    if (info != null && info.getBSSID().equals(olist.get(i).BSSID)) {
//                        // 当前已连接设备不显示在列表中
//                        continue;
//                    }
                    // 该热点SSID是否已在列表中
                    int position = getItemPosition(nlist, olist.get(i));
                    if (position != -1) { // 已在列表
                        // 相同SSID热点，取信号强的
                        if (nlist.get(position).level < olist.get(i).level) {
                            nlist.remove(position);
                            nlist.add(position, olist.get(i));
                        }
                    } else {
                        nlist.add(olist.get(i));
                    }
                }
//                if (comparator != null) {
//                    // 按信号强度排序
//                    Collections.sort(nlist, comparator);
//                }
                return nlist;
            }
        }
        return null;
    }

    /**
     * 返回item在list中的坐标
     */
    private static int getItemPosition(List<ScanResult>list, ScanResult item) {
        for (int i = 0; i < list.size(); i++) {
            if (item.SSID.equals(list.get(i).SSID)) {
                return i;
            }
        }
        return -1;
    }

    /**
     * 保存网络
     */
    public static void saveNetworkByConfig(WifiManager manager, WifiConfiguration config) {
        if (manager == null) {
            return;
        }
        try {
            Method save = manager.getClass().getDeclaredMethod("save", WifiConfiguration.class, Class.forName("android.net.wifi.WifiManager$ActionListener"));
            if (save != null) {
                save.setAccessible(true);
                save.invoke(manager, config, null);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 断开连接
     */
    public static boolean disconnectNetwork(WifiManager manager) {
        return manager != null && manager.disconnect();
    }


    /**
     * 获取当前wifi名字
     *
     * @return
     */
    public static String getWiFiName(WifiManager manager) {
        WifiInfo wifiInfo = manager.getConnectionInfo();
        String name = wifiInfo.getSSID();
        return name.replace("\"", "");
    }

    /**
     * 获取当前WIFI信号强度
     *
     * @param manager
     * @return
     */
    public static int getWiFiLevel(WifiManager manager) {
        WifiInfo wifiInfo = manager.getConnectionInfo();
        return wifiInfo.getRssi();
    }

    /**
     * 获取wifi加密方式
     */
    public static String getEncrypt(WifiManager mWifiManager, ScanResult scanResult) {
        if (mWifiManager != null) {
            String capabilities = scanResult.capabilities;
            if (!TextUtils.isEmpty(capabilities)) {
                if (capabilities.contains("WPA") || capabilities.contains("wpa")) {
                    return "WPA";
                } else if (capabilities.contains("WEP") || capabilities.contains("wep")) {
                    return "WEP";
                } else {
                    return "没密码";
                }
            }
        }
        return "获取失败";
    }

    /**
     * 是否开启wifi，没有的话打开wifi
     */
    public static void openWifi(WifiManager mWifiManager) {
        if (!mWifiManager.isWifiEnabled()) {
            mWifiManager.setWifiEnabled(true);
        }
    }

    /**
     * 是否开启wifi，没有的话打开wifi
     */
    public static void closeWifi(WifiManager mWifiManager) {
        mWifiManager.setWifiEnabled(false);
    }

    /**
     * 有密码连接
     *
     * @param ssid
     * @param pws
     */
    public static void connectWifiPws(Context context, String ssid, String pws) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q)
        {
            NetworkSpecifier specifier =
                    new WifiNetworkSpecifier.Builder()
                            .setSsidPattern(new PatternMatcher(ssid, PatternMatcher.PATTERN_PREFIX))
                            .setWpa2Passphrase(pws)
                            .build();

            NetworkRequest request =
                    new NetworkRequest.Builder()
                            .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
                            .removeCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                            .setNetworkSpecifier(specifier)
                            .build();

            ConnectivityManager connectivityManager = (ConnectivityManager)
                    context.getSystemService(Context.CONNECTIVITY_SERVICE);

            ConnectivityManager.NetworkCallback networkCallback = new ConnectivityManager.NetworkCallback() {
                @Override
                public void onAvailable(Network network) {
                    Log.i("onAvailable", "success");
                }

                @Override
                public void onUnavailable() {
                    Log.i("onUnavailable", "failed");
                }
            };
            connectivityManager.requestNetwork(request, networkCallback);
            // Release the request when done.
            // connectivityManager.unregisterNetworkCallback(networkCallback);
        } else {
            WifiManager mWifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
            mWifiManager.disableNetwork(mWifiManager.getConnectionInfo().getNetworkId());
            int netId = mWifiManager.addNetwork(getWifiConfig(mWifiManager, ssid, pws, true));
            mWifiManager.enableNetwork(netId, true);
        }
    }

    /**
     * wifi设置
     *
     * @param ssid
     * @param pws
     * @param isHasPws
     */
    private static WifiConfiguration getWifiConfig(WifiManager mWifiManager, String ssid, String pws, boolean isHasPws) {
        WifiConfiguration config = new WifiConfiguration();
        config.allowedAuthAlgorithms.clear();
        config.allowedGroupCiphers.clear();
        config.allowedKeyManagement.clear();
        config.allowedPairwiseCiphers.clear();
        config.allowedProtocols.clear();
        config.SSID = "\"" + ssid + "\"";

        WifiConfiguration tempConfig = isExist(ssid, mWifiManager);
        if (tempConfig != null) {
            mWifiManager.removeNetwork(tempConfig.networkId);
        }
        if (isHasPws) {
            config.preSharedKey = "\"" + pws + "\"";
            config.hiddenSSID = true;
            config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
            config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
            config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
            config.status = WifiConfiguration.Status.ENABLED;
        } else {
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
        }
        return config;
    }

    /**
     * 得到配置好的网络连接
     *
     * @param ssid
     * @return
     */
    private static WifiConfiguration isExist(String ssid, WifiManager mWifiManager) {
        @SuppressLint("MissingPermission") List<WifiConfiguration> configs = mWifiManager.getConfiguredNetworks();
        for (WifiConfiguration config : configs) {
            if (config.SSID.equals("\"" + ssid + "\"")) {
                return config;
            }
        }
        return null;
    }
}

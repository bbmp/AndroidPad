package com.robam.common.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class NetworkUtils {

    public static final String NETWORK_TYPE_WIFI = "wifi";
    public static final String NETWORK_TYPE_3G = "eg";
    public static final String NETWORK_TYPE_2G = "2g";
    public static final String NETWORK_TYPE_WAP = "wap";
    public static final String NETWORK_TYPE_UNKNOWN = "unknown";
    public static final String NETWORK_TYPE_DISCONNECT = "disconnect";

    /**
     * 检查网络是否可用，包括任何可用连接
     *
     * @param cx
     * @return
     */
    public static boolean isConnect(Context cx) {

        // 获取手机所有连接管理对象（包括对wi-fi,net等连接的管理）
        ConnectivityManager cm = (ConnectivityManager) cx
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm == null) {
            return false;
        }

        // 获取网络连接管理的对象
        NetworkInfo info = cm.getActiveNetworkInfo();
        if (info != null && info.isConnected()) {
            // 判断当前网络是否已经连接
            return info.getState() == NetworkInfo.State.CONNECTED;
        }

        return false;
    }

    /**
     * 检测网络连接是否可用
     *
     * @param ctx
     * @return true 可用; false 不可用
     */
    public static boolean isAvailable(Context ctx) {
        ConnectivityManager cm = (ConnectivityManager) ctx
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm == null) {
            return false;
        }
        NetworkInfo[] netinfo = cm.getAllNetworkInfo();
        if (netinfo == null) {
            return false;
        }
        for (int i = 0; i < netinfo.length; i++) {
            if (netinfo[i].isAvailable()) {
                return true;
            }
        }
        return false;
    }

    /**
     * 获取本地默认ip (仅ipv4, 排除环回地址)
     *
     * @return
     */
    public static String getLocalIp() {
        try {
            String ipv4;
            List<NetworkInterface> list = Collections.list(NetworkInterface
                    .getNetworkInterfaces());
            for (NetworkInterface ni : list) {

                if (ni.isLoopback())
                    continue;

                if (!ni.isUp())
                    continue;

                List<InetAddress> addressList = Collections.list(ni
                        .getInetAddresses());
                for (InetAddress address : addressList) {

                    if (address.isLoopbackAddress())
                        continue;
                    if (address.isMulticastAddress())
                        continue;

                    ipv4 = address.getHostAddress();
                    if (address instanceof Inet4Address) {
                        return ipv4;
                    }
                }
            }
        } catch (Exception ex) {
        }
        return null;
    }

    /**
     * 获取默认mac地址
     *
     * @return
     */
    public static String getLocalMac() {
        String ip = getLocalIp();
        String mac = getMacByIp(ip);
        return mac;
    }

    /**
     * 获取默认ip对应的mac地址
     *
     * @param ip
     * @return
     */
    public static String getMacByIp(String ip) {
        if (TextUtils.isEmpty(ip))
            return null;

        try {
            NetworkInterface ne = NetworkInterface.getByInetAddress(InetAddress
                    .getByName(ip));
            byte[] bytes = ne.getHardwareAddress();
            String mac = byte2hex(bytes);
            return mac;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * 获取默认子网广播地址
     *
     * @return
     */
    public static String getBroadcastAddress() {

        String broadcastAddr = "255.255.255.255";
        try {
            String localIp = getLocalIp();
            NetworkInterface ni = NetworkInterface.getByInetAddress(InetAddress
                    .getByName(localIp));

            if (!ni.isLoopback() && ni.isUp()) {
                List<InterfaceAddress> addressList = ni.getInterfaceAddresses();

                for (InterfaceAddress ia : addressList) {
                    if (ia.getBroadcast() != null) {
                        broadcastAddr = ia.getBroadcast().getHostAddress();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return broadcastAddr;
    }

    static public String getGatewayIpAddress(Context cx) {

        WifiManager wm = (WifiManager) cx
                .getSystemService(Context.WIFI_SERVICE);
        int gatwayVal = wm.getDhcpInfo().gateway;
        return int2IP(gatwayVal);
    }

    public static List<NetworkInterface> getNetworkInterfaces() {
        List<NetworkInterface> list = null;
        try {
            list = Collections.list(NetworkInterface.getNetworkInterfaces());
        } catch (SocketException e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * Get network type
     *
     * @param context
     * @return
     */
    public static String getNetWorkType(Context context) {
        ConnectivityManager manager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo;
        String type = NETWORK_TYPE_DISCONNECT;
        if (manager == null
                || (networkInfo = manager.getActiveNetworkInfo()) == null) {
            return type;
        }

        if (networkInfo.isConnected()) {
            String typeName = networkInfo.getTypeName();
            if ("WIFI".equalsIgnoreCase(typeName)) {
                type = NETWORK_TYPE_WIFI;
            } else if ("MOBILE".equalsIgnoreCase(typeName)) {
                String proxyHost = android.net.Proxy.getDefaultHost();
                type = TextUtils.isEmpty(proxyHost) ? (isFastMobileNetwork(context) ? NETWORK_TYPE_3G
                        : NETWORK_TYPE_2G)
                        : NETWORK_TYPE_WAP;
            } else {
                type = NETWORK_TYPE_UNKNOWN;
            }
        }
        return type;
    }

    /**
     * Whether is fast mobile network
     *
     * @param context
     * @return
     */
    private static boolean isFastMobileNetwork(Context context) {
        TelephonyManager telephonyManager = (TelephonyManager) context
                .getSystemService(Context.TELEPHONY_SERVICE);
        if (telephonyManager == null) {
            return false;
        }

        switch (telephonyManager.getNetworkType()) {
            case TelephonyManager.NETWORK_TYPE_1xRTT:
                return false;
            case TelephonyManager.NETWORK_TYPE_CDMA:
                return false;
            case TelephonyManager.NETWORK_TYPE_EDGE:
                return false;
            case TelephonyManager.NETWORK_TYPE_EVDO_0:
                return true;
            case TelephonyManager.NETWORK_TYPE_EVDO_A:
                return true;
            case TelephonyManager.NETWORK_TYPE_GPRS:
                return false;
            case TelephonyManager.NETWORK_TYPE_HSDPA:
                return true;
            case TelephonyManager.NETWORK_TYPE_HSPA:
                return true;
            case TelephonyManager.NETWORK_TYPE_HSUPA:
                return true;
            case TelephonyManager.NETWORK_TYPE_UMTS:
                return true;
            case TelephonyManager.NETWORK_TYPE_EHRPD:
                return true;
            case TelephonyManager.NETWORK_TYPE_EVDO_B:
                return true;
            case TelephonyManager.NETWORK_TYPE_HSPAP:
                return true;
            case TelephonyManager.NETWORK_TYPE_IDEN:
                return false;
            case TelephonyManager.NETWORK_TYPE_LTE:
                return true;
            case TelephonyManager.NETWORK_TYPE_UNKNOWN:
                return false;
            default:
                return false;
        }
    }

    static private String int2IP(int i) {

        String ip = String
                .format(Locale.getDefault(), "%d.%d.%d.%d", (i & 0xFF),
                        (i >> 8 & 0xFF), (i >> 16 & 0xFF), (i >> 24 & 0xFF));
        return ip;

    }

    private static String byte2hex(byte[] b) {
        StringBuffer hs = new StringBuffer(b.length);
        String stmp = "";
        int len = b.length;
        for (int n = 0; n < len; n++) {
            stmp = Integer.toHexString(b[n] & 0xFF);
            if (stmp.length() == 1)
                hs = hs.append("0").append(stmp);
            else {
                hs = hs.append(stmp);
            }
        }
        return String.valueOf(hs);
    }
}

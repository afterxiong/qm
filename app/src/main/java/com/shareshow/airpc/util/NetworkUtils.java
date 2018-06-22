package com.shareshow.airpc.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;

import com.shareshow.App;

import java.io.BufferedReader;
import java.io.FileReader;
import java.lang.reflect.Method;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * @author xiongchengguang
 * @ClassName NetorkUtils
 * @Descripton 网络工具类
 * @date 2016年5月16日上午11:21:08
 */
public class NetworkUtils {

    // WIFI
    public static final String wlan0 = "wlan0";

    public static final int TYPE_AP = 0;
    public static final int TYPE_WIFI = 1;
    public static final int TYPE_NULL = -1;
    public static final int TYPE_ETHERNET = 9;

    public static final String NETWORK_TYPE = "network_type";
    public static final String NETWORK_IP = "network_ip";
    public static final String NETWORK_MAC = "network_mac";
    public static final String NETWORK_NAME = "network_name";
    public static final String TAG = "NetorkUtils";

    private static ConnectivityManager getConnectivityManager(){
        return (ConnectivityManager) App.getApp().getSystemService(Context.CONNECTIVITY_SERVICE);
    }

    private static WifiManager getWifiManager(){
        return (WifiManager) App.getApp().getApplicationContext().getSystemService(Context.WIFI_SERVICE);
    }

    public static int getNetworkType() {
        if (isWifiApEnabled()) {
            return TYPE_AP;

        } else {
            NetworkInfo info = getConnectivityManager().getActiveNetworkInfo();
            if (info != null && info.isConnected()) {
                if (info.getType() == ConnectivityManager.TYPE_ETHERNET) {
                    return TYPE_ETHERNET;
                } else if (info.getType() == ConnectivityManager.TYPE_WIFI) {
                    return TYPE_WIFI;
                } else {
                    return TYPE_NULL;
                }
            } else {
                return TYPE_NULL;
            }
        }
    }

    public static String getNetworkName() {
        String ssid = "net unknown";
        int type = getNetworkType();
        switch (type) {
            case TYPE_AP:
                ssid = getNetworkSSID();
                break;
            case TYPE_WIFI:
                ssid = getWifiManager().getConnectionInfo().getSSID();
                break;
            case TYPE_ETHERNET:
                ssid = "有线网络";
                break;
            case TYPE_NULL:
                ssid = "未知网络";
                break;

            default:
                break;
        }

        return ssid;
    }


    //内存溢出了
    private static String getIP(String type) throws SocketException {
        Enumeration<NetworkInterface> enumeration = NetworkInterface.getNetworkInterfaces();
        while (enumeration.hasMoreElements()) {
            NetworkInterface networkInterface = enumeration.nextElement();
            String name = networkInterface.getDisplayName();
            if (name.equals(type)) {
                Enumeration<InetAddress> addressEnumeration = networkInterface.getInetAddresses();
                while (addressEnumeration.hasMoreElements()) {
                    InetAddress address = addressEnumeration.nextElement();
                    if (!address.isLoopbackAddress() && address instanceof Inet4Address) {
                        String ip = address.getHostAddress();
                        return ip;
                    }
                }
            }
        }
        return null;
    }

    public static String getNetworkIP() {
        String address = "";
        int type = getNetworkType();
        switch (type) {
            case TYPE_AP:
                address = "192.168.43.1";
                break;
            case TYPE_WIFI:
            /*
			 * int ip = wm.getConnectionInfo().getIpAddress(); address = ((ip &
			 * 0xff) + "." + (ip >> 8 & 0xff) + "." + (ip >> 16 & 0xff) + "." +
			 * (ip >> 24 & 0xff));
			 */
                try {
                    address = getIP(NetworkUtils.wlan0);
                } catch (SocketException e) {
                    e.printStackTrace();
                }
                break;
            case TYPE_ETHERNET:
                address = localAddress();
                break;
            case TYPE_NULL:
                break;

            default:
                break;
        }
        return address;
    }

    public static String getNetworkMac() {
        String mac = "";
        int type = getNetworkType();
        switch (type) {
            case TYPE_AP:
                mac = getWifiManager().getConnectionInfo().getMacAddress();
                if (mac == null) {
                    mac = UUID.randomUUID().toString();
                }
                break;
            case TYPE_WIFI:
                mac = getWifiManager().getConnectionInfo().getMacAddress();
                if (mac == null) {
                    mac = UUID.randomUUID().toString();
                }
                break;
            case TYPE_ETHERNET:
                mac = localMac();
                break;
            case TYPE_NULL:
                break;

            default:
                break;
        }
        return mac.toUpperCase();
    }

    private static String getNetworkSSID() {
        try {
            Method method = getWifiManager().getClass().getMethod("getWifiApConfiguration");
            method.setAccessible(true);
            WifiConfiguration configuration = (WifiConfiguration) method.invoke(getWifiManager());
            return configuration.SSID;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "net unknown";
    }

    public static boolean isWifiApEnabled() {
        try {
            Method method = getWifiManager().getClass().getMethod("isWifiApEnabled");
            method.setAccessible(true);
            return (Boolean) method.invoke(getWifiManager());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static String loadReadFile(String path) {
        StringBuffer sb = new StringBuffer();
        try {
            BufferedReader reader = new BufferedReader(new FileReader(path));
            char[] buf = new char[8192];
            int len = 0;
            while ((len = reader.read(buf)) != -1) {
                String str = String.valueOf(buf, 0, len);
                sb.append(str);
            }
            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sb.toString();
    }

    public static String localAddress() {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    Pattern p = Pattern.compile("^[1-9][0-9]*.[0-9][0-9]*.[0-9][0-9]*.[0-9][0-9]*$");
                    Matcher m = p.matcher(inetAddress.getHostAddress().toString());
                    if (!inetAddress.isLoopbackAddress() && m.matches()) {
                        return inetAddress.getHostAddress().toString();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String localMac() {
        try {
            return loadReadFile("/sys/class/net/eth0/address").toUpperCase().substring(0, 17);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public Map getAllNetworkInfo() {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put(NETWORK_TYPE, getNetworkType());
        map.put(NETWORK_IP, getNetworkIP());
        map.put(NETWORK_MAC, getNetworkMac());
        map.put(NETWORK_NAME, getNetworkName());
        return map;
    }

    public void startAP() {
        if (getWifiManager().isWifiEnabled()) {
            startAP(getNetworkSSID(), "12345678");
        }
    }

    private void startAP(String ssid, String password) {
        Method method1 = null;
        try {
            method1 = getWifiManager().getClass().getMethod("setWifiApEnabled", WifiConfiguration.class, boolean.class);
            WifiConfiguration netConfig = new WifiConfiguration();

            netConfig.SSID = ssid;
            netConfig.preSharedKey = password;

            netConfig.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
            netConfig.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
            netConfig.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
            netConfig.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
            netConfig.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
            netConfig.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
            netConfig.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
            netConfig.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
            method1.invoke(getWifiManager(), netConfig, true);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean isNetworkConnected(){
        ConnectivityManager manager = (ConnectivityManager) App.getApp().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        if (networkInfo == null) {
            return false;
        } else {
            return networkInfo.isConnected();
        }
    }

    private static ScanResult getLocalScan() {
        if (getWifiManager() != null) {
            List<ScanResult> scans = getWifiManager().getScanResults();
            String ssid = getNetworkName();
            if (ssid != null && ssid.contains("\"")) {
                ssid = ssid.substring(1, ssid.length() - 1);
            }
            for (ScanResult scan : scans) {
                if (scan.SSID.equals(ssid)) {
                    return scan;
                }
            }
        }
        return null;
    }


    /**
     * @return 判断是不是5G网络
     */
    public static boolean is5GLocalNet() {
        if (getNetworkType() == TYPE_WIFI) {
            ScanResult scan = getLocalScan();
            if (scan != null) {
                return is5GHz(scan.frequency);
            }
        }
        return false;
    }


    private static boolean is5GHz(int freq) {
        if (freq > 4900 && freq < 5900) {
            return true;
        }

        return false;
    }


    public static boolean isWifi() {
        ConnectivityManager connectivityManager = (ConnectivityManager) App.getApp().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();
        if (activeNetInfo != null && activeNetInfo.getType() == ConnectivityManager.TYPE_WIFI) {
            return true;
        }
        return false;
    }

}

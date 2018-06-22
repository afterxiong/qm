package com.shareshow.aide.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.shareshow.App;
import com.socks.library.KLog;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;

/**
 * Created by xiongchengguang on 2017/12/5.
 */

public class SampleNetworkInterface {

    public static String getHostAddress() {
        try {
            for (Enumeration<NetworkInterface> networkInterface = NetworkInterface.getNetworkInterfaces(); networkInterface.hasMoreElements(); ) {
                NetworkInterface network = networkInterface.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = network.getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (inetAddress instanceof Inet4Address && !inetAddress.isLoopbackAddress()) {
                        return inetAddress.getHostAddress();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "255.255.255.255";
    }

    public static String getMacAddress() {
        try {
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            while (interfaces.hasMoreElements()) {
                NetworkInterface network = interfaces.nextElement();
                byte[] buffer = network.getHardwareAddress();
                if (buffer == null || buffer.length == 0) {
                    continue;
                }
                if (!network.getName().equals("wlan0")) {
                    continue;
                }
                StringBuffer sb = new StringBuffer();
                for (byte b : buffer) {
                    sb.append(String.format("%02x", b));
                }
                if (sb.length() > 0) {
                    sb.deleteCharAt(sb.length() - 1);
                }
                String mac = sb.toString();
                KLog.d(mac);
                return mac;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "0000000000";
    }

    public static boolean isNetworkAvailable() {
        ConnectivityManager connectivity = (ConnectivityManager) App.getApp().getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo info = connectivity.getActiveNetworkInfo();
            if (info != null && info.isConnected()) {
                if (info.getState() == NetworkInfo.State.CONNECTED) {
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean isNetworkConnect() {
        ConnectivityManager manager = (ConnectivityManager) App.getApp().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        if (networkInfo != null) {
            return networkInfo.isAvailable();
        }
        return false;
    }
}

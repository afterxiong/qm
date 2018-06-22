package com.xcg.zxing.core;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;

import com.socks.library.KLog;

/**
 * Created by xiongchengguang on 2017/4/14.
 */

public class NetworkChangeReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        String action = intent.getAction();
        if (listener == null) {
            return;
        }
        if (action.equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
            if (networkInfo == null) {
                if (wifiManager.isWifiEnabled()) {
                    listener.gone();
                    listener.loading();
                }
                return;
            }
            if (networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                if (networkInfo.isConnected()) {
                    KLog.d("连接上了");
                    listener.visiable();
                } else {
                    KLog.d("断开了");
                    listener.gone();
                }
            }
        }

        if (action.endsWith(WifiManager.WIFI_STATE_CHANGED_ACTION)) {
            int wifiState = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, WifiManager.WIFI_STATE_UNKNOWN);
            if (wifiState == WifiManager.WIFI_STATE_ENABLING) {
                listener.loading();
            } else if (wifiState == WifiManager.WIFI_STATE_DISABLING) {
                listener.gone();
            }
        }

        if (action.equals("android.net.wifi.WIFI_AP_STATE_CHANGED")) {
            int apState = intent.getIntExtra("wifi_state", 14);
            if (apState == 13) {
                KLog.d("热点开启");
                listener.visiable();
            } else if (apState == 11) {
                if (!wifiManager.isWifiEnabled()) {
                    KLog.d("热点关闭");
                    listener.gone();
                }
            } else if (apState == 12) {
                listener.loading();
                KLog.d("热点开启中");
            }
        }
        if (action.equals(WifiManager.NETWORK_STATE_CHANGED_ACTION)) {
            NetworkInfo info = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
            if (info.getType() == ConnectivityManager.TYPE_WIFI) {
                if (info.getState().equals(NetworkInfo.State.CONNECTING)) {
                    KLog.d("连接中");
                    listener.loading();
                }
            }
        }
    }

    private OnReceivertListener listener;

    public void setOnReceivertListener(OnReceivertListener listener) {
        this.listener = listener;
    }
}

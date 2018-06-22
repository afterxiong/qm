package com.shareshow.airpc.activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.shareshow.airpc.ftpserver.FTPServerService;
import com.shareshow.airpc.util.DebugLog;
import com.shareshow.airpc.util.NetworkUtils;


/**
 * Created by TEST on 2017/10/19.
 */

public class BaseShareActivity extends AppCompatActivity {

    private NetReceiver netReceiver;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initRegisterNet();
    }


    private void initRegisterNet(){
        netReceiver = new NetReceiver();
        IntentFilter intentFilter = new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE");
        intentFilter.addAction("android.net.wifi.WIFI_AP_STATE_CHANGED");
        registerReceiver(netReceiver, intentFilter);
    }


    private class NetReceiver extends BroadcastReceiver {
        public void onReceive(Context context, Intent intent){
            String action = intent.getAction();
            if (action!=null&&(ConnectivityManager.CONNECTIVITY_ACTION.equals(action)||action.equals("android.net.wifi.WIFI_AP_STATE_CHANGED"))){
                boolean isConnected = NetworkUtils.isNetworkConnected();
                DebugLog.showLog(this,"isConnected:"+isConnected);
                if (isConnected){
                    startFTPService();
                }else{
                    stopFTPService();
                }
            }
        }
    }

    public void unRegisterReceiver(){
        if(netReceiver!=null){
            unregisterReceiver(netReceiver);
        }
    }

    //关闭FTP服务；
    public void stopFTPService(){
        Intent intent2 = new Intent(getApplicationContext(), FTPServerService.class);
        if (FTPServerService.isRunning()){
            this.stopService(intent2);
        }
    }


    //开启FTP服务
    public void startFTPService(){
        Intent intent2 = new Intent(getApplicationContext(), FTPServerService.class);
        if (!FTPServerService.isRunning()){
            this.startService(intent2);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopFTPService();
        unRegisterReceiver();
    }
}

package com.shareshow.aide.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;

import com.shareshow.App;
import com.shareshow.aide.event.NetworkEvent;
import com.shareshow.airpc.ftpserver.FTPServerService;
import com.shareshow.airpc.util.NetworkUtils;
import com.socks.library.KLog;

import org.greenrobot.eventbus.EventBus;

/**
 * 网络变化监听广播
 * Created by xiongchengguang on 2018/1/22.
 */

public class NetworkBroadcast extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        KLog.d(intent.getAction());
        if (ConnectivityManager.CONNECTIVITY_ACTION.equals(intent.getAction())) {
            EventBus.getDefault().post(new NetworkEvent());
        }
    }




}

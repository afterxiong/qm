package com.shareshow.airpc.socket.command;

import com.shareshow.airpc.model.RootPoint;
import com.shareshow.airpc.util.DebugLog;

/**
 * Created by Administrator on 2018/3/16 0016.
 */

public interface HeartListener {

    public void onReceiveHeart(RootPoint rootPoint);

}

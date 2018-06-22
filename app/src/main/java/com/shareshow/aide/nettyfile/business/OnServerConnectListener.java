package com.shareshow.aide.nettyfile.business;

import io.netty.channel.ChannelFuture;

/**
 * Created by user on 2016/10/26.
 */

public interface OnServerConnectListener {
    void onConnectSuccess(ChannelFuture future);
    void onConnectFailed();
}

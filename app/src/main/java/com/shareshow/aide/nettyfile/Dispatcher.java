package com.shareshow.aide.nettyfile;

import android.support.v4.util.ArrayMap;

import com.shareshow.aide.nettyfile.business.OnReceiveListener;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * Created by user on 2016/10/27.
 */

public class Dispatcher extends SimpleChannelInboundHandler<ClientMessage.ProtoClientMessage> {
    private ArrayMap<Integer, OnReceiveListener> receiveListenerHolder;

    public Dispatcher() {
        receiveListenerHolder = new ArrayMap<>();
    }

    public void holdListener(ClientMessage.ProtoClientMessage test, OnReceiveListener onReceiveListener) {
        receiveListenerHolder.put(test.getId(), onReceiveListener);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, ClientMessage.ProtoClientMessage protoClientMessage) throws Exception {
        if (receiveListenerHolder.containsKey(protoClientMessage.getId())) {
            OnReceiveListener listener = receiveListenerHolder.remove(protoClientMessage.getId());
            if (listener != null) {
                listener.handleReceive(protoClientMessage);
            }
        }
    }
}

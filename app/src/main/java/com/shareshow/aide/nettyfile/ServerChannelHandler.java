package com.shareshow.aide.nettyfile;

import android.util.Log;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * Created by user on 2016/10/27.
 */

public class ServerChannelHandler extends SimpleChannelInboundHandler<ClientMessage.ProtoClientMessage> {
    private static final String TAG = "ServerChannelHandler";
    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, ClientMessage.ProtoClientMessage protoClientMessage) throws Exception {
        Log.d(TAG, "channelRead0: " + channelHandlerContext.name());
        ClientMessage.ProtoClientMessage res = ClientMessage.ProtoClientMessage.newBuilder()
                .setId(protoClientMessage.getId())
                .setTitle("res" + protoClientMessage.getTitle())
                .setContent("res" + protoClientMessage.getContent())
                .build();
        channelHandlerContext.writeAndFlush(res);
    }
}

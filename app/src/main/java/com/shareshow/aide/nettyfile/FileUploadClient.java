package com.shareshow.aide.nettyfile;

import com.shareshow.airpc.util.DebugLog;
import com.shareshow.db.FileChannelBean;

import java.util.concurrent.TimeUnit;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import io.netty.handler.timeout.IdleStateHandler;

public class FileUploadClient{

	public static final int FILE_PORT = 9991;

	public void connectFile(int port, String host,
			final FileChannelBean.ContentBean bean) throws Exception{
		EventLoopGroup group = new NioEventLoopGroup();
		long time = System.currentTimeMillis();
		try{
			Bootstrap b = new Bootstrap();
			b.group(group).channel(NioSocketChannel.class)
					.option(ChannelOption.TCP_NODELAY, true)
					.handler(new ChannelInitializer<Channel>(){
						@Override
						protected void initChannel(Channel ch) throws Exception{
							ch.pipeline().addLast(new ObjectEncoder());
							ch.pipeline().addLast(new ObjectDecoder(ClassResolvers.weakCachingConcurrentResolver(null)));
							ch.pipeline().addLast( new IdleStateHandler(25, 15, 10, TimeUnit.SECONDS));
							ch.pipeline().addLast(new FileUploadClientHandler(bean));
						}

					});

			ChannelFuture f = b.connect(host, port).sync();
		    f.channel().closeFuture().sync();
			DebugLog.showLog(this,"发送成功 所用时间："+(System.currentTimeMillis()-time)/(1000)+"秒");
		}finally{
			group.shutdownGracefully();
		}
	}


	public void connectMessage(int port, String host, final TcpMessageListener listener) throws Exception{
		EventLoopGroup group = new NioEventLoopGroup();
		long time = System.currentTimeMillis();
		try{
			Bootstrap b = new Bootstrap();
			b.group(group).channel(NioSocketChannel.class)
					.option(ChannelOption.TCP_NODELAY, true)
					.handler(new ChannelInitializer<Channel>(){
						@Override
						protected void initChannel(Channel ch) throws Exception{
							ch.pipeline().addLast(new ObjectEncoder());
							ch.pipeline().addLast(new ObjectDecoder(ClassResolvers.weakCachingConcurrentResolver(null)));
							ch.pipeline().addLast( new IdleStateHandler(25, 15, 10, TimeUnit.SECONDS));
							ch.pipeline().addLast(new UploadMessageClientHandler(listener));
						}
					});

			ChannelFuture f = b.connect(host, port).sync();
			f.channel().closeFuture().sync();
			DebugLog.showLog(this,"发送成功 所用时间："+(System.currentTimeMillis()-time)/(1000)+"秒");
		}finally{
			group.shutdownGracefully();
		}
	}
}

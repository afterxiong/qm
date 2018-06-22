package com.shareshow.aide.nettyfile;

import android.util.Log;

import com.google.gson.Gson;
import com.shareshow.airpc.util.DebugLog;
import com.shareshow.db.FileChannelBean;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;


public class FileUploadClientHandler extends ChannelInboundHandlerAdapter {
	private FileChannelBean.ContentBean mContentBean;
	private int byteRead;
	private volatile long start =0;
	private volatile int lastLength = 0;
	public  RandomAccessFile randomAccessFile;
	private File fileUploadFile;
	private FileUploadFile readFile;


	public FileUploadClientHandler(FileChannelBean.ContentBean bean){
		File file = new File(bean.getPath());
		if (file.exists()){
			if (!file.isFile()){
				DebugLog.showLog(this,"文件不存在...");
				return;
			}
		}
		this.mContentBean = bean;
		this.fileUploadFile=file;
	}

	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception{
		// TODO Auto-generated method stub
		super.channelInactive(ctx);
	}

	public void channelActive(ChannelHandlerContext ctx){
            Gson gson = new Gson();
		    String json =gson.toJson(mContentBean);
			ctx.writeAndFlush("FILE&"+json);
			lastLength = 1024 * 64;
	}


	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg)
			throws Exception{
		   Log.i("test","收到回传");
	   if(msg instanceof byte[]){
			Log.i("test","msg :"+((byte[]) msg).length);
			ctx.writeAndFlush("123321");
		}else if(msg instanceof String){
			Log.i("test","收到msg:"+ (String)msg);
		}else if(msg instanceof  Long){
			   start =  (long)msg;
			   Log.i("test","msg:"+start);
			   if (start!= 0){
				   randomAccessFile = new RandomAccessFile(
						   fileUploadFile, "r");
				   Log.i("test","正在上传："+fileUploadFile.getName()+"...");
				   randomAccessFile.seek(start);
				   int a = (int) (randomAccessFile.length() - start);
				   int b = (int) (randomAccessFile.length() / 1024*2);
				   if (a < lastLength&&a>0){
					   lastLength = a;
				   }
				   byte[] bytes = new byte[lastLength];
				   if ((byteRead = randomAccessFile.read(bytes)) != -1
						   && (randomAccessFile.length() - start)> 0){
					   try {
						   ctx.writeAndFlush(bytes);
					   } catch (Exception e){
						   e.printStackTrace();
						   Log.i("test","Exception:"+e.getMessage());
					   }

				   }else{
					   randomAccessFile.close();
					   ctx.close();
				   }
			   }else{
				   try {
					   randomAccessFile = new RandomAccessFile(fileUploadFile,
							   "r");
					   randomAccessFile.seek(0);
					   if(fileUploadFile.length()<lastLength){
						   lastLength= (int) fileUploadFile.length();
					   }
					   byte[] bytes = new byte[lastLength];
					   if ((byteRead = randomAccessFile.read(bytes)) != -1){
						   ctx.writeAndFlush(bytes);
					   }else{

					   }
					   lastLength = 1024 * 64;
				   } catch (FileNotFoundException e){
					   e.printStackTrace();
				   } catch (IOException i){
					   i.printStackTrace();
				   }
			   }
		   }
	}


	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause){
		cause.printStackTrace();
		ctx.close();
		Log.i("test","exceptionCaught");
	}

	@Override
	public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception{
		super.userEventTriggered(ctx, evt);
		Log.i("test","超时了");
		if (evt instanceof IdleStateEvent){  // 2
			IdleStateEvent event = (IdleStateEvent) evt;
			String type = "";
			if (event.state() == IdleState.READER_IDLE) {
				type = "read idle";
			} else if (event.state() == IdleState.WRITER_IDLE) {
				type = "write idle";
			} else if (event.state() == IdleState.ALL_IDLE) {
				type = "all idle";
			}

			 System.out.println( ctx.channel().remoteAddress()+"超时类型：" + type);
		} else {
			  super.userEventTriggered(ctx, evt);
		}
		ctx.close();
	}

}

package com.shareshow.airpc.socket.common;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Message;

import com.shareshow.aide.R;
import com.shareshow.airpc.ftpclient.FTPClient;
import com.shareshow.airpc.ftpclient.FTPException;
import com.shareshow.airpc.ftpclient.FTPFile;
import com.shareshow.airpc.ftpclient.FTPIllegalReplyException;
import com.shareshow.airpc.model.FTPConfig;
import com.shareshow.airpc.model.QMLocalFile;
import com.shareshow.airpc.model.RootPoint;
import com.shareshow.airpc.ports.ConnectFTPListener;
import com.shareshow.airpc.util.DebugLog;
import com.shareshow.airpc.util.QMUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class FTPUtil {

	private static final int OK=1000;//登陆成功
	
	private static final int ERROR=1001;//登陆失败
	
	private FTPClient mFTPClient;
	
	private ExecutorService mThreadPool;
	
	private static FTPUtil ftputil=null;
	
	private static FTPConfig mFTPConfig;
	
	private ArrayList<FTPFile> remote_file = new ArrayList<FTPFile>();
	
	public ArrayList<FTPFile> getRemote_file() {
		return remote_file;
	}

	public static RootPoint beforPoint;

	private FTPUtil(){
		mFTPClient=new FTPClient();
		mFTPClient.getConnector().setCloseTimeout(5);
		mFTPClient.getConnector().setConnectionTimeout(5);
		mThreadPool = Executors.newFixedThreadPool(5);
	}
	
	public static FTPUtil getInstance(RootPoint rootPoint){
		if(ftputil==null)
		ftputil=new FTPUtil();
		mFTPConfig=new FTPConfig();
		mFTPConfig.setIp(rootPoint.getAddress());
		if(rootPoint.getdType()==2&&!rootPoint.isMac())
			mFTPConfig.setPort(21);
		else
			mFTPConfig.setPort(2121);
		if(rootPoint!=null){
			beforPoint=rootPoint;
		}
		return ftputil;
	}
	/**
	 * 连接FTP服务
	 * @param mLoinListener
	 */
	public void connectFTP(final ConnectFTPListener mLoinListener){
		if(isConnect()){
			if(mLoinListener!=null)
				mLoinListener.success();
			return ;
		}
		final Handler handler=new Handler(){
			@Override
			public void handleMessage(Message msg){
				super.handleMessage(msg);
				switch (msg.what){
				case OK:
					if(mLoinListener!=null)
						mLoinListener.success();
					break;
				case ERROR:
					if(mLoinListener!=null)
						mLoinListener.fail(-1);
					break;
				}
			}
		};
		
		mThreadPool.execute(new Runnable() {
			
			@Override
			public void run(){
			//	int count=0;
				//while (count<3){
					try{
						loinMethod();
						handler.sendEmptyMessage(OK);
					//	break;
					}catch (Exception ex){

						ex.printStackTrace();
						DebugLog.showLog(this,"message:"+ex.getMessage());
						//if(count>=2){
							// unConnectException();
							handler.sendEmptyMessage(ERROR);

						//}
					}
				//	count++;
//					try {
//						Thread.sleep(300);
//					} catch (InterruptedException e) {
//						e.printStackTrace();
//					}
			//	}
			}
		});
	}
	
	
	public void loinMethod() throws IllegalStateException, IOException, FTPIllegalReplyException, FTPException {
		mFTPClient.connect(mFTPConfig.getIp(), mFTPConfig.getPort());
		mFTPClient.login(mFTPConfig.getUsername(), mFTPConfig.getPasswd());
	}

	/**
	 * 取消下载或上传文件时断开连接再次连接
	 * @param flag  断开后需不需要登陆 
	 */
	public void interruptConnectByThread(final boolean flag){
		final Handler handler=new Handler(){

			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				QMUtil.getInstance().closeDialog();
			}
		};
		
		mThreadPool.execute(new Runnable() {
			@Override
			public void run(){
				try {
					interruptConnect(flag);
					handler.sendEmptyMessage(ERROR);
				}catch (Exception e){
					e.printStackTrace();
					unConnectException();
					handler.sendEmptyMessage(ERROR);
				}
			}
		});
	}
	/**
	 * 断开连接
	 */
	public void interruptConnect(boolean flag){
		try {
			unConnectMrthod();
			Thread.sleep(500);
			if(flag)
				loinMethod();
		}catch (Exception e){
			e.printStackTrace();
			unConnectException();
		}
	}
	
	/**
	 * 获取相应设备的文件列表
	 * @param rp
	 * @param directory  是否有文件夹
	 * @return
	 * @throws Exception
	 */
	public FTPFile[] requestFTPList(Context context, RootPoint rp, String directory) throws Exception {
		FTPFile[] ftpFiles = null;
		if(directory==null){//根目录请求
			if (!rp.isNew()) {
				if (rp.getdType() == 0)
					ftpFiles = FTPUtil.getInstance(rp).getFTPClient().list(directory,-1);
				else
					ftpFiles = FTPUtil.getInstance(rp).getFTPClient().list(directory,-2);
			} else {
				// 新模式
				if (rp.getdType() == -1)
					ftpFiles = FTPUtil.getInstance(rp).getFTPClient().list(directory,2);
				else {
					if (rp.getdType() == 0)
						ftpFiles = FTPUtil.getInstance(rp).getFTPClient().list(directory,0);
					else if (rp.getdType() == 1)
						ftpFiles = FTPUtil.getInstance(rp).getFTPClient().list(directory,1);
					else
						ftpFiles = FTPUtil.getInstance(rp).getFTPClient().list(directory,1);
				}
			}
		}else{//某文件夹下的请求
			if(rp.getdType() == 1){
				ftpFiles = FTPUtil
						.getInstance(rp).getFTPClient()
						.list(directory, 1);
			}else if (rp.getdType() == 2){
				ftpFiles = FTPUtil
						.getInstance(rp).getFTPClient()
						.list(context.getResources().getString(R.string.shareDirectory) + directory, 1);
			} else
				ftpFiles = FTPUtil
						.getInstance(rp).getFTPClient()
						.list(directory, 0);
		}
		return ftpFiles;
	}

	public void unConnectMrthodDelet() throws IOException, FTPIllegalReplyException, IllegalStateException, FTPException{
		mFTPClient.abortCurrentDataTransfer(true);
		mFTPClient.abortCurrentConnectionAttempt();
	}
	
	public void unConnectMrthod() throws IOException, FTPIllegalReplyException, IllegalStateException, FTPException{
		mFTPClient.abortCurrentDataTransfer(true);
		mFTPClient.abortCurrentConnectionAttempt();
		if(mFTPClient.isConnected()){
			mFTPClient.disconnect(false);
		}
	}
	
	public void unConnectException(){
		mFTPClient.setConnected(false);
	}

	public void setDisconnect(ConnectFTPListener listener){

		try {
			mFTPClient.disconnect(false);
			if(listener!=null){
				listener.success();
			}
			beforPoint=null;
		} catch (IOException e){
			e.printStackTrace();
			if(listener!=null){
				listener.fail(-1);
			}
		} catch (FTPIllegalReplyException e) {
			e.printStackTrace();
			if(listener!=null){
				listener.fail(-1);
			}
		} catch (FTPException e) {
			e.printStackTrace();
			if(listener!=null){
				listener.fail(-1);
			}
		}

	}

	public void deleteLocalFile(final Activity context, final ArrayList<QMLocalFile> al, final ConnectFTPListener mLoinListener){
		final Handler handler=new Handler(){
			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				switch (msg.what) {
					case OK:
						if(mLoinListener!=null)
							mLoinListener.success();
						break;
					case ERROR:
						if(mLoinListener!=null)
							mLoinListener.fail(-1);
						break;
				}
			}
		};
		//取消上传的核心方法
		mThreadPool.execute(new Runnable(){
			@Override
			public void run(){
				try {
					unConnectMrthodDelet();
//					Thread.sleep(500);
//					loinMethod();
					for (int i = 0; i < al.size(); i++){
						if(al.get(i).isUping()){
							if(mFTPConfig.getPort()==21)
								getFTPClient().deleteFile(context.getResources().getString(R.string.recieve_file)+al.get(i).getName());
							else
								getFTPClient().deleteFile(al.get(i).getName());
						}
					}
					handler.sendEmptyMessage(OK);
				}catch (Exception e){
					e.printStackTrace();
					//unConnectException();
					handler.sendEmptyMessage(ERROR);
				}
			}
		});
	}

	
	
	
	public void delete(final Activity context, final ArrayList<QMLocalFile> al, final ConnectFTPListener mLoinListener){
		final Handler handler=new Handler(){
			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				switch (msg.what){
				case OK:
					if(mLoinListener!=null)
						mLoinListener.success();
					break;
				}
			}
		};
		//取消上传的核心方法
		mThreadPool.execute(new Runnable(){
			@Override
			public void run() {
				try {
					unConnectMrthod();
					Thread.sleep(500);
					loinMethod();
					for (int i = 0; i < al.size(); i++) {
						if(al.get(i).isUping()){
							if(mFTPConfig.getPort()==21)
								getFTPClient().deleteFile(context.getResources().getString(R.string.recieve_file)+al.get(i).getRemoteName());
							else
								getFTPClient().deleteFile(al.get(i).getRemoteName());
						}
					}
					handler.sendEmptyMessage(OK);
				}catch (Exception e){
					e.printStackTrace();
					unConnectException();
					handler.sendEmptyMessage(OK);
				}
			}
		});
	}


	public  boolean isConnect(){
		if(mFTPClient!=null&&mFTPClient.isConnected())
			return true;
		else{
			return false;
		}
	}
	
	public FTPClient getFTPClient(){
		return mFTPClient;
	}

	public ExecutorService getThreadPool() {
		return mThreadPool;
	}

}

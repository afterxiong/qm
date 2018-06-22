package com.shareshow.airpc.socket.common;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.shareshow.aide.R;
import com.shareshow.airpc.ftpclient.FTPDataTransferListener;
import com.shareshow.airpc.model.QMRemoteFTPFile;
import com.shareshow.airpc.model.RootPoint;
import com.shareshow.airpc.ports.ConnectFTPListener;
import com.shareshow.airpc.ports.DownLoadListener;
import com.shareshow.airpc.util.OpenAppUtils;
import com.shareshow.airpc.util.QMDialog;
import com.shareshow.airpc.util.QMFileType;
import com.shareshow.airpc.util.QMUtil;

import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;

public class DownLoadFile {

	private Activity context;
	
	private TextView title;
	private TextView name;
	private ProgressBar progressBar;
	private TextView net_speed;
	private TextView retainTime;
	private TextView progress_value;
	
	
	
	private AsyncTask<Void, Integer, Boolean> mm;
	private RootPoint rootPoint;
	private ArrayList<QMRemoteFTPFile> al=new ArrayList<QMRemoteFTPFile>();
	private QMRemoteFTPFile mMyFTPFile;
	
	private DownLoadListener mLoinListener;
	private String dir="";
	
	private QMDialog dialog;
	private int Loading=0;

	public DownLoadFile(){
		super();
	}

	public DownLoadFile(Activity context, ArrayList<QMRemoteFTPFile> al, RootPoint rootPoint, String dir, DownLoadListener mLoinListener) {
		this();
		this.context = context;
		for (int i = 0; i <al.size(); i++) {
			if(al.get(i).isChoose())
				this.al.add(al.get(i));
		}
		this.rootPoint = rootPoint;
		if(dir!=null)
			this.dir = dir+"/";
		this.mLoinListener = mLoinListener;
		initData(true);
	}
	
	public DownLoadFile(Activity context, QMRemoteFTPFile mMyFTPFile, RootPoint rootPoint, String dir, boolean flag, DownLoadListener mLoinListener) {
		this();
		this.context = context;
		this.rootPoint = rootPoint;
		if(dir!=null)
			this.dir = dir+"/";
		this.mLoinListener = mLoinListener;
		if(flag){//下载单独文件
			this.al.add(mMyFTPFile);
			initData(true);
		}else{
			//缓存文件
			this.mMyFTPFile = mMyFTPFile;
			initData(false);
		}
	}

	private Handler handler = new Handler() {

		private int i;

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);

			switch (msg.what) {
			case 0:
				progress_value.setText( msg.obj + "%");
				break;
			case 1:
				Bundle bundle1 = msg.getData();
				title.setText(context.getResources().getString(R.string.downloading)+"("+bundle1.getInt("count")+"/"+al.size()+")");
				name.setText(bundle1.getString("name") + "");
				net_speed.setText("0KB/s");
				retainTime.setText("00:00");
				progressBar.setProgress(0);
				break;
			case 2:
				Bundle bundle2 = msg.getData();
				progressBar.setProgress((int)(bundle2.getFloat("progress_value")));
				progress_value.setText(bundle2.getFloat("progress_value") + "%");
				if (i % 50 == 0) {
					long ss = bundle2.getLong("net_speed");
					if (ss < 1024)
						net_speed.setText(ss + " KB/s");
					else
						net_speed.setText(ss / 1024 + " MB/s");
				}
				if (i % 30 == 0)
					retainTime.setText(bundle2.getString("time"));
				i++;
				break;
			}
		}
	};
	
	private void initViewCache() {
		View vv = View.inflate(context, R.layout.cache_remotefile_dialog, null);
		name = (TextView) vv.findViewById(R.id.name);
		name.setText(context.getResources().getString(R.string.jiaLoading)+mMyFTPFile.getName());
		progress_value = (TextView) vv.findViewById(R.id.progress_value);
		((Button) vv.findViewById(R.id.cancel))
		.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if(dialog!=null)
					dialog.dismiss();
				if(Loading==0){
					Loading=1;
					return ;
				}
				QMUtil.getInstance().showProgressDialog(context, R.string.cancelimg2);
				mm.cancel(true);
				new File(QMFileType.CACHE + "/"+ mMyFTPFile.getName()).delete();
				FTPUtil.getInstance(rootPoint).interruptConnectByThread(true);
			}
		});
		dialog=new QMDialog(context, vv);
	}
	/**
	 * 下载远程文件的布局
	 */
	private void initViewDownLoad() {
		View vv = View.inflate(context, R.layout.load_remotefile_dialog, null);
		title = (TextView) vv.findViewById(R.id.title);
		name = (TextView) vv.findViewById(R.id.name);
		progressBar = (ProgressBar) vv.findViewById(R.id.progressBar);
		net_speed = (TextView) vv.findViewById(R.id.net_speed);
		retainTime = (TextView) vv.findViewById(R.id.retainTime);
		title.setText(context.getResources().getString(R.string.ready_load));
		retainTime.setText("00:00");	
		progress_value = (TextView) vv.findViewById(R.id.progress_value);
		name.setText(al.get(0).getName());
		((Button) vv.findViewById(R.id.cancel)).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(dialog!=null)
					dialog.dismiss();
				if(Loading==0){
					Loading=1;
					return ;
				}
				QMUtil.getInstance().showProgressDialog(context, R.string.cancelimg);
				mm.cancel(true);
				FTPUtil.getInstance(rootPoint).interruptConnectByThread(true);
				delete();
			}
		});
		dialog=new QMDialog(context, vv);
	}
	
	private void delete() {
		for (int i = 0; i < al.size(); i++) {
			if (al.get(i).isLoading()) {
				String path = QMFileType.LOCALPATH + "/"+ al.get(i).getLocalName();
				new File(path).delete();
				al.get(i).setLoading(false);
			}
		}
	}
	/**
	 * 
	 * @param flag  true下载   false缓存
	 */
	private void initData(final boolean flag) {
		if(flag){
			initViewDownLoad();
		}
		else{
			initViewCache();				
		}
		FTPUtil.getInstance(rootPoint).connectFTP(
				new ConnectFTPListener() {

					@Override
					public void success() {
						if(Loading==1)
							return ;
						Loading=2;
						if(flag){
							initThreadDownLoad();
						}
						else{
							initThreadCache();
						}
					}

					@Override
					public void fail(int state) {
						if(dialog!=null)
							dialog.dismiss();
						if (mLoinListener !=null)
							mLoinListener.fail();
					}
				});
	}

	private void initThreadCache() {
		mm = new AsyncTask<Void, Integer, Boolean>() {
			private int totolTransferred = 0;
			@Override
			protected Boolean doInBackground(Void... params) {
				try {
						String path = QMFileType.CACHE + "/"+ mMyFTPFile.getName();
						String str=dir+mMyFTPFile.getName();
						if(rootPoint.getdType()==0){								
							str=mMyFTPFile.getLink();
						}else if(rootPoint.getdType()==2){
							str=context.getResources().getString(R.string.shareDirectory)+str;
						}
						
						FTPUtil.getInstance(rootPoint).getFTPClient().download(str, new File(path),
								
								new FTPDataTransferListener() {

									@Override
									public void transferred(int length) {
										if (isCancelled()){
											return;
										}
										totolTransferred += length;
										float percent = (float)100 *totolTransferred
												/ mMyFTPFile.getSize();
										percent=new BigDecimal(percent).setScale(1, BigDecimal.ROUND_HALF_UP).floatValue();
										if(percent>=100)
											percent=100;
										
										Message message = handler.obtainMessage();
										message.what =0;
										message.obj=percent;
										handler.sendMessage(message);
									}

									@Override
									public void started() {
									}

									@Override
									public void failed() {
									}

									@Override
									public void completed() {
									}

									@Override
									public void aborted() {
									}
								});
				} catch (Exception ex) {
					ex.printStackTrace();
					return false;
				}

				return true;
			}

			protected void onPostExecute(Boolean result) {
				if(!QMUtil.getInstance().isValid(context))
					return ;
				if(dialog!=null)
					dialog.dismiss();
				if (result) {
					new OpenAppUtils(context)
						.openFiles(QMFileType.CACHE + "/"
								+ mMyFTPFile.getName());
				} else {
					new File(QMFileType.CACHE + "/"
							+ mMyFTPFile.getName()).delete();
					FTPUtil.getInstance(rootPoint).unConnectException();
					QMUtil.getInstance().showToast(context, R.string.load_file_error);
				}

			}
		};
		mm.execute();
	}
	/**
	 * 重命名下载到本地文件
	 */
	private String ReName(String localName){
		String end=localName.substring(localName.lastIndexOf("."), localName.length());
		localName=localName.substring(0, localName.lastIndexOf("."));
		
		ArrayList<String> sameFile=new ArrayList<String>();//存放本地文件与下载文件有相同名称或者重命名的文件
		File file = new File(QMFileType.LOCALPATH);
		if(!file.exists())
			file.mkdirs();
		File[] files=file.listFiles();
		sameFile.clear();
		for (int k = 0; k < files.length; k++) {
			if(files[k].isDirectory())
				continue;
			String localfile_name = files[k].getName();
			String localfile_end=localfile_name.substring(localfile_name.lastIndexOf("."), localfile_name.length());
			if(!end.toLowerCase().equals(localfile_end.toLowerCase()))
				continue;
			localfile_name=localfile_name.substring(0, localfile_name.lastIndexOf("."));
			if(localfile_name.startsWith(localName)){
				sameFile.add(localfile_name);
			}
		}
		
		
		
		String constantName=localName;
		for (int i = 0; i <sameFile.size()+1; i++) {
			if(i!=0)
				localName=constantName+"("+i+")";
			int kk=0;
			for (int j = 0; j<sameFile.size(); j++) {
				if(localName.equals(sameFile.get(j))){
						kk=1;
						break;
				}
			}
			if(kk==0)
				break;
		}
		return localName+end;
	}
	
	/**
	 * 开始下载文件
	 */
	private void initThreadDownLoad() {
		mm = new AsyncTask<Void, Integer, Boolean>() {
			private int totolTransferred = 0;
			private long startTime = 0;
			private long fileLength = 0;
			@Override
			protected Boolean doInBackground(Void... params) {
				try {
						
						for (int i = 0; i < al.size(); i++) {
							startTime= System.currentTimeMillis();
							totolTransferred = 0;
							
							final QMRemoteFTPFile mFTPFile=al.get(i);
							mFTPFile.setLoading(true);
							mFTPFile.setLocalName(ReName(mFTPFile.getName()));
							String path = QMFileType.LOCALPATH + "/"+ mFTPFile.getLocalName();
							fileLength=mFTPFile.getSize();
								//发送准备开始下载的消息
								Message message = handler.obtainMessage();
								Bundle bundle=new Bundle();
								bundle.putString("name", mFTPFile.getName());
								bundle.putInt("count",i+1);
								message.setData(bundle);
								message.what = 1;
								handler.sendMessage(message);
								
								String str=dir+mFTPFile.getName();
								if(rootPoint.getdType()==0){								
									str=mFTPFile.getLink();
								}else if(rootPoint.getdType()==2){
									str=context.getResources().getString(R.string.shareDirectory)+str;
								}
								FTPUtil.getInstance(rootPoint).getFTPClient().download(str,
										new File(path),
										new FTPDataTransferListener() {
									
									@Override
									public void transferred(int length) {
										if (isCancelled())
											return;
										totolTransferred += length;
										float percent = (float)totolTransferred
												/ fileLength;
										float progerss=100 * percent;
										progerss=new BigDecimal(progerss).setScale(1, BigDecimal.ROUND_HALF_UP).floatValue();
										if(progerss>=100.0f)
											progerss=100.0f;
										Message message = handler
												.obtainMessage();
										Bundle bundle=new Bundle();
										bundle.putFloat("progress_value",progerss);
										long times= System.currentTimeMillis()-startTime;
										bundle.putLong("net_speed",totolTransferred/times*1000/1024);
										long time=(long) ((1-percent)/percent*times);
										if(time<0)
											time=time*(-1);
										bundle.putString("time",QMFileType.getTime(time));
										message.setData(bundle);
										message.what =2;
										handler.sendMessage(message);
									}

									@Override
									public void started() {
									}
									
									@Override
									public void failed() {
									}
									
									@Override
									public void completed() {
										mFTPFile.setLoading(false);
									}
									
									@Override
									public void aborted() {
									}
								});
						}
				} catch (Exception ex) {
					ex.printStackTrace();
					return false;
				}
				return true;
			}
			
			protected void onPostExecute(Boolean result) {
				if(!QMUtil.getInstance().isValid(context))
					return ;
				if(dialog!=null)
					dialog.dismiss();
				if (result) {
					if (mLoinListener != null)
						mLoinListener.success();
					QMUtil.getInstance().showToast2(context,context.getResources().getString(R.string.loadsuccess) + QMFileType.LOCALPATH);
				} else {
					delete();
					FTPUtil.getInstance(rootPoint).unConnectException();
					QMUtil.getInstance().showToast(context, R.string.download_fail_unnet);
					if (mLoinListener != null)
						mLoinListener.fail();
				}
				
			}
			
		};
		
		mm.execute();
	}
}

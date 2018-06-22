package com.shareshow.airpc.socket.common;

import android.annotation.SuppressLint;
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
import android.widget.Toast;

import com.shareshow.aide.R;
import com.shareshow.airpc.ftpclient.FTPAbortedException;
import com.shareshow.airpc.ftpclient.FTPDataTransferException;
import com.shareshow.airpc.ftpclient.FTPDataTransferListener;
import com.shareshow.airpc.ftpclient.FTPException;
import com.shareshow.airpc.ftpclient.FTPFile;
import com.shareshow.airpc.ftpclient.FTPIllegalReplyException;
import com.shareshow.airpc.model.QMLocalFile;
import com.shareshow.airpc.model.RootPoint;
import com.shareshow.airpc.ports.ConnectFTPListener;
import com.shareshow.airpc.share.HeartThread;
import com.shareshow.airpc.util.DebugLog;
import com.shareshow.airpc.util.QMDialog;
import com.shareshow.airpc.util.QMFileType;
import com.shareshow.airpc.util.QMUtil;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;


/**
 * @author caicancan
 * @function 上传文件
 * 
 * */
@SuppressLint("HandlerLeak")
public class UploadFile implements HeartThread.HeartListener{

	private  boolean isShowDialog;
	private Activity context;

	private TextView title;
	private TextView name;
	private ProgressBar progressBar;
	private TextView net_speed;
	private TextView retainTime;
	private TextView progress_value;
	
	private AsyncTask<String, Integer, Integer> mm;
	
	private ArrayList<QMLocalFile> al=new ArrayList<QMLocalFile>();
	
	
	private RootPoint rootPoint;
	
	private ConnectFTPListener mLoinListener;
	
	private QMDialog dialog;
	private int Loading=0;
	private TextView sendProduct;
	private ProgressBar bar;
	private TextView gress;
	private int localPos;

	private final  static int ERROR =0x001;

	private final  static int SUCCESS =0x002;

	private final  static int ABORT =0x003;


	private final  static int TIME_OUT =0x004;
	private HeartThread heartThread;

	//private boolean isSendFile =false;


	public UploadFile(Activity context, QMLocalFile mMyLocalFile, RootPoint rootPoint, ConnectFTPListener mLoinListener) {
		super();
		this.context = context;
		this.rootPoint = rootPoint;
		this.mLoinListener = mLoinListener;
		this.al.add(mMyLocalFile);
		initData();
	}
	
	public UploadFile(Activity context, ArrayList<QMLocalFile> al, RootPoint rootPoint, ConnectFTPListener mLoinListener) {
		super();
		this.context = context;
		for (int i = 0; i <al.size(); i++) {
			if(al.get(i).isChoose())
				this.al.add(al.get(i));
		}
		this.rootPoint = rootPoint;
		this.mLoinListener = mLoinListener;
		initData();
	}

	public UploadFile(Activity context, ArrayList<QMLocalFile> al, RootPoint rootPoint, ConnectFTPListener mLoinListener, boolean isShow, TextView send_product, ProgressBar bar, TextView gress) {
		super();
		this.context = context;
		for (int i = 0; i <al.size(); i++) {
			if(al.get(i).isChoose())
				this.al.add(al.get(i));
		}
		this.rootPoint = rootPoint;
		this.mLoinListener = mLoinListener;
		this.isShowDialog=isShow ;
		this.sendProduct=send_product;
		this.bar=bar;
		this.gress=gress;
		initData();
	}


	
	private Handler handler = new Handler() {
		
		private int i;
		
		@Override
		public void handleMessage(Message msg){
			super.handleMessage(msg);
			switch (msg.what){
			case 0:
				Bundle bundle1 = msg.getData();
				title.setText(context.getResources().getString(R.string.uploading)+"("+bundle1.getInt("count")+"/"+al.size()+")");
				name.setText(bundle1.getString("name") + "");
				net_speed.setText("0KB/s");
				retainTime.setText("00:00");
				progressBar.setProgress(0);
				if(bar!=null){
					bar.setProgress(0);
				}
				if(sendProduct!=null){
					sendProduct.setText("正在发送-"+bundle1.getString("name"));
				}
//				if(bar!=null){
//				    bar.setProgress((bundle1.getInt("count")-1));
//			    }
				if(gress!=null){
					gress.setText((bundle1.getInt("count")-1)+"/"+al.size());
				}

				break;
			case 1:
				Bundle bundle = msg.getData();
				progressBar.setProgress((int)bundle.getFloat("progress_value"));
				progress_value.setText(bundle.getFloat("progress_value") + "%");
				int pos=bundle.getInt("pos");
//				if(bar!=null){
//					int progress = (bundle.getInt("count")-1)/al.size()+(int)bundle.getFloat("progress_value")/(100*(al.size()));
//					bar.setProgress(progress);
//				}

				if(bar!=null){
					bar.setProgress((int)bundle.getFloat("progress_value"));
				}

				if(i%5==0){
					long ss=bundle.getLong("net_speed");
					if(ss<1024)
						net_speed.setText(ss+" KB/s");
					else
						net_speed.setText(ss/1024+" MB/s");
				}

				if(i%10==0)
					retainTime.setText(bundle.getString("time"));
				i++;
				break;
			}
			
		}
	};

	private void initData(){
		if(heartThread==null){
			heartThread=new HeartThread();
			heartThread.setHeartListener(this);
		}

		if (al.size()==0){
			Toast.makeText(context,rootPoint.getName()+"请选择文件上传！", Toast.LENGTH_SHORT).show();
			return;
		}
//		isSendFile=true;
//		HeartThread.start();
		initViewDownLoad();
		FTPUtil.getInstance(rootPoint).connectFTP(
				new ConnectFTPListener(){

					@Override
					public void success(){
						if(Loading==1)
							return ;
						Loading=2;
						initThreadUpLoad();
					}

					@Override
					public void fail(int state){

						if(dialog!=null)
							dialog.dismiss();
						if (mLoinListener !=null)
							mLoinListener.fail(state);
						Toast.makeText(context,rootPoint.getName()+"上传失败，请重试！", Toast.LENGTH_SHORT).show();
					}
				});
	     }


	/**
	 * 开始上传文件
	 */
	private void initThreadUpLoad(){
		    mm=new AsyncTask<String, Integer, Integer>(){
			private int totolTransferred = 0;//每个文件上传的进度大小
			private long startTime = 0;
			@Override
			protected Integer doInBackground(String... params){
		//	try {
				int ftpServerCode=0;

				try{
					String[] strings= FTPUtil.getInstance(rootPoint).getFTPClient().serverStatus();
					if(strings.length>0){
						 ftpServerCode= Integer.parseInt(strings[0]);
					}
					DebugLog.showLog(this,"ftpServerCode:"+ftpServerCode);
				  }catch(Exception e){
					e.printStackTrace();
					ftpServerCode=0;
				  }

				ArrayList<String> arrayList = new ArrayList<String>();

				if(ftpServerCode==0){
					FTPFile[] ftpFiles= new FTPFile[0];
					try {
						ftpFiles = FTPUtil.getInstance(rootPoint).requestFTPList(context,rootPoint,null);
					}catch (Exception e){
						e.printStackTrace();
						DebugLog.showLog(this,"上传失败的原因1:"+e.getMessage());
						return ERROR;
					}

					if(ftpFiles!=null){
						for (int i = 0; i < ftpFiles.length; i++){
							if(ftpFiles[i]!=null&&ftpFiles[i].getDir()==1){
								arrayList.add(ftpFiles[i].getName());
							}
						 }
					   }
				    }else{
                       if(heartThread!=null){
						   heartThread.sendHeart(rootPoint,true);
					   }
			    	}

//				   if(!isShowDialog){
//					   try {
//						   FTPUtil.getInstance(rootPoint).getFTPClient().sendCustomCommand("isShareFile");
//						   DebugLog.showLog(this,"本地文件发送...");
//					   } catch (Exception e){
//						   e.printStackTrace();
//						   DebugLog.showLog(this,"上传失败的原因2:"+e.getMessage());
//						   return ERROR;
//					   }
//				   }

					for (int i = 0; i < al.size(); i++){
						localPos=i;
						startTime= System.currentTimeMillis();
						totolTransferred=0;
						final QMLocalFile qmLocalFile=al.get(i);
						String reName=null;
						if(ftpServerCode==0){
							reName= ReName(qmLocalFile.getName(),arrayList);
						}else{
							 reName=qmLocalFile.getName();
						}
						qmLocalFile.setRemoteName(reName);
						qmLocalFile.setUping(true);
						String dir = null;
						if(rootPoint.getdType()==1)
							dir="Downloads/";
						else
							dir=null;

							Message message = handler.obtainMessage();
							Bundle bundle=new Bundle();
							bundle.putString("name", qmLocalFile.getName());
							bundle.putInt("count",i+1);
							message.setData(bundle);
							message.what =0;
							handler.sendMessage(message);

						try {
							FTPUtil.getInstance(rootPoint).getFTPClient().upload(qmLocalFile,dir,new FTPDataTransferListener() {
								@Override
								public void transferred(int length){
									if(isCancelled())
										return ;
									totolTransferred += length;
									float percent = (float) totolTransferred /qmLocalFile.getSize();
									float progerss=100 * percent;
									progerss=new BigDecimal(progerss).setScale(1, BigDecimal.ROUND_HALF_UP).floatValue();
									if(progerss>=100.0f)
										progerss=100.0f;

									Message message = handler.obtainMessage();
									Bundle bundle=new Bundle();
									bundle.putFloat("progress_value",progerss);
									long times= System.currentTimeMillis()-startTime;
									bundle.putLong("net_speed",totolTransferred/times*1000/1024);
									long time=(long) ((1-percent)/percent*times);
									bundle.putString("time", QMFileType.getTime(time));
									bundle.putInt("pos",localPos);
									message.setData(bundle);
									message.what=1;
									handler.sendMessage(message);
								}
								@Override
                                public void started(){
								}
								@Override
                                public void failed(){
									DebugLog.showLog(this,"upload上传失败！");
								}

								@Override
                                public void completed(){
									qmLocalFile.setUping(false);
									DebugLog.showLog(this,"上传完成！");

								}

								@Override
                                public void aborted(){
									DebugLog.showLog(this,"主动中断！");
									if(heartThread!=null){
										heartThread.stopSendHeart();
									}
								}
							});
						}catch (IOException e){
							e.printStackTrace();
							DebugLog.showLog(this,"上传失败1："+e.getMessage());
							return TIME_OUT;
						}catch (FTPIllegalReplyException e){
							e.printStackTrace();
							DebugLog.showLog(this,"上传失败2："+e.getMessage());
							return ERROR;
						}catch (FTPException e){
							e.printStackTrace();
							DebugLog.showLog(this,"上传失败3："+e.getMessage());
							return ERROR;
						}catch (FTPDataTransferException e){
							e.printStackTrace();
							DebugLog.showLog(this,"上传失败4："+e.getMessage());
							return ERROR;
						}catch (FTPAbortedException e){
							e.printStackTrace();
							return ABORT;
						}
					}
//				} catch (Exception ex){
//					ex.printStackTrace();
//					DebugLog.showLog(this, ex.getMessage()+"");
//					return false;
//				}
				return SUCCESS;
			}

			protected void onProgressUpdate(Integer... progress) {

			}

			protected void onPostExecute(Integer result){
				if(heartThread!=null){
					heartThread.stopSendHeart();
				}

				if(!QMUtil.getInstance().isValid(context))
					return ;
				if(dialog!=null)
					dialog.dismiss();
				if(result==SUCCESS){
//					if(bar!=null){
//						bar.setProgress(al.size());
//					}
					if(gress!=null){
						gress.setText(al.size()+"/"+al.size());
					}
					if(mLoinListener!=null)
						mLoinListener.success();
					Toast.makeText(context,rootPoint.getName()+"上传文件成功！", Toast.LENGTH_SHORT).show();
					//	QMUtil.getInstance().showToast(context, R.string.upload_fail_success);

				}else if(result==ERROR){
					if(mLoinListener!=null)
						mLoinListener.fail(0);
					// FTPUtil.getInstance(rootPoint).unConnectException();
				    // QMUtil.getInstance().showToast(context, R.string.upload_fail_error);
					Toast.makeText(context,rootPoint.getName()+"上传失败，请重试！", Toast.LENGTH_SHORT).show();
				}else if(result==ABORT){
					if(mLoinListener!=null)
						mLoinListener.fail(1);
				//	FTPUtil.getInstance(rootPoint).unConnectException();
					// QMUtil.getInstance().showToast(context, R.string.upload_fail_error);
				//	Toast.makeText(context,rootPoint.getName()+"上传失败，请重试！",Toast.LENGTH_SHORT).show();
				}else if(result==TIME_OUT){
					if(mLoinListener!=null)
						mLoinListener.fail(2);

				}
			}

		};
		mm.execute();
	}

	/**
	 * 初始化上传的视图
	 */
	private void initViewDownLoad(){
		View vv = View.inflate(context, R.layout.load_remotefile_dialog, null);
		title = (TextView) vv.findViewById(R.id.title);
		name = (TextView) vv.findViewById(R.id.name);
		progressBar = (ProgressBar) vv.findViewById(R.id.progressBar);
		net_speed = (TextView) vv.findViewById(R.id.net_speed);
		retainTime = (TextView) vv.findViewById(R.id.retainTime);
		title.setText(context.getResources().getString(R.string.ready_upload));
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
				mm.cancel(true);
				delete();
			}
		});

		if(!isShowDialog){
			dialog=new QMDialog(context, vv);
		}
	}


	public void delete(){
		QMUtil.getInstance().showProgressDialog(context, R.string.cancel_upload);
		FTPUtil.getInstance(rootPoint).delete(context,al,new ConnectFTPListener(){
			
			@Override
			public void success(){
				QMUtil.getInstance().closeDialog();
//				new Thread(new Runnable() {
//					@Override
//					public void run() {
//						try {
//							FTPUtil.getInstance(rootPoint).getFTPClient().sendCustomCommand("FINISH&&DEVICENAME="+ Collocation.getCollocation().getDeviceName()+"&&ADDRESS="+ NetworkUtils.getInstance(context).getNetworkIP());
//						} catch (Exception e) {
//						   e.printStackTrace();
//						   DebugLog.showLog("UploadFile","发送Finish失败!");
//						}
//					}
//				}).start();
			}
			
			@Override
			public void fail(int state){
				QMUtil.getInstance().closeDialog();
			}
		});
	}
	
	/**
	 * 重命名下载到本地文件
	 */
	public static String ReName(String localName, ArrayList<String> files){
		try{
			String end=localName.substring(localName.lastIndexOf("."), localName.length());
			localName=localName.substring(0, localName.lastIndexOf("."));

			ArrayList<String> sameFile=new ArrayList<String>();//存放本地文件与下载文件有相同名称或者重命名的文件
			sameFile.clear();
			for (int k = 0; k < files.size(); k++){
				String localfile_name = files.get(k);
				String localfile_end=localfile_name.substring(localfile_name.lastIndexOf("."), localfile_name.length());
				if(!end.toLowerCase().equals(localfile_end.toLowerCase()))
					continue;
				localfile_name=localfile_name.substring(0, localfile_name.lastIndexOf("."));
				if(localfile_name.startsWith(localName)){
					sameFile.add(localfile_name);
				}
			}

			String constantName=localName;
			for (int i = 0; i <sameFile.size()+1; i++){
				if(i!=0)
					localName=constantName+"("+i+")";
				int kk=0;
				for (int j = 0; j<sameFile.size(); j++){
					if(localName.equals(sameFile.get(j))){
						kk=1;
						break;
					}
				}
				if(kk==0)
					break;
			}
			return localName+end;
		}catch (Exception e){
			e.getMessage();
			return localName;
		}
	}

	@Override
	public void OnTimeOutHeart(RootPoint rootPoint){
		DebugLog.showLog(this,"网络超时！");
		if(mLoinListener!=null){
			mLoinListener.fail(2);
		}
		if(!isShowDialog&&dialog!=null&&dialog.isShowing()){
			dialog.dismiss();
		}
	}

}

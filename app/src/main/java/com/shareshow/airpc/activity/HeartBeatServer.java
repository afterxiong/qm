package com.shareshow.airpc.activity;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.shareshow.aide.activity.MoreScreenActivity;
import com.shareshow.airpc.QMCommander;
import com.shareshow.airpc.model.RootPoint;
import com.shareshow.airpc.socket.command.CommandExectuorMobile;
import com.shareshow.airpc.socket.command.CommandExecutorBox;
import com.shareshow.airpc.socket.command.CommandExecutorLancher;
import com.shareshow.airpc.socket.command.CommandExecutorLancherFile;
import com.shareshow.airpc.socket.common.HeatbeatEvent;
import com.shareshow.airpc.socket.common.QMDevice;
import com.shareshow.airpc.util.DebugLog;
import com.shareshow.airpc.util.QMUtil;

import org.greenrobot.eventbus.EventBus;

/**
 * 将定时器的功能放在后台服务中执行
 * 定时器主要处理投屏的心跳、与任盒的心跳
 * @author tanwei
 *
 */
public class HeartBeatServer extends Service {
	
	private GetPCbyTimer timer;//定时器，用于处理投屏后发心跳的指令

	private long TIME_OUT=2*1000;
	
	@Override
	public IBinder onBind(Intent intent){
		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId){
		if(timer==null){
			timer=new GetPCbyTimer();
			new Thread(timer).start();
			//读取SDCARD中的所有文件，除了图片及视频
			if(!QMUtil.getInstance().getQmDocumentFile().isFileDealFinished()){
				QMUtil.getInstance().getQmDocumentFile().getDocumentAndOtherData();
			}
		}
		return START_STICKY;
	}
	
	
	private class GetPCbyTimer implements Runnable {
		@Override
		 public void run(){
			while (true){
					for (int i = 0; i < QMDevice.getInstance().getSize(); i++){
						RootPoint point=QMDevice.getInstance().getRootPoint(i);
						if (point.isTouPing()&&point.getdType()!=2){
							//线程在第一次开始投屏就开启了，只有投上的盒子才会不停的发心跳的
							if(point.getdType()==-1){
								CommandExecutorBox.getOnlyExecutor().connectMessage(QMCommander.TYPE_HEARTBEAT, point.getAddress(), null);
							}else if(point.getdType()==0||point.getdType()==1){
								CommandExectuorMobile.getOnlyExecutor().connectMessage(QMCommander.TYPE_HEARTBEAT_M, point.getAddress());
							}else if(point.getdType()==2){
								CommandExecutorLancher.getOnlyExecutor().replayResponsePcOnly(QMCommander.TYPE_SEARCH,point.getAddress());
							}

							//改变底部【投屏】按钮的状态
							if(point.getHeartbeat()>3){
								TIME_OUT =1*1000;
							}
							if(point.getHeartbeat()>12){
								DebugLog.showLog(this,"心跳超时，发送投屏断开的消息...");
								point.setTouPing(false);
								point.setHeartbeat(0);
								Intent intent1=new Intent();
								intent1.setAction(MoreScreenActivity.ACTION_BUTTON);
								intent1.putExtra("position", i);
								intent1.putExtra("type",point.getdType());
								intent1.putExtra("aa", 1);
								sendBroadcast(intent1);
								TIME_OUT=2*1000;
							}

							point.setHeartbeat(point.getHeartbeat()+1);
						}else if(point.isShareing()&&point.getdType()==2){

//							if(point.getdType()==0||point.getdType()==1){
//								CommandExectuorMobile.getOnlyExecutor().connectMessage(QMCommander.TYPE_HEARTBEAT_M, point.getAddress());
//							}else

							CommandExecutorLancher.getOnlyExecutor().replayRequestPcOnly(QMCommander.TYPE_SEARCH,point.getAddress());

							if(point.getShareHeart()>3){
								TIME_OUT =1*1000;
							}


							if(point.getShareHeart()>12){
								point.setShareHeart(0);
								point.setShareing(false);
								DebugLog.showLog(this,"心跳超时，发送分享断开的消息...");
								EventBus.getDefault().post(new HeatbeatEvent(point.getAddress()));
								TIME_OUT=2*1000;
							}

							point.setShareHeart(point.getShareHeart()+1);
						}

						if(point.isPlay()&&point.getdType()==-1){
							point.setHeartbeat2(point.getHeartbeat2()+1);
							CommandExecutorLancherFile.getOnlyExecutor().heartBeatMessage(QMCommander.TYPE_BOX_ONLINE,point.getAddress());
							CommandExecutorLancher.getOnlyExecutor().heartBeatMessage(QMCommander.TYPE_BOX_ONLINE,point.getAddress());
						   }
					     }

						 try {
							Thread.sleep(TIME_OUT);
						 }catch (InterruptedException e){
							e.printStackTrace();
						 }

			    }
			}
		}

}

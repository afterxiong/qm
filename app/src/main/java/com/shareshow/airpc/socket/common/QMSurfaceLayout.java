package com.shareshow.airpc.socket.common;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.widget.LinearLayout;

import com.shareshow.aide.R;
import com.xtmedia.xtview.GlRenderNative;
import com.shareshow.airpc.activity.ScreenShareActivity;
import com.shareshow.airpc.model.RootPoint;
import com.shareshow.airpc.record.SendDisconStream;
import com.shareshow.airpc.util.DebugLog;
import com.shareshow.airpc.widget.QMSurfaceView;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 处理分享调用底层接口的类
 * @author tanwei
 *
 */

public class QMSurfaceLayout extends LinearLayout implements DisconnectListener {

	private ExecutorService threadPool;
	private String host;//盒子的IP地址
	private long lobj;
	private int struts;
	private ScreenShareActivity screenShareActivity;
	public  QMSurfaceView surfaceView;//自定义的surfaceview
	private SurfaceHolder holder;
	private RootPoint rootPoint;
	private long rthandle;
	private boolean isLocalPlay;
	private boolean isShareScreen;
	private int play_state=0;


    //播放器的状态
	private static final int STATE_IDLE= 0x0001;
	//播放器加载完成处于闲置
	private static final int STATE_PLAYING= 0x0002;
	//播放器调用播放接口处于等待画面
	private static final int STATE_PLAY= 0x0003;
	//播放器处于正在播放状态
	private static final int STATE_STOPPING= 0x0004;
	//播放器处于正在停止状态
	private static final int STATE_CREATE_FAILE=0x0005;
    //创建转发源失败


	//业务的状态
	private static final int STATE_STOPPING_FINISH=0x0006;
	private static final int STATE_PLAY_SUCCESS =0x0007;
	private static final int RESTART_PLAY =0x0008 ;
	private static final int START_PLAY_FAILED = 0x0009;
	private static final int START_DECODER_FAILED =0x0010 ;
	private static final int STAT_PLAY_READY =0x0011;
	private static final int STATE_START_PLAY =0x0012;


	private ScreenStarteListener listener;
	private boolean isDisconnectStream;

	// private Object dbLock= new Object();

	public QMSurfaceLayout(Context context, AttributeSet attrs){
		super(context, attrs);
		initData();
	}

	/**
	 * 初始化一些数据
	 */
	private void initData(){

		threadPool = Executors.newFixedThreadPool(5);

	}

	//在创建surfaceview时做的操作，作用未知
	private Runnable runnable = new Runnable(){
		public void run(){
			lobj = GlRenderNative.SetVideoSurface(holder.getSurface());
			DebugLog.showLog(this,"lobj:" + lobj);
			mHandler.sendEmptyMessage(STAT_PLAY_READY);
		}
	};


	public boolean isLocalPlay(){
		return isLocalPlay;
	}

	public void setLocalPlay(boolean localPlay){
		isLocalPlay = localPlay;
	}



	/**
	 * 加载SurfaceView
	 * @param
	 * 解决解码中的线程调度
	 *
	 */

	private Handler mHandler =new Handler(){

		@Override
		public void dispatchMessage(Message msg){
			super.dispatchMessage(msg);
            switch (msg.what){

//				case STATE_STOPPING_FINISH:
//				case STAT_PLAY_READY:
//
//					break;

				case RESTART_PLAY:

					RootPoint rootPoint= (RootPoint) msg.obj;
					QMSurfaceLayout.this.rootPoint=rootPoint;
					reStartPlay(rootPoint);
					break;


				case START_DECODER_FAILED:
					//  play_state=STATE_CREATE_FAILE;
					  removeAllCallbacks();
					  play_state=STATE_IDLE;
					//  stopPlay(false);

						if(listener!=null){
							if(isDisconnectStream){
								listener.showCloseActivity();
								isDisconnectStream=false;
							}else{
								listener.backToMainActivity(0);
							}

					}
					//screenShareActivity.backtoMainActivity(0);
					break;

				case STATE_PLAY_SUCCESS:
					play_state=STATE_PLAY;
					playImg();
					break;

				case START_PLAY_FAILED:
					playError();
					break;

				case STATE_START_PLAY:
					play_state=STATE_PLAYING;
					threadPool.submit(new StartPlayThread(QMSurfaceLayout.this.rootPoint));
					break;
			    }
		   }
	  };


	public void loadSurfaceView(ScreenShareActivity activity, int maxWidth, int maxHeight, boolean isShareScreen, ScreenStarteListener listener){
		this.listener = listener;
		this.screenShareActivity=activity;
		this.isShareScreen = isShareScreen;
		removeAllViews();
		surfaceView = new QMSurfaceView(activity,maxWidth,maxHeight);
		surfaceView.setBackgroundResource(R.color.transparent);
		LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		surfaceView.setLayoutParams(params);
		surfaceView.setmMoveListener(activity);
		addView(surfaceView);
		holder = surfaceView.getHolder();
		holder.addCallback(new Callback(){

			public void surfaceDestroyed(SurfaceHolder holder){

			}

			public void surfaceCreated(SurfaceHolder holder) {
					threadPool.submit(runnable);
			}

			public void surfaceChanged(SurfaceHolder holder, int format,
                                       int width, int height){
			}
		});
	}


	/**
	 * 初始化开始点播的对象
	 */

	public synchronized void startPlay(RootPoint rootPoint){

		if(listener!=null){
			listener.LoadingAnimate();
		}

		this.rootPoint=rootPoint;
		if(isLocalPlay()){
			stopPlay(true);
		}else{
			if((play_state==STATE_STOPPING||play_state==STATE_PLAYING||play_state==STATE_PLAY)&&!isLocalPlay()){
				DebugLog.showLog(this,"此状态不能播放："+play_state);
			}else{
				play_state=STATE_PLAYING;
				threadPool.submit(new StartPlayThread(rootPoint));
			}
		}

	}
	
	/**
	/**
	 * 初始化停止点播的对象
	 */
	public synchronized void stopPlay(boolean isRestart){
		DebugLog.showLog(this,"停止点播------------");
		if(play_state==STATE_STOPPING||play_state==STATE_IDLE||play_state==STATE_CREATE_FAILE){
			DebugLog.showLog(this,"此状态不能停止："+play_state);
		}else{
			play_state=STATE_STOPPING;
			threadPool.submit(new StopPlayThread(isRestart));
		}

//		try {
//			Thread.sleep(1000);
//		}catch(InterruptedException e) {
//			e.printStackTrace();
//		}
	}



	/**
	 * @param rootPoint
	 * 断流
	 */
	@Override
	public void disconnectStream(RootPoint rootPoint){
		   DebugLog.showLog(this,"断流了---rootPoint:"+rootPoint);
		if(!isDisconnectStream){
			isDisconnectStream =true;
			Message msg=new Message();
			msg.obj=rootPoint;
			msg.what=RESTART_PLAY;
			mHandler.sendMessage(msg);
		}else{
			DebugLog.showLog(this,"只能断流重点一次!");
		}

		//mHandler.sendEmptyMessage(RESTART_PLAY);
	}

	private void reStartPlay(RootPoint rootPoint){

			if(rootPoint!=null){
				startPlay(rootPoint);
			}else{
				if(listener!=null){
					listener.backToMainActivity(0);
				}
			}
	}

	/**
	 * 分享点播的核心方法
	 * @author tanwei
	 *
	 */
	private class StartPlayThread extends Thread {

		private String url;
		private RootPoint rootPoint;

		public StartPlayThread(RootPoint rootPoint){
			super();
			this.rootPoint=rootPoint;
			this.url = 	this.rootPoint.getAddress();
		}

		public void run(){
			super.run();
			int model=	this.rootPoint.getdType();
			while (true){
				if (lobj == 0){
					SystemClock.sleep(100);
					DebugLog.showLog(this,"----------------------XXXXX");
				  }else{
					host = url;
					if(	this.rootPoint.getdType()==2){
						if(isShareScreen){

							url= "rtsp://" + this.rootPoint.getPlayurl() + ":1554/"+this.rootPoint.getChannel();

						}else{

							url= "rtsp://" + this.rootPoint.getAddress() + ":1554/0";

						}
					}else{

						if(isShareScreen){
							url = "rtsp://" + url + ":1554/1";//rtsp方式点播，注释此行将是UDP方式点播
						}else{
							url = "rtsp://" + url + ":1554/0";//rtsp方式点播，注释此行将是UDP方式点播
						}
					}

					removeAllCallbacks();
					postDelayed(palyDelay,25*1000);;//25秒内没有点播成功做个处理
//					GlRenderNative.setSyncInfo(true, 1,1000 * 1000 * 0);
					DebugLog.showLog(this,"----------------------演示模式");
					if(model == 2){
						//pc
						DebugLog.showLog(this, "媒体模式");
						GlRenderNative.setSyncInfo(true, 1, 1000 * 1000 * 2);
						GlRenderNative.MediaSkipToIFrame(false, 30);
					}else if (model == 0|| model==1){

						DebugLog.showLog(this, "手机模式");
						GlRenderNative.setSyncInfo(true, 1, 0);
					}else{
						//盒子
						DebugLog.showLog(this, "演示模式");
						GlRenderNative.setSyncInfo(true, 1, 0);
						GlRenderNative.MediaSkipToIFrame(true, 30);
					}

					try{
						Thread.sleep(1000);
					}catch (InterruptedException e){
						e.printStackTrace();
					}

					DebugLog.showLog(this,"点播的URL------------" + url);
					if (this.rootPoint.getMutilcast().equals("0")){
						DebugLog.showLog(this,"----------------单播模式");
						//断流重点的接口回调
//						   if(isShareScreen){
//							   GlRenderNative.rtRegisterDataBreakCallback(1000 * 10, SendDisconStream.getStream(rootPoint), 0);
//					           struts = GlRenderNative.opensingleex(url, 19900, 0,lobj);
//						    }else{
							   int[] chan = new int[1];
							   chan[0]=1;
							   rthandle = GlRenderNative.rtStartPlay(172, url, 19900, 0, chan);
							   if(rthandle != 0 && rthandle != -1 && rthandle != -2 && rthandle != -3&&!isLocalPlay){

								   GlRenderNative.rtRegisterDataBreakCallback(1000 * 10, SendDisconStream.getStream(this.rootPoint, QMSurfaceLayout.this), 0);
								   struts = GlRenderNative.mediaLocalPlayEx(rthandle, lobj);
								   GlRenderNative.RequestIframeX(rthandle);
								   DebugLog.showLog(this,"点播状态...rthandle:"+rthandle+"struts:"+struts+"lobj:"+lobj);
								   setLocalPlay(true);
								   isDisconnectStream=false;
							   }else{
								   struts=-1;
								   DebugLog.showLog(this,"点播失败...rthandle:"+rthandle+"isLocalPlay:"+isLocalPlay);
								   endPlay();
								   return;
							 //  }
						       }
		//			        GlRenderNative.rtRegisterDataBreakCallback(1000 * 10, SendDisconStream.getStream(rootPoint), 0);
//							struts = GlRenderNative.opensingleex(url, 19900, 0,lobj);
							DebugLog.showLog(this,"所出来的状态:-----------" + struts);
							if (struts == 0){
								return;
							}else if (struts ==-1){
								endPlay();
								return ;
							}
					}else{
						/*long rthandle=GlRenderNative.openStdMulticast(url, 0, false,rootPoint.getMutilcastAddress(), 19999, lobj);
						DebugLog.showLog(this,"----------------组播模式"+rootPoint.getMutilcastAddress()+"----------------"+rthandle);
						*/
					}
					
					return;
				}

			  }

		}
	}

	/**
	 * 调用停止点播的底层接口
	 */
	private class StopPlayThread extends Thread {

		private boolean isRestart;


		private StopPlayThread(boolean isRestart){
			this.isRestart =isRestart;
		}

		public void run(){
			super.run();
				if (isLocalPlay){
					DebugLog.showLog(this, "停止解码---struts:" + struts + "rthandle:" + rthandle);
					GlRenderNative.mediaLocalStop(struts, rthandle);
					GlRenderNative.rtStopPlay(rthandle);
					rthandle = 0;
					setLocalPlay(false);
					play_state =STATE_IDLE;
					if(isRestart){
						mHandler.sendEmptyMessage(STATE_START_PLAY);
					}
					//	mHandler.sendEmptyMessage(STATE_STOPPING_FINISH);
				}

				//	DebugLog.showLog(this, "停止耗时:" + (System.currentTimeMillis() - playTime));

				//GlRenderNative.stoprtspplay(struts);
			}
		}


	
	/**
	 * 断流回调的监听方法
	 * @param state
	 */
	public void onPlayState(int state){
		DebugLog.showLog(this,"返回的状态码---------" + state);
		switch (state) {
		case 16://16表示点播成功了

			mHandler.sendEmptyMessage(STATE_PLAY_SUCCESS);

			break;
		case 2:
			//手机投屏不需要断流重点
			if (rootPoint.getdType()!=-1){
				return ;
			}
		   mHandler.sendEmptyMessage(START_PLAY_FAILED);
			break;
		}
		
	}

	/**
	 * 返回状态为-1，没有播放源
	 */
	private void endPlay(){

		 mHandler.sendEmptyMessage(START_DECODER_FAILED);
	}
	

	/**
	 * 断流或者盒子退出情况，
	 */
	private void playError(){
		if(listener!=null){
			listener.playError(false,host);
		}
		//screenShareActivity.setPlay(false);
		postDelayed(palyInterrupt, 10*1000);
		stopPlay(false);
//		CommandExecutorBox.getOnlyExecutor().connectMessage(
//				QMCommander.TYPE_SET_CLIENT_MSG, host, null);
	}

	/**
	 * 正常播放
	 */
	private void playImg(){
		// 加入断流检测,10秒没收到数据流，重新点播
		GlRenderNative.setStreamDownCallback(0, 1000 *10);
		//GlRenderNative.setStreamDownCallback(0, 1000 *1);
		removeAllCallbacks();
		surfaceView.setBackgroundResource(R.color.transparent);
		if(listener!=null){
			listener.playSuccess();
		}
	}

	/**
	 * 25秒内没有成功点播，直接退出
	 */
	private Runnable palyDelay = new Runnable(){
		@Override
		public void run(){
			if(listener!=null){
				listener.backToMainActivity(0);
			}
			//screenShareActivity.backtoMainActivity(0);
			stopPlay(false);
		}
	};
	
	/**
	 * 10秒内 断流没有成功点播，直接退出
	 */
	private Runnable palyInterrupt = new Runnable(){
		@Override
		public void run(){
			if(listener!=null){
				listener.InterruptTimeOut();
			}
			//screenShareActivity.showClosePD();
		}
	};
	//取消Runnable
	public void removeAllCallbacks(){
		removeCallbacks(palyDelay);
		removeCallbacks(palyInterrupt);
	};

}

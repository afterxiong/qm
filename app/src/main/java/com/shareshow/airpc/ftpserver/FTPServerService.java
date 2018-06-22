/*
Copyright 2009 David Revell

This file is part of SwiFTP.

SwiFTP is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

SwiFTP is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with SwiFTP.  If not, see <http://www.gnu.org/licenses/>.
*/

package com.shareshow.airpc.ftpserver;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiManager.WifiLock;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.PowerManager;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.util.Log;
import android.widget.RemoteViews;

import com.shareshow.App;
import com.shareshow.aide.R;
import com.shareshow.aide.activity.MoreScreenActivity;
import com.shareshow.airpc.Collocation;
import com.shareshow.aide.activity.MainActivity;
import com.shareshow.airpc.ftpclient.NetSpeed;
import com.shareshow.airpc.model.QMLocalFile;
import com.shareshow.airpc.model.RootPoint;
import com.shareshow.airpc.socket.common.QMDevice;
import com.shareshow.airpc.util.DebugLog;
import com.shareshow.airpc.util.QMFileType;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class FTPServerService extends Service implements Runnable {


	protected static Thread serverThread = null;
	protected boolean shouldExit = false;
	protected MyLog myLog = new MyLog(getClass().getName());
	protected static MyLog staticLog =
		new MyLog(FTPServerService.class.getName());
	
	public static final int BACKLOG = 21;
	public static final int MAX_SESSIONS = 5;
	public static final String WAKE_LOCK_TAG = "SwiFTP";
	
	//protected ServerSocketChannel wifiSocket;
	protected ServerSocket listenSocket;
	protected static WifiLock wifiLock = null;
	
//	protected static InetAddress serverAddress = null;
	
	protected static List<String> sessionMonitor = new ArrayList<String>();
	protected static List<String> serverLog = new ArrayList<String>();
	protected static int uiLogLevel = Defaults.getUiLogLevel();
	
	// The server thread will check this often to look for incoming 
	// connections. We are forced to use non-blocking accept() and polling
	// because we cannot wait forever in accept() if we want to be able
	// to receive an exit signal and cleanly exit.
	public static final int WAKE_INTERVAL_MS = 1000; // milliseconds
	
	protected static int port;
	protected static boolean acceptWifi;
	protected static boolean acceptNet;
	protected static boolean fullWake;
	
	private TcpListener wifiListener = null;
	private List<SessionThread> sessionThreads = new ArrayList<SessionThread>();
	
	private static SharedPreferences settings = null;

	private static final String FILE_NOTIFY="fileNotify";

	public static final String FILE_SEND_FINISH ="fileSendFinish";
	
	NotificationManager notificationMgr = null;
	PowerManager.WakeLock wakeLock;

	private NotificationManager nm;
	private Notification notify;

	private ArrayList<String> files= new ArrayList<String>();
	private ExecutorService threadPool;
	private static NetSpeed speed;
	private NotifiCationReceiver receiver;

	public FTPServerService() {

	}

	public IBinder onBind(Intent intent) {
		// We don't implement this functionality, so ignore it
		return null;
	}
	
	public void onCreate(){
		DebugLog.showLog(this,"开启FTP服务");
		EventBus.getDefault().register(this);
		myLog.l(Log.DEBUG, "SwiFTP server created");
		// Set the application-wide context global, if not already set
		Context myContext = Globals.getContext();
		threadPool = Executors.newFixedThreadPool(5);
		if(myContext == null) {
			myContext = getApplicationContext();
			if(myContext != null) {
				Globals.setContext(myContext);
			}
		}

		initBroadCast();
		initNetSpeed();
		return;
	}

	private void initNetSpeed(){
        if(speed==null){
			speed = new NetSpeed();
		}

	}


	public static int getSendSpeed(){
	  if(speed!=null){
		  return speed.getSpeedInt()[1];
	  }
		return -1;
	}

	public static int getRecvSpeed(){
		if(speed!=null){
			return speed.getSpeedInt()[0];
		}
		return -1;
	}


	private void initBroadCast() {

		receiver = new NotifiCationReceiver();
		IntentFilter intentFilter = new IntentFilter(FILE_NOTIFY);
		registerReceiver(receiver, intentFilter);
	}

	public void onStart(Intent intent, int startId ){
		super.onStart(intent, startId);
		DebugLog.showLog(this,"onStart");
		shouldExit = false;
		int attempts = 10;
		// The previous server thread may still be cleaning up, wait for it
		// to finish.

		while(serverThread != null){
			myLog.l(Log.WARN, "Won't start, server thread exists");
			if(attempts > 0){
				attempts--;
				Util.sleepIgnoreInterupt(1000);
			}else{
				myLog.l(Log.ERROR, "Server thread already exists");
				return;
			}
		}

		myLog.l(Log.DEBUG, "Creating server thread");
		serverThread = new Thread(this);
		serverThread.start();
		
		// todo: we should broadcast an intent to inform anyone who cares
	}
	
	public static boolean isRunning() {
		// return true if and only if a server Thread is running
		if(serverThread == null) {
			staticLog.l(Log.DEBUG, "Server is not running (null serverThread)");
			return false;
		}
		if(!serverThread.isAlive()) {
			staticLog.l(Log.DEBUG, "serverThread non-null but !isAlive()");
		} else {
			staticLog.l(Log.DEBUG, "Server is alive");
		}
		return true;
	}
	
	public void onDestroy(){
		DebugLog.showLog(this,"关闭FTP服务");
		EventBus.getDefault().unregister(this);
		unregisterReceiver(receiver);
		if(speed!=null){
			speed.closeTimer();
			speed=null;
		}
		myLog.l(Log.INFO, "onDestroy() Stopping server");
		shouldExit = true;
		if(serverThread == null) {
			myLog.l(Log.WARN, "Stopping with null serverThread");
			return;
		} else {
			serverThread.interrupt();
			try {
				serverThread.join(10000);  // wait 10 sec for server thread to finish
			} catch (InterruptedException e) {}
			if(serverThread.isAlive()) {
				myLog.l(Log.WARN, "Server thread failed to exit");
				// it may still exit eventually if we just leave the
				// shouldExit flag set
			} else {
				myLog.d("serverThread join()ed ok");
				serverThread = null;
			}
		}
		try {
			if(listenSocket != null) {
				myLog.l(Log.INFO, "Closing listenSocket");
				listenSocket.close();
			}
		} catch (IOException e) {}

		UiUpdater.updateClients();
		if(wifiLock != null) {
			wifiLock.release();
			wifiLock = null;
		}
		clearNotification();
		myLog.d("FTPServerService.onDestroy() finished");
		if(threadPool!=null){
			threadPool.shutdown();
			threadPool=null;
		}
	}
	
	private boolean loadSettings() {
		myLog.l(Log.DEBUG, "Loading settings");
		settings = getSharedPreferences(
				Defaults.getSettingsName(), Defaults.getSettingsMode());
		port = Constans.PORT;
		if(port == 0) {
			// If port number from settings is invalid, use the default
			port = Defaults.portNumber;
		}
		myLog.l(Log.DEBUG, "Using port " + port);
		
		acceptNet = Constans.ACCEPTNET;
		acceptWifi = Constans.ACCEPTWIFI;
		fullWake = Constans.AWAKE;
		
		// The username, password, and chrootDir are just checked for sanity
		String username = Constans.USERNAME;
		String password = Constans.PASSWORD;
		String chrootDir = Constans.CHROOTDIR;
		
		validateBlock: {
			if(username == null || password == null) {
				myLog.l(Log.ERROR, "Username or password is invalid");
				break validateBlock;
			}
			File chrootDirAsFile = new File(chrootDir);
			if (!chrootDirAsFile.exists()) {
				chrootDirAsFile.mkdirs();
			}
			if(!chrootDirAsFile.isDirectory()) {
				myLog.l(Log.ERROR, "Chroot dir is invalid");
				break validateBlock;
			}
			Globals.setChrootDir(chrootDirAsFile);
			Globals.setUsername(username);
			return true;
		}
		// We reach here if the settings were not sane
		return false;
	}
	
	// This opens a listening socket on all interfaces. 
	void setupListener() throws IOException {
		listenSocket = new ServerSocket();
		listenSocket.setReuseAddress(true);
		listenSocket.bind(new InetSocketAddress(port));
	}
	

	
	private void clearNotification() {
		if(notificationMgr == null) {
			// Get NotificationManager reference
			String ns = Context.NOTIFICATION_SERVICE;
			notificationMgr = (NotificationManager) getSystemService(ns);
		}
		notificationMgr.cancelAll();
		myLog.d("Cleared notification");
	}
	
	public void run() {

		UiUpdater.updateClients();
				
		myLog.l(Log.DEBUG, "Server thread running");
		
		// set our members according to user preferences
		if(!loadSettings()) {
			// loadSettings returns false if settings are not sane
			cleanupAndStopService();
			return;
		}
		
		
		// Initialization of wifi
		if(acceptWifi) {
			// If configured to accept connections via wifi, then set up the socket
			try {
				setupListener();
			} catch (IOException e) {
				myLog.l(Log.WARN, "Error opening port, check your network connection.");
//				serverAddress = null;
				cleanupAndStopService();
				return;
			}
			takeWifiLock();
		}		
		takeWakeLock();
		
		myLog.l(Log.INFO, "SwiFTP server ready");
		//setupNotification();

		// We should update the UI now that we have a socket open, so the UI
		// can present the URL
		UiUpdater.updateClients();
		while(!shouldExit) {
			if(acceptWifi) {
				if(wifiListener != null){
					if(!wifiListener.isAlive()){
						myLog.l(Log.DEBUG, "Joining crashed wifiListener thread");
						try {
							wifiListener.join();
						} catch (InterruptedException e) {}
						wifiListener = null;
					}
				}

				if(wifiListener == null) {
					// Either our wifi listener hasn't been created yet, or has crashed,
					// so spawn it
					wifiListener = new TcpListener(listenSocket, this); 
					wifiListener.start();
				}
			}
			try {
				// todo: think about using ServerSocket, and just closing
				// the main socket to send an exit signal
				Thread.sleep(WAKE_INTERVAL_MS);
			} catch(InterruptedException e) {
				myLog.l(Log.DEBUG, "Thread interrupted");
			}
		}
			
		terminateAllSessions();

		if(wifiListener != null) {
			wifiListener.quit();
			wifiListener = null;
		}
		shouldExit = false; // we handled the exit flag, so reset it to acknowledge
		myLog.l(Log.DEBUG, "Exiting cleanly, returning from run()");
		clearNotification();
		releaseWakeLock();
		releaseWifiLock();		
	}
	
	private void terminateAllSessions(){
		myLog.i("Terminating " + sessionThreads.size() + " session thread(s)");
		synchronized(this){
			for(SessionThread sessionThread : sessionThreads) {
				if(sessionThread != null) {
					sessionThread.closeDataSocket();
					sessionThread.closeSocket();
				}
			}
		}
	}
	
	public void cleanupAndStopService() {
		// Call the Android Service shutdown function
		Context context = getApplicationContext();
		Intent intent = new Intent(context,	FTPServerService.class);
		context.stopService(intent);
		releaseWifiLock();
		releaseWakeLock();
		clearNotification();
	}
	
	private void takeWakeLock() {
		if(wakeLock == null) {
			PowerManager pm = (PowerManager)getSystemService(Context.POWER_SERVICE);
			
			// Many (all?) devices seem to not properly honor a PARTIAL_WAKE_LOCK,
			// which should prevent CPU throttling. This has been 
			// well-complained-about on android-developers.
			// For these devices, we have a config option to force the phone into a 
			// full wake lock.
			if(fullWake) {
				wakeLock = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK,
						WAKE_LOCK_TAG);
			} else {
				wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
						WAKE_LOCK_TAG);
			}
			wakeLock.setReferenceCounted(false);
		}
		myLog.d("Acquiring wake lock");
		wakeLock.acquire();
	}
	
	private void releaseWakeLock() {
		myLog.d("Releasing wake lock");
		if(wakeLock != null) {
			wakeLock.release();
			wakeLock = null;
			myLog.d("Finished releasing wake lock");
		} else {
			myLog.i("Couldn't release null wake lock");
		}
	}
	
	private void takeWifiLock() {
		myLog.d("Taking wifi lock");
		if(wifiLock == null) {
			WifiManager manager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
			wifiLock = manager.createWifiLock("SwiFTP");
			wifiLock.setReferenceCounted(false);
		}
		wifiLock.acquire();
	}
	
	private void releaseWifiLock(){
		myLog.d("Releasing wifi lock");
		if(wifiLock != null) {
			wifiLock.release();
			wifiLock = null;
		}
	}
	
	public void errorShutdown() {
		myLog.l(Log.ERROR, "Service errorShutdown() called");
		cleanupAndStopService();
	}

	/**
	 * Gets the IP address of the wifi connection.
	 * @return The integer IP address if wifi enabled, or null if not.
	 */
	public static InetAddress getWifiIp() {
		Context myContext = Globals.getContext();
		if(myContext == null) {
			throw new NullPointerException("Global context is null");
		}
		WifiManager wifiMgr = (WifiManager) App.getApp().getApplicationContext().getSystemService(Context.WIFI_SERVICE);
		if(isWifiEnabled()) {
			int ipAsInt = wifiMgr.getConnectionInfo().getIpAddress();
			if(ipAsInt == 0) {
				return null;
			} else {
				return Util.intToInet(ipAsInt);
			}
		} else {
			return null;
		}
	}
	
	public static boolean isWifiEnabled(){
		Context myContext = Globals.getContext();
		if(myContext == null) {
			throw new NullPointerException("Global context is null");
		}
		WifiManager wifiMgr = (WifiManager)App.getApp().getApplicationContext()
		                        .getSystemService(Context.WIFI_SERVICE);
		if(wifiMgr.getWifiState() == WifiManager.WIFI_STATE_ENABLED) {
			return true;
		} else {
			return false;
		}
	}
	
	public static List<String> getSessionMonitorContents() {
		return new ArrayList<String>(sessionMonitor);
	}
	
	public static List<String> getServerLogContents() {
		return new ArrayList<String>(serverLog);
	}
	
	public static void log(int msgLevel, String s) {
		serverLog.add(s);
		int maxSize = Defaults.getServerLogScrollBack();
		while(serverLog.size() > maxSize) {
			serverLog.remove(0);
		}
		//updateClients();
	}
	
	public static void updateClients() {
		UiUpdater.updateClients();
	}
	
	public static void writeMonitor(boolean incoming, String s) {}
//	public static void writeMonitor(boolean incoming, String s) {
//		if(incoming) {
//			s = "> " + s;
//		} else {
//			s = "< " + s;
//		}
//		sessionMonitor.add(s.trim());
//		int maxSize = Defaults.getSessionMonitorScrollBack();
//		while(sessionMonitor.size() > maxSize) {
//			sessionMonitor.remove(0);
//		}
//		updateClients();
//	}

	public static int getPort() {
		return port;
	}

	public static void setPort(int port) {
		FTPServerService.port = port;
	}

	/**
	 * The FTPServerService must know about all running session threads so they
	 * can be terminated on exit. Called when a new session is created.
	 */
	public void registerSessionThread(SessionThread newSession) {
		// Before adding the new session thread, clean up any finished session
		// threads that are present in the list.
		
		// Since we're not allowed to modify the list while iterating over
		// it, we construct a list in toBeRemoved of threads to remove
		// later from the sessionThreads list.
		synchronized(this) {
			List<SessionThread> toBeRemoved = new ArrayList<SessionThread>();
			for(SessionThread sessionThread : sessionThreads) {
				if(!sessionThread.isAlive()) {
					myLog.l(Log.DEBUG, "Cleaning up finished session...");
					try {
						sessionThread.join();
						myLog.l(Log.DEBUG, "Thread joined");
						toBeRemoved.add(sessionThread);
						sessionThread.closeSocket(); // make sure socket closed
					} catch (InterruptedException e) {
						myLog.l(Log.DEBUG, "Interrupted while joining");
						// We will try again in the next loop iteration
					}
				}
			}
			for(SessionThread removeThread : toBeRemoved) {
				sessionThreads.remove(removeThread);
			}
			
			// Cleanup is complete. Now actually add the new thread to the list.
			sessionThreads.add(newSession);
		}
		myLog.d("Registered session thread");
	}

	static public SharedPreferences getSettings() {
		return settings;
	}


	@Subscribe
	 public void onEventMainThread(FtpEvent event){
	//	String friendName=null;
		String friendAddress=null;
		 String msg=event.getMsg();
		 if(msg!=null){
			 String str[]=msg.split("&&");
			 if(str[0].equals("STOR")){
				 files.add(0,str[1]);
				 DebugLog.showLog(this,"STOR:"+str[1]);
			 }else if(str[0].equals("FINISH")){
				// friendName=str[1].split("=")[1];
				 friendAddress=str[1].split("=")[1];
//				 if(friendAddress.equalsIgnoreCase("pc")){
//					 friendAddress=str[3].split("=")[1];
//				 }
				 DebugLog.showLog(this,"friendAddress:"+friendAddress);

				 if(TextUtils.isEmpty(friendAddress)){
					 return;
				 }

				 DebugLog.showLog(this,"files:"+files.toString());

                 if(files.size()==0){
					 return;
				 }

				 if(isMain()){
					 //直接向activity发送消息
						 Intent intent= new Intent(FILE_SEND_FINISH);
						 intent.putStringArrayListExtra("FILES",files);
						 // intent.putExtra("friendName",friendName);
						 intent.putExtra("friendAddress",friendAddress);
						 sendBroadcast(intent);
					     DebugLog.showLog(this,"发送广播!");
				 }else{
                   //通过打开activity
					     RootPoint root= getRootRecieveFile(friendAddress);
					     getDownLoadFile(files,root);
				 }

				  files.clear();
			 }
		 }
	 }

	private RootPoint getRootRecieveFile(String friendAddress){
		ArrayList<RootPoint> roots= QMDevice.getInstance().getAl();
		DebugLog.showLog(this,"收到address："+friendAddress);
		for (RootPoint root : roots) {
			if(root.getAddress().equals(friendAddress)){
				root.setIsfileRecieve(true);
				return root;
			}
		}
		return  null;
	}


	   private boolean isMain(){

		    ActivityManager mActivityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
			List<ActivityManager.RunningTaskInfo> rti = mActivityManager.getRunningTasks(1);
		    return MoreScreenActivity.class.getName().equals(rti.get(0).topActivity.getClassName());

	 }


		private void openNotification(RootPoint info){

			nm= (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
			NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this);
			RemoteViews mRemoteViews = new RemoteViews(getPackageName(), R.layout.file_notify);
			mRemoteViews.setImageViewResource(R.id.notify_image_view, R.mipmap.title_icon);
			// API3.0 以上的时候显示按钮，否则消失
			mRemoteViews.setTextViewText(R.id.notify_button_file, "好友"+info.getName()+"给你发来了"+ info.getFiles().size()+"个文件");

			// 点击通知栏【停止】按钮的广播进行注册
			Intent buttonIntent = new Intent(FILE_NOTIFY);
			buttonIntent.putExtra("root",info);
			// 上一首按钮
			PendingIntent intent_paly = PendingIntent.getBroadcast(this, 1, buttonIntent, PendingIntent.FLAG_UPDATE_CURRENT);
			mRemoteViews.setOnClickPendingIntent(R.id.notify_button_random, intent_paly);
			Intent intt = new Intent(this, MoreScreenActivity.class);
			intt.putExtra("page",3);
			intt.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
//			TaskStackBuilder taskStackBuilder=TaskStackBuilder.create(this);
//			taskStackBuilder.addNextIntent(intt);
		//	PendingIntent pi = taskStackBuilder.getPendingIntent(1,PendingIntent.FLAG_UPDATE_CURRENT);
			PendingIntent pi = PendingIntent.getActivity(this, 1, intt, PendingIntent.FLAG_UPDATE_CURRENT);
			mBuilder.setContent(mRemoteViews).setContentIntent(pi).setTicker("您收到好友"+info.getName()+"发过来"+ info.getFiles().size()+"个文件").setWhen(System.currentTimeMillis())
					// 通知产生的时间，会在通知信息里显示
					.setPriority(Notification.PRIORITY_DEFAULT)// 设置该通知优先级
					.setSmallIcon(R.mipmap.title_icon);
			notify = mBuilder.build();
			notify.flags = Notification.FLAG_NO_CLEAR;
			notify.defaults |= Notification.DEFAULT_SOUND;
			nm.notify(1001, notify);

			//TODO 这个地方要改一下
			threadPool.submit(new Runnable(){
				@Override
				public void run(){
					boolean isOpenMain=true;
					while (isOpenMain){
						if(isMain()){
							isOpenMain=false;
							if(nm!=null){
								nm.cancel(1001);
							}
							try {
								Thread.sleep(300);
							} catch (InterruptedException e){
								e.printStackTrace();
							}
						}
					}
				}
			});
		}




	private class NotifiCationReceiver  extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent){

			 if(intent.getAction().equals(FILE_NOTIFY)){
				 if(nm!=null){
					 nm.cancel(1001);
				 }

				 Intent mainIntent= new Intent(FTPServerService.this,MoreScreenActivity.class);
				 mainIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				 startActivity(mainIntent);

			//	 RootPoint point= (RootPoint) intent.getSerializableExtra("root");
			//	 DebugLog.showLog(this,"root:"+point);
//				 getDownLoadFile(Collocation.getCollocation().getFileList());

				// DebugLog.showLog(this,point.getFiles().size()+"");


//				 ArrayList<String> files=Collocation.getCollocation().getFileList();
//				 files.remove(0);
//				 Collocation.getCollocation().saveFileList(files);

			 }
		}
	}

    private ArrayList<QMLocalFile> downLoadFiles =new ArrayList<QMLocalFile>();

	private void getDownLoadFile( ArrayList<String> fileNames){
		downLoadFiles.clear();
		File root=new File(QMFileType.LOCALPATH);
		File[] files=root.listFiles();
		for (File file:files) {
			if(file.isDirectory()){
				continue;
			}else{
				String name = file.getName();
				for (String fileName : fileNames){
					if(name.equals(fileName)){
						long size=file.length();
						if(size==0)
							continue;
						int type=QMFileType.getType(file.getName());
						String path = file.getPath();
						long update=file.lastModified();
						QMLocalFile localFile=new QMLocalFile(name,path, size, update, type);
						downLoadFiles.add(localFile);
					}
				}
			}
		}
		DebugLog.showLog(this,downLoadFiles.size()+"size");
	}

	/**
	 * 加载从任盒或手机中下载的文件
	 */
	private void getDownLoadFile(ArrayList<String> fileNames, RootPoint point){
		downLoadFiles=new ArrayList<QMLocalFile>();
		File root=new File(QMFileType.LOCALPATH);
		File[] files=root.listFiles();
		for (File file:files){
			if(file.isDirectory()){
				continue;
			}else{
				String name = file.getName();
				for (String fileName : fileNames){
					if(name.equals(fileName)){
						long size=file.length();
						if(size==0)
							continue;
						int type=QMFileType.getType(file.getName());
						String path = file.getPath();
						long update=file.lastModified();
						QMLocalFile localFile=new QMLocalFile(name,path, size, update, type);
						downLoadFiles.add(localFile);
					}
				}
			}
		}
		DebugLog.showLog(this,"downLoadFile:"+downLoadFiles.toString());
		Message msg= new Message();
		msg.obj=point;
		msg.what=0x001;
		Bundle bundle=new Bundle();
		bundle.putSerializable("down",downLoadFiles);
		msg.setData(bundle);
		handler.sendMessage(msg);
	}

	private Handler handler = new Handler(Looper.getMainLooper()){

		@Override
		public void dispatchMessage(Message msg){
			super.dispatchMessage(msg);
			switch (msg.what){
				case 0x001:
					RootPoint point= (RootPoint) msg.obj;
					ArrayList<QMLocalFile> downLoadFiles= (ArrayList<QMLocalFile>) msg.getData().getSerializable("down");
					if(downLoadFiles==null||downLoadFiles.size()==0){
						return;
					}

					if(point!=null){

						  if(point.getFiles()==null){
							     point.setFiles(new ArrayList<QMLocalFile>());
						   }
						//else{
							for(QMLocalFile downLoadFile : downLoadFiles){
								 point.getFiles().add(0,downLoadFile);
							 }
						//}

							openNotification(point);
//					 Collocation.getCollocation().saveFileList(files);
//					 Collocation.getCollocation().savaFileFriendName(friendName);
							Collocation.getCollocation().savaFileFriendAddress(point.getAddress());
					 }
					break;
			}
		}
	};


}

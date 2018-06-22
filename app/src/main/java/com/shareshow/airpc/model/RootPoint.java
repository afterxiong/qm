package com.shareshow.airpc.model;

import android.os.Handler;

import com.shareshow.airpc.QMCommander;
import com.shareshow.airpc.ports.PositionListener;
import com.shareshow.airpc.socket.command.CommandExecutorLancher;
import com.shareshow.airpc.socket.command.CommandExecutorLancherFile;

import java.io.Serializable;
import java.util.ArrayList;

public class RootPoint implements Serializable {

	private String address;//盒子的地址，若盒子那边由无线切换到热点此地址不变
	private boolean canShare=true;//手机是否是投屏端
	private int category = -1;//返回的对象是属于发送的那个指令
	private int dType=-1;//-1 任盒         0 Android     1 IOS       2  PC
	private String enablecontrol;//盒子能否进行远程控制
	private String enablepwd="false"; //盒子那边是否设置了密码
	private int heartbeat=0;//投屏心跳检测
	private int heartbeat2=0;//心跳检测2
	private String identity="";//手机或平板的唯一标识
	private boolean isClient;//手机是否是投屏端
	private boolean isNew=true;
	private boolean isOnline=true;//盒子是否在线
	private boolean isPlay=false;//盒子有没有勾选上
	private boolean isResponse;//每个请求有没有正确响应，正确响应后才能继续解析XML
	private String isrunning="";//屏幕投屏有没有打开
	private boolean isRunning=true;//文件共享之前先判断文件共享的软件是否正在运行，像20002端口发
	private boolean isTouPing;//盒子是否在进行投屏
	private LC lc;
	private int Lcount=0;
	private String linkstate="false";//发送的密码验证是否对错
	private long memory;//内存大小
	private MyHandler mHandler=new MyHandler();
	private String mutilcast="0";//PC 开关   1表示组播 0 表示单播
	private boolean isMac;
	private boolean isShareScreen;

	private boolean isShareing; //正在分享

	private int shareHeart;

	public int getShareHeart() {
		return shareHeart;
	}

	public void setShareHeart(int shareHeart) {
		this.shareHeart = shareHeart;
	}

	public boolean isShareing() {
		return isShareing;
	}

	public void setShareing(boolean shareing) {
		isShareing = shareing;
	}

	public boolean isShareScreen() {
		return isShareScreen;
	}

	public void setShareScreen(boolean shareScreen) {
		isShareScreen = shareScreen;
	}

	public boolean isMac() {
		return isMac;
	}

	public void setMac(boolean mac) {
		isMac = mac;
	}

	private String mutilcastAddress="";//表示组播地址

	private String name;//盒子名称

	private int orientation=2;//moren  shuping
	
	private String password="";//盒子的密码

	private String playurl="";//分享时播放的地址

	private boolean progress=false;//验证密码时进度条显示下
	
	private boolean stopByHandle=true;//true默认  false表示手动停止了投屏操作,这样在投屏心跳中收到消息就不用处理

	private TP tp;

	private String uuid="";//用于区分盒子的唯一地址

	private boolean isfileRecieve;

	private String host;//对方的ip

	private String channel;

	public String getChannel() {
		return channel;
	}

	public void setChannel(String channel) {
		this.channel = channel;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	private ArrayList<QMLocalFile> files;

	private String xmlMessage;

	public String getXmlMessage() {
		return xmlMessage;
	}

	public void setXmlMessage(String xmlMessage) {
		this.xmlMessage = xmlMessage;
	}

	private boolean isPC;


	public boolean isPC() {
		return isPC;
	}

	public void setPC(boolean PC) {
		isPC = PC;
	}

	public ArrayList<QMLocalFile> getFiles() {
		return files;
	}

	public void setFiles(ArrayList<QMLocalFile> files) {
		this.files = files;
	}

	public boolean isfileRecieve() {
		return isfileRecieve;
	}



	public void setIsfileRecieve(boolean isfileRecieve) {
		this.isfileRecieve = isfileRecieve;
	}


	public RootPoint() {

	}


	public String getAddress() {
		return address;
	}

	public int getCategory() {
		return category;
	}


	public int getdType() {
		return dType;
	}

	public String getEnablecontrol() {
		return enablecontrol;
	}

	public String getEnablepwd() {
		return enablepwd;
	}
	
	public int getHeartbeat() {
		return heartbeat;
	}

	public int getHeartbeat2() {
		return heartbeat2;
	}

	public String getIdentity() {
		return identity;
	}

	public String getIsrunning() {
		return isrunning;
	}

	public String getLinkstate() {
		return linkstate;
	}

	public long getMemory() {
		return memory;
	}

	public String getMutilcast() {
		return mutilcast;
	}

	public String getMutilcastAddress() {
		return mutilcastAddress;
	}

	public String getName() {
		return name;
	}

	public int getOrientation() {
		return orientation;
	}

	public String getPassword() {
		return password;
	}

	public String getPlayurl() {
		return playurl;
	}

	public String getUuid() {
		return uuid;
	}

	public boolean isCanShare() {
		return canShare;
	}

	public boolean isClient() {
		return isClient;
	}

	public boolean isNew() {
		return isNew;
	}

	public boolean isOnline() {
		return isOnline;
	}


	

	public boolean isPlay() {
		return isPlay;
	}
	
	public boolean isProgress() {
		return progress;
	}

	public boolean isResponse() {
		return isResponse;
	}

	public boolean isRunning() {
		return isRunning;
	}


	public boolean isStopByHandle() {
		return stopByHandle;
	}

	public boolean isTouPing() {
		return isTouPing;
	}
	



	public void setAddress(String address) {
		this.address = address;
	}

	public void setCanShare(boolean canShare) {
		this.canShare = canShare;
	}




	public void setCategory(int category) {
		this.category = category;
	}

	public void setClient(boolean isClient) {
		this.isClient = isClient;
	}

	public void setdType(int dType) {
		this.dType = dType;
	}



	public void setEnablecontrol(String enablecontrol) {
		this.enablecontrol = enablecontrol;
	}



	public void setEnablepwd(String enablepwd) {
		this.enablepwd = enablepwd;
	}

	public void setHeartbeat(int heartbeat) {
		this.heartbeat = heartbeat;
	}

	public void setHeartbeat2(int heartbeat2) {
		this.heartbeat2 = heartbeat2;
	}

	public void setIdentity(String identity) {
		this.identity = identity;
	}

	public void setIsrunning(String isrunning) {
		this.isrunning = isrunning;
	}

	//0    -1
	public void setLcount(int lcount) {
		this.Lcount=lcount;
		switch (lcount) {
		case 0:
			lc=new LC();
			mHandler.post(lc);
			break;

		case -1:
			CommandExecutorLancherFile.getOnlyExecutor().connectMessage(
					QMCommander.TYPE_UNCONNECT,getAddress(),
					"");
			CommandExecutorLancher.getOnlyExecutor().connectMessage(
					QMCommander.TYPE_UNCONNECT,getAddress(),
					"");
			
			if(lc!=null){
				mHandler.removeCallbacks(lc);
				lc=null;
			}
			break;
		case -2:
			if(lc!=null){
				mHandler.removeCallbacks(lc);
				lc=null;
			}
			break;
		}
	}

	public void setLinkstate(String linkstate){
		this.linkstate = linkstate;
	}


	public void setMemory(long memory) {
		this.memory = memory;
	}

	public void setMutilcast(String mutilcast){
		this.mutilcast = mutilcast;
	}

	public void setMutilcastAddress(String mutilcastAddress){
		this.mutilcastAddress = mutilcastAddress;
	}

	public void setName(String name){
		this.name = name;
	}

	public void setNew(boolean isNew){
		this.isNew = isNew;
	}

	public void setOnline(boolean isOnline){
		this.isOnline = isOnline;
	}

	public void setOrientation(int orientation) {
		this.orientation = orientation;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	
	
	public void setPlay(boolean isPlay) {
		this.isPlay = isPlay;
	}
	
	public void setPlayurl(String playurl) {
		this.playurl = playurl;
	}
	public void setProgress(boolean progress) {
		this.progress = progress;
	}
	
	public void setResponse(boolean isResponse) {
		this.isResponse = isResponse;
		if(tp!=null&&isResponse){
			mHandler.removeCallbacks(tp);//此处没有移除的话在对象传递过程中会有一异常
			tp=null;
		}
	}

	public void setRunning(boolean isRunning) {
		this.isRunning = isRunning;
	}
	
	
	public void setStopByHandle(boolean stopByHandle) {
		this.stopByHandle = stopByHandle;
	}

	public void setTouPing(boolean isTouPing) {
		this.isTouPing = isTouPing;
	}
	
	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public void startHandler(final PositionListener cPosition){
		tp=new TP(cPosition);
		mHandler.postDelayed(tp, 15*1000);
	}


	private class LC implements Runnable,Serializable {
		
		@Override
		public void run(){
			if(Lcount>3){
				setLcount(-2);
				return ;
			}

			CommandExecutorLancherFile.getOnlyExecutor().connectMessage(
					QMCommander.TYPE_CONNECT,getAddress(),
					"");
			mHandler.postDelayed(this, 1000);
			Lcount++;
		}
		
	}

	private class MyHandler extends Handler implements Serializable {
		
	}
	private class TP implements Runnable,Serializable {
		
		private PositionListener cPosition;
		
		public TP() {
			super();
		}
		
		public TP(PositionListener cPosition) {
			super();
			this.cPosition = cPosition;
		}

		@Override
		public void run(){
			if(!isResponse&&cPosition!=null)
				cPosition.method(0);
			    setResponse(true);
		}
		
	}
}

package com.shareshow.airpc.socket.command;

import android.app.Activity;
import android.util.DisplayMetrics;

import com.shareshow.App;
import com.shareshow.aide.activity.MoreScreenActivity;
import com.shareshow.aide.util.uuid.CacheHelper;
import com.shareshow.aide.util.uuid.Devices;
import com.shareshow.airpc.Collocation;
import com.shareshow.airpc.QMCommander;
import com.shareshow.airpc.model.RootPoint;
import com.shareshow.airpc.ports.BoxOnClickListener;
import com.shareshow.airpc.socket.common.QMDevice;
import com.shareshow.airpc.util.NetworkUtils;
import com.shareshow.airpc.util.QMUtil;

import org.xmlpull.v1.XmlPullParser;

import java.net.DatagramSocket;


/**
 * 2004端口监听的类,与手机交互的
 * @author tanwei
 *
 */
public class CommandExectuorMobile extends CommandExecutor {
	
	private CommandListenerMobile listener;
	private static CommandExectuorMobile executor;
	private String uuid="";
	private int width;
	private int heigh;
	private String type;
	
	//单例exectuor
	public synchronized static CommandExectuorMobile getOnlyExecutor() {
		if (executor == null) {
			executor = new CommandExectuorMobile();
		}
		return executor;
	}
	
	private CommandExectuorMobile(){
		super();
		initData();
		ReceiveCommand();
	}
	
	private void initData(){
		uuid = Devices.getInstace(new CacheHelper()).getKey();
				//QMUtil.getInstance().getUUID(App.getApp());
		type = QMUtil.getInstance().getMobileType(App.getApp());
		try {
			if (datagramSocket == null){
				datagramSocket = new DatagramSocket(QMCommander.PORT_Mobile);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	//设置监听
	public CommandExectuorMobile setListener(CommandListenerMobile listener) {
		this.listener = listener;
		return executor;
	}
	
	public void setWH(Activity context){
		DisplayMetrics dm = new DisplayMetrics();
		context.getWindowManager().getDefaultDisplay().getMetrics(dm);
		width = dm.widthPixels;
		heigh = dm.heightPixels;
	}
	
	/**
	 * 搜索设备
	 * @param category
	 */
	public void searchMessage(int category){
		String ip= NetworkUtils.getNetworkIP();
		StringBuilder sb = new StringBuilder();
		sb.append("name='"+ Collocation.getCollocation().getDeviceName()+"' address='"+ip + "' uuid='"+uuid+"'"+" type='android'"+" devicetype='android'" +" version='new'");
		if(("192.168.43.1").equals(ip))
			SendRequestCommand(category,"192.168.43.255", sb.toString());
		else
			SendRequestCommand(category,"255.255.255.255", sb.toString());
	}

	/**
	 * huifu   搜索设备
	 * @param category
	 */
	public void sendsearchMessage(int category){
		String ip=NetworkUtils.getNetworkIP();
		StringBuilder sb = new StringBuilder();
		sb.append("name='"+Collocation.getCollocation().getDeviceName()+"' address='"+ip + "' uuid='"+uuid+"'"+" type='android'"+" devicetype='android'"+" version='new'");
		if(ip.equals("192.168.43.1"))
			SendResponseCommand(category,"192.168.43.255", sb.toString());
		else
			SendResponseCommand(category,"255.255.255.255", sb.toString());
	}
	/**
	 * huifu   搜索设备
	 * @param category
	 */
	public void sendsearchMessage(int category,String host){
		String ip=NetworkUtils.getNetworkIP();
		StringBuilder sb = new StringBuilder();
		sb.append("name='"+Collocation.getCollocation().getDeviceName()+"' address='"+ip + "' uuid='"+uuid+"'"+" type='android'"+" devicetype='android'"+" host='"+host+"'"+" version='new'");
		if(ip.equals("192.168.43.1"))
			SendResponseCommand(category,"192.168.43.255", sb.toString());
		else
			SendResponseCommand(category,"255.255.255.255", sb.toString());
	}
	
	
	/**
	 * huifu 连接设备
	 * @param category
	 * 发送连接或者断开设备的信息，里面的指令的参数是看其是否是连接还是断开
	 */
	public void sendconnectMessage(int category,String host){
		StringBuilder sb = new StringBuilder();
		sb.append("name='"+Collocation.getCollocation().getDeviceName()+"'  address='"+ NetworkUtils.getNetworkIP() + "' uuid='1'");
		SendResponseCommand(category,host, sb.toString());
	}
	/**
	 * 连接设备,进行投屏
	 * @param category
	 * 发送连接或者断开设备的信息，里面的指令的参数是看其是否是连接还是断开
	 */
	public void connectMessage(int category,String host){
	//	Log.i("test","host"+host);
		StringBuilder sb = new StringBuilder();
		sb.append("name='"+Collocation.getCollocation().getDeviceName()+"'  address='"+ NetworkUtils.getNetworkIP()+"' width= '" +width+"' height='"+heigh+ "' uuid='1'"+" type='"+type+"' host='"+host+"'");
		SendRequestCommand(category,host,sb.toString());
	}

	public void connectMessage(int category, String host, boolean isPc){
		//	Log.i("test","host"+host);
		StringBuilder sb = new StringBuilder();
		sb.append("name='"+Collocation.getCollocation().getDeviceName()+"'  address='"+ NetworkUtils.getNetworkIP()+"' width= '" +width+"' height='"+heigh+ "' uuid='1'"+" type='"+type+"' host='"+host+"' isPC='"+isPc+"'");
		SendRequestCommand(category,host,sb.toString());
	}


	public void shareScreenMessage(int category,String host){
		StringBuilder sb = new StringBuilder();
		sb.append("name='"+Collocation.getCollocation().getDeviceName()+"'  address='"+ NetworkUtils.getNetworkIP()+"' width= '" +width+"' height='"+heigh+ "' uuid='1'"+" type='"+type+"' host='"+host+"'");
		SendRequestCommand(category,host,sb.toString());
	}
	
	/**
	 * 连接设备进行分享
	 * @param category
	 */
	public void controlMessage(int category, String host, String msg){
		StringBuilder sb = new StringBuilder();
		sb.append("name='"+Collocation.getCollocation().getDeviceName()+"'address='"+ NetworkUtils.getNetworkIP() + "' uuid='1' ");
		if(msg!=null)
			sb.append(" msg='"+msg+"' ");	
		SendRequestCommand(category,host, sb.toString());
	}

   /*
    *文件共享的心跳
    */
	public void heartBeatMessage(int category,String host){
		StringBuilder sb = new StringBuilder();
		sb.append("name='"+Collocation.getCollocation().getDeviceName()+"' devicetype='"+type+"'  address='"+ NetworkUtils.getNetworkIP() + "'  ");
		SendRequestCommand(category,host, sb.toString());
	}

   /*
    *
    */

	public void repsonsheartBeatMessage(int category,String host){
		StringBuilder sb = new StringBuilder();
		sb.append("name='"+Collocation.getCollocation().getDeviceName()+"' devicetype='"+type+"'  address='"+ NetworkUtils.getNetworkIP() + "'  ");
		SendResponseCommand(category,host,"return file heart");
	}


	private void SendRequestCommand(int category, String host, String message){
		SendRequestCommand(category,host, message,QMCommander.PORT_Mobile);
	}
	
	private void SendResponseCommand(int category, String host, String message){
		SendResponseCommand(category,host, message,QMCommander.PORT_Mobile);
	}

	@Override
	public void startAnalysis(XmlPullParser xmlPullParser, RootPoint rp) {
		String name = xmlPullParser.getName();
		switch (name) {
		case QMCommander.XML.CLIENT:
			rp.setName(xmlPullParser.getAttributeValue(null, "name"));
			rp.setUuid(xmlPullParser.getAttributeValue(null, "uuid"));
			rp.setdType(0);
			if(rp.getCategory()==QMCommander.TYPE_SCREEN_M){
				String info = xmlPullParser.getAttributeValue(null, "devicetype");
				String isPC = xmlPullParser.getAttributeValue(null, "isPC");
				if (isPC != null) {
					rp.setPC(true);
				}

				if (info.equals("ios")){
					rp.setdType(1);
			    }else {
				    rp.setdType(0);
			    }

			}
			if(rp.getCategory()==QMCommander.TYPE_SEARCH_M){
				String info=xmlPullParser.getAttributeValue(null, "type");
				if(xmlPullParser.getAttributeValue(null, "version")==null)
					rp.setNew(false);
				if(info.equals("ios"))
					rp.setdType(1);
				else
					rp.setdType(0);
			}

			break;
		case QMCommander.XML.SEVER:
			rp.setName(xmlPullParser.getAttributeValue(null, "name"));
			rp.setUuid(xmlPullParser.getAttributeValue(null, "uuid"));
			rp.setdType(0);
			if(rp.getCategory()==QMCommander.TYPE_SCREEN_M){
				String info=xmlPullParser.getAttributeValue(null, "devicetype");
				if(info.equals("ios"))
					rp.setdType(1);
				else
					rp.setdType(0);
			}
			if(rp.getCategory()==QMCommander.TYPE_SEARCH_M){
			String info=xmlPullParser.getAttributeValue(null, "type");
			if(xmlPullParser.getAttributeValue(null, "version")==null)//version
				rp.setNew(false);
				if(info.equals("ios"))
					rp.setdType(1);
				else
					rp.setdType(0);
			}

			if(rp.getCategory()==QMCommander.TYPE_SEARCH_SCAN){
				String host= xmlPullParser.getAttributeValue(null,"host");
//				Log.i("test","host:"+host);
//				Log.i("test","category:"+rp.getCategory());
				rp.setHost(host);
		 	}
			break;					
		default:
			break;
		}
	}

  //接收到搜索指令后的处理
	@Override
	public void listenerCallBack(RootPoint rp){
		int category = rp.getCategory();
		switch (category) {
		case  QMCommander.TYPE_SEARCH_M:
				if(rp.getUuid()==null||rp.getUuid().equals("")||rp.getUuid().equals("null")||rp.getUuid().equals(uuid))
					return ;//没有uuid唯一标示的过滤，是自己手机设备过滤
				if(listener!=null)
					listener.searchMobile(rp);
				if(rp.isClient()){
					//把自身的ip进行封装发送出去，反之让对方可以接收到
					if(Collocation.getCollocation().getIsScan()){
						sendsearchMessage(QMCommander.TYPE_SEARCH_SCAN);
					}else {
						sendsearchMessage(category);
					}
				}
			break;
		case QMCommander.TYPE_SEARCH_SCAN:
			//收到扫描搜索的指令
			if(rp.getUuid()==null||rp.getUuid().equals("")||rp.getUuid().equals("null")||rp.getUuid().equals(uuid))
				return ;//没有uuid唯一标示的过滤，是自己手机设备过滤
			//一个是被扫描添加，一个是扫描添加别人，怎么去的ip进行匹配
//			if(!MainActivity.isScan){
//			MainActivity.netIp=rp.getAddress();
//			}
			//TODO 不知道有何影响
			if(rp.isClient()){//把自身的ip进行封装发送出去，反之让对方可以接收到
				sendsearchMessage(category);
		    }

			if(MoreScreenActivity.isScan){
			//	Log.i("test","回发101001指令...");
				sendsearchMessage(category);
			}

			if(listener!=null)
				listener.searchMobile(rp);

			break;
		case QMCommander.TYPE_SCREEN_M:
			//调用的是投屏的方法
			if(listener!=null){
				listener.screenMobile(rp);
			}
			break;
		case QMCommander.TYPE_STOP_M:
				//在这里可以收到断开投屏连接的指令
			if(listener!=null)
				listener.screenInterruptMobile(rp);
			break;
		case QMCommander.TYPE_SHARE_STOP_M:
			if(listener!=null)
				listener.screenInterruptMobile(rp);
			break;
		//手机间的心跳的检测
		case QMCommander.TYPE_HEARTBEAT_M:
			if (listener!=null) 
			listener.heartBeatMobile(rp);
			if(!rp.isClient()){
				QMDevice.getInstance().oprationSameDevice(rp, new BoxOnClickListener() {
					
					@Override
					public void onClick(RootPoint rootPoint) {
						rootPoint.setHeartbeat(0);
						rootPoint.setTouPing(true);
						rootPoint.setPlay(true);
					}
				    });
				}
			break;
		case QMCommander.TYPE_COVER_M:
			if(listener!=null)
				listener.screenInterruptMobile(rp);
			break;
		case QMCommander.TYPE_FORBIDDEN_M:
			if(listener!=null)
				listener.screenInterruptMobile(rp);
			break;
		case QMCommander.TYPE_NEED_SHARED:
			if (listener!=null) {
				listener.screenMobile(rp);
			}
			break;
		  case QMCommander.TYPE_REFUSED_M:
		  case QMCommander.TYPE_NO_SCREEN_M:
			if (listener!=null) {
				listener.screenMobile(rp);
			}
			break;

		  case QMCommander.TYPE_MOBILE_FILE_ONLINE:
			if(rp.isClient()){
				repsonsheartBeatMessage(QMCommander.TYPE_MOBILE_FILE_ONLINE,rp.getAddress());
			}else{
				QMDevice.getInstance().oprationSameDevice(rp, new BoxOnClickListener(){
					@Override
					public void onClick(RootPoint rootPoint){
						rootPoint.setHeartbeat2(0);
					}
				});
			}
			break;
			case QMCommander.TYPE_STOP_SHARE_SCREEN:
				if(listener!=null){
					listener.screenInterruptMobile(rp);
				}
				break;

			case QMCommander.TYPE_COVER_SCREEN_SHARE:
				//切换视频源

				if(listener!=null){
					listener.swichMobileScreen(rp);
				}
				break;

			case QMCommander.TYPE_SCREEN_SCREEN_SUCCESS:
				//分享成功
				if(listener!=null){
					listener.screenMobile(rp);
				}
				break;
			case QMCommander.TYPE_SCREEN_REQUEST_FRAME:
				if(listener!=null){
					listener.requestScreenFrame();
				}
				break;
		
		default:
			break;
		}
	}
	
}

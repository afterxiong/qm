package com.shareshow.airpc.socket.command;

import android.app.Activity;
import android.util.DisplayMetrics;

import com.shareshow.App;
import com.shareshow.aide.util.uuid.CacheHelper;
import com.shareshow.aide.util.uuid.Devices;
import com.shareshow.airpc.Collocation;
import com.shareshow.airpc.QMCommander;
import com.shareshow.airpc.model.RootPoint;
import com.shareshow.airpc.ports.BoxOnClickListener;
import com.shareshow.airpc.socket.common.QMDevice;
import com.shareshow.airpc.util.DebugLog;
import com.shareshow.airpc.util.NetworkUtils;
import com.shareshow.airpc.util.QMUtil;

import org.xmlpull.v1.XmlPullParser;

import java.net.DatagramSocket;

//20002端口监听的类,与lancher交互的
public class CommandExecutorLancher extends CommandExecutor {

	private static CommandExecutorLancher executor;
	private CommandListenerLancher listener;
	private HeartListener heartListener;
	private String type;
	private String uuid="";//设备序列号
	
	public synchronized static CommandExecutorLancher getOnlyExecutor(){
		if (executor == null) {
			executor = new CommandExecutorLancher();
		}
		return executor;
	}

	private CommandExecutorLancher(){
		super();
		initData();
		ReceiveCommand();
	}
	
	
	private void initData(){
		uuid= Devices.getInstace(new CacheHelper()).getKey();
				//QMUtil.getInstance().getUUID(App.getApp());
		type=QMUtil.getInstance().getMobileType(App.getApp());
		try{
			if (datagramSocket == null){
				datagramSocket = new DatagramSocket(QMCommander.PORT_Lancher);
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	private int width;
	private int heigh;
	public void setWH(Activity context){
		DisplayMetrics dm = new DisplayMetrics();
		context.getWindowManager().getDefaultDisplay().getMetrics(dm);
		width = dm.widthPixels;
		heigh = dm.heightPixels;
	}
	
	public void setListener(CommandListenerLancher listener) {
		this.listener = listener;
	}

	public void setHeartListener(HeartListener listener) {
		this.heartListener = listener;
	}

	
	/**
	 * 搜索手机设备
	 * @param category
	 */
	public void searchRequestMessageMobile(int category){
		String ip= NetworkUtils.getNetworkIP();
		StringBuilder sb = new StringBuilder();
		sb.append("name='"+ Collocation.getCollocation().getDeviceName()+"' address='"+ip + "' uuid='"+uuid+"'"+" devicetype='android'");
		if(("192.168.43.1").equals(ip))
			SendRequestCommand(category,"192.168.43.255", sb.toString());
		else
			SendRequestCommand(category,"255.255.255.255", sb.toString());
	}
	
	/**
	 * 回复搜索手机设备的信息
	 * @param category
	 */
	public void replyResponseMessageMobile(int category){
		String ip=NetworkUtils.getNetworkIP();
		StringBuilder sb = new StringBuilder();
		sb.append("name='"+Collocation.getCollocation().getDeviceName()+"' address='"+ip + "' uuid='"+uuid+"'"+" devicetype='android'");
		if(("192.168.43.1").equals(ip))
			SendResponseCommand(category,"192.168.43.255", sb.toString());
		else
			SendResponseCommand(category,"255.255.255.255", sb.toString());
	}
	
	
	/**
	 * 搜索任盒设备
	 * @param category
	 */
	public void searchMessageLancher(int category){
		String ip=NetworkUtils.getNetworkIP();
		StringBuilder sb = new StringBuilder();

		//sb.append("address='"+ip + "'");
		sb.append("name='"+Collocation.getCollocation().getDeviceName()+"' address='"+ip + "' uuid='"+uuid+"'"+" devicetype='android'");
		if(("192.168.43.1").equals(ip))
			SendRequestCommand(category,"192.168.43.255", sb.toString());
		else
			SendRequestCommand(category,"255.255.255.255", sb.toString());
	}

	
	/**
	 * 搜索PC设备
	 * @param category
	 */
	public void searchRequestMessagePC(int category){
		String ip=NetworkUtils.getNetworkIP();
		StringBuilder sb = new StringBuilder();
		sb.append("address='"+ip + "' name='"+Collocation.getCollocation().getDeviceName()+ "' uuid='"+uuid+ "' password=''  devicetype='android'");
		if(("192.168.43.1").equals(ip))
			SendRequestCommand(category,"192.168.43.255", sb.toString());
		else
			SendRequestCommand(category,"255.255.255.255", sb.toString());
	}
	
	/**
	 *回复搜索PC设备的信息
	 * @param category
	 */
	public void replyResponseMessagePC(int category){
		String ip=NetworkUtils.getNetworkIP();
		StringBuilder sb = new StringBuilder();
		sb.append("name='"+Collocation.getCollocation().getDeviceName()+"' address='"+ip + "' uuid='"+uuid+"' enablepwd='false'  devicetype='android'");
		if(("192.168.43.1").equals(ip))
			SendResponseCommand(category,"192.168.43.255", sb.toString());
		else
			SendResponseCommand(category,"255.255.255.255", sb.toString());
	}

	public void replayResponsePcOnly(int  category,String pcIp){
		String ip=NetworkUtils.getNetworkIP();
		StringBuilder sb = new StringBuilder();
		sb.append("name='"+Collocation.getCollocation().getDeviceName()+"' address='"+ip + "' uuid='"+uuid+"' enablepwd='false'  devicetype='android'");
		SendResponseCommand(category,pcIp,sb.toString());
	}


	public void replayRequestPcOnly(int  category,String pcIp){
		String ip=NetworkUtils.getNetworkIP();
		StringBuilder sb = new StringBuilder();
		sb.append("name='"+Collocation.getCollocation().getDeviceName()+"' address='"+ip + "' uuid='"+uuid+"' enablepwd='false'  devicetype='android'");
		SendRequestCommand(category,pcIp,sb.toString());
	}

	/**
	 * 连接设备,进行pc投屏
	 * @param category
	 * 发送连接或者断开设备的信息，里面的指令的参数是看其是否是连接还是断开
	 */
	public void connectPcMessage(int category,String host){
		StringBuilder sb = new StringBuilder();
		sb.append("name='"+Collocation.getCollocation().getDeviceName()+"'  address='"+ NetworkUtils.getNetworkIP()+"' width= '" +width+"' height='"+heigh+ "' uuid='1'"+" type='"+type+"'");
		SendRequestCommand(category,host,sb.toString());
	}

	public void connectPcMessage(int category, String host, int channel, boolean isPC){
		StringBuilder sb = new StringBuilder();
		sb.append("name='"+Collocation.getCollocation().getDeviceName()+"'  address='"+ NetworkUtils.getNetworkIP()+"' width= '" +width+"' channel= '" +channel+"' height='"+heigh+ "' uuid='1'"+" type='"+type+"' isPC='"+isPC+"' player='"+NetworkUtils.getNetworkIP()+"'");
		SendRequestCommand(category,host,sb.toString());
	}

	/**
	 * 与任盒的连接与取消操作     
	 * @param category
	 */
	public void connectMessage(int category, String host, String password){
		StringBuilder sb = new StringBuilder();
		sb.append("name='"+Collocation.getCollocation().getDeviceName()+"' devicetype='"+type+"' address='"+ NetworkUtils.getNetworkIP() + "' uuid='1' password='"+password+"' host='"+ host+"' ");
		SendRequestCommand(category,host, sb.toString());
	}
	
	/**
	 * 与任盒心跳消息
	 * @param category
	 * @param host
	 */
	public void heartBeatMessage(int category,String host ){
		StringBuilder sb = new StringBuilder();
		sb.append("name='"+Collocation.getCollocation().getDeviceName()+"' devicetype='"+type+"' address='"+ NetworkUtils.getNetworkIP() + "'  ");
		SendRequestCommand(category,host, sb.toString());
	}
	
	/**
	 * 回复PC连接手机的消息
	 * @param category
	 * @param host
	 */
	public void replyConnectMessagePC(int category,String host){
		StringBuilder sb = new StringBuilder();
		sb.append("name='"+Collocation.getCollocation().getDeviceName()+"' address='"+ NetworkUtils.getNetworkIP() + "' linkstate ='true' devicetype ='android'");
		SendResponseCommand(category,host, sb.toString());
	}


	/**
	 * 发送修改手机名称的消息
	 * @param category
	 */
	public void sendNameModify(int category,String oldname){
		StringBuilder sb = new StringBuilder();
		sb.append("name='"+Collocation.getCollocation().getDeviceName()+"' devicetype='"+type+"'  address='"+ NetworkUtils.getNetworkIP() + "' uuid='1' password='' ");
		SendRequestCommand(category,"255.255.255.255", sb.toString());
	}

	
	/**
	 * 控制指令
	 * @param category
	 */
	public void controlMessage(int category,String host){
		StringBuilder sb = new StringBuilder();
		sb.append("address='"+ NetworkUtils.getNetworkIP() + "' uuid='1' ");
		SendRequestCommand(category,host, sb.toString());
	}
	
	
	/**
	 * 控制指令------有密码传送的XML格式
	 * @param category
	 */
	public void controlMessageInput(int category, String host, String text){
		StringBuilder sb = new StringBuilder();
		sb.append("address='"+NetworkUtils.getNetworkIP() + "' uuid='1' text='"+text+"'");
		SendRequestCommand(category,host, sb.toString());
	}
	
	
	/**
	 * 控制指令 ------鼠标UI部分中，上下左右滑动通过触摸模仿鼠标移动的XML格式
	 * @param category
	 */
	public void controlMessageXY(int category, String host, String text){
		StringBuilder sb = new StringBuilder();
		sb.append("address='"+NetworkUtils.getNetworkIP() + "' uuid='1' ");
		sb.append(text);
		SendRequestCommand(category,host, sb.toString());
	}


	public void scrollMessage(int category, float moveX, float moveY, String host){
		StringBuilder sb = new StringBuilder();
		sb.append("moveX='"+ moveX +"' moveY='"+ moveY +"' address='"+ NetworkUtils.getNetworkIP() + "' uuid='1' ");
		SendRequestCommand(category,host, sb.toString());
	}

	
	private void SendRequestCommand(int category, String host, String message){
		SendRequestCommand(category,host, message,QMCommander.PORT_Lancher);
	}
	
	private void SendResponseCommand(int category, String host, String message){
		SendResponseCommand(category,host, message,QMCommander.PORT_Lancher);
	}


	@Override
	public void startAnalysis(XmlPullParser xmlPullParser, RootPoint rp){
		String name = xmlPullParser.getName();
		switch (name) {
		case QMCommander.XML.SEVER:
			//改名设置
			rp.setName(xmlPullParser.getAttributeValue(null, "name"));
			switch (rp.getCategory()){
			case QMCommander.TYPE_SEARCH:
				rp.setUuid(xmlPullParser.getAttributeValue(null, "uuid"));
				rp.setEnablepwd(xmlPullParser.getAttributeValue(null, "enablepwd"));
				String info=xmlPullParser.getAttributeValue(null, "devicetype");
				if(info!=null){
					if(info.equals("pc")){
						rp.setdType(2);
					}
					else if(info.equals("ios")){
						rp.setdType(1);
					}else if(info.equals("mac")){
						rp.setdType(2);
						rp.setMac(true);
					}else {
						rp.setdType(0);
					}
				}
				break;
			case QMCommander.MEMORY_Response:
				rp.setMemory(Long.parseLong(xmlPullParser.getAttributeValue(null, "freeSpace")));
				break;
			case QMCommander.PASSWD_Alter:
				rp.setUuid(xmlPullParser.getAttributeValue(null, "uuid"));
				rp.setEnablepwd(xmlPullParser.getAttributeValue(null,"enablepwd"));
				break;
			case QMCommander.TYPE_CONNECT:
				rp.setLinkstate(xmlPullParser.getAttributeValue(null,"linkstate"));
				DebugLog.showLog(this,"linkstate:"+rp.getLinkstate());
				break;
			case QMCommander.TYPE_OPEN_CONTROL:
				rp.setEnablecontrol (xmlPullParser.getAttributeValue(null,"enablecontrol"));
				break;
			case QMCommander.SCREEN_OPEN:
				rp.setIsrunning(xmlPullParser.getAttributeValue(null,"isrunning"));
				break;

			case QMCommander.TYPE_NAME:
				DebugLog.showLog(this,"xml:"+rp.getXmlMessage());
				if(listener!=null)
					listener.searchLancher(rp);
					break;
			}

			break;	
		case QMCommander.XML.CLIENT:

			rp.setName(xmlPullParser.getAttributeValue(null, "name"));

			if(rp.getCategory()==QMCommander.TYPE_SEARCH){
				rp.setUuid(xmlPullParser.getAttributeValue(null, "uuid"));
				String info = xmlPullParser.getAttributeValue(null, "devicetype");
				if (info != null) {
					if (info.equals("pc")){
						rp.setdType(2);
						rp.setMac(false);
					} else if (info.equals("mac")){
						rp.setdType(2);
						rp.setMac(true);
					} else if (info.equals("ios")){
						rp.setdType(1);
					} else {
						rp.setdType(0);
					}

				}
			}

			if(rp.getCategory()==QMCommander.TYPE_CONNECT){
				String info=xmlPullParser.getAttributeValue(null, "devicetype");
				if(info!=null){
					if(info.equals("pc")){
						rp.setdType(2);
					}else if(info.equals("mac")){
						rp.setMac(true);
						rp.setdType(2);
					}
				}
			}

			if(rp.getCategory()==QMCommander.TYPE_NAME){
				if(listener!=null)
					listener.searchLancher(rp);
			}


			if(rp.getCategory()==QMCommander.TYPE_PCTP||rp.getCategory()==QMCommander.TYPE_COVER_SHARED_PC){
				String channel=xmlPullParser.getAttributeValue(null, "channel");
				DebugLog.showLog(this,"channel:"+channel);
				rp.setChannel(channel);
				String player=xmlPullParser.getAttributeValue(null, "player");
				DebugLog.showLog(this,"player:"+player);
				rp.setPlayurl(player);
			}

			break;
		
		default:
			break;
		}
	}

	@Override
	public void listenerCallBack(RootPoint rp){
		int category = rp.getCategory();
//		if(rp.getdType()==-1&&rp.getCategory()==QMCommander.TYPE_SEARCH){
//			DebugLog.showLog(this,"message :"+rp.getXmlMessage());
//		}
		if(!rp.isClient()){
			if(category==QMCommander.TYPE_BOX_ONLINE){
				if (heartListener != null){
					heartListener.onReceiveHeart(rp);
				}
				QMDevice.getInstance().oprationSameDevice(rp, new BoxOnClickListener(){
					@Override
					public void onClick(RootPoint rootPoint){
						rootPoint.setHeartbeat2(0);
					}

				});
				return ;
			}
		}

		switch (category){
		case  QMCommander.TYPE_SEARCH:
			if(listener!=null)
				listener.searchLancher(rp);
			if(rp.isClient()){
				if(rp.getUuid()!=null&&!rp.getUuid().equals("null")&&!uuid.equals(rp.getUuid())){
					if(rp.getdType()==2){
						if (!Collocation.getCollocation().getIsScan()){
							//replyResponseMessagePC(category);
							replayResponsePcOnly(category,rp.getAddress());
						   }else{
							 replayResponsePcOnly(QMCommander.TYPE_SEARCH_SCAN,rp.getAddress());
							// replyResponseMessagePC(QMCommander.TYPE_SEARCH_SCAN);
						   }
					}else{

					  if(!Collocation.getCollocation().getIsScan()){
						replyResponseMessageMobile(category);
					  }else {
						replyResponseMessageMobile(QMCommander.TYPE_SEARCH_SCAN);
					  }
				  }
				 }
			     }else if(rp.getdType()==2){
			     RootPoint rootPoint = QMDevice.getInstance().getSameRootPoint(rp);
				 if(rootPoint!=null&&rootPoint.isShareing()){
					rootPoint.setShareHeart(0);
				 }
			    }

			break;
		case QMCommander.TYPE_SEARCH_SCAN:
			//收到扫描搜索的指令
			if(rp.getUuid()==null||rp.getUuid().equals("")||rp.getUuid().equals("null")||rp.getUuid().equals(uuid))
				return ;//没有uuid唯一标示的过滤，是自己手机设备过滤
//			if (!MainActivity.isScan){
//			MainActivity.netIp=rp.getAddress();}
			if(listener!=null)
				listener.searchLancher(rp);
			if(rp.isClient()){
				//把自身的ip进行封装发送出去，反之让对方可以接收到
				replyResponseMessageMobile(category);
			}
			break;

		case QMCommander.TYPE_CONNECT:
			DebugLog.showLog(this,"QMCommander:"+QMCommander.TYPE_CONNECT+"ip:"+rp.getAddress());
			if(rp.getdType()==2){
				// replyConnectMessagePC(category, rp.getAddress());
				break;
			}

			if(listener!=null)
				listener.connectLancher(rp);
			    if("true".equals(rp.getLinkstate())){
					CommandExecutorLancherFile.getOnlyExecutor().connectMessage(
							QMCommander.TYPE_CONNECT, rp.getAddress(), "");
				}

			break;
		case QMCommander.TYPE_OPEN_CONTROL:
			if(listener!=null)
				listener.controlLancher(rp);
			break;
		case QMCommander.SCREEN_OPEN:
			if(listener!=null)
				listener.screenOpenLancher(rp);
			break;
		case QMCommander.PASSWD_Alter:
			if(listener!=null)
				listener.passwdAlterLancher(rp);
			break;
		case QMCommander.TYPE_JIANCE_CONTROL:
			if(listener!=null)
				listener.controlHeartBeatLancher(rp);
			break;

		case QMCommander.TYPE_SUCCESS_PC:
			if (listener!=null) {
				listener.touPingPc(rp);
			}
			break;

		case QMCommander.TYPE_EXIT_PC:
			if (listener!=null) {
				listener.stopPc(rp);
			}
			break;
		case QMCommander.TYPE_PCTP:
			if (listener!=null) {
				listener.pcTouPing(rp);
			}
			break;
			//收到pc的关闭指令
		case QMCommander.TYPE_PC_STOP:
			if (listener!=null) {
				listener.pcTouPing(rp);
			}
			break;
			//收到pc的分享指令
		case QMCommander.TYPE_PC_SHARED:
			if (listener!=null) {
				listener.pcTouPing(rp);
			}
			break;
			//收到pc端拒绝分享的指令
	    case QMCommander.TYPE_PCSHARED_REFUSED:
		  if(listener!=null){
			listener.pcTouPing(rp);
			}
			break;

		case  QMCommander.TYPE_SCREEN_REFUSED:

			if(listener!=null){
				listener.pcTouPing(rp);
			}
				break;

			case QMCommander.TYPE_COVER_SHARED_PC:

				if(listener!=null){
					listener.pcCoverShare(rp);
				}
				break;

		default:
			break;
		}
	}

}

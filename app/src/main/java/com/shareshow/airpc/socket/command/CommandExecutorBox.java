package com.shareshow.airpc.socket.command;

import com.shareshow.airpc.Collocation;
import com.shareshow.airpc.QMCommander;
import com.shareshow.airpc.model.RootPoint;
import com.shareshow.airpc.socket.common.QMDevice;
import com.shareshow.airpc.util.DebugLog;
import com.shareshow.airpc.util.NetworkUtils;

import org.xmlpull.v1.XmlPullParser;

import java.net.DatagramSocket;


//20001端口，与盒子交互的
public class CommandExecutorBox extends CommandExecutor{

	private static CommandExecutorBox executor;
	private CommandListenerBox listener;


	private CommandExecutorBox(){
		super();
		initData();
		ReceiveCommand();
	}

	private void initData(){
		try {
			if (datagramSocket == null) {
				datagramSocket = new DatagramSocket(QMCommander.PORT_Box);
			}
		}catch (Exception e){
			e.printStackTrace();
		}
	}

	public synchronized static CommandExecutorBox getOnlyExecutor(){
		if (executor == null) {
			executor = new CommandExecutorBox();
		}
		return executor;
	}

	public void setListener(CommandListenerBox listener) {
		this.listener = listener;
	}
	
	/**
	 * Box 连接、投屏、心跳统一消息入口
	 * @param category
	 */

	public void connectMessage(int category, String host, String password){
		StringBuilder sb = new StringBuilder();
		sb.append("<?xml version='1.2' encoding='utf-8'?>");
		sb.append("<xtxk.ipz.android>");
		sb.append("<request category='" + category + "'>");
		sb.append("<host  address='"+ NetworkUtils.getNetworkIP() + "' ");
		sb.append(" >");
		sb.append("</host>");
		addBody(sb, category, password);
		sb.append("</request>");
		sb.append("</xtxk.ipz.android>");

		SendCommand(host, sb.toString(),QMCommander.PORT_Box);
	}
	
	private void addBody(StringBuilder sb, int category, String password){
		sb.append("<body>");
		sb.append("<identity>"+NetworkUtils.getNetworkMac()+"</identity>");
		sb.append("<workModel>"+2+"</workModel>");
		if(category==QMCommander.TYPE_HEARTBEAT){
			addClientXml(sb, category);
		}
		if(category==QMCommander.TYPE_START_PLAY||category==QMCommander.TYPE_STOP_PLAY){
			sb.append("<index>"+1+"</index>");
			addClientXml(sb, category);
		}
		if(category==QMCommander.TYPE_SET_CLIENT_MSG){
			if(password==null){
				sb.append("<password></password>");
			}else{
			sb.append("<password>"+password+"</password>");
			}
			addClientXml(sb, category);
		}
		sb.append("</body>");
	}

	private void addClientXml(StringBuilder sb, int category){
		sb.append("<client name='"+ Collocation.getCollocation().getDeviceName()+"' mac='" +NetworkUtils.getNetworkMac() + "' address='"+ NetworkUtils.getNetworkIP() + "' device='moble' isshow ='"+Collocation.getCollocation().getPermit()+"' />");
		addEncoderXml(sb);
	}

	private void addEncoderXml(StringBuilder sb){
		sb.append("<encoder format='1' type='1' address='"+NetworkUtils.getNetworkIP() + "'/>");
	}
	

	@Override
	public void startAnalysis(XmlPullParser xmlPullParser, RootPoint rp){
		String name = xmlPullParser.getName();
		switch (name) {
		case QMCommander.XML.SEVER:
			rp.setName(xmlPullParser.getAttributeValue(null, "name"));
			if (rp.getCategory()==QMCommander.TYPE_SET_CLIENT_MSG) 
				rp.setLinkstate(xmlPullParser.getAttributeValue(null,"linkstate"));
			break;	
		case QMCommander.XML.SETTING:
			if(rp.getCategory()==QMCommander.TYPE_SET_CLIENT_MSG){
				rp.setMutilcast(xmlPullParser.getAttributeValue(null, "isMulticast"));
				rp.setMutilcastAddress(xmlPullParser.getAttributeValue(null, "multicastAddress"));
				String str=xmlPullParser.getAttributeValue(null, "isshow");
				if(str!=null)
					rp.setCanShare(Boolean.parseBoolean(str));
			}
			break;
		case QMCommander.XML.HOST:
			rp.setName(xmlPullParser.getAttributeValue(null, "name"));
			break;
		case QMCommander.XML.POS:
			rp.setPlayurl(xmlPullParser.getAttributeValue(null, "player"));
			rp.setIdentity(xmlPullParser.getAttributeValue(null, "identity"));
			break;
		default:
			break;
		}
	}

	/**
	 * 监听回调数据
	 */
	@Override
	public void listenerCallBack(RootPoint rp){
		if(rp.isClient())//过滤自己发送的广播
			return ;
		int category = rp.getCategory();
		switch (category){
		case QMCommander.SEND_SCREEN_STATE:
			if(listener!=null){
				listener.screenSuccessBox(rp);
			}

			break;

		case QMCommander.TYPE_HEARTBEAT:
			//DebugLog.showLog(this,"收到盒子心跳");
			String player = rp.getPlayurl();
			String identity =rp.getIdentity();

			//收到正常投屏心跳的数据
			if(player.equals(identity)&&player.equals(NetworkUtils.getNetworkMac())){
				for (int i = 0; i < QMDevice.getInstance().getSize(); i++){
					RootPoint point = QMDevice.getInstance().getRootPoint(i);
					if (point.getAddress().equals(rp.getAddress()) && point.isStopByHandle()){
						point.setHeartbeat(0);
						point.setTouPing(true);
						if(listener!=null){
							listener.heartBeatBox(rp);
						}

						break;
					}
				}
			}else{
				DebugLog.showLog(this,"收到心跳点播源不一样，断开！");
				if(listener!=null){
					listener.screenCoverBox(rp);
				}

			}

//		    RootPoint rootPoint=QMDevice.getInstance().getSameRootPoint(rp);
//
//			if(rootPoint!=null&&rootPoint.isShareScreen()){
//				listener.heartBeatBox(rp);
//			}

			break;
		case QMCommander.TYPE_SET_CLIENT_MSG:
			if(listener!=null){
				listener.connectBox(rp);
			}
			break;
		case QMCommander.TYPE_MOUSE_EXIT:
			if(listener!=null){
				listener.exitBox(rp);
			}
			break;
		case QMCommander.TYPE_VALIDATE2:
			connectMessage(QMCommander.TYPE_VALIDATE2, rp.getAddress(),null);
			break;
		case QMCommander.TYPE_VALIDATE:
			if(listener!=null){
				listener.playBox(rp);
			}
			break;
		case QMCommander.TYPE_MOUSE_MODEL:
			if(listener!=null){
				listener.playBox(rp);
			}
			break;
		default:
			break;
		}
	}
}

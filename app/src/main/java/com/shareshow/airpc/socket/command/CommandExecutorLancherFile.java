package com.shareshow.airpc.socket.command;

import com.shareshow.App;
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
 * 20003端口监听的类,与lancherFile交互的
 * 主要监听 1.连接设备
 *        2.取消连接设备
 *        3.发送手机名称修改的消息
 *        4.心跳保持
 *        5.手机剩余内存大小
 * @author tanwei
 *
 */
public class CommandExecutorLancherFile extends CommandExecutor {

	private static CommandExecutorLancherFile executor;
	private String type;

	private CommandExecutorLancherFile() {
		super();
		initData();
		ReceiveCommand();
	}
	
	public synchronized static CommandExecutorLancherFile getOnlyExecutor() {
		if (executor == null) {
			executor = new CommandExecutorLancherFile();
		}
		return executor;
	}
	
	private void initData(){
		type= QMUtil.getInstance().getMobileType(App.getApp());
		try {
			if (datagramSocket == null) {
				datagramSocket = new DatagramSocket(QMCommander.PORT_LancherFile);
			}
		} catch (Exception e){
			e.printStackTrace();
		}
	}
	
	/**
	 * 与任盒文件共享的连接与取消操作
	 * @param category
	 */
	public void connectMessage(int category, String host, String password){
		StringBuilder sb = new StringBuilder();
		sb.append("name='"+ Collocation.getCollocation().getDeviceName()+"' devicetype='"+type+"'  address='"+ NetworkUtils.getNetworkIP() + "' uuid='1' password='"+password+"' ");
		SendRequestCommand(category,host, sb.toString());
	}
	
	/**
	 * 与任盒文件共享的心跳消息
	 * @param category
	 * @param host
	 */
	public void heartBeatMessage(int category,String host){
		StringBuilder sb = new StringBuilder();
		sb.append("name='"+Collocation.getCollocation().getDeviceName()+"' devicetype='"+type+"'  address='"+ NetworkUtils.getNetworkIP() + "'  ");
		SendRequestCommand(category,host, sb.toString());
	}
	
	
	/**
	 * 手机内存空间回复
	 * @param category
	 */
	public void replySDcardMemory(int category, String host, String memory){
		
		StringBuilder sb = new StringBuilder();
		sb.append("name='" + Collocation.getCollocation().getDeviceName()
					+ "' address='"+ NetworkUtils.getNetworkIP() +"'  freeSpace='"+memory+ "'");
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

	private void SendRequestCommand(int category, String host, String message) {
		SendRequestCommand(category,host, message,QMCommander.PORT_LancherFile);
	}
	
	private void SendResponseCommand(int category, String host, String message) {
		SendResponseCommand(category,host, message,QMCommander.PORT_LancherFile);
	}
	

	@Override
	public void startAnalysis(XmlPullParser xmlPullParser, RootPoint rp) {
		
	}

	@Override
	public void listenerCallBack(RootPoint rp) {
		int category = rp.getCategory();
		if(category==QMCommander.MEMORY_Request){
			replySDcardMemory(QMCommander.MEMORY_Response,  rp.getAddress(), QMUtil.getInstance().getSDCardMemorry()+"");
			return ;
		}
		if(!rp.isClient()){
			if(category==QMCommander.TYPE_BOX_ONLINE)
				QMDevice.getInstance().oprationSameDevice(rp, new BoxOnClickListener() {
					
					@Override
					public void onClick(RootPoint rootPoint) {
						rootPoint.setHeartbeat2(0);
					}
				});
			return ;
		}
	}

}

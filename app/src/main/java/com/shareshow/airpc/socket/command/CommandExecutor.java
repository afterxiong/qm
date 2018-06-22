package com.shareshow.airpc.socket.command;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Xml;

import com.shareshow.airpc.QMCommander;
import com.shareshow.airpc.model.RootPoint;
import com.shareshow.airpc.util.DebugLog;

import org.xmlpull.v1.XmlPullParser;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

/**
 * Socket交互
 * @author tanwei
 *
 */
@SuppressLint("HandlerLeak")
public abstract class CommandExecutor{
	
	public DatagramSocket datagramSocket;

	private static final int SOCKET_TIME_OUT =1*1000 ;

	public CommandExecutor(){

	}

	/**
	 * 解析各自的数据
	 * @param xmlPullParser
	 * @param rp
	 */

	public abstract void startAnalysis(XmlPullParser xmlPullParser, RootPoint rp);

	
	/**
	 * 监听回调数据
	 */
	public abstract void listenerCallBack(RootPoint rp);


	//收到XML消息后进行分析处理
	private Handler handler =new Handler(Looper.getMainLooper()){

		@Override
		public void handleMessage(Message msg){
			super.handleMessage(msg);
			Bundle bundle=msg.getData();
			messageAnalysis((String)bundle.getString("message"),getSendIp(bundle.getString("sendIp")));//解析xml
		}

	};

	private String getSendIp(String sendIp){
		sendIp=sendIp.substring(1, sendIp.length());
    	String ip[]=sendIp.split(":");
		return ip[0];
	}

	/**
	 * 处理公共部分的数据  ---Address已处理
	 * @param message
	 * @param sendIp
	 */
	
	private void messageAnalysis(String message, String sendIp){
		RootPoint mRootPoint=null;
		XmlPullParser xmlPullParser = Xml.newPullParser();
		try {
			InputStream inputStream = new ByteArrayInputStream(message.getBytes("UTF-8"));
			xmlPullParser.setInput(inputStream, "UTF-8");
			int eventType = xmlPullParser.getEventType();
			while (eventType != XmlPullParser.END_DOCUMENT){
				switch (eventType) {
				case XmlPullParser.START_DOCUMENT:
					break;
				case XmlPullParser.START_TAG:
					String name = xmlPullParser.getName();
					switch (name){
					case QMCommander.XML.XTXK:
						mRootPoint = new RootPoint();
						break;
					case QMCommander.XML.XTXK_ANDROID:
						mRootPoint = new RootPoint();
						break;
					case QMCommander.XML.RESPONSE:
						mRootPoint.setClient(false);
						mRootPoint.setResponse(true);
						mRootPoint.setAddress(sendIp);
						mRootPoint.setCategory(Integer.parseInt(xmlPullParser.getAttributeValue(0)));
						mRootPoint.setXmlMessage(message);
						break;
					case QMCommander.XML.REQUEST:
						mRootPoint.setClient(true);
						mRootPoint.setResponse(true);
						mRootPoint.setAddress(sendIp);
						mRootPoint.setXmlMessage(message);
						mRootPoint.setCategory(Integer.parseInt(xmlPullParser.getAttributeValue(0)));
						break;
					default:
						if(!mRootPoint.isResponse()){
							return ;
						}
						startAnalysis(xmlPullParser,mRootPoint);
						break;
					}
					break;
				case XmlPullParser.END_TAG:
					break;
				case XmlPullParser.END_DOCUMENT:
					break;
				default:
					break;
				}
				eventType = xmlPullParser.next();
			}
		} catch (Exception e){
			e.printStackTrace();
		}
		if(mRootPoint!=null)
			listenerCallBack(mRootPoint);
	}

	/**
	 * 发送request指令
	 * @param category  指令
	 * @param host      address地址
	 * @param message   发送的消息
	 * @param port      端口
	 */
	public void SendRequestCommand(int category, String host, String message, int port){
		StringBuilder sb = new StringBuilder();
		sb.append("<?xml version='1.2' encoding='utf-8'?>");
		sb.append("<xtxk>");
		sb.append("<request category='" + category + "'>");
		sb.append("<client ");
		sb.append(message);
		sb.append(" >");
		sb.append("</client>");
		sb.append("</request>");
		sb.append("</xtxk>");
		SendCommand(host, sb.toString(), port);
	}


	/**
	 * 发送response指令
	 * @param category  指令
	 * @param host      address地址
	 * @param message   发送的消息
	 * @param port      端口
	 */
	public void SendResponseCommand(int category, String host, String message, int port){
		StringBuilder sb = new StringBuilder();
		sb.append("<?xml version='1.2' encoding='utf-8'?>");
		sb.append("<xtxk>");
		sb.append("<response category='" + category + "'>");
		sb.append("<server ");
		sb.append(message);
		sb.append(" >");
		sb.append("</server>");
		sb.append("</response>");
		sb.append("</xtxk>");
		SendCommand(host, sb.toString(), port);
	}


	/**
	 * 发送命令
	 */
	public void SendCommand(final String host, final String message, final int port){
		new Thread(new Runnable(){
			public void run(){
				DatagramPacket datagramPacket;
				try{
					byte[] data = message.getBytes();
					int length = message.getBytes("UTF-8").length;
					InetAddress address = InetAddress.getByName(host);
					datagramPacket = new DatagramPacket(data, length, address,port);
					datagramSocket.send(datagramPacket);
				    }catch (Exception e){
					 e.printStackTrace();
				   }
			  }
		}).start();
	}


	/**
	 * 接收指令
	 */
	public void ReceiveCommand(){
		new Thread(){
			public void run(){
					try{
						while (true){
						byte[] buffer = new byte[8192];
						DatagramPacket datagramPacket = new DatagramPacket(
									buffer, buffer.length);
						datagramSocket.receive(datagramPacket);
						String command = new String(datagramPacket.getData(),
								0, datagramPacket.getLength());
						command = new String(command.getBytes("UTF-8")).trim();
						Message message = handler.obtainMessage();
						Bundle bundle = new Bundle();
						bundle.putString("message", command);
						bundle.putString("sendIp", datagramPacket.getSocketAddress()
								.toString());
						message.setData(bundle);
						message.sendToTarget();
						}
					}catch (Exception e){
						e.printStackTrace();
					}

			};
		}.start();
	}


}

package com.shareshow.aide.nettyfile.socket;

import android.content.Context;

import com.google.gson.Gson;
import com.shareshow.airpc.util.DebugLog;
import com.shareshow.airpc.util.NetworkUtils;
import com.shareshow.db.SearchBean;

import org.greenrobot.eventbus.EventBus;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

/**
 * Created by TEST on 2017/12/12.
 */

public class SearchMessage {

     private static final int UDP_PORT = 20005 ;
     private  Context mContext;
     private DatagramSocket datagramSocket;
     private Gson gson;
     public static SearchMessage message = null;

     private SearchMessage(Context mContext){
          this.mContext =mContext;
          init();
     }

     public static SearchMessage getIntance(Context mContext){
          if(message==null){
               synchronized (SearchMessage.class){
                    if(message==null){
                         message=new SearchMessage(mContext);
                    }
               }

          }
          return message;
     }



     private void init(){
          gson = new Gson();
          initData();
          ReceiveThread.start();
     }


     private void initData(){
          try {
                  if(datagramSocket == null){
                    datagramSocket = new DatagramSocket(UDP_PORT);
                  }
          }catch (Exception e){
               e.printStackTrace();
          }
     }


     public  String createJsonData(){
          SearchBean bean = new SearchBean(SearchBean.CMD_SEACH,"",0,"");
          String json =gson.toJson(bean);
          return json;
     }

     public void sendMessage(){
          final String json = createJsonData();
          new Thread(new Runnable(){
               @Override
               public void run(){
                    try{
                         InetAddress address =null;
                         byte[] data = json.getBytes();
                         int length = json.getBytes("UTF-8").length;
                         if(NetworkUtils.getNetworkIP().equals(("192.168.43.1"))){
                              address= InetAddress.getByName("192.168.43.255");
                         }else{
                              address= InetAddress.getByName("255.255.255.255");
                         }
                         DatagramPacket datagramPacket = new DatagramPacket(data, length, address, UDP_PORT);
                         for (int i = 0; i < 3; i++){
                              datagramSocket.send(datagramPacket);
                              Thread.sleep(100);
                         }
                        DebugLog.showLog(this,"发送成功搜索");
                    }catch (Exception e){
                         e.printStackTrace();
                         DebugLog.showLog(this,"发送失败 IP:"+ NetworkUtils.getNetworkIP());
                    }

               }
          }).start();
      }


      /*
       * 接受文件
       *
       */
     public Thread ReceiveThread = new Thread(){

           public void run(){
                try {
                     byte[] buffer = new byte[8192];
                     DatagramPacket datagramPacket = new DatagramPacket(
                             buffer, buffer.length);
                     while (true){
                          datagramSocket.receive(datagramPacket);
                          String command = new String(datagramPacket.getData(),
                                  0, datagramPacket.getLength());
                          command = new String(command.getBytes("UTF-8")).trim();
                          SearchBean bean = gson.fromJson(command, SearchBean.class);
                          if (datagramPacket.getAddress() != null) {
                               bean.setIp(datagramPacket.getAddress().toString().split("/")[1]);
                          }
                          EventBus.getDefault().post(new SearchEvent(gson.toJson(bean)));
                     }
                }catch(Exception e){
                     e.printStackTrace();
                }
           }
      };



}

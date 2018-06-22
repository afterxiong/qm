package com.shareshow.airpc.socket.command;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Process;

/**
 * Created by TEST on 2017/11/1.
 * 处理消息的后台线程
 */

public class MessageThread extends HandlerThread {

    private static MessageThread messageThread;
    private static Handler messageHandler;

    public MessageThread(){
        super("MessageThread", Process.THREAD_PRIORITY_DEFAULT);
    }

    //出始化handlerThread

    public static void prepareThread(){
            if(messageThread==null){
                       messageThread = new MessageThread();
                       messageThread.start();
                       messageHandler =  new Handler(messageThread.getLooper());
             }
      }

    //发送消息
    public static void post(Runnable runnable){
         if(messageHandler!=null){
             messageHandler.post(runnable);
         }
    }

    //发送延时消息
    public static void postDelayed(Runnable runnable , long delayMillis){
         if(messageHandler!=null){
             messageHandler.postDelayed(runnable,delayMillis);
         }
    }

    //销毁线程
    public static void destoryThread(){

        if(messageThread!=null){
            messageThread.quit();
        }

        messageThread=null;
        messageHandler=null;
    }


}

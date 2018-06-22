package com.shareshow.airpc.share;

import com.shareshow.airpc.QMCommander;
import com.shareshow.airpc.model.RootPoint;
import com.shareshow.airpc.socket.command.CommandExectuorMobile;
import com.shareshow.airpc.socket.command.CommandExecutorLancher;
import com.shareshow.airpc.socket.command.CommandExecutorLancherFile;
import com.shareshow.airpc.util.DebugLog;


/**
 * Created by TEST on 2017/8/21.
 */

public class HeartThread {

    private HeartBeatThread heartBeatThread;
    private HeartListener listener;

    //发送文件过程的心跳
    public void sendHeart(RootPoint root, boolean isSendFile){
        if(heartBeatThread ==null){
            heartBeatThread = new HeartBeatThread(root,isSendFile);
            heartBeatThread.start();
        }else{
            heartBeatThread.setSendFile(isSendFile);
            heartBeatThread.setRoot(root);
        }
    }

    public void stopSendHeart(){
        if(heartBeatThread !=null) {
            heartBeatThread.setSendFile(false);
            try{
                heartBeatThread.interrupt();
            }catch (Exception e){
                e.printStackTrace();
            }
            heartBeatThread =null;
        }

    }
    
    public void setHeartListener(HeartListener listener){
        this.listener=listener;
    }

    private class HeartBeatThread extends Thread {

        private  boolean isSendFile;

        private RootPoint root;

        public void setSendFile(boolean isSendFile){
            this.isSendFile=isSendFile;
        }

        public void setRoot(RootPoint root){
            this.root=root;
        }

        public HeartBeatThread(RootPoint root, boolean isSendFile){
            this.isSendFile=isSendFile;
            this.root=root;
        }
        @Override
        public void run(){
            while (isSendFile){
                if(root.getdType()==-1){
                    root.setHeartbeat2(root.getHeartbeat2()+1);
                    CommandExecutorLancher.getOnlyExecutor().heartBeatMessage(QMCommander.TYPE_BOX_ONLINE,root.getAddress());
                    CommandExecutorLancherFile.getOnlyExecutor().heartBeatMessage(QMCommander.TYPE_BOX_ONLINE,root.getAddress());
                }else if(root.getdType()==0||root.getdType()==1){
                    root.setHeartbeat2(root.getHeartbeat2()+1);
                    CommandExectuorMobile.getOnlyExecutor().heartBeatMessage(QMCommander.TYPE_MOBILE_FILE_ONLINE,root.getAddress());
                }

                if(root.getHeartbeat2()>10){
                    if(listener!=null){
                        listener.OnTimeOutHeart(root);
                    }
//                    if(binder!=null){
//                        binder.getListener().heartTimeOut(root);
//                    }
                    isSendFile=false;
                }

                DebugLog.showLog(this,"发送心跳"+root.getHeartbeat2());

                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e){
                    e.printStackTrace();
                }
            }
        }
    }



    public interface  HeartListener{

        void OnTimeOutHeart(RootPoint rootPoint);
        
    }
}

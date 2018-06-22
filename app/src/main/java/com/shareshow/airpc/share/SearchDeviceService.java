package com.shareshow.airpc.share;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import com.shareshow.aide.util.uuid.CacheHelper;
import com.shareshow.aide.util.uuid.Devices;
import com.shareshow.airpc.Collocation;
import com.shareshow.airpc.QMCommander;
import com.shareshow.airpc.model.RootPoint;
import com.shareshow.airpc.socket.command.CommandExectuorMobile;
import com.shareshow.airpc.socket.command.CommandExecutorBox;
import com.shareshow.airpc.socket.command.CommandExecutorLancher;
import com.shareshow.airpc.socket.command.CommandExecutorLancherFile;
import com.shareshow.airpc.socket.command.CommandListenerBox;
import com.shareshow.airpc.socket.command.CommandListenerLancher;
import com.shareshow.airpc.socket.command.CommandListenerMobile;
import com.shareshow.airpc.socket.common.QMDevice;
import com.shareshow.airpc.util.DebugLog;
import com.shareshow.airpc.util.NetworkUtils;
import com.shareshow.airpc.util.QMUtil;


/**
 * Created by Administrator on 2017/7/11 0011.
 */

public class SearchDeviceService extends Service implements CommandListenerLancher, CommandListenerBox, CommandListenerMobile {


    private SearcherBinder binder= new SearcherBinder();

    private Handler mHandler =new Handler();

    private String uuid;
    private String netIp;
    // private HeartBeatThread heartBeatThread;
    //  private String netIp=null;

    @Override
    public IBinder onBind(Intent intent) {

        // initSearch();
        if(!Collocation.getCollocation().getIsScan()){
               search();
         }
         return this.binder;
    }

//    private void initSearch(){
//        search();
//    }

    private void search(){
        QMDevice.getInstance().setOffline();

        new Thread(new Runnable(){
            @Override
            public void run() {
                searchDevice();
            }

        }).start();

        mHandler.postDelayed(searchBox, 1* 1000);
    }

    private void searchDevice(){

        if(!Collocation.getCollocation().getIsScan()){
            for (int i = 0; i < 3; i++) {
                // 发送与任盒的搜索够令--102
                CommandExecutorLancher.getOnlyExecutor().searchMessageLancher(QMCommander.TYPE_SEARCH);
                try {
                    Thread.sleep(300);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                // 发送与Android、IOS的搜索够令--102
                CommandExecutorLancher.getOnlyExecutor().searchRequestMessageMobile(QMCommander.TYPE_SEARCH);
                try {
                    Thread.sleep(300);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                // 发送与Android、IOS的搜索够令--101000 兼容旧版本设计的
                CommandExectuorMobile.getOnlyExecutor().searchMessage(QMCommander.TYPE_SEARCH_M);
                try {
                    Thread.sleep(300);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                // 发送与PC的搜索够令--102
                CommandExecutorLancher.getOnlyExecutor().searchRequestMessagePC(QMCommander.TYPE_SEARCH);
                try {
                    Thread.sleep(300);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    @Override
    public void onCreate(){
        super.onCreate();
        initListener();
    }

    private void initListener(){
        CommandExecutorLancher.getOnlyExecutor().setListener(this);// 20002端口监听
        CommandExecutorBox.getOnlyExecutor().setListener(this);// 20001端口监听
        CommandExectuorMobile.getOnlyExecutor().setListener(this);// 20004端口监听
        CommandExecutorLancherFile.getOnlyExecutor();// 启动20003端口
        uuid = Devices.getInstace(new CacheHelper()).getKey();
                //QMUtil.getInstance().getUUID(this);
    }


    @Override
    public void connectBox(RootPoint rootPoint) {

    }

    @Override
    public void playBox(RootPoint rootPoint) {

    }

    @Override
    public void exitBox(RootPoint rootPoint) {

    }

    @Override
    public void heartBeatBox(RootPoint rootPoint) {

    }

    @Override
    public void screenSuccessBox(RootPoint rootPoint) {

    }

    @Override
    public void screenCoverBox(RootPoint rootPoint) {

    }

    @Override
    public void searchLancher(RootPoint rootPoint){

        DebugLog.showLog(this,"name:"+rootPoint.getName());

        if (rootPoint.getCategory() == QMCommander.TYPE_NAME) {
            QMDevice.getInstance().getSameRootPoint(rootPoint).setName(rootPoint.getName());
        }

        if (Collocation.getCollocation().getIsScan() && rootPoint.getCategory() != QMCommander.TYPE_SEARCH_SCAN) {

            //当是选择的是扫描互联的时候，和回复的是不是扫描互联的指令的时候,针对的是老版本及盒子的回复
             DebugLog.showLog(this,"address:"+rootPoint.getAddress()+"uuid:"+uuid+"root uuid:"+rootPoint.getUuid());
            if (rootPoint.getAddress().equals(netIp)|| rootPoint!=null&&((NetworkUtils.getNetworkIP().equals(rootPoint.getHost())))) {
                if (rootPoint.getUuid() == null || rootPoint.getUuid().equals("") || rootPoint.getUuid().equals("null") || rootPoint.getUuid().equals(uuid))
                    return;// 没有uuid唯一标示的过滤，是自己手机设备过滤
                Log.d("CCC","老指令"+rootPoint.getName());
                rootPoint.setPlay(true);
                addCollotion(rootPoint);
            } else {
                return;
            }
        } else if (Collocation.getCollocation().getIsScan() && rootPoint.getCategory() == QMCommander.TYPE_SEARCH_SCAN) {
            //当是选择的是扫描互联的时候，收到的也是扫描互联的指令的时候
            if (rootPoint.getUuid() == null || rootPoint.getUuid().equals("") || rootPoint.getUuid().equals("null") || rootPoint.getUuid().equals(uuid))
                return;// 没有uuid唯一标示的过滤，是自己手机设备过滤
         // DebugLog.showLog(this,"netIp:"+ScreenActivity.netIp+"address:"+rootPoint.getAddress());
            if (rootPoint.getAddress().equals(netIp)|| rootPoint!=null&&((NetworkUtils.getNetworkIP().equals(rootPoint.getHost())))) {
                Log.d("CCC","新指令"+rootPoint.getName());
               // rootPoint.setPlay(true);
                addCollotion(rootPoint);
            }
        } else {
            //当自己选择的不是扫描互联的时候
            if (rootPoint.getName() == null || rootPoint.getName().equals(""))// 过滤没有名称的设备，不知为啥会收到这样的消息
                return;
            if (rootPoint.getdType() != -1) {// 非任盒设备
                if (rootPoint.getUuid() == null || rootPoint.getUuid().equals("") || rootPoint.getUuid().equals("null") || rootPoint.getUuid().equals(uuid))
                    return;// 没有uuid唯一标示的过滤，是自己手机设备过滤
                if (rootPoint.getCategory() != QMCommander.TYPE_SEARCH_SCAN || isScan||(NetworkUtils.getNetworkIP().equals(rootPoint.getHost()))||rootPoint.getAddress().equals(netIp)){
                    addCollotion(rootPoint);
                }
            } else {// 任盒设备
                if (rootPoint.getAddress().equals(NetworkUtils.getNetworkIP()))
                    return;// 过滤自己收到的自己发送的搜索指令
                addCollotion(rootPoint);// 添加到本类集合中
            }
        }

        if (QMDevice.getInstance().getSize() == 1){// 默认设备搜索的时候第一个盒子没有密码就勾选上
            RootPoint point = QMDevice.getInstance().getRootPoint(0);
            switch (point.getdType()) {
                case -1:
                    if (point.getEnablepwd() != null && point.getEnablepwd().equals("false")) {
                        point.setPlay(true);// 与任盒链接需要发送文件共享链接消息
                        CommandExecutorLancher.getOnlyExecutor().connectMessage(QMCommander.TYPE_CONNECT, point.getAddress(), "");
                    }
                    break;
                case 0:
                case 1:
                case 2:
                    point.setPlay(true);
                    break;
            }
        }
        // 当是扫码得到的ip的时候将传过来的设备进行连接
        if (netIp != null && rootPoint.getAddress().equals(netIp)) {
            rootPoint.setPlay(true);
            netIp = null;
            isScan=false;
        }

    }


    private Runnable searchBox = new Runnable() {

        @Override
        public void run(){
          DebugLog.showLog(this,"数据完毕！");
            boolean isConnected = NetworkUtils.isNetworkConnected();
            if (!isConnected){
                // 没有网就显示"网络没有连接"
             if (binder.getListener()!=null){
                 binder.getListener().SearchSuccess(0);
                 //TODO 没有网络
                 }
            } else {
                if (QMDevice.getInstance().getSize() == 0) {
                    binder.getListener().SearchSuccess(1);
                    //TODO 有网络，但是没有好友
                }else{
                    //TODO 有好友
                    QMDevice.getInstance().removeOffline();
                    binder.getListener().SearchSuccess(2);

                }
            }
        }
    };

    public void addCollotion(RootPoint rootPoint) {
        if (rootPoint.getAddress().equals(NetworkUtils.getNetworkIP())) {
            return;
        }
        if (NetworkUtils.isNetworkConnected())
            QMDevice.getInstance().add(rootPoint);
    }

    @Override
    public void connectLancher(RootPoint rootPoint) {

    }

    @Override
    public void controlLancher(RootPoint rootPoint) {

    }

    @Override
    public void screenOpenLancher(RootPoint rootPoint) {

    }

    @Override
    public void passwdAlterLancher(RootPoint rootPoint) {

    }

    @Override
    public void controlHeartBeatLancher(RootPoint rootPoint){

    }

    @Override
    public void touPingPc(RootPoint rp){

    }

    @Override
    public void stopPc(RootPoint rp){

    }

    @Override
    public void pcTouPing(RootPoint rp){

        CommandExecutorLancher.getOnlyExecutor().connectPcMessage(QMCommander.TYPE_SCREEN_REFUSED , rp.getAddress());
    }

    @Override
    public void pcCoverShare(RootPoint rp) {

    }

    @Override
    public void searchMobile(RootPoint rootPoint) {
              searchLancher(rootPoint);
    }

    @Override
    public void screenMobile(RootPoint rootPoint){

        if (QMCommander.TYPE_NEED_SHARED == rootPoint.getCategory()){

            CommandExectuorMobile.getOnlyExecutor().sendconnectMessage(QMCommander.TYPE_NO_SCREEN_M, rootPoint.getAddress());

            return;
        }

        if(QMCommander.TYPE_SCREEN_M==rootPoint.getCategory()){

            CommandExectuorMobile.getOnlyExecutor().sendconnectMessage(QMCommander.TYPE_FORBIDDEN_M,rootPoint.getAddress());

            return;
        }




    }

    @Override
    public void screenInterruptMobile(RootPoint rootPoint) {

    }

    @Override
    public void heartBeatMobile(RootPoint mRootPoint) {

    }

    @Override
    public void swichMobileScreen(RootPoint mRootPoint) {

    }

    @Override
    public void requestScreenFrame() {

    }

    private  boolean isScan;

    public class  SearcherBinder extends Binder {

        private DataListener listener;

        public DataListener getListener() {
            return listener;
        }

        public void setListener(DataListener listener) {
            this.listener = listener;
        }


        public void onScanForsearch(String ip, boolean isScan) {
            scanForsearch(ip);
            SearchDeviceService.this.isScan=isScan;
            SearchDeviceService.this.netIp=ip;
        }

        public void onSearch(){
            search();
        }

//        public void sendHeart(RootPoint rootPoint,boolean isSend){
//            SearchDeviceService.this.sendHeart(rootPoint,isSend);
//        }
//
//        public void stopHeart(){
//            SearchDeviceService.this.stopSendHeart();
//        }


    }

    public interface  DataListener{

       //state  0:没网络  1，有网络没有好友  2，有好友
        void  SearchSuccess(int state);

      //  void  heartTimeOut(RootPoint rootPoint);


    }

    public void scanForsearch(String ip){
        // 发送搜索指令
      DebugLog.showLog(this,"发送扫码搜索！");
        CommandExectuorMobile.getOnlyExecutor().sendsearchMessage(QMCommander.TYPE_SEARCH_SCAN,ip);
        CommandExecutorLancher.getOnlyExecutor().replyResponseMessagePC(QMCommander.TYPE_SEARCH_SCAN_PC);
        CommandExecutorLancher.getOnlyExecutor().replyConnectMessagePC(QMCommander.TYPE_SEARCH_SCAN_PC,ip);
        //兼容旧版本的搜索指令
        CommandExecutorLancher.getOnlyExecutor().searchMessageLancher(QMCommander.TYPE_SEARCH);
        CommandExecutorLancher.getOnlyExecutor().searchRequestMessagePC(QMCommander.TYPE_SEARCH);
        CommandExectuorMobile.getOnlyExecutor().connectMessage(QMCommander.TYPE_SEARCH_M, ip);
        mHandler.postDelayed(searchBox, 1* 1000);
    }

    @Override
    public void onDestroy(){
        super.onDestroy();

      DebugLog.showLog(this,"service Destory");
    }



}

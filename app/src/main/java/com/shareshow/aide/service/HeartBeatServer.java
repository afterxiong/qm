package com.shareshow.aide.service;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.widget.RemoteViews;

import com.google.gson.Gson;
import com.shareshow.aide.R;
import com.shareshow.aide.activity.MainActivity;
import com.shareshow.aide.event.ControlEvent;
import com.shareshow.aide.event.DevOnlineEvent;
import com.shareshow.aide.nettyfile.socket.SearchEvent;
import com.shareshow.aide.nettyfile.socket.SearchMessage;
import com.shareshow.airpc.QMCommander;
import com.shareshow.airpc.model.RootPoint;
import com.shareshow.airpc.ports.ConnectFTPListener;
import com.shareshow.airpc.record.RecordManager;
import com.shareshow.airpc.socket.command.CommandExecutorBox;
import com.shareshow.airpc.socket.command.CommandExecutorLancher;
import com.shareshow.airpc.socket.command.CommandListenerBox;
import com.shareshow.airpc.socket.command.CommandListenerLancher;
import com.shareshow.airpc.socket.command.HeartListener;
import com.shareshow.airpc.socket.common.QMDevice;
import com.shareshow.airpc.util.DebugLog;
import com.shareshow.airpc.util.NetworkUtils;
import com.shareshow.airpc.util.QMUtil;
import com.shareshow.db.SearchBean;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;


/**
 * Created by hzk on 2018/3/16 0016.
 */

public class HeartBeatServer extends Service implements HeartListener,CommandListenerLancher,CommandListenerBox {

    public static final String DEV_IDS = "dev_ids";
    private boolean isRun;
    private Thread thread;
    private String devids = null;
    public SearchBean bean;
    public static RootPoint rootPoint = null;
    public static boolean isOnline = false;
    private long lastTime = 0;
    public static boolean istp = false;

    public static final String ACTION_BUTTON = "com.shareshow.aide.activity.MainActivity";// 通知栏点击停止发送的广播
    NotifiCationReceiver notifiCationReceiver;
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        DebugLog.showLog(this,"onBind");
        return null;
    }

    @Override
    public void onCreate(){
        EventBus.getDefault().register(this);
        CommandExecutorLancher.getOnlyExecutor().setHeartListener(this);// 20002端口监听
        notifiCationReceiver = new NotifiCationReceiver();
        IntentFilter intentFilter = new IntentFilter(ACTION_BUTTON);
        registerReceiver(notifiCationReceiver, intentFilter);
        DebugLog.showLog(this,"onCreate");
        isRun = true;
        thread = new Thread(new Runnable(){
            @Override
            public void run()  {
                while (isRun){
                    if (bean == null){
                        rootPoint = null;
                        synchronized (thread){
                            DebugLog.showLog(this,"wait");
                            try {
                                thread.wait();
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                        continue;
                    }

                    if(rootPoint!=null&&rootPoint.isTouPing()){
                        CommandExecutorBox.getOnlyExecutor().connectMessage(QMCommander.TYPE_HEARTBEAT, rootPoint.getAddress(), null);
                    }
                    CommandExecutorLancher.getOnlyExecutor().heartBeatMessage(QMCommander.TYPE_BOX_ONLINE,bean.getIp());
                    try {
                        Thread.sleep(3000);
                    } catch (InterruptedException e){
                        e.printStackTrace();
                    }
                    RootPoint rp = QMDevice.getInstance().getSelectDevice();
                    if(rp!=null){
                        rp.setHeartbeat(rp.getHeartbeat()+1);
                        if(rp.getHeartbeat()>4&&rp.isTouPing()){
                            exitTP(rp, -1, R.string.exception);
                        }
                    }
                    if ((System.currentTimeMillis() - lastTime) > 12000){
                        //判定绑定设备离线
                        if (isOnline) {
                            isOnline = false;
                            QMDevice.getInstance().clear();
                            EventBus.getDefault().post(new DevOnlineEvent(0));
                        }

                    } else {
                        if (!isOnline) {
                            isOnline = true;
                            EventBus.getDefault().post(new DevOnlineEvent(1));
                            if (!QMUtil.getInstance().getQmDocumentFile().isFileDealFinished()) {
                                QMUtil.getInstance().getQmDocumentFile().getDocumentAndOtherData();
                            }
                        }
                    }

                }
            }
        });
        thread .start();
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        unregisterReceiver(notifiCationReceiver);
        EventBus.getDefault().unregister(this);
        CommandExecutorLancher.getOnlyExecutor().setListener(null);
        CommandExecutorBox.getOnlyExecutor().setListener(null);// 20001端口监听
        CommandExecutorLancher.getOnlyExecutor().setHeartListener(null);// 20002端口监听
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId){
        if(intent==null){
            return super.onStartCommand(intent, flags, startId);
        }
        CommandExecutorLancher.getOnlyExecutor().setListener(this);
        CommandExecutorBox.getOnlyExecutor().setListener(this);// 20001端口监听
        String devids = intent.getStringExtra(DEV_IDS);
        DebugLog.showLog(this,"onStartCommand:"+devids+";"+this.devids);
        if (devids != null){
            if (!devids.equals(this.devids)){
//                isOnline = false;
                this.bean = null;
//                //变更为离线状态
//                EventBus.getDefault().post(new DevOnlineEvent(0));
            }
            this.devids = devids;
            SearchMessage.getIntance(this).sendMessage();
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(SearchEvent event){
        Gson gson = new Gson();
        SearchBean bean = gson.fromJson(event.getEvent(), SearchBean.class);
        if(bean.getIp().equals(NetworkUtils.getNetworkIP())){
            return;
        }
        if (devids != null && devids.equals(bean.getIds())){
            //搜索到绑定设备 变更为在线状态
            if (!isOnline) {
                if (!QMUtil.getInstance().getQmDocumentFile().isFileDealFinished()) {
                    QMUtil.getInstance().getQmDocumentFile().getDocumentAndOtherData();
                }
            }
            isOnline = true;
            this.bean = bean;
            EventBus.getDefault().post(new DevOnlineEvent(1));
            //唤醒心跳线程
            synchronized (thread){
                thread.notify();
            }
        }
    }

    //网络绑定设备在线状态
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onServerControl(ControlEvent controlEvent) {
        if (controlEvent.controlCod == 1) {
            judgeTP(controlEvent);
        } else if (controlEvent.controlCod == 2) {
            closeTP();
        }
    }

    /**
     * 判断能否投屏
     */
    private void judgeTP(ControlEvent controlEvent){
        // 如果已经在投屏了，直接关闭投屏
        if (istp){
            closeTP();
        }else{
            int type = 0;
            if (RecordManager.canT){
                // 手机在截屏
//                if (RecordManager.getRecordManager().typeT != type)// 但投屏的类型与截屏分辨率不一致
//                {
                    RecordManager.getRecordManager().endrecord();// 先停止之前的截屏
                    RecordManager.getRecordManager().typeT = type;
                    reRecord(controlEvent);
//                }else{
//                    readyTP(controlEvent);// 一致情况直接投屏去
//                }
            }else{// 程序第一次进来的时候弹出的提示框若点击了“取消”,则要投屏的话这个提示框还要弹出
                RecordManager.getRecordManager().typeT = type;
                reRecord(controlEvent);
            }
        }
    }
     /*
     * 切换任盒或手机 重新截屏
     */
    public void reRecord(ControlEvent controlEvent) {
        RecordManager.getRecordManager().startrecord(new ConnectFTPListener(){

            @Override
            public void success() {
                readyTP(controlEvent);
            }

            @Override
            public void fail(int state) {
                QMUtil.getInstance().showToast(controlEvent.activity, R.string.must_choose);
            }
        },controlEvent.listener);
    }

    boolean endStop;
    /**
     * 开始投屏
     */
    private void readyTP(ControlEvent controlEvent){
//        TorS = 1;// TorS=1表示要投屏的操作，TorS=2表示要分享的操作.
        endStop = false;
        QMUtil.getInstance().showProgressDialog(controlEvent.activity, R.string.touping);
        if (rootPoint != null) {
            DebugLog.showLog2(this,"发送投屏信令1");
            // 首先请求屏幕投屏有没有打开 监听接口是run2（）方法中
            CommandExecutorLancher.getOnlyExecutor().controlMessage(QMCommander.SCREEN_OPEN, rootPoint.getAddress());
        }
        if (controlEvent.runnable != null) {
            new Handler().postDelayed(controlEvent.runnable, 15 * 1000);
        }
    }

    /**
     * 关闭投屏
     */
    private void closeTP(){
        endStop = true;// 是否是点击了底部【投屏】按钮来进行停止投屏的功能
        if (rootPoint != null) {
            // 已经进行投屏的盒子全部发送停止投屏的指令
            CommandExecutorBox.getOnlyExecutor().connectMessage(QMCommander.TYPE_STOP_PLAY, rootPoint.getAddress(), null);
        }
        closeState();
    }


    // 关闭通知栏
    private void closeState(){
        istp = false;
        endStop = true;// 为true的话在接收20001端口发送的200数据不处理
        // 改变状态及取消通知栏
        rootPoint.setTouPing(false);
        rootPoint.setStopByHandle(false);
        RecordManager.getRecordManager().endrecord();
        // screenCast.setBackground(getResources().getDrawable(R.color.tab_background));
        EventBus.getDefault().post(new DevOnlineEvent(3));
        if (nm != null)
            nm.cancel(1000);
    }

    @Override
    public void onReceiveHeart(RootPoint rootPoint){
        lastTime = System.currentTimeMillis();
        DebugLog.showLog2(this,"onReceiveHeart");
        if (this.rootPoint == null){
            this.rootPoint = rootPoint;
            this.rootPoint.setPlay(true);
            QMDevice.getInstance().clear();
            QMDevice.getInstance().add(rootPoint);
        }
    }


    @Override
    public void searchLancher(RootPoint rootPoint) {
        DebugLog.showLog(this,"searchLancher");
    }

    @Override
    public void connectLancher(RootPoint rootPoint) {
        DebugLog.showLog(this,"connectLancher");
    }

    @Override
    public void controlLancher(RootPoint rootPoint) {

    }

    int TorS = 1;
    @Override
    public void screenOpenLancher(RootPoint rootPoint) {
        DebugLog.showLog2(this,"screenOpenLancher");
        DebugLog.showLog2(this,"收到800："+rootPoint.getAddress());
        RootPoint rp = rootPoint;
        if (rp == null)
            return;
        // 屏幕投屏如果打开了
        if("true".equals(rootPoint.getIsrunning())){
            if (TorS == 1) {// 投屏操作发送的请求
                endStop = false;
                // 有密码的设备先进行密码连接请求。监听接口connect（）,与盒子交互的
                if (rp.getEnablepwd().equals("true")){
                    CommandExecutorBox.getOnlyExecutor().connectMessage(QMCommander.TYPE_SET_CLIENT_MSG, rootPoint.getAddress(), rp.getPassword());
                } else {
                    rp.setStopByHandle(true);
                    // 没有密码的话就发送投屏播放的指令，监听接口为toPlay（） 与盒子交互的；
                    CommandExecutorBox.getOnlyExecutor().connectMessage(QMCommander.TYPE_START_PLAY, rootPoint.getAddress(), null);
                }

            } else if (TorS == 2){// 分享操作发送的请求直接进入分享的UI
                // 有密码的设备先进行密码连接请求。监听接口connect（）,与盒子交互的
                if (rp.getEnablepwd().equals("true")) {
                    CommandExecutorBox.getOnlyExecutor().connectMessage(QMCommander.TYPE_SET_CLIENT_MSG, rootPoint.getAddress(), rp.getPassword());
                } else {
                    CommandExecutorBox.getOnlyExecutor().connectMessage(QMCommander.TYPE_SET_CLIENT_MSG, rootPoint.getAddress(), null);
                }
            }
        }else{// 屏幕投屏如果没有打开
            switch (TorS){
                case 1:
                    rp.setResponse(true);
                    // 开始选择投屏的个数是1的情况
                    if (QMDevice.getInstance().selectDeviceCount() == 1)
                        QMUtil.getInstance().closeDialog();

                    // 开始选择投屏的个数大于1的情况，没有打开屏幕投屏的设备勾选状态取消
                    if (QMDevice.getInstance().selectDeviceCount() > 1) {
                        rp.setPlay(false);
//                            boxAdapter.notifyDataSetChanged();
                        rp.setLcount(-1);
                    }
                    // 已经投屏的时候，再次启用其他盒子投屏
                    if (QMDevice.getInstance().hasScreenDevice()) {
                        rp.setPlay(false);
//                            boxAdapter.notifyDataSetChanged();
                        rp.setLcount(-1);
                        QMUtil.getInstance().closeDialog();
                    }
                    break;
                case 2:
//                        shareResponse = true;
                    QMUtil.getInstance().closeDialog();
//                        mHandler.removeCallbacks(share);
                    break;
            }
            QMUtil.getInstance().showToast2(this, rootPoint.getName() + getResources().getString(R.string.not_open));
        }
    }

    @Override
    public void passwdAlterLancher(RootPoint rootPoint) {
        DebugLog.showLog(this,"passwdAlterLancher");
    }

    @Override
    public void controlHeartBeatLancher(RootPoint rootPoint) {
        DebugLog.showLog(this,"controlHeartBeatLancher");
    }

    @Override
    public void touPingPc(RootPoint rp) {
        DebugLog.showLog(this,"touPingPc");
    }

    @Override
    public void stopPc(RootPoint rp) {
        DebugLog.showLog(this,"stopPc");
    }

    @Override
    public void pcTouPing(RootPoint rp) {
        DebugLog.showLog(this,"pcTouPing");
    }

    @Override
    public void pcCoverShare(RootPoint rp) {
        DebugLog.showLog(this,"pcCoverShare");
    }

    @Override
    public void connectBox(RootPoint rootPoint) {
        DebugLog.showLog(this,"connectBox");
    }

    @Override
    public void playBox(RootPoint rootPoint) {
        DebugLog.showLog(this,"playBox");
    }

    @Override
    public void exitBox(RootPoint rootPoint){
        DebugLog.showLog(this,"exitBox");
        closeTP();
    }

    @Override
    public void heartBeatBox(RootPoint rootPoint) {
        DebugLog.showLog(this,"heartBeatBox");
    }

    @Override
    public void screenSuccessBox(RootPoint rp){
        DebugLog.showLog(this,"screenSuccessBox");
        DebugLog.showLog(this,"投屏成功："+rp.getXmlMessage());
        String player = rp.getPlayurl();// player为空没有投上。不为空此设备投屏成功
        String identity = rp.getIdentity();// identity与自己的相等表示不是此手机投屏收到的消息
        if (rp == null) {// 返回数据中设备唯一标示的字段为空直接忽略
            DebugLog.showLog(this, "rp == null");
            return;
        }

        // 收到投屏失败信号
        if (player.equals("")){
            // 此手机发送的投屏请求得到的响应 -----当前设备投屏失败
            if (identity.equals(NetworkUtils.getNetworkMac()))
                tpFail(rp);
                // 不是是此手机发送的投屏请求得到的响应------其他设备投屏失败
            else
                tpFailOther(rp);
        }
        // 收到投屏成功信号
        else{
            // a.是此手机发送的投屏请求得到的响应 ----当前设备投屏成功
            if (player.equals(NetworkUtils.getNetworkMac())){
                rootPoint.setPlayurl(player);
                rootPoint.setResponse(true);
                rootPoint.setTouPing(true);
                rootPoint.setHeartbeat(0);
                tpSuccess(rp);
                // b1.不是此手机发送的投屏请求得到的响应----其他设备投屏覆盖
            } else{
                tpSuccessOther(rp);
            }

        }
    }

    @Override
    public void screenCoverBox(RootPoint rootPoint) {
        DebugLog.showLog(this,"screenCoverBox");
        exitTP(rootPoint, -1, R.string.exception);
    }

    /**
     * 此手机发送的投屏请求得到的响应 -----当前设备投屏失败
     *
     * @param rp
     */
    private void tpFail(RootPoint rp){
        if (endStop) {// 如果是自己手动停止投屏 发送1003指令也会收到此消息。忽略！
            return;
        }
        QMUtil.getInstance().closeDialog();
        rp.setTouPing(false);
        rp.setResponse(true);
        QMUtil.getInstance().showToast(this, R.string.exception3);
        otherBoxTP(rp);
    }

    /**
     * 不是是此手机发送的投屏请求得到的响应------其他设备投屏失败 会影响当前已投屏设备
     *
     * @param rp
     */
    private void tpFailOther(RootPoint rp) {
        if (rp.isTouPing()) {// 如果此设备在投屏的话，能够收到此消息，则另外手机投屏是失败了
            // 会影响当前已投屏设备 盒子端会显示"点播超时"的提示
            QMUtil.getInstance().showToast2(this, rp.getName() + getResources().getString(R.string.tp_interrupt));
            rp.setTouPing(false);
            otherBoxTP(rp);
        }
    }

    /**
     * 不是此手机发送的投屏请求得到的响应---投屏覆盖
     *
     * @param rp
     */
    private void tpSuccessOther(RootPoint rp){
        if (rp.isTouPing()) {// 此设备正在投屏却收到其他设备投屏的消息，是盒子屏幕被其他设备投屏覆盖的原因
            QMUtil.getInstance().showToast(this, R.string.tp_cut);
            rp.setTouPing(false);
            otherBoxTP(rp);
        }
    }

    /**
     * 投屏成功,并且是此手机发送的投屏请求得到的响应
     *
     * @param rp
     */

    private void tpSuccess(RootPoint rp){
        // 在此之前没有其他任盒在投屏
        if (!QMDevice.getInstance().hasScreenDevice()){
            openNotification(getResources().getString(R.string.tp_now));// 启动通知栏消息
        }
        TimerCloseDialog();
        // 将成功投上的盒子的状态更改为true
        if(!rp.isTouPing()){
            istp = true;
            QMUtil.getInstance().closeDialog();
            QMUtil.getInstance().showToast(this, R.string.tp_success);
            EventBus.getDefault().post(new DevOnlineEvent(2));
        }
    }

    private void TimerCloseDialog(){
        new Timer().schedule(new TimerTask(){
            @Override
            public void run(){
                QMUtil.getInstance().closeDialog();
            }
        }, 3000);
    }

    /**
     * 还有没有其他设备在投屏 在的话取消当前设备勾选 没有的话取消通知栏及恢复投屏按钮
     *
     * @param rp
     */
    public void otherBoxTP(RootPoint rp){
        if (QMDevice.getInstance().hasScreenDevice()){
            rp.setPlay(false);
            rp.setLcount(-1);
        } else {
            closeState();
            backToScreenFragment();
        }
    }

    private void backToScreenFragment() {
        ActivityManager am = (ActivityManager)getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> infos = am.getRunningTasks(100);
        for (ActivityManager.RunningTaskInfo rapi : infos) {
            if (rapi.topActivity.getClassName().equals(ACTION_BUTTON)) {
                am.moveTaskToFront(rapi.id, ActivityManager.MOVE_TASK_WITH_HOME);
                return;
            }
        }
    }


    NotificationManager nm;
    /**
     * 投屏成功后通知栏的显示
     */
    private void openNotification(String info){
        nm = (NotificationManager) getSystemService(this.NOTIFICATION_SERVICE);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this);
        RemoteViews mRemoteViews = new RemoteViews(this.getPackageName(), R.layout.main_notify);
        mRemoteViews.setImageViewResource(R.id.notify_image_view, R.mipmap.title_icon);
        mRemoteViews.setImageViewResource(R.id.notify_button_random1, R.mipmap.touping_stop);
        mRemoteViews.setTextColor(R.id.notify_button_random2, getResources().getColor(R.color.xt_red));
        // API3.0 以上的时候显示按钮，否则消失
        mRemoteViews.setTextViewText(R.id.notify_button_play_pause, info);
        // mRemoteViews.setTextViewText(R.id.notify_button_random, "停止");
        // 点击通知栏【停止】按钮的广播进行注册
        Intent buttonIntent = new Intent(ACTION_BUTTON);
        // 上一首按钮
        PendingIntent intent_paly = PendingIntent.getBroadcast(this, 1, buttonIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        mRemoteViews.setOnClickPendingIntent(R.id.notify_button_random, intent_paly);
        mRemoteViews.setOnClickPendingIntent(R.id.notify_button_random1, intent_paly);
        mRemoteViews.setOnClickPendingIntent(R.id.notify_button_random2, intent_paly);
        Intent intt = new Intent(this, MainActivity.class);
        intt.putExtra("page",0);
        intt.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pi = PendingIntent.getActivity(this, 1, intt, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContent(mRemoteViews).setContentIntent(pi).setTicker("投屏在后台运行").setWhen(System.currentTimeMillis())
                // 通知产生的时间，会在通知信息里显示
                .setContentTitle(getResources().getString(R.string.tp_now)).setPriority(Notification.PRIORITY_DEFAULT)// 设置该通知优先级
                .setSmallIcon(R.mipmap.icon2);

        Notification notify = mBuilder.build();
        notify.flags = Notification.FLAG_NO_CLEAR;
        notify.defaults |= Notification.DEFAULT_SOUND;
        nm.notify(1000, notify);
    }

    // 投屏发送心跳8秒内没有收到消息做的处理
    public void exitTP(RootPoint rp, int type, int id){
        rp.setTouPing(false);
        rootPoint.setTouPing(false);
        QMUtil.getInstance().showToast2(this, rp.getName() + getResources().getString(id));
        backToScreenFragment();
        closeState();
    }

    private class NotifiCationReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            // aa为0表示通知栏点击停止按钮的广播
            // aa为1表示的是投屏发送心跳8秒内没有接受到数据发送的广播通知
            DebugLog.showLog(this, "收到广播的code :" + intent.getIntExtra("aa", 0));
            switch (intent.getIntExtra("aa", 0)) {
                case 0:
                    closeTP();// 停止所有设备的投屏
//                    collapseStatusBar();// 收起通知栏以便显示程序的UI
                    backToScreenFragment();
                    break;
                case 1:
                    closeTP();
                    int position = intent.getIntExtra("position", 0);
                    int type = intent.getIntExtra("type", 0);
                    exitTP(QMDevice.getInstance().getRootPoint(position), type, R.string.tp_interrupt);// 任盒退出
                    DebugLog.showLog(this, "收到心跳断开的接口");
                    break;
                case 2:
                    if (nm != null) {
                        nm.cancel(1000);
                        break;
                    }
                    break;
            }
        }
    }


}

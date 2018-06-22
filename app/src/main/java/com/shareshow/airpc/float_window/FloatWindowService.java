package com.shareshow.airpc.float_window;

import android.app.ActivityManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;

import com.shareshow.aide.R;
import com.shareshow.aide.activity.MainActivity;
import com.shareshow.airpc.QMCommander;
import com.shareshow.airpc.model.RootPoint;
import com.shareshow.airpc.ports.PositionListener;
import com.shareshow.airpc.record.RecordScreenActivity;
import com.shareshow.airpc.socket.command.CommandExectuorMobile;
import com.shareshow.airpc.socket.command.CommandExecutorBox;
import com.shareshow.airpc.socket.command.CommandExecutorLancher;
import com.shareshow.airpc.socket.command.CommandListenerBox;
import com.shareshow.airpc.socket.command.CommandListenerLancher;
import com.shareshow.airpc.socket.command.CommandListenerMobile;
import com.shareshow.airpc.socket.common.QMDevice;
import com.shareshow.airpc.util.DebugLog;
import com.shareshow.airpc.util.NetworkUtils;
import com.shareshow.airpc.util.QMUtil;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Administrator on 2017/6/6 0006.
 *
 */

public class FloatWindowService extends Service implements WindowCallback,CommandListenerBox,CommandListenerLancher,CommandListenerMobile {

    private static final String MAIN_ACTION = "android.intent.action.MAINACTIVITY";

    private static final String MAIN_CATEGORY = "android.intent.category.MAINACTIVITY";

    private static final String PACKAGE_NAME = "com.xtxk.airpc";
    private static final int PC_TP_SUCCESS =0x001 ;

    private int typeT = 0;

    private Timer timer;

    private FloatWindowManager floatWindowManager;

    private boolean toMainFlag;

    private ActivityManager am;

    private boolean isTpSuccess=false;

    private Handler handler =new Handler(){

        @Override
        public void dispatchMessage(Message msg){
            super.dispatchMessage(msg);
            switch (msg.what){
                case PC_TP_SUCCESS:
                    QMUtil.getInstance().showToast(FloatWindowService.this, R.string.tp_pc_success);
                    setTpSuccess(true);
                    break;
            }
        }
    };

    public void onCreate(){
        super.onCreate();
        if(floatWindowManager==null){
            floatWindowManager = FloatWindowManager.getIntance(getApplicationContext());
        }
    }


    public IBinder onBind(Intent intent){
        CommandExecutorBox.getOnlyExecutor().setListener(this);
        CommandExecutorLancher.getOnlyExecutor().setListener(this);
        CommandExectuorMobile.getOnlyExecutor().setListener(this);

        if (timer == null){
            timer = new Timer();
            timer.scheduleAtFixedRate(new  RefreshTask(), 0, 500);
        }

        return new Mybinder();
    }


    public int onStartCommand(Intent intent, int flags, int startId) {

        return super.onStartCommand(intent, flags, startId);
    }

    public void onDestroy(){
        super.onDestroy();
        DebugLog.showLog(this, "service----->onDestroy");
        closeLauncherWindow();
        if(timer!=null){
            timer.cancel();
            timer = null;
        }
//        App.isServiceConnect=false;
    }

    private void closeLauncherWindow(){
        if(floatWindowManager!=null){
            floatWindowManager.closeWindow();
        }
    }

    class RefreshTask extends TimerTask {

        public void run(){
            if (!isMain()&&floatWindowManager!=null&&!floatWindowManager.isShow()){
                handler.post(new Runnable(){

                    public void run(){
                        floatWindowManager.createMainFloatWindow(-1, -1,true, FloatWindowService.this,false);
                    }

                });
            }

        }

    }

    private boolean isMain(){
        String localPackageName=null;
        ActivityManager mActivityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        if (android.os.Build.VERSION.SDK_INT >=21){
            localPackageName= getCurrentPkgName(getApplicationContext());
        }else{
            List<ActivityManager.RunningTaskInfo> rti = mActivityManager.getRunningTasks(1);
            localPackageName= rti.get(0).topActivity.getPackageName();
        }

        return PACKAGE_NAME.equals(localPackageName);
    }

    private String getCurrentPkgName(Context context) {
        ActivityManager.RunningAppProcessInfo  currentInfo=null;
        Field field =null;

        int START_TASK_TO_FRONT=2;
        String pkgName=null;
        try {
            field = ActivityManager.RunningAppProcessInfo.class.getDeclaredField("processState");
        } catch (Exception e) {
            e.printStackTrace();
        }
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> apps = am.getRunningAppProcesses();
        for (ActivityManager.RunningAppProcessInfo app : apps) {
            if (app.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                Integer state = null;
                try {
                    state = field.getInt( app );
                } catch (Exception e){

                    e.printStackTrace();
                }
                if (state != null && state == START_TASK_TO_FRONT) {
                    currentInfo = app;
                    break;
                }
            }
        }
        if (currentInfo != null){

            pkgName = currentInfo.pkgList[0];

        }

        return pkgName;
    }

    private boolean isHome(){
        ActivityManager mActivityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> rti = mActivityManager.getRunningTasks(1);
        return getHomes().contains(rti.get(0).topActivity.getPackageName());
    }


    private List<String> getHomes(){
        List<String> names = new ArrayList<String>();
        PackageManager packageManager = this.getPackageManager();
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        List<ResolveInfo> resolveInfo = packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
        for (ResolveInfo ri : resolveInfo) {
            names.add(ri.activityInfo.packageName);
        }
        return names;
    }

    public void ReturnMainMenu(){
        closeTimer();
        backTomainActivity();
    }

    private void backTomainActivity(){
        Intent intent = new Intent(getBaseContext(), MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    public void onStartCastScreen(){
        closeTimer();
        startCastScreen();
    }


    private void startCastScreen(){
        if(!isCastScreen()){
            return;
        }
        int chooseCount = 0;
        int type = -1;
        for (int i = 0; i < QMDevice.getInstance().getSize(); i++) {
            final RootPoint point = QMDevice.getInstance().getRootPoint(i);
            if (point.isPlay()) {
                switch (point.getdType()) {
                    case -1:
                        if (type == 1){
                            QMUtil.getInstance().showToast(this, R.string.phone_box_error);
                            return;
                        }
                        type = 0;
                        break;
                    case 0:
                    case 1:
                        if (type == 0) {
                            QMUtil.getInstance().showToast(this, R.string.phone_box_error);
                            return;
                        }
                        type = 1;
                        break;
                    // case 2:
                    // QMUtil.getInstance().showToast(this,
                    // R.string.pc_forbidden);
                    // return;
                }
                chooseCount++;
            }

            if(point.isPlay()&&point.getdType()==-1){
                CommandExecutorLancher.getOnlyExecutor().controlMessage(QMCommander.SCREEN_OPEN, point.getAddress());
                setTimeOutListener(point);
            }else if(point.isPlay()&&point.getdType()==2){
                CommandExecutorLancher.getOnlyExecutor().controlMessage(QMCommander.TYPE_PCTP, point.getAddress());
                setTimeOutListener(point);
            }else if(point.isPlay()&&(point.getdType()==1||point.getdType()==0)){
                CommandExectuorMobile.getOnlyExecutor().connectMessage(QMCommander.TYPE_SCREEN_M, point.getAddress());
                setTimeOutListener(point);
            }

        }

        if (chooseCount == 0) {
            QMUtil.getInstance().showToast(this, R.string.without_selected_device);
            backTomainActivity();
            return;
        }
    }

    /**
     * 超时回调
     * @param point
     */

    private void setTimeOutListener(final RootPoint point) {
        point.setResponse(false);
        DebugLog.showLog(this,"point:"+point);
        point.startHandler(new PositionListener() {

            public void method(int position) {
               // if (QMDevice.getInstance().hasSelectDevice()) {
                    point.setPlay(false);
                    if (point.getdType() == -1){
                        point.setLcount(-1);
                    }
                    setTpSuccess(false);
               // }
                QMUtil.getInstance().showToast2(getApplicationContext(), point.getName() + "\t" + getString(R.string.no_response));
            }

            @Override
            public void cancel() {

            }
        });
    }



    private boolean  isCastScreen() {

        if (android.os.Build.VERSION.SDK_INT < 21){
            QMUtil.getInstance().showToast(this, R.string.version_higher);
            return false;
        }


        if(!RecordScreenActivity.canT){
            backTomainActivity();
            QMUtil.getInstance().showToast(this, R.string.not_open);
            return false;
        }

        if (QMDevice.getInstance().getSize() == 0){
            QMUtil.getInstance().showToast(this, R.string.no_tp_divice);
            return false;
        }

        return true;
    }


    public void onStopCastScreen(){
        closeTimer();
        stopCastScreen();
        stopNotifyCation();
    }

    private void stopNotifyCation() {
        //关闭通知
//        Intent intent= new Intent();
//        intent.setAction(MainActivity.ACTION_BUTTON);
//        intent.putExtra("aa",2);
//        sendBroadcast(intent);
    }

    private void stopCastScreen(){

        for (int i = 0; i < QMDevice.getInstance().getSize(); i++) {
            RootPoint point = QMDevice.getInstance().getRootPoint(i);
            if(point.getdType()==-1&&point.isPlay()){
                point.setStopByHandle(false);
                CommandExecutorBox.getOnlyExecutor().connectMessage(QMCommander.TYPE_STOP_PLAY, point.getAddress(), null);
                setTpSuccess(false);
            }else if(point.getdType()==2&&point.isPlay()){
                CommandExecutorLancher.getOnlyExecutor().connectPcMessage(QMCommander.TYPE_PC_STOP, point.getAddress());
                setTpSuccess(false);
            }else if((point.getdType()==0||point.getdType()==1)&&point.isPlay()){
                CommandExectuorMobile.getOnlyExecutor().connectMessage(QMCommander.TYPE_STOP_M, point.getAddress());
                setTpSuccess(false);
            }
            point.setTouPing(false);
        }


    }


    private void closeTimer() {
        if(timer!=null){
            timer.cancel();
            timer = null;
        }

    }



    public void connectBox(RootPoint rootPoint) {
        DebugLog.showLog(this, "connectBox");

    }


    public void playBox(RootPoint rootPoint) {
        DebugLog.showLog(this, "playBox ");

    }


    public void exitBox(RootPoint rootPoint){
        DebugLog.showLog(this, "exitBox");
        RootPoint rp = QMDevice.getInstance().getSameRootPoint(rootPoint);
//		if (rp == null || !rp.isTouPing()
//				|| !QMDevice.getInstance().hasScreenDevice())// 系统不在投屏状态收到盒子退出屏幕投屏的消息不做处理
//			return;
        rp.setTouPing(false);
        QMUtil.getInstance().showToast2(getApplicationContext(),
                rp.getName() + getResources().getString(R.string.exception));
        if (QMDevice.getInstance().hasScreenDevice()) {// 如果还有其他投屏设备，此设备要取消选中状态
            rp.setPlay(false);
        }
        setTpSuccess(false);
    }


    public void heartBeatBox(RootPoint rootPoint) {
        DebugLog.showLog(this, "heartBeatBox");

    }

    public void screenSuccessBox(RootPoint rootPoint){
       RootPoint rp = QMDevice.getInstance().getSameRootPoint(rootPoint);
        DebugLog.showLog(this,  "screenSuccessBox");
        //	RootPoint rp = QMDevice.getInstance().getSameRootPoint(rootPoint);
        String player = rootPoint.getPlayurl();// player为空没有投上。不为空此设备投屏成功     //TODO
        String identity = rootPoint.getIdentity();// identity与自己的相等表示不是此手机投屏收到的消息
        if (rp == null || identity.equals(""))// 返回数据中设备唯一标示的字段为空直接忽略
            return;
        if(player.equals("")){
            QMUtil.getInstance().showToast(this, R.string.tp_box_failed);
        }else{
            rp.setResponse(true);
            rp.setTouPing(true);
            rp.setHeartbeat(0);
            DebugLog.showLog(this, "screenSuccessBox");
            QMUtil.getInstance().showToast(this, R.string.tp_box_success);
        }
        setTpSuccess(true);

    }

    @Override
    public void screenCoverBox(RootPoint rootPoint) {

    }


    public void searchLancher(RootPoint rootPoint) {
        DebugLog.showLog(this, "searchLancher");

    }


    public void connectLancher(RootPoint rootPoint){
        DebugLog.showLog(this, "connectLancher");

    }

    public void controlLancher(RootPoint rootPoint) {
        DebugLog.showLog(this, "controlLancher");

    }


    public void screenOpenLancher(RootPoint rootPoint) {
        if (rootPoint.getIsrunning().equals("true")) {

            DebugLog.showLog(this, "screenOpenLancher");
            RootPoint rp = QMDevice.getInstance().getSameRootPoint(rootPoint);
            rp.setStopByHandle(true);
            // 没有密码的话就发送投屏播放的指令，监听接口为toPlay（） 与盒子交互的；
            CommandExecutorBox.getOnlyExecutor().connectMessage(QMCommander.TYPE_START_PLAY, rp.getAddress(), null);

        }
    }


    public void passwdAlterLancher(RootPoint rootPoint) {
        DebugLog.showLog(this, "passwdAlterLancher");

    }

    public void controlHeartBeatLancher(RootPoint rootPoint) {
        DebugLog.showLog(this, "controlHeartBeatLancher");

    }


    public void touPingPc(RootPoint rootPoint){
        DebugLog.showLog(this, "touPingPc");
        RootPoint rp = QMDevice.getInstance().getSameRootPoint(rootPoint);
        rp.setResponse(true);
        new Timer().schedule(new TimerTask() {
            @Override
            public void run(){
                handler.sendEmptyMessage(PC_TP_SUCCESS);
            }
        }, 3000);
    }


    private void setTpSuccess(boolean isTpSuccess) {
        if(floatWindowManager!=null){
            floatWindowManager.setTpSuccess(isTpSuccess);
        }
    }


    public void stopPc(RootPoint rootPoint){
        DebugLog.showLog(this, "stopPc");
        RootPoint rp= QMDevice.getInstance().getSameRootPoint(rootPoint);
        if (rp.isTouPing()){// 是投屏状态才处理下面的断开情况
                rp.setTouPing(false);
                rp.setPlay(false);
           }
        QMUtil.getInstance().showToast2(getApplicationContext(),
                rp.getName() + getResources().getString(R.string.exception));
        setTpSuccess(false);
    }


    public void pcTouPing(RootPoint rp) {
        DebugLog.showLog(this, "pcTouPing");

    }

    @Override
    public void pcCoverShare(RootPoint rp) {

    }


    public void onRemoveMenu() {

        if(floatWindowManager!=null){
            floatWindowManager.removeMenuWindow();
        }

    }

    public class Mybinder extends Binder {


    }

    public void onHideWindow(){
        closeLauncherWindow();
        closeTimer();

    }


    @Override
    public void searchMobile(RootPoint rootPoint) {

        DebugLog.showLog(this,"searchMobile");

    }


    public void screenMobile(RootPoint rootPoint){
        DebugLog.showLog(this,"screenMobile");
        RootPoint rp= QMDevice.getInstance().getSameRootPoint(rootPoint);
        if(rootPoint!=null&&!rootPoint.isClient()){
            QMUtil.getInstance().showToast(this, R.string.tp_mobile_success);
            setTpSuccess(true);
            rp.setResponse(true);
        }
    }


    public void screenInterruptMobile(RootPoint rootPoint){
        DebugLog.showLog(this,"screenInterruptMobile");
        QMUtil.getInstance().showToast2(getApplicationContext(),
                rootPoint.getName() + getResources().getString(R.string.exception));
        setTpSuccess(false);
        if (rootPoint.getAddress().equals(NetworkUtils.getNetworkIP()))
            return;
        RootPoint rp = QMDevice.getInstance().getSameRootPoint(rootPoint);
        if (rp == null){
            return;
        }
        rp.setTouPing(false);
        rp.setPlay(false);


    }


    @Override
    public void heartBeatMobile(RootPoint mRootPoint){
        DebugLog.showLog(this,"heartBeatMobile");
    }

    @Override
    public void swichMobileScreen(RootPoint mRootPoint) {

    }

    @Override
    public void requestScreenFrame() {

    }

}

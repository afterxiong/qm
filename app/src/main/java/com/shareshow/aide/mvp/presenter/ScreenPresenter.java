package com.shareshow.aide.mvp.presenter;

import android.app.Activity;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.shareshow.App;
import com.shareshow.aide.R;
import com.shareshow.aide.activity.MoreScreenActivity;
import com.shareshow.aide.event.ScanEvent;
import com.shareshow.aide.mvp.model.ScreenModel;
import com.shareshow.aide.mvp.view.ScreenView;
import com.shareshow.aide.util.uuid.CacheHelper;
import com.shareshow.aide.util.uuid.Devices;
import com.shareshow.airpc.Collocation;
import com.shareshow.airpc.QMCommander;
import com.shareshow.airpc.model.RootPoint;
import com.shareshow.airpc.record.RecordManager;
import com.shareshow.airpc.socket.command.CommandExectuorMobile;
import com.shareshow.airpc.socket.command.CommandExecutorBox;
import com.shareshow.airpc.socket.command.CommandExecutorLancher;
import com.shareshow.airpc.socket.common.QMDevice;
import com.shareshow.airpc.util.DebugLog;
import com.shareshow.airpc.util.NetworkUtils;
import com.shareshow.airpc.util.QMUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;


/**
 * Created by TEST on 2017/12/11.
 */

public class ScreenPresenter extends BasePresenter<ScreenView> {

    private ScreenModel model;
    private String scanIp;
    public static boolean isScan;
    private String uuid;
    private Activity mContext;

    public ScreenPresenter(Activity activity){
         // init(activity);
    }

    private void init(Activity activity){
        model = new ScreenModel(this,activity);
        EventBus.getDefault().register(this);
        uuid = Devices.getInstace(new CacheHelper()).getKey();
                //QMUtil.getInstance().getUUID(activity);
        this.mContext = activity;
    }

    public void destroyView() {
        EventBus.getDefault().unregister(this);
    }


    //扫码返回的结果
    // http://www.shareshow.com.cn/download/index.html?{"ip":"192.168.43.1","dt":"2","ds":"4U8RUQ4MLW"}
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(ScanEvent event){
         String result = event.getResult();
         handleResult(result);
    }

    private void handleResult(String result){
        String json = result.subSequence( result.indexOf("?") + 1, result.length()).toString();
        try {
            JSONObject jsonObject = new JSONObject(json);
            scanIp = jsonObject.getString("ip");
            if(scanIp !=null){
               String net = scanIp.substring(0, scanIp.lastIndexOf("."));
               String netIp = NetworkUtils.getNetworkIP();
               if(TextUtils.isEmpty(netIp)){
                   //如果是空，就是没有联网
                   Toast.makeText(App.getApp(),"请调整到扫码设备同网段",Toast.LENGTH_SHORT).show();
               }else if((netIp.substring(0,netIp.lastIndexOf("."))).equals(net)){
                     //同一网段
                  RootPoint rootPoint = QMDevice.getInstance().getSameRootPoint(netIp);
                  if(rootPoint==null){
                      //如果为空，就是发起搜索添加
                      scanForsearch(netIp);
                  }else{
                     //打钩修改UI

                  }
               }else{

                  //不相同就要盒子调整到同一网段
                   Toast.makeText(App.getApp(),"请调整到扫码设备同网段",Toast.LENGTH_SHORT).show();
               }

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void scanForsearch(String ip){
        // 发送搜索指令
        CommandExectuorMobile.getOnlyExecutor().sendsearchMessage(QMCommander.TYPE_SEARCH_SCAN,ip);
        CommandExecutorLancher.getOnlyExecutor().replyResponseMessagePC(QMCommander.TYPE_SEARCH_SCAN_PC);
        CommandExecutorLancher.getOnlyExecutor().replyConnectMessagePC(QMCommander.TYPE_SEARCH_SCAN_PC,ip);
        //兼容旧版本的搜索指令
        CommandExecutorLancher.getOnlyExecutor().searchMessageLancher(QMCommander.TYPE_SEARCH);
        CommandExecutorLancher.getOnlyExecutor().searchRequestMessagePC(QMCommander.TYPE_SEARCH);
        CommandExectuorMobile.getOnlyExecutor().connectMessage(QMCommander.TYPE_SEARCH_M, ip);
    }


    public void OnSearch(RootPoint rootPoint){
        //收到的是改名
        if (rootPoint.getCategory() == QMCommander.TYPE_NAME){
            QMDevice.getInstance().getSameRootPoint(rootPoint).setName(rootPoint.getName());
        }

        if (Collocation.getCollocation().getIsScan() && rootPoint.getCategory() != QMCommander.TYPE_SEARCH_SCAN){
            //当是选择的是扫描互联的时候，和回复的是不是扫描互联的指令的时候,针对的是老版本及盒子的回复
            if (rootPoint!=null&&rootPoint.getAddress().equals(scanIp)|| rootPoint!=null&&((NetworkUtils.getNetworkIP().equals(rootPoint.getHost())))) {
                if (rootPoint.getUuid() == null || rootPoint.getUuid().equals("") || rootPoint.getUuid().equals("null") || rootPoint.getUuid().equals(uuid))
                    return;// 没有uuid唯一标示的过滤，是自己手机设备过滤
                Log.d("CCC","老指令"+rootPoint.getName());
                addCollotion(rootPoint);
            } else {
                return;
            }
        }else if (Collocation.getCollocation().getIsScan() && rootPoint.getCategory() == QMCommander.TYPE_SEARCH_SCAN){
            //当是选择的是扫描互联的时候，收到的也是扫描互联的指令的时候
            if (rootPoint.getUuid() == null || rootPoint.getUuid().equals("") || rootPoint.getUuid().equals("null") || rootPoint.getUuid().equals(uuid))
                return;// 没有uuid唯一标示的过滤，是自己手机设备过滤
            if (rootPoint.getAddress().equals(scanIp)||rootPoint!=null&&((NetworkUtils.getNetworkIP().equals(rootPoint.getHost())))){
                Log.d("CCC","新指令"+rootPoint.getName());
                addCollotion(rootPoint);
            }
        }else{
            //当自己选择的不是扫描互联的时候
            if (rootPoint.getName() == null || rootPoint.getName().equals(""))// 过滤没有名称的设备，不知为啥会收到这样的消息
                return;
            if (rootPoint.getdType() != -1) {// 非任盒设备
                if (rootPoint.getUuid() == null || rootPoint.getUuid().equals("") || rootPoint.getUuid().equals("null") || rootPoint.getUuid().equals(uuid))
                    return;// 没有uuid唯一标示的过滤，是自己手机设备过滤
                //    DebugLog.showLog(mContext,"搜索------isScan111:"+isScan+"指令："+rootPoint.getCategory());
                if (rootPoint.getCategory() != QMCommander.TYPE_SEARCH_SCAN || isScan||(NetworkUtils.getNetworkIP().equals(rootPoint.getHost()))||rootPoint.getAddress().equals(scanIp)){
                    addCollotion(rootPoint);
                    //    DebugLog.showLog(mContext,"添加rootPoint："+rootPoint.getName());
                }
            } else {// 任盒设备
                if (rootPoint.getAddress().equals(NetworkUtils.getNetworkIP()))
                    return;// 过滤自己收到的自己发送的搜索指令
                addCollotion(rootPoint);// 添加到本类集合中
            }
        }


        if (QMDevice.getInstance().getSize() == 1){// 默认设备搜索的时候第一个盒子没有密码就勾选上
            RootPoint point = QMDevice.getInstance().getRootPoint(0);
            switch (point.getdType()){
                case -1:
                    if (point.getEnablepwd() != null && point.getEnablepwd().equals("false")){
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
        if (scanIp != null && rootPoint.getAddress().equals(scanIp)){
            rootPoint.setPlay(true);
            scanIp = null;
        }


          //TODO 更新UI

//        mHandler.removeCallbacks(searchBox);
//        swipe_refresh.setRefreshing(false);
//        boxAdapter.notifyDataSetChanged();
//        isScan=false;
//        if(QMDevice.getInstance().getSize()!=0){
//            show_search_error.setVisibility(View.GONE);
//        }


    }


    private void addCollotion(RootPoint rootPoint) {
        if (rootPoint.getAddress().equals(NetworkUtils.getNetworkIP())) {
            return;
        }

        if (NetworkUtils.isNetworkConnected()){
            QMDevice.getInstance().add(rootPoint);
        }
    }



    public void OnScreenMobile(RootPoint rootPoint){
        DebugLog.showLog(this,"信令："+rootPoint.getCategory());

        if(QMCommander.TYPE_SCREEN_SCREEN_SUCCESS==rootPoint.getCategory()){
            return;
        }

        if(QMCommander.TYPE_NO_SCREEN_M==rootPoint.getCategory()){

            Toast.makeText(App.getApp(),rootPoint.getName()+"没有可以分享的播放源！", Toast.LENGTH_SHORT).show();
            return;
        }


        if (QMCommander.TYPE_REFUSED_M == rootPoint.getCategory()){
            Toast.makeText(App.getApp(), rootPoint.getName() + "拒绝了您的分享请求", Toast.LENGTH_LONG).show();
            return;
        }

        // 收到其他手机的提示信息,弹出提示框，需要进行投屏(收到对方分享的指令108000)
        RootPoint p=QMDevice.getInstance().getSameRootPoint(rootPoint);

        if (QMCommander.TYPE_NEED_SHARED == rootPoint.getCategory()){
            CommandExectuorMobile.getOnlyExecutor().sendconnectMessage(QMCommander.TYPE_NO_SCREEN_M, rootPoint.getAddress());
            return;
        }

        // 忽略掉收到自己发送的广播
        if (rootPoint.getAddress().equals(NetworkUtils.getNetworkIP()))
            return;

        // 手机收到来自其他手机的投屏请求 ---进入分享界面
        if (rootPoint.isClient()){
            // 此手机不在主页在其他页面做操作时 或者 手机在投屏状态 则不允许其他手机投屏上来
            if (QMDevice.getInstance().hasScreenDevice() || !QMUtil.getInstance().isForeground(mContext, MoreScreenActivity.ACTION_BUTTON)) {
                // ---发送消息给投屏的手机
                CommandExectuorMobile.getOnlyExecutor().sendconnectMessage(QMCommander.TYPE_FORBIDDEN_M, rootPoint.getAddress());
                return;
            }
             // 区分分享的是手机还是任盒

              //TODO 更新UI
//            Intent intent = new Intent(mContext, ScreenShareActivity.class);
//            intent.putExtra("rootPoint", rootPoint);
//            intent.putExtra("shareScreen",isScreenShare);
//            isScreenShare=false;
//            startActivityForResult(intent, code1);
        }
        // 手机投屏成功后得到的响应要做的处理 ----手机投屏手机成功
        else{
            // 改变底部【投屏】按钮的状态
            RootPoint rp = QMDevice.getInstance().getSameRootPoint(rootPoint);
            if (rp == null)
                return;
            if (!rp.isTouPing()){
                //TODO 更新UI
              //  tpSuccess(rp);// 手机间的投屏成功的处理
            }
        }

    }

    public void OnScreenInterruptMobile(RootPoint rootPoint){
        if(rootPoint.getCategory()==QMCommander.TYPE_STOP_SHARE_SCREEN){
            return;
        }
        // 忽略掉收到自己发送的广播
        if (rootPoint.getAddress().equals(NetworkUtils.getNetworkIP()))
            return;
        RootPoint rp = QMDevice.getInstance().getSameRootPoint(rootPoint);
        if (rp == null)
            return;
        QMUtil.getInstance().closeDialog();
        if (QMCommander.TYPE_FORBIDDEN_M == rootPoint.getCategory()){
            rp.setResponse(true);
            QMUtil.getInstance().showToast2(mContext, rp.getName() + mContext.getResources().getString(R.string.tp_forbidde));
            rp.setPlay(false);
            return;
        }

        if (rp.isTouPing()){// 是投屏状态才处理下面的断开情况

             //TODO 更新UI

//            rp.setTouPing(false);
//            if (QMDevice.getInstance().hasScreenDevice()){
//                rp.setPlay(false);
//                boxAdapter.notifyDataSetChanged();
//            }else{
//                closeState();
//                backToMoreScreenActivity();
//            }
//
//            if (QMCommander.TYPE_COVER_M == rootPoint.getCategory()){
//                QMUtil.getInstance().showToast2(mContext, mContext.getResources().getString(R.string.tp_cut));
//            } else {
//                QMUtil.getInstance().showToast2(mContext, rp.getName() + mContext.getResources().getString(R.string.exit_shared));
//            }


        }

    }

    public void OnHeartBeatMobile(RootPoint rootPoint) {

        if (rootPoint.getAddress().equals(NetworkUtils.getNetworkIP()))
            return;
        //在这里进行了连接的判断,解决了不断跳出投屏成功的问题
        if (rootPoint.isTouPing()){
            //TODO 更新UI
           // heartBeatBox(rootPoint);
        }
    }

    public void OnSwichMobileScreen(RootPoint mRootPoint){


    }

    public void OnRequestScreenFrame(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            RecordManager.getRecordManager().requestRate();
        }
    }



    public void OnConnectLancher(RootPoint rootPoint){
        //TODO 更新UI

    }

    public void OnControlLancher(RootPoint rootPoint) {
        //TODO 更新UI
    }

    public void OnScreenOpenLancher(RootPoint rootPoint) {
        //TODO 更新UI
    }

    public void OnPasswdAlterLancher(RootPoint rootPoint) {

        RootPoint rp = QMDevice.getInstance().getSameRootPoint(rootPoint);
        if (rp == null || rp.getdType() != -1)
            return;
        rp.setEnablepwd(rootPoint.getEnablepwd());
        Collocation.getCollocation().savePassWd(rp.getUuid(), "");
        if (rp.getEnablepwd().equals("true") && rp.isPlay()) {// 勾选状态
            // 收到密码为true的情况
            // 取消勾选
            QMUtil.getInstance().showToast2(mContext, rp.getName() + mContext.getResources().getString(R.string.password_changed));
            rp.setLcount(-1);
            rp.setPlay(false);
            //TODO 更新UI
          //  boxAdapter.notifyDataSetChanged();

        }
    }

    public void OnControlHeartBeatLancher(RootPoint rootPoint) {

    }
    
    public void OnTouPingPc(RootPoint rootPoint) {
        DebugLog.showLog(this,"touPingPc信令:"+rootPoint.getCategory());
        // TODO Auto-generated method stub
        RootPoint rp = QMDevice.getInstance().getSameRootPoint(rootPoint);
        if (rp == null)
            return;
        if(!rp.isTouPing()){
            //TODO 更新UI
            //tpSuccess(rp);// 手机间的投屏成功的处理
        }
    }

    public void OnStopPc(RootPoint rootPoint){
        DebugLog.showLog(this,"stopPc信令:"+rootPoint.getCategory());
        // 忽略掉收到自己发送的广播
        if (rootPoint.getAddress().equals(NetworkUtils.getNetworkIP()))
            return;
        RootPoint rp = QMDevice.getInstance().getSameRootPoint(rootPoint);
        if (rp == null)
            return;
        QMUtil.getInstance().closeDialog();

        if (rp.isTouPing()){// 是投屏状态才处理下面的断开情况
             //TODO 更新UI
//            rp.setTouPing(false);
//            if (QMDevice.getInstance().hasScreenDevice()){
//                rp.setPlay(false);
//                boxAdapter.notifyDataSetChanged();
//            }else{
//                closeState();
//                backToMoreScreenActivity();
//            }

        }
    }

    public void OnPcTouPing(RootPoint rootPoint) {

        if(QMCommander.TYPE_PC_SHARED==rootPoint.getCategory()){
            //QMUtil.getInstance().showToast2(mContext,"没有播放源，无法分享");
            CommandExecutorLancher.getOnlyExecutor().connectPcMessage(QMCommander.TYPE_PCSHARED_REFUSED,rootPoint.getAddress());
            return;
        }

        DebugLog.showLog(mContext,"pcTouPing信令:"+rootPoint.getCategory());
        if (QMCommander.TYPE_PCSHARED_REFUSED == rootPoint.getCategory()){
            QMUtil.getInstance().showToast2(mContext, rootPoint.getName()+"没有可以分享的播放源!");
            return;
        }

        if(QMCommander.TYPE_SCREEN_REFUSED == rootPoint.getCategory()){
            // QMUtil.getInstance().showToast2(mContext, rootPoint.getName() + "拒绝了您的投屏请求");
            QMUtil.getInstance().showToast2(mContext, rootPoint.getName() + mContext.getResources().getString(R.string.tp_forbidde));
            QMUtil.getInstance().closeDialog();
            RootPoint rp=QMDevice.getInstance().getSameRootPoint(rootPoint);
            rp.setResponse(true);
            return;
        }

        if (rootPoint.getCategory() == QMCommander.TYPE_PC_STOP){
            QMUtil.getInstance().closeDialog();
            RootPoint rp=QMDevice.getInstance().getSameRootPoint(rootPoint);
            rp.setResponse(true);
            if(rp.isTouPing()){
                //TODO 更新UI
               // exitTP(rp,rp.getdType(), R.string.exception);
            }
            return;
        }
//        else if (rootPoint.getCategory() == QMCommander.TYPE_PC_SHARED) {
//            //收到pc端的分享指令
//            rootPoint.setdType(2);
//            showFXDialog(rootPoint);
//        }
        else{
            if (QMDevice.getInstance().hasScreenDevice() || !QMUtil.getInstance().isForeground(mContext, MoreScreenActivity.ACTION_BUTTON)) {
                // ---发送消息给投屏的手机
                CommandExecutorLancher.getOnlyExecutor().connectPcMessage(QMCommander.TYPE_SCREEN_REFUSED , rootPoint.getAddress());
                return;
            }
            //TODO 更新UI
//            DebugLog.showLog(this,"收到的信令:"+rootPoint.getCategory());
//            rootPoint.setdType(2);// 区分分享的是手机还是任盒
//            Intent intent = new Intent(mContext, ScreenShareActivity.class);
//            intent.putExtra("rootPoint", rootPoint);
//            if(TorS==2){
//                intent.putExtra("shareScreen", true);
//            }
//            startActivityForResult(intent, code1);
        }
    }

    public void OnPcCoverShare(RootPoint rp) {

    }

    public void OnConnectBox(RootPoint rootPoint) {
        RootPoint rp = QMDevice.getInstance().getSameRootPoint(rootPoint);
        DebugLog.showLog(mContext,"信令："+rootPoint.getCategory()+"rp:"+rp);
        if (rp == null)
            return;
        //TODO 更新UI
//        if (TorS == 2){
//            // 分享连接成功的处理
//            shareResponse = true;
//            mHandler.removeCallbacks(share);
//            QMUtil.getInstance().closeDialog();
//            if (!rootPoint.isCanShare()){
//                QMUtil.getInstance().showToast(mContext, R.string.seek);
//                return;
//            }
//            Intent intent = new Intent(mContext, ScreenShareActivity.class);
//            intent.putExtra("rootPoint", rootPoint);
//            // intent.putExtra("shareScreen",isScreenShare);
//            isScreenShare=false;
//            //startActivity(intent);
//            startActivityForResult(intent, code1);
//            return;
//        }
//
//        // 投屏连接成功的处理
//        endStop = false;
//        rp.setStopByHandle(true);
        // 设备连接成功就发送投屏播放的指令，监听接口为toPlay（） 与盒子交互的；

        CommandExecutorBox.getOnlyExecutor().connectMessage(QMCommander.TYPE_START_PLAY, rootPoint.getAddress(), null);

    }

    public void OnPlayBox(RootPoint rootPoint) {
        if (QMCommander.TYPE_MOUSE_MODEL == rootPoint.getCategory())
            QMUtil.getInstance().showToast(mContext, R.string.tp_c2);
    }

    public void OnExitBox(RootPoint rootPoint) {
        RootPoint rp = QMDevice.getInstance().getSameRootPoint(rootPoint);
        if (rp == null || !rp.isTouPing() || !QMDevice.getInstance().hasScreenDevice())// 系统不在投屏状态收到盒子退出屏幕投屏的消息不做处理
            return; // 盒子没有投屏，收到盒子退出屏幕投屏的消息也不做处理(这种情况是其他手机盒子投屏了，解码端盒子
        // 盒子退出时发的消息
        if(isViewAttached()){
            getView().exitTP(rp, -1, R.string.exception);
        }
    }

    public void OnHeartBeatBox(RootPoint rootPoint) {

    }

    public void OnScreenSuccessBox(RootPoint rootPoint) {

        if(isViewAttached()){
            getView().screenSuccessBox(rootPoint);
        }
    }

    public void OnScreenCoverBox(RootPoint rootPoint) {
        RootPoint rp = QMDevice.getInstance().getSameRootPoint(rootPoint);
        if (rp == null || !rp.isTouPing() || !QMDevice.getInstance().hasScreenDevice())// 系统不在投屏状态收到盒子退出屏幕投屏的消息不做处理
            return;
        if(isViewAttached()){
            getView().exitTP(rp,-1,R.string.tp_cut);
        }

    }


}

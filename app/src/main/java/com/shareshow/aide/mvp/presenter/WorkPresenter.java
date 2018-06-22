package com.shareshow.aide.mvp.presenter;


import android.content.Context;
import android.content.Intent;
import android.media.projection.MediaProjectionManager;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.shareshow.aide.R;
import com.shareshow.aide.adapter.DatumAdapter;
import com.shareshow.aide.event.ControlEvent;
import com.shareshow.aide.event.DevOnlineEvent;
import com.shareshow.aide.event.MessageEvent;
import com.shareshow.aide.mvp.model.BoxDataModel;
import com.shareshow.aide.mvp.model.WorkModel;
import com.shareshow.aide.mvp.view.WorkView;
import com.shareshow.aide.retrofit.entity.AmplyNotify;
import com.shareshow.aide.nettyfile.DownLoadListener;
import com.shareshow.aide.retrofit.entity.AmplyNotify;
import com.shareshow.aide.retrofit.entity.TeamMorningData;
import com.shareshow.aide.util.CacheConfig;
import com.shareshow.aide.service.HeartBeatServer;
import com.shareshow.aide.util.CacheUserInfo;
import com.shareshow.aide.util.Fixed;
import com.shareshow.airpc.Collocation;
import com.shareshow.airpc.model.RootPoint;
import com.shareshow.airpc.record.RecodListener;
import com.shareshow.airpc.record.RecordManager;
import com.shareshow.airpc.util.DebugLog;
import com.shareshow.airpc.util.QMUtil;
import com.shareshow.db.AmplyNotifyDao;
import com.shareshow.db.GreenDaoManager;
import com.shareshow.db.TeamMorningDataDao;
import com.shareshow.db.VisitDataDao;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by xiongchengguang on 2017/12/7.
 */

public class WorkPresenter extends BasePresenter<WorkView> implements RecodListener {

    private WorkModel model;

    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    private BoxDataModel boxDataModel;
    private boolean isMorningRegister = false;//是否签到
    private int visitCount = 0;//拜访次数
    private int studycount = CacheUserInfo.get().getStudyRecord();//学习次数
    private boolean isBindDev = false;//是否绑定设备
    private boolean isBindDevOnline = false;//绑定设备是否在线
    private boolean isScreenProjection = false;//是否投屏

    private MediaProjectionManager mMediaProjectionManager;

    private static final int REQUEST_CODE_CAPTURE_PERM = 0X010;

    private long startStudyTime = 0;

    public boolean isScreenProjection() {
        return isScreenProjection;
    }

    public boolean isBindDevOnline() {
        return isBindDevOnline;
    }

    public boolean isMorningRegister() {
        return isMorningRegister;
    }

    public int getVisitCount() {
        visitCount = GreenDaoManager
                .getDaoSession()
                .getVisitDataDao()
                .queryBuilder()
                .where(VisitDataDao.Properties.VrDate.eq(Fixed.getToDay()))
                .where(VisitDataDao.Properties.VrUrId.eq(CacheUserInfo.get().getUserId()))
                .list()
                .size();
        return visitCount;
    }

    public int getStudycount() {
        return studycount;
    }

    public boolean isBindDev() {
        return isBindDev;
    }

    public WorkPresenter(){
        DebugLog.showLog(this,"new WorkPresenter");
        model = new WorkModel(this);
        boxDataModel = BoxDataModel.getBoxDataModel();
        if(!EventBus.getDefault().isRegistered(this)){
            EventBus.getDefault().register(this);
        }
    }

    public void getDevGetNotification() {
        String cachePhone = CacheUserInfo.get().getUserPhone();
        model.getDevGetNotification(cachePhone);
    }

    public void getStudyAndVisitData() {
        String cachePhone = CacheUserInfo.get().getUserPhone();
        model.getStudyAndVisitData(cachePhone);
    }

    public void startFlipping(){
        if (isViewAttached()) {
            List<AmplyNotify> amplyNotifyList = GreenDaoManager.getDaoSession().getAmplyNotifyDao().queryBuilder().orderDesc(AmplyNotifyDao.Properties.NosCreatetime).list();
            getView().onViewNotificationListener(amplyNotifyList);
        }
    }

    //

//    public void startFlipping() {
//        if (isViewAttached()) {
//            getView().startFlipping();
//        }
//    }
//
//    public void onVisitTrackListener(VisitRecord visitList) {
//        if (isViewAttached()) {
//            getView().onVisitTrackListener(visitList);
//        }
//    }
//
//
//    public void teamAudioData(List<TeamAudioData.DatasBean> teamDatas) {
//        if(isViewAttached()){
//            getView().onTeamAudioData(teamDatas);
//        }
//    }
//
    public void getTeamAudioData() {
        String date=dateFormat.format(System.currentTimeMillis());
        model.getTeamAudioData(date);
    }

    public void onRefurbishData() {
        getDevGetNotification();
        getStudyAndVisitData();
        getTeamAudioData();
        getVisitTrack();
    }

    public void onPresenterNotification (AmplyNotify ans) {
        if (isViewAttached())
            getView().onViewNotificationListener(ans);
    }

    public void onPresenterMorningRegister(boolean bool){
      //  if (this.isMorningRegister != bool){
            setMorningTrack(bool);
            if (isViewAttached())
                getView().onViewMorningRegister(this.isMorningRegister);
       // }

    }

    private void setMorningTrack(boolean bool){
       String today = dateFormat.format(new Date(System.currentTimeMillis()));
        if(!bool){
            List<TeamMorningData> datas = GreenDaoManager.getDaoSession().getTeamMorningDataDao().queryBuilder().where(TeamMorningDataDao.Properties.PhoneNum.eq(CacheUserInfo.get().getUserPhone())
                    , TeamMorningDataDao.Properties.Day.eq(today)).list();
            if(datas==null||datas.size()==0){
                this.isMorningRegister=false;
                CacheConfig.get().saveMorningState(today,false);
            }else{
                this.isMorningRegister=true;
                CacheConfig.get().saveMorningState(today,true);
            }
        }else{
                this.isMorningRegister=true;
                CacheConfig.get().saveMorningState(today,true);
        }
    }

//
//    public void OnDevGetStudyAndVisitCompile() {
//        if(isViewAttached()){
//            getView().OnDevGetStudyAndVisitCompile();
//        }
//    }


    /**
     * 拜访和学习资料获取完成
     */
    public void onPresenterSmvmCompile() {
        if (isViewAttached()) {
            getView().onViewSmvmCompile();
        }
    }

    public void startStudy (){
        if (DatumAdapter.isClickItem){
            startStudyTime = System.currentTimeMillis();
        }
    }

    public void endStudy (){
        if (DatumAdapter.isClickItem) {
            DatumAdapter.isClickItem = false;
            long nowTime = System.currentTimeMillis();
            long studyTime = nowTime - startStudyTime;
            if (studyTime < 1000 * 60) {
                if(isViewAttached()){
                    getView().onToastShow("培训时间太短");
                }
            } else {
                CacheUserInfo.get().addStudyRecord();
                studycount++;
                if(isViewAttached()){
                    getView().onToastShow("完成一次培训学习");
                    getView().onStudycountChange(studycount);
                }
            }
        }
    }
    boolean isTPSuccess = false;
    public void openScreenProjection(){
        if (!isViewAttached()) {
            return;
        }
        // 系统低于5.0不准投屏
        if (Build.VERSION.SDK_INT < 21) {
            QMUtil.getInstance().showToast(getView().getBindActivity(), R.string.version_higher);
            return;
        }
        if (!RecordManager.getRecordManager().supportScreen) {
            QMUtil.getInstance().showToast(getView().getBindActivity(), R.string.version_self);
            return;
        }
        ControlEvent controlEvent = new ControlEvent();
        controlEvent.controlCod = 1;
        controlEvent.activity = getView().getBindActivity();
        controlEvent.listener = this;
        isTPSuccess = false;
        controlEvent.runnable = new Runnable() {
            @Override
            public void run(){
                if (!isTPSuccess){
                    QMUtil.getInstance().closeDialog();
                    QMUtil.getInstance().showToast2(controlEvent.activity, "设备" + "\t" + controlEvent.activity.getString(R.string.no_response));
                    closeScreenProjection();
                }
            }
        };
        EventBus.getDefault().post(controlEvent);
    }

    public void closeScreenProjection(){
        ControlEvent controlEvent = new ControlEvent();
        controlEvent.controlCod = 2;
        EventBus.getDefault().post(controlEvent);
    }


    public void getVisitTrack() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String data = dateFormat.format(System.currentTimeMillis());
        String userId = CacheUserInfo.get().getUserId();
        model.getVisitTrack(data, userId);
    }


    public RootPoint getRootPoint() {
        return  HeartBeatServer.rootPoint;
    }


    public void checkNetwokrBindDevice () {
        boxDataModel.getBinds(new DownLoadListener() {
            @Override
            public void OnSuccess(int index, int isUpdate) {
                checklocalBindDevice();
            }

            @Override
            public void OnFail(int index) {
                checklocalBindDevice();
            }
        });
    }

    private void checklocalBindDevice (){
        String ids = Collocation.getCollocation().getIDS();
        if (ids != null) {
            if (!isBindDev) {
                isBindDev = true;
                if (isViewAttached())
                    getView().onIsBindDevChange(isBindDev,ids);
            }
        } else {
           if (isBindDev) {
               isBindDev = false;
               if (isViewAttached())
                    getView().onIsBindDevChange(isBindDev,null);
           }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEventSendFileComplete(MessageEvent event){
        if (event.getSign() == MessageEvent.SEND_FILE_COMPILETE){
            if(isViewAttached())
                getView().onViewNotificationListener(event.getString());
        }
    }

    //网络绑定设备在线状态
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onBindDevonlineState(DevOnlineEvent devOnlineEvent){
        if (devOnlineEvent.onlineState == 0){
            if (isBindDevOnline) {
                isBindDevOnline = false;
                if(isViewAttached())
                    getView().onBindDevOnlineChange(isBindDevOnline);
            }
        } else if (devOnlineEvent.onlineState == 1){
            if (!isBindDevOnline ){
                isBindDevOnline = true;
                if(isViewAttached())
                    getView().onBindDevOnlineChange(isBindDevOnline);
            }
        } else if (devOnlineEvent.onlineState == 2){
            this.isTPSuccess = true;
            if (!isScreenProjection){
                this.isScreenProjection = true;
                if(isViewAttached())
                    getView().onScreenProjectionChang(this.isScreenProjection);
            }
            if (isViewAttached()) {
                if(QMUtil.getInstance().isForeground(getView().getBindActivity(), HeartBeatServer.ACTION_BUTTON)||getView().isResume()){
                    //   if(rp.getdType()!=2){
//                    getHome();// 让程序在后台运行
                    Intent i = new Intent(Intent.ACTION_MAIN);
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
                    i.addCategory(Intent.CATEGORY_HOME);
                    getView().getBindActivity().startActivity(i);
                    //  }
                }else{
                    DebugLog.showLog(this,"不在前台");
                }
            }
        } else if (devOnlineEvent.onlineState == 3){
                this.isScreenProjection = false;
                if(isViewAttached())
                    getView().onScreenProjectionChang(this.isScreenProjection);
        }
    }

    @Override
    public void startRecod(){
        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP){
            if (!isViewAttached()) {
                return;
            }
            mMediaProjectionManager = (MediaProjectionManager)getView().getBindActivity().getSystemService(Context.MEDIA_PROJECTION_SERVICE);
            Intent permissionIntent = mMediaProjectionManager.createScreenCaptureIntent();
            ((Fragment)getView()).startActivityForResult(permissionIntent, REQUEST_CODE_CAPTURE_PERM);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        DebugLog.showLog(this,"onActivityResult");
        if (requestCode == REQUEST_CODE_CAPTURE_PERM) {
            RecordManager.getRecordManager().onActivityResult(mMediaProjectionManager, requestCode, resultCode, data);
        }
    }

    public void reasle(){
        EventBus.getDefault().unregister(this);
    }
}

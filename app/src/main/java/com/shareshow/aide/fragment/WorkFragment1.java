//package com.shareshow.aide.fragment;
//
//import android.app.ActivityManager;
//import android.app.Notification;
//import android.app.NotificationManager;
//import android.app.PendingIntent;
//import android.content.BroadcastReceiver;
//import android.content.ComponentName;
//import android.content.Context;
//import android.content.Intent;
//import android.content.IntentFilter;
//import android.media.projection.MediaProjectionManager;
//import android.os.Build;
//import android.os.Bundle;
//import android.os.Handler;
//import android.support.annotation.Nullable;
//import android.support.annotation.RequiresApi;
//import android.support.v4.app.NotificationCompat;
//import android.support.v4.widget.SwipeRefreshLayout;
//import android.support.v7.widget.DividerItemDecoration;
//import android.support.v7.widget.LinearLayoutManager;
//import android.support.v7.widget.RecyclerView;
//import android.text.SpannableString;
//import android.text.Spanned;
//import android.text.TextPaint;
//import android.text.method.LinkMovementMethod;
//import android.text.style.ClickableSpan;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.widget.LinearLayout;
//import android.widget.RemoteViews;
//import android.widget.TextView;
//import android.widget.Toast;
//import android.widget.ViewFlipper;
//
//import com.shareshow.aide.R;
//import com.shareshow.aide.activity.MainActivity;
//import com.shareshow.aide.activity.MoreActivity;
//import com.shareshow.aide.adapter.DatumAdapter;
//import com.shareshow.aide.dialog.AmplyNotifyDialog;
//import com.shareshow.aide.event.DevOnlineEvent;
//import com.shareshow.aide.event.GetAdEvent;
//import com.shareshow.aide.event.MessageEvent;
//import com.shareshow.aide.event.NetworkEvent;
//import com.shareshow.aide.mvp.model.BoxDataModel;
//import com.shareshow.aide.mvp.presenter.WorkPresenter;
//import com.shareshow.aide.mvp.view.WorkView;
//import com.shareshow.aide.nettyfile.DownLoadListener;
//import com.shareshow.aide.retrofit.entity.AmplyNotify;
//import com.shareshow.aide.retrofit.entity.StudyMaterialsVisitingMaterials;
//import com.shareshow.aide.retrofit.entity.TeamAudioData;
//import com.shareshow.aide.retrofit.entity.TeamMorningData;
//import com.shareshow.aide.retrofit.entity.VisitRecord;
//import com.shareshow.aide.service.HeartBeatServer;
//import com.shareshow.aide.util.CacheUserInfo;
//import com.shareshow.airpc.Collocation;
//import com.shareshow.airpc.QMCommander;
//import com.shareshow.airpc.activity.ControlActivity;
//import com.shareshow.airpc.activity.QuiteFileActivity;
//import com.shareshow.airpc.model.RootPoint;
//import com.shareshow.airpc.ports.ConnectFTPListener;
//import com.shareshow.airpc.ports.PositionListener;
//import com.shareshow.airpc.record.RecodListener;
//import com.shareshow.airpc.record.RecordManager;
//import com.shareshow.airpc.socket.command.CommandExecutorBox;
//import com.shareshow.airpc.socket.command.CommandExecutorLancher;
//import com.shareshow.airpc.socket.command.CommandListenerBox;
//import com.shareshow.airpc.socket.command.CommandListenerLancher;
//import com.shareshow.airpc.socket.common.QMDevice;
//import com.shareshow.airpc.util.DebugLog;
//import com.shareshow.airpc.util.NetworkUtils;
//import com.shareshow.airpc.util.QMDialog;
//import com.shareshow.airpc.util.QMUtil;
//import com.shareshow.db.AmplyNotifyDao;
//import com.shareshow.db.GreenDaoManager;
//import com.shareshow.db.TeamMorningDataDao;
//import com.shareshow.db.VisitDataDao;
//import com.socks.library.KLog;
//import com.tencent.tauth.IUiListener;
//import com.tencent.tauth.Tencent;
//import com.tencent.tauth.UiError;
//
//import org.greenrobot.eventbus.EventBus;
//import org.greenrobot.eventbus.Subscribe;
//import org.greenrobot.eventbus.ThreadMode;
//
//import java.io.File;
//import java.text.SimpleDateFormat;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Timer;
//import java.util.TimerTask;
//
//import butterknife.BindView;
//import butterknife.ButterKnife;
//import butterknife.OnClick;
//
//import static com.shareshow.aide.service.HeartBeatServer.rootPoint;
//
//
///**
// * Created by xiongchengguang on 2017/12/7.
// */
//
//public class WorkFragment1 extends BaseFragment<WorkView, WorkPresenter> implements WorkView, SwipeRefreshLayout.OnRefreshListener,RecodListener,CommandListenerLancher,CommandListenerBox {
//
//    @BindView(R.id.flipper)
//    public ViewFlipper flipper;
//    @BindView(R.id.more_affiche_notify)
//    public TextView moreAfficheNotify;
//    @BindView(R.id.picRecycler)
//    public RecyclerView picRecycler;
//    @BindView(R.id.refresh_layout)
//    public SwipeRefreshLayout refreshLayout;
//    @BindView(R.id.tv_visit_count)
//    public TextView visitCount;
//    @BindView(R.id.tv_work_tip4)
//    public TextView workTip4;
//    @BindView(R.id.tv_morning_track)
//    public TextView morningTrack;
//    @BindView(R.id.donwloading)
//    public TextView donwloading;
//    @BindView(R.id.network_disabled)
//    public TextView networkDisabled;
//    @BindView(R.id.emputy_state)
//    LinearLayout emputyState;
//
//    @BindView(R.id.device_group_view)
//    public View deviceGroupView;
//    @BindView(R.id.guide_text)
//    public TextView guideText;
//    @BindView(R.id.bind_device_state)
//    public TextView bindDevState;
//    @BindView(R.id.screen_projection)
//    public TextView screenProjection;
//    @BindView(R.id.work_file)
//    public View workFile;
//    @BindView(R.id.remote_control)
//    public View remoteControl;
//    private BoxDataModel boxDataModel;
//
//    private DatumAdapter adapter;
//    private LinearLayoutManager manager;
//    private List<StudyMaterialsVisitingMaterials> totalData = new ArrayList<>();
//    private List<File> netData = new ArrayList<>();
//    private boolean isFragmentCreate;
//    private boolean isMorningTrack = false;
//
//    private MediaProjectionManager mMediaProjectionManager;
//    private static final int REQUEST_CODE_CAPTURE_PERM = 0X010;
//    public static final int code1 = 223;// Activity回调
//
//    @Override
//    public WorkPresenter createPresenter() {
//        return new WorkPresenter();
//    }
//
//    @Override
//    public int getLayoutResource() {
//        return R.layout.fragment_work;
//    }
//
//    @Override
//    public void onCreate(@Nullable Bundle savedInstanceState){
//        super.onCreate(savedInstanceState);
//        EventBus.getDefault().register(this);
//        boxDataModel = BoxDataModel.getBoxDataModel();
//    }
//
//    @Override
//    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
//        ButterKnife.bind(this, view);
//        RecordManager.canT = false;
//        initOtherCast();
//        initView();
//        getMorningTrack();
//        autoRefreshData();
//    }
//
//    public void initView(){
//        guideText.setHighlightColor(getResources().getColor(android.R.color.transparent));
//        SpannableString spannableString = new SpannableString("请打开任易屏设备,\n连接到手机相同的 \nWiFi或者开启热点.");
//        spannableString.setSpan(new ClickableSpan() {
//            @Override
//            public void onClick(View widget) {
//                ComponentName cm = new ComponentName("com.android.settings","com.android.settings.TetherSettings");
//                Intent intent = new Intent("android.intent.action.VIEW");
//                intent.setComponent(cm);
//                getActivity().startActivity(intent);
//            }
//
//            @Override
//            public void updateDrawState(TextPaint ds) {
//                ds.setColor(getResources().getColor(R.color.blue));
//                ds.setUnderlineText(true);
//            }
//        },26,30, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
//        guideText.setText(spannableString);
//        guideText.setMovementMethod(LinkMovementMethod.getInstance());
//        workTip4.setText("培训学习"+CacheUserInfo.get().getStudyRecord()+"次");
//        refreshLayout.setProgressBackgroundColorSchemeResource(android.R.color.white);
//        refreshLayout.setColorSchemeResources(R.color.colorAccent, R.color.colorPrimary, R.color.colorPrimaryDark);
//        refreshLayout.setOnRefreshListener(this);
//        adapter = new DatumAdapter(getActivity(), totalData);
//        manager = new LinearLayoutManager(getActivity());
//        picRecycler.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));
//        picRecycler.setLayoutManager(manager);
//        picRecycler.setAdapter(adapter);
//        isFragmentCreate = true;
//    }
//
//
//    private void autoRefreshData(){
//        refreshLayout.post(new Runnable() {
//            @Override
//            public void run() {
//                refreshLayout.setRefreshing(false);
//                refreshLayout.setEnabled(false);
//                onRefreshData();
//            }
//        });
//    }
//
//    @Override
//    public void onRefresh(){
//        onRefreshData();
//    }
//
//    private void onRefreshData(){
//        netData.clear();
//        flipper.removeAllViews();
//        moreAfficheNotify.setVisibility(View.GONE);
//        adapter.notifyDataSetChanged();
//        donwloading.setVisibility(View.VISIBLE);
//        presenter.getDevGetNotification();
//        presenter.getStudyAndVisitData();
//        presenter.getTeamAudioData();
//        presenter.getVisitTrack();
//        startFlipping();
//        EventBus.getDefault().post(new GetAdEvent());
//    }
//
//    private boolean isResume = false;
//    @Override
//    public void onResume(){
//        super.onResume();
//        onEndStudy();
//        isResume=true;
//        CommandExecutorLancher.getOnlyExecutor().setListener(this);
//        CommandExecutorBox.getOnlyExecutor().setListener(this);// 20001端口监听
//        if (checkNetworkDisabled()) {
//            checkNetwokrBindDevice();
//        } else {
//            checklocalBindDevice();
//        }
//    }
//
//    long startStudyTime = 0;
//
//    private void onStartStudy (){
//        if (DatumAdapter.isClickItem){
//            DebugLog.showLog(this, "检测到文件学习开始动作");
//            startStudyTime = System.currentTimeMillis();
//        }
//    }
//
//    private void onEndStudy (){
//        if (DatumAdapter.isClickItem) {
//            DatumAdapter.isClickItem = false;
//            long nowTime = System.currentTimeMillis();
//            long studyTime = nowTime - startStudyTime;
//            if (studyTime < 1000 * 60) {
//                Toast.makeText(getActivity(),"培训时间太短",Toast.LENGTH_SHORT).show();
//            } else {
//                CacheUserInfo.get().addStudyRecord();
//                Toast.makeText(getActivity(),"完成一次培训学习",Toast.LENGTH_SHORT).show();
//                workTip4.setText("培训学习"+CacheUserInfo.get().getStudyRecord()+"次");
//            }
//        }
//    }
//
//    private void checkNetwokrBindDevice () {
//        boxDataModel.getBinds(new DownLoadListener() {
//            @Override
//            public void OnSuccess(int index, int isUpdate) {
//                checklocalBindDevice();
//            }
//
//            @Override
//            public void OnFail(int index) {
//                checklocalBindDevice();
//            }
//        });
//    }
//
//    private void checklocalBindDevice (){
//        String ids = Collocation.getCollocation().getIDS();
//        if (ids != null) {
//            if (!HeartBeatServer.isOnline) {
//                bindDevState.setText("(离线)");
//                guideText.setVisibility(View.VISIBLE);
//                screenProjection.setVisibility(View.GONE);
//                workFile.setVisibility(View.GONE);
//                remoteControl.setVisibility(View.GONE);
//            } else {
//                bindDevState.setText("(在线)");
//                guideText.setVisibility(View.GONE);
//                screenProjection.setVisibility(View.VISIBLE);
//                workFile.setVisibility(View.VISIBLE);
//                remoteControl.setVisibility(View.VISIBLE);
//                setTpState(istp);
//            }
//            deviceGroupView.setVisibility(View.VISIBLE);
//        } else {
//            //不存在设备绑定
//            deviceGroupView.setVisibility(View.GONE);
//        }
//        Intent intent = new Intent(getActivity(), HeartBeatServer.class);
//        intent.putExtra(HeartBeatServer.DEV_IDS,ids);
//        getActivity().startService(intent);
//    }
//
//
//    @Override
//    public void onPause() {
//        super.onPause();
//        onStartStudy();
//        isResume=false;
////        refreshLayout.setRefreshing(false);
//        refreshLayout.setEnabled(true);
//        CommandExecutorLancher.getOnlyExecutor().setListener(null);
//        CommandExecutorBox.getOnlyExecutor().setListener(null);// 20001端口监听
//    }
//
////    @Override
////    public void addFile(File file) {
////        boolean flag = false;
////        for (File localfile : totalData){
////            if (localfile.getName().equals(file.getName())) {
////                flag = true;
////                break;
////            }
////        }
////        netData.add(file);
////        if (!flag) {
////            DebugLog.showLog(this,"添加线上文件："+file.getName());
////            donwloading.setVisibility(View.VISIBLE);
////            totalData.add(file);
////            emputyState.setVisibility(View.GONE);
////            adapter.notifyDataSetChanged();
////        }
////    }
////
////    @Override
////    public void error(Throwable e) {
////        netData.clear();
////        localFileload();
////    }
////
////    private void localFileload () {
////        totalData.clear();
//////        refreshLayout.setRefreshing(false);
////        refreshLayout.setEnabled(true);
////        donwloading.setVisibility(View.GONE);
////        String cachePhone = CacheUserInfo.get().getUserPhone();
////        String pathname = Fixed.getRootPath() + File.separator + cachePhone + File.separator + "StudyVisitData";
////        File file = new File(pathname);
////        File[] files = file.listFiles();
////        if (files == null || files.length == 0) {
////            emputyState.setVisibility(View.VISIBLE);
////            return;
////        }
////        totalData.addAll(Arrays.asList(files));
////        emputyState.setVisibility(View.GONE);
//////         Collections.sort(totalData, new FileComparator());
////        adapter.notifyDataSetChanged();
////    }
////
////
////
////    @Override
////    public void onComplete() {
////        for(int i = totalData.size() - 1; i > 0 ; i --) {
////            boolean flag = false;
////            for(File netfile : netData) {
////                if (totalData.get(i).getName().equals(netfile.getName())) {
////                    flag = true;
////                    break;
////                }
////            }
////            if (!flag) {
////                File file = totalData.remove(i);
////                file.delete();
////                DebugLog.showLog(this,"刷新文件检测线上文件："+file.getName()+"不存在，删除本地文件");
////            }
////        }
////        adapter.notifyDataSetChanged();
////        Observable
////                .create(new ObservableOnSubscribe<Boolean>() {
////                    @Override
////                    public void subscribe(ObservableEmitter<Boolean> emitter) throws Exception {
////                        while (!CacheConfig.get().getAdDodnloadComplete()) {
////                            emitter.onNext(false);
////                            SystemClock.sleep(1000);
////                        }
////                        emitter.onNext(true);
////                        emitter.onComplete();
////                    }
////                })
////                .subscribeOn(Schedulers.newThread())
////                .observeOn(AndroidSchedulers.mainThread())
////                .subscribe(new Consumer<Boolean>() {
////                    @Override
////                    public void accept(Boolean aBoolean) throws Exception {
////                        if (aBoolean) {
////                            CacheConfig.get().setAdDodnloadComplete();
////                            donwloading.setVisibility(View.GONE);
//////                            refreshLayout.setRefreshing(false);
////                            refreshLayout.setEnabled(true);
////                        }
////                    }
////                });
////        EventBus.getDefault().post(new FileDownLoadEvent());
////        if (totalData.size() == 0) {
////            emputyState.setVisibility(View.VISIBLE);
////        } else {
////            emputyState.setVisibility(View.GONE);
////        }
////    }
//
//    @Override
//    public void onTeamAudioData(List<TeamAudioData.DatasBean> teamDatas) {
//        if (teamDatas != null) {
//            for (TeamAudioData.DatasBean teamData : teamDatas) {
//                if (teamData.getChmFilename().equals(CacheUserInfo.get().getUserName())) {
//                    this.isMorningTrack = true;
//                    getMorningTrack();
//                    return;
//                }
//            }
//        }
//
//        this.isMorningTrack = false;
//    }
//
//    @Override
//    public void OnDevGetStudyAndVisitCompile() {
//
//    }
//
//    //网络状态监听回调
//    @Subscribe(threadMode = ThreadMode.MAIN)
//    public void onEventMainThread(NetworkEvent event) {
//        checkNetworkDisabled();
//    }
//
//    //网络绑定设备在线状态
//    @Subscribe(threadMode = ThreadMode.MAIN)
//    public void onBindDevonlineState(DevOnlineEvent devOnlineEvent) {
//       if (devOnlineEvent.onlineState == 0) {
//           bindDevState.setText("(离线)");
//           guideText.setVisibility(View.VISIBLE);
//           screenProjection.setVisibility(View.GONE);
//           workFile.setVisibility(View.GONE);
//           remoteControl.setVisibility(View.GONE);
//       } else if (devOnlineEvent.onlineState == 1) {
//           bindDevState.setText("(在线)");
//           guideText.setVisibility(View.GONE);
//           screenProjection.setVisibility(View.VISIBLE);
//           workFile.setVisibility(View.VISIBLE);
//           remoteControl.setVisibility(View.VISIBLE);
//       }
//    }
//
//
//    @Subscribe(threadMode = ThreadMode.MAIN)
//    public void onMessageEventSendFileComplete(MessageEvent event){
//        if (event.getSign() == MessageEvent.SEND_FILE_COMPILETE){
//            flipper.removeAllViews();
//            View view = LayoutInflater.from(getActivity()).inflate(R.layout.item_flipper, null);
//            TextView value = view.findViewById(R.id.flipper_value);
//            value.setText(event.getString());
//            flipper.addView(view);
//            moreAfficheNotify.setVisibility(View.VISIBLE);
//        }
//    }
//
//    public boolean checkNetworkDisabled(){
//        boolean disabled = NetworkUtils.isNetworkConnected();
//        KLog.d("网络是否连接:" + disabled);
//        if (disabled) {
//            networkDisabled.setVisibility(View.GONE);
//        } else {
//            networkDisabled.setVisibility(View.VISIBLE);
//            donwloading.setVisibility(View.GONE);
//        }
//        return disabled;
//    }
//
//    @Override
//    public void startFlipping(){
//        List<AmplyNotify> afficheNotifyList = GreenDaoManager.getDaoSession().getAmplyNotifyDao().queryBuilder().orderDesc(AmplyNotifyDao.Properties.NosCreatetime).list();
//        if (afficheNotifyList.size() > 0) {
//            flipper.removeAllViews();
//            View view = LayoutInflater.from(getActivity()).inflate(R.layout.item_flipper, null);
//            TextView value = view.findViewById(R.id.flipper_value);
//            value.setText(afficheNotifyList.get(0).getNosTitle());
//            flipper.addView(view);
//            moreAfficheNotify.setVisibility(View.VISIBLE);
//        }
//        for (int i = 0; i < afficheNotifyList.size(); i++) {
//            flipper.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    int index = flipper.getDisplayedChild();
//                    AmplyNotifyDialog.newInstance(afficheNotifyList.get(index)).show(getActivity().getFragmentManager(), "NotifyDetailedDialog");
//                }
//            });
//        }
//    }
//
//    @Override
//    public void onVisitTrackListener(VisitRecord visitList) {
//        List<VisitRecord.VisitData> dataList = visitList.getDatas();
//        String userId=CacheUserInfo.get().getUserId();
//        int notCommit = GreenDaoManager.getDaoSession().getVisitDataDao().queryBuilder().where(VisitDataDao.Properties.Today.eq(getToDay())).where(VisitDataDao.Properties.UserId.eq(userId)).list().size();
//        if (visitList != null) {
//            visitCount.setText("拜访客户" + (dataList.size() + notCommit) + "次");
//        } else {
//            visitCount.setText("拜访客户" + 0 + "次");
//        }
//    }
//
//    public String getToDay() {
//        long currenTime = System.currentTimeMillis();
//        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
//        String data = dateFormat.format(currenTime);
//        return data;
//    }
//
//
//    @OnClick(R.id.more_affiche_notify)
//    public void moreNotification() {
//        Intent intent = new Intent(getActivity(), MoreActivity.class);
//        Bundle bundle = new Bundle();
//        intent.putExtras(bundle);
//        startActivity(intent);
//    }
//
//    @OnClick(R.id.screen_projection)
//    public void openScreenProjection() {
//        //开启投屏
//        tp();
//    }
//
//    // 点击投屏按钮
//
//    public void tp(){
//        // 系统低于5.0不准投屏
//        if (Build.VERSION.SDK_INT < 21) {
//            QMUtil.getInstance().showToast(getActivity(), R.string.version_higher);
//            return;
//        }
//        if (!RecordManager.getRecordManager().supportScreen) {
//            QMUtil.getInstance().showToast(getActivity(), R.string.version_self);
//            return;
//        }
//        judgeTP();
//    }
//
//    private boolean istp = false;
//    /**
//     * 判断能否投屏
//     */
//    private void judgeTP(){
//        // 如果已经在投屏了，直接关闭投屏
//        if (istp){
//            closeTP();
//        }else{
//            int type = 0;
//            if (RecordManager.canT){
//                // 手机在截屏
//                if (RecordManager.getRecordManager().typeT != type)// 但投屏的类型与截屏分辨率不一致
//                {
//                    RecordManager.getRecordManager().endrecord();// 先停止之前的截屏
//                    RecordManager.getRecordManager().typeT = type;
//                    reRecord();
//                }else{
//                    readyTP();// 一致情况直接投屏去
//                }
//            }else{// 程序第一次进来的时候弹出的提示框若点击了“取消”,则要投屏的话这个提示框还要弹出
//                RecordManager.getRecordManager().typeT = type;
//                reRecord();
//            }
//        }
//    }
//
//    /**
//     * 切换任盒或手机 重新截屏
//     */
//    public void reRecord() {
//        RecordManager.getRecordManager().startrecord(new ConnectFTPListener(){
//
//            @Override
//            public void success() {
//                readyTP();
//            }
//
//            @Override
//            public void fail(int state) {
//                QMUtil.getInstance().showToast(getActivity(), R.string.must_choose);
//            }
//        },this);
//    }
//
//
//    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
//    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        Tencent.onActivityResultData(requestCode, resultCode, data, new IUiListener() {
//            @Override
//            public void onComplete(Object o) {
//                DebugLog.showLog(this,"onComplete");
//            }
//
//            @Override
//            public void onError(UiError uiError) {
//                DebugLog.showLog(this,"onError");
//            }
//
//            @Override
//            public void onCancel() {
//                DebugLog.showLog(this,"onCancel");
//            }
//        });
//        if (requestCode == REQUEST_CODE_CAPTURE_PERM) {
//            RecordManager.getRecordManager().onActivityResult(mMediaProjectionManager, requestCode, resultCode, data);
//        }
//    }
//
//    @Override
//    public void startRecod(){
//        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP){
//            mMediaProjectionManager = (MediaProjectionManager)getActivity().getSystemService(Context.MEDIA_PROJECTION_SERVICE);
//            Intent permissionIntent = mMediaProjectionManager.createScreenCaptureIntent();
//            startActivityForResult(permissionIntent, REQUEST_CODE_CAPTURE_PERM);
//        }
//    }
//
//    boolean isTPSuccess = false;
//    /**
//     * 开始投屏
//     */
//    private void readyTP(){
////        TorS = 1;// TorS=1表示要投屏的操作，TorS=2表示要分享的操作.
//        endStop = false;
//        QMUtil.getInstance().showProgressDialog(getActivity(), R.string.touping);
//     if (rootPoint != null) {
//         // 首先请求屏幕投屏有没有打开 监听接口是run2（）方法中
//         CommandExecutorLancher.getOnlyExecutor().controlMessage(QMCommander.SCREEN_OPEN, rootPoint.getAddress());
//     }
//        isTPSuccess = false;
//    new Handler().postDelayed(new Runnable() {
//        @Override
//        public void run() {
//            if (!isTPSuccess) {
//                QMUtil.getInstance().closeDialog();
//                QMUtil.getInstance().showToast2(getActivity(), "设备" + "\t" + getString(R.string.no_response));
//            }
//        }
//    }, 15 * 1000);
//    }
//
//
////    //超时就直接发送退出的消息
////    private void stopTp(RootPoint rootPoint) {
////
////        for (int i = 0; i < 3; i++) {
////            if (rootPoint.getdType() == -1) {
////                rootPoint.setStopByHandle(false);
////                // 已经进行投屏的盒子全部发送停止投屏的指令
////                CommandExecutorBox.getOnlyExecutor().connectMessage(QMCommander.TYPE_STOP_PLAY, rootPoint.getAddress(), null);
////            } else if (rootPoint.getdType() == 2) {
////                // 发送停止投屏到PC端
////                CommandExecutorLancher.getOnlyExecutor().connectPcMessage(QMCommander.TYPE_PC_STOP, rootPoint.getAddress());
////            }
////            try {
////                Thread.sleep(100);
////            } catch (InterruptedException e) {
////                e.printStackTrace();
////            }
////
////        }
////    }
//
//    @OnClick(R.id.work_file)
//    public void openWorkFile(){
//        //进入工作文件
//        if(QMDevice.getInstance().hasScreenDevice()){
//            close_TP(R.string.close_tp_tip3);
//        }else{
//            startFileShare();
//        }
//    }
//
//
//    /**
//     * 文件共享
//     */
//    private void startFileShare(){
//        int chooseCount = QMDevice.getInstance().selectDeviceCount();
//        Intent intent = new Intent(getActivity(), QuiteFileActivity.class);
//        startActivity(intent);
//    }
//
//    /**
//     * 开启分享关闭投屏的提示框
//     */
//    private void close_TP(int id) {
//        new QMDialog(getActivity(), id, R.string.yes, new PositionListener() {
//            @Override
//            public void method(int position) {
//                closeTP();
//            }
//
//        });
//    }
//
//    /**
//     * 关闭投屏
//     */
//    private void closeTP(){
//        endStop = true;// 是否是点击了底部【投屏】按钮来进行停止投屏的功能
//        sendStop();
//        RecordManager.getRecordManager().endrecord();
//    }
//
//    /**
//     * 停止所有的已经点起来的设备 发送停止投屏的指令
//     */
//    private void sendStop(){
//        setTpState(false);
////        QMDevice.getInstance().oprationScreenDevice(new BoxOnClickListener(){
////            @Override
////            public void onClick(RootPoint rootPoint){
////                rootPoint.setTouPing(false);
////                for (int i = 0; i < 3; i++){
////                    if (rootPoint.getdType() == -1){
////                        rootPoint.setStopByHandle(false);
//        if (rootPoint != null) {
//            // 已经进行投屏的盒子全部发送停止投屏的指令
//            CommandExecutorBox.getOnlyExecutor().connectMessage(QMCommander.TYPE_STOP_PLAY, rootPoint.getAddress(), null);
//        }
////                    } else if (rootPoint.getdType() == 2){
////                        // 发送停止投屏到PC端
////                        CommandExecutorLancher.getOnlyExecutor().connectPcMessage(QMCommander.TYPE_PC_STOP, rootPoint.getAddress());
////                    }else{
////                        CommandExectuorMobile.getOnlyExecutor().connectMessage(QMCommander.TYPE_STOP_M, rootPoint.getAddress());
////                    }
////
////                    try {
////                        Thread.sleep(100);
////                    } catch (InterruptedException e) {
////                        e.printStackTrace();
////                    }
////                }
////            }
////        });
//        closeState();
//    }
//
//    // 关闭通知栏
//    private void closeState(){
//        endStop = true;// 为true的话在接收20001端口发送的200数据不处理
//        // 改变状态及取消通知栏
//        RecordManager.getRecordManager().endrecord();
//        // screenCast.setBackground(getResources().getDrawable(R.color.tab_background));
//        if (nm != null)
//            nm.cancel(1000);
//    }
//
//
//    @OnClick(R.id.remote_control)
//    public void openRemoteControl() {
//        //开启远程控制
//        control();
//    }
//
//    // 点击远程控制按钮
//    public void control(){
//        // 要控制之前如果已经投屏了先关闭投屏
//        if (istp){
//            close_TP(R.string.close_tp_tip2);
//        }else{
//            shareOrYk(1);
//        }
//    }
//
//    /**
//     * 开始分享或远程控制
//     */
//    private void shareOrYk(int aa){
////        int chooseCount = 0;
////        RootPoint rp = null;// 需要分享或控制的设备
////        for (int i = 0; i < QMDevice.getInstance().getSize(); i++) {
////            RootPoint point = QMDevice.getInstance().getRootPoint(i);
////            if (point.isPlay()){
////                // switch (point.getdType()) {
////                // case -1:
////                chooseCount++;
////                if (chooseCount > 1){
////                    switch (aa) {
//////                        case 0:
//////                            QMUtil.getInstance().showToast(mContext, R.string.just_one_can_share);
//////                            return;
////                        case 1:
////                            QMUtil.getInstance().showToast(getActivity(), R.string.just_one_can_control);
////                            return;
////                    }
////                }
////
////                rp = point;
////            }
////        }
//
////        if (chooseCount == 0){
////            QMUtil.getInstance().showToast(getActivity(), R.string.without_selected_device);
////            return;
////        }
////
////        if(rp!=null&&rp.getdType()!=-1){
////            QMUtil.getInstance().showToast(getActivity(), R.string.without_device_control);
////            return;
////        }
//
//        if(aa==1 && rootPoint != null){
//            controlLancher(rootPoint);
//            //control(QMCommander.TYPE_OPEN_CONTROL, rp);
//        }
//
////        switch (aa){
////            // 0是分享
////            case 0:
////
////                break;
////            case 1:
////
////                break;
////        }
//
//
//    }
//
//
//
//    /**
//     * 10002端口300指令请求远程控制的监听，与lancher交互的
//     */
//    public void controlLancher(RootPoint rootPoint){
////        RootPoint rp = QMDevice.getInstance().getSameRootPoint(rootPoint);
////        if (rp == null)
////            return;
////        QMUtil.getInstance().closeDialog();
////        shareResponse = false;
////        mHandler.removeCallbacks(share);
//        //       if ("true".equals(rootPoint.getEnablecontrol())) {// 如果可以进行远程控制就跳到控制的UI中
//        Intent intent = new Intent(getActivity(), ControlActivity.class);
//        intent.putExtra("rootPoint", rootPoint);
//        startActivityForResult(intent, code1);
////        } else {
////            QMUtil.getInstance().showToast(ScreenFragment.mContext, R.string.unable_yk);
////        }
//    }
//
//    @Override
//    public void setUserVisibleHint(boolean isVisibleToUser) {
//        super.setUserVisibleHint(isVisibleToUser);
//        if (isFragmentCreate && isVisibleToUser) {
//            getMorningTrack();
//        }
//    }
//
//    private void getMorningTrack(){
//        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
//        String time = dateFormat.format(System.currentTimeMillis());
//        List<TeamMorningData> morningData = GreenDaoManager.getDaoSession().getTeamMorningDataDao().queryBuilder().where(
//                TeamMorningDataDao.Properties.PhoneNum.eq(CacheUserInfo.get().getUserPhone()),
//                TeamMorningDataDao.Properties.Day.eq(time)
//        ).build().list();
//        if ((morningData != null&&morningData.size()>0)|| isMorningTrack) {
//            morningTrack.setText("晨会签到已经完成");
//        } else {
//            morningTrack.setText("晨会未签到");
//        }
//    }
//
//    @Override
//    public void onDestroy() {
//        super.onDestroy();
//        isFragmentCreate = false;
//        EventBus.getDefault().unregister(this);
//    }
//
//    @Override
//    public void showLoading() {
//
//    }
//
//    @Override
//    public void hideLoading() {
//
//    }
//
//
//    @Override
//    public void searchLancher(RootPoint rootPoint) {
//        DebugLog.showLog(this,"searchLancher");
//    }
//
//    @Override
//    public void connectLancher(RootPoint rootPoint) {
//        DebugLog.showLog(this,"connectLancher");
//    }
//    int TorS = 1;
//    @Override
//    public void screenOpenLancher(RootPoint rootPoint) {
//        DebugLog.showLog(this,"screenOpenLancher");
//            DebugLog.showLog(getActivity(),"收到800："+rootPoint.getAddress());
//            RootPoint rp = rootPoint;
//            if (rp == null)
//                return;
//            // 屏幕投屏如果打开了
//            if("true".equals(rootPoint.getIsrunning())){
//                if (TorS == 1) {// 投屏操作发送的请求
//                    endStop = false;
//                    // 有密码的设备先进行密码连接请求。监听接口connect（）,与盒子交互的
//                    if (rp.getEnablepwd().equals("true")){
//                        CommandExecutorBox.getOnlyExecutor().connectMessage(QMCommander.TYPE_SET_CLIENT_MSG, rootPoint.getAddress(), rp.getPassword());
//                    } else {
//                        rp.setStopByHandle(true);
//                        // 没有密码的话就发送投屏播放的指令，监听接口为toPlay（） 与盒子交互的；
//                        CommandExecutorBox.getOnlyExecutor().connectMessage(QMCommander.TYPE_START_PLAY, rootPoint.getAddress(), null);
//                    }
//
//                } else if (TorS == 2){// 分享操作发送的请求直接进入分享的UI
//                    // 有密码的设备先进行密码连接请求。监听接口connect（）,与盒子交互的
//                    if (rp.getEnablepwd().equals("true")) {
//                        CommandExecutorBox.getOnlyExecutor().connectMessage(QMCommander.TYPE_SET_CLIENT_MSG, rootPoint.getAddress(), rp.getPassword());
//                    } else {
//                        CommandExecutorBox.getOnlyExecutor().connectMessage(QMCommander.TYPE_SET_CLIENT_MSG, rootPoint.getAddress(), null);
//                    }
//                }
//            }else{// 屏幕投屏如果没有打开
//                switch (TorS){
//                    case 1:
//                        rp.setResponse(true);
//                        // 开始选择投屏的个数是1的情况
//                        if (QMDevice.getInstance().selectDeviceCount() == 1)
//                            QMUtil.getInstance().closeDialog();
//
//                        // 开始选择投屏的个数大于1的情况，没有打开屏幕投屏的设备勾选状态取消
//                        if (QMDevice.getInstance().selectDeviceCount() > 1) {
//                            rp.setPlay(false);
////                            boxAdapter.notifyDataSetChanged();
//                            rp.setLcount(-1);
//                        }
//                        // 已经投屏的时候，再次启用其他盒子投屏
//                        if (QMDevice.getInstance().hasScreenDevice()) {
//                            rp.setPlay(false);
////                            boxAdapter.notifyDataSetChanged();
//                            rp.setLcount(-1);
//                            QMUtil.getInstance().closeDialog();
//                        }
//                        break;
//                    case 2:
////                        shareResponse = true;
//                        QMUtil.getInstance().closeDialog();
////                        mHandler.removeCallbacks(share);
//                        break;
//                }
//                QMUtil.getInstance().showToast2(getActivity(), rootPoint.getName() + getResources().getString(R.string.not_open));
//            }
//    }
//
//    @Override
//    public void passwdAlterLancher(RootPoint rootPoint) {
//        DebugLog.showLog(this,"passwdAlterLancher");
//    }
//
//    @Override
//    public void controlHeartBeatLancher(RootPoint rootPoint) {
//        DebugLog.showLog(this,"controlHeartBeatLancher");
//    }
//
//    @Override
//    public void touPingPc(RootPoint rp) {
//        DebugLog.showLog(this,"touPingPc");
//    }
//
//    @Override
//    public void stopPc(RootPoint rp) {
//        DebugLog.showLog(this,"stopPc");
//    }
//
//    @Override
//    public void pcTouPing(RootPoint rp) {
//        DebugLog.showLog(this,"pcTouPing");
//    }
//
//    @Override
//    public void pcCoverShare(RootPoint rp) {
//        DebugLog.showLog(this,"pcCoverShare");
//    }
//
//    @Override
//    public void connectBox(RootPoint rootPoint) {
//        DebugLog.showLog(this,"connectBox");
//    }
//
//    @Override
//    public void playBox(RootPoint rootPoint) {
//        DebugLog.showLog(this,"playBox");
//    }
//
//    @Override
//    public void exitBox(RootPoint rootPoint) {
//        DebugLog.showLog(this,"exitBox");
//        sendStop();
//    }
//
//    @Override
//    public void heartBeatBox(RootPoint rootPoint) {
//        DebugLog.showLog(this,"heartBeatBox");
//    }
//
//    @Override
//    public void screenSuccessBox(RootPoint rootPoint){
//        DebugLog.showLog(this,"screenSuccessBox");
//            DebugLog.showLog(this,"投屏成功："+rootPoint.getXmlMessage());
//            RootPoint rp = rootPoint;
//            String player = rootPoint.getPlayurl();// player为空没有投上。不为空此设备投屏成功
//            String identity = rootPoint.getIdentity();// identity与自己的相等表示不是此手机投屏收到的消息
//            if (rp == null) {// 返回数据中设备唯一标示的字段为空直接忽略
//                DebugLog.showLog(this, "rp == null");
//                return;
//            }
//            // 收到投屏失败信号
//            if (player.equals("")){
//                // 此手机发送的投屏请求得到的响应 -----当前设备投屏失败
//                if (identity.equals(NetworkUtils.getNetworkMac()))
//                    tpFail(rp);
//                    // 不是是此手机发送的投屏请求得到的响应------其他设备投屏失败
//                else
//                    tpFailOther(rp);
//            }
//            // 收到投屏成功信号
//            else{
//                // a.是此手机发送的投屏请求得到的响应 ----当前设备投屏成功
//                if (player.equals(NetworkUtils.getNetworkMac()))
//                    tpSuccess(rp);
//                    // b1.不是此手机发送的投屏请求得到的响应----其他设备投屏覆盖
//                else
//                    tpSuccessOther(rp);
//            }
//    }
//
//    @Override
//    public void screenCoverBox(RootPoint rootPoint) {
//        DebugLog.showLog(this,"screenCoverBox");
//    }
//
//    /**
//     * 不是是此手机发送的投屏请求得到的响应------其他设备投屏失败 会影响当前已投屏设备
//     *
//     * @param rp
//     */
//    private void tpFailOther(RootPoint rp) {
//        if (rp.isTouPing()) {// 如果此设备在投屏的话，能够收到此消息，则另外手机投屏是失败了
//            // 会影响当前已投屏设备 盒子端会显示"点播超时"的提示
//            QMUtil.getInstance().showToast2(getActivity(), rp.getName() + getResources().getString(R.string.tp_interrupt));
//            rp.setTouPing(false);
//            otherBoxTP(rp);
//        }
//    }
//
//    /**
//     * 不是此手机发送的投屏请求得到的响应---投屏覆盖
//     *
//     * @param rp
//     */
//    private void tpSuccessOther(RootPoint rp){
//        if (rp.isTouPing()) {// 此设备正在投屏却收到其他设备投屏的消息，是盒子屏幕被其他设备投屏覆盖的原因
//            QMUtil.getInstance().showToast(getActivity(), R.string.tp_cut);
//            rp.setTouPing(false);
//            otherBoxTP(rp);
//        }
//
//    }
//
//    /**
//     * 投屏成功,并且是此手机发送的投屏请求得到的响应
//     *
//     * @param rp
//     */
//
//    private void tpSuccess(RootPoint rp){
//        // 在此之前没有其他任盒在投屏
//        if (!QMDevice.getInstance().hasScreenDevice()){
//            // 改变底部【投屏】按钮的状态
////            ll_tp.setBackground(null);
////            tp.setBackgroundResource(R.drawable.tp_);
////            tv.setPressed(true);
////            tv.setTextColor(getResources().getColor(R.color.textselector_));
//            //   screenCast.setBackground(getResources().getDrawable(R.color.colorPrimary));
//            openNotification(getResources().getString(R.string.tp_now));// 启动通知栏消息
//        }
//        TimerCloseDialog();
//        // 将成功投上的盒子的状态更改为true
//        if(!rp.isTouPing()){
//            rp.setResponse(true);
//            rp.setTouPing(true);
//            rp.setHeartbeat(0);
//            QMUtil.getInstance().closeDialog();
//            QMUtil.getInstance().showToast(getActivity(), R.string.tp_success);
//            setTpState(true);
//            if(QMUtil.getInstance().isForeground(getActivity(), ACTION_BUTTON)||isResume){
//                //   if(rp.getdType()!=2){
//                getHome();// 让程序在后台运行
//                //  }
//            }else{
//                DebugLog.showLog(getActivity(),"不在前台");
//            }
//
//
//        }
//    }
//
//    private void setTpState (boolean istp){
//        if (istp) {
//            isTPSuccess = true;
//            screenProjection.setText(R.string.cancel_screen_projection);
//        } else {
//            screenProjection.setText("投        屏");
//        }
//        this.istp = istp;
//    }
//
//    /**
//     * Home键，回到桌面的方法
//     */
//    private void getHome(){
//        Intent i = new Intent(Intent.ACTION_MAIN);
//        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
//        i.addCategory(Intent.CATEGORY_HOME);
//        startActivity(i);
//    }
//
//
//    private void TimerCloseDialog(){
//        new Timer().schedule(new TimerTask(){
//            @Override
//            public void run(){
//                QMUtil.getInstance().closeDialog();
//            }
//        }, 3000);
//    }
//
//    NotificationManager nm;
//    /**
//     * 投屏成功后通知栏的显示
//     */
//    private void openNotification(String info){
//        nm = (NotificationManager) getActivity().getSystemService(getActivity().NOTIFICATION_SERVICE);
//        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getActivity());
//        RemoteViews mRemoteViews = new RemoteViews(getActivity().getPackageName(), R.layout.main_notify);
//        mRemoteViews.setImageViewResource(R.id.notify_image_view, R.mipmap.title_icon);
//        mRemoteViews.setImageViewResource(R.id.notify_button_random1, R.mipmap.touping_stop);
//        mRemoteViews.setTextColor(R.id.notify_button_random2, getResources().getColor(R.color.xt_red));
//        // API3.0 以上的时候显示按钮，否则消失
//        mRemoteViews.setTextViewText(R.id.notify_button_play_pause, info);
//        // mRemoteViews.setTextViewText(R.id.notify_button_random, "停止");
//        // 点击通知栏【停止】按钮的广播进行注册
//        Intent buttonIntent = new Intent(ACTION_BUTTON);
//        // 上一首按钮
//        PendingIntent intent_paly = PendingIntent.getBroadcast(getActivity(), 1, buttonIntent, PendingIntent.FLAG_UPDATE_CURRENT);
//        mRemoteViews.setOnClickPendingIntent(R.id.notify_button_random, intent_paly);
//        mRemoteViews.setOnClickPendingIntent(R.id.notify_button_random1, intent_paly);
//        mRemoteViews.setOnClickPendingIntent(R.id.notify_button_random2, intent_paly);
//        Intent intt = new Intent(getActivity(), MainActivity.class);
//        intt.putExtra("page",0);
//        intt.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
//        PendingIntent pi = PendingIntent.getActivity(getActivity(), 1, intt, PendingIntent.FLAG_UPDATE_CURRENT);
//        mBuilder.setContent(mRemoteViews).setContentIntent(pi).setTicker("投屏在后台运行").setWhen(System.currentTimeMillis())
//                // 通知产生的时间，会在通知信息里显示
//                .setContentTitle(getResources().getString(R.string.tp_now)).setPriority(Notification.PRIORITY_DEFAULT)// 设置该通知优先级
//                .setSmallIcon(R.mipmap.icon2);
//
//        Notification notify = mBuilder.build();
//        notify.flags = Notification.FLAG_NO_CLEAR;
//        notify.defaults |= Notification.DEFAULT_SOUND;
//        nm.notify(1000, notify);
//    }
//
//
//    boolean endStop;
//
//    /**
//     * 此手机发送的投屏请求得到的响应 -----当前设备投屏失败
//     *
//     * @param rp
//     */
//    private void tpFail(RootPoint rp){
//        if (endStop) {// 如果是自己手动停止投屏 发送1003指令也会收到此消息。忽略！
//            return;
//        }
//        QMUtil.getInstance().closeDialog();
//        rp.setTouPing(false);
//        rp.setResponse(true);
//        QMUtil.getInstance().showToast(getActivity(), R.string.exception3);
//        otherBoxTP(rp);
//    }
//
//    /**
//     * 还有没有其他设备在投屏 在的话取消当前设备勾选 没有的话取消通知栏及恢复投屏按钮
//     *
//     * @param rp
//     */
//    public void otherBoxTP(RootPoint rp){
//        if (QMDevice.getInstance().hasScreenDevice()){
//            rp.setPlay(false);
//            rp.setLcount(-1);
////            boxAdapter.notifyDataSetChanged();
//        } else {
//            closeState();
//            backToScreenFragment();
//        }
//    }
//    public static final String ACTION_BUTTON = "com.shareshow.aide.activity.MainActivity";// 通知栏点击停止发送的广播
//    /**
//     * 返回主页面
//     */
//    private void backToScreenFragment() {
//        ActivityManager am = (ActivityManager) getActivity().getSystemService(Context.ACTIVITY_SERVICE);
//        List<ActivityManager.RunningTaskInfo> infos = am.getRunningTasks(100);
//        for (ActivityManager.RunningTaskInfo rapi : infos) {
//            if (rapi.topActivity.getClassName().equals(ACTION_BUTTON)) {
//                am.moveTaskToFront(rapi.id, ActivityManager.MOVE_TASK_WITH_HOME);
//                return;
//            }
//        }
//    }
//
//    NotifiCationReceiver boxRemoveReceiver;
//    private void initOtherCast(){
//        // 注册接收消息广播
//        boxRemoveReceiver = new WorkFragment1.NotifiCationReceiver();
//        IntentFilter intentFilter = new IntentFilter(ACTION_BUTTON);
//        intentFilter.addAction(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
//        getActivity().registerReceiver(boxRemoveReceiver, intentFilter);
//    }
//
//
//    // 投屏发送心跳8秒内没有收到消息做的处理
//    public void exitTP(RootPoint rp, int type, int id) {
//        rp.setTouPing(false);
//        QMUtil.getInstance().showToast2(getActivity(), rp.getName() + getResources().getString(id));
//        if (QMDevice.getInstance().hasScreenDevice()) {// 如果还有其他投屏设备，此设备要取消选中状态
//            rp.setPlay(false);
//            if (type == -1)// 任盒要取消与文件共享的链接
//                rp.setLcount(-1);
////            boxAdapter.notifyDataSetChanged();
//        } else {
//            backToScreenFragment();
//            closeState();
//        }
//    }
//
//    private class NotifiCationReceiver extends BroadcastReceiver {
//
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            String action = intent.getAction();
//            if (Intent.ACTION_CLOSE_SYSTEM_DIALOGS.equals(action)) {
//                String reason = intent.getStringExtra("reason");
//                if (reason != null) {
//                    if (reason.equals("homekey")) {
//                            onEndStudy();
//                    } else if (reason.equals("recentapps")) {
//                            onEndStudy();
//                    }
//                }
//            } else {
//                // aa为0表示通知栏点击停止按钮的广播
//                // aa为1表示的是投屏发送心跳8秒内没有接受到数据发送的广播通知
//                DebugLog.showLog(this, "收到广播的code :" + intent.getIntExtra("aa", 0));
//                switch (intent.getIntExtra("aa", 0)) {
//                    case 0:
//                        sendStop();// 停止所有设备的投屏
////                    collapseStatusBar();// 收起通知栏以便显示程序的UI
//                        backToScreenFragment();
//                        break;
//                    case 1:
//                        sendStop();
//                        int position = intent.getIntExtra("position", 0);
//                        int type = intent.getIntExtra("type", 0);
//                        exitTP(QMDevice.getInstance().getRootPoint(position), type, R.string.tp_interrupt);// 任盒退出
//                        DebugLog.showLog(getActivity(), "收到心跳断开的接口");
//                        break;
//                    case 2:
//                        if (nm != null) {
//                            nm.cancel(1000);
//                            break;
//                        }
//                        break;
//                }
//            }
//        }
//    }
//}

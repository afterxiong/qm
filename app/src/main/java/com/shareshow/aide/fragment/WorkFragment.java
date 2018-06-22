package com.shareshow.aide.fragment;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.shareshow.App;
import com.shareshow.aide.R;
import com.shareshow.aide.activity.NotificationActivity;
import com.shareshow.aide.adapter.DatumAdapter;
import com.shareshow.aide.dialog.AmplyNotifyDialog;
import com.shareshow.aide.event.DownloadRefurbish;
import com.shareshow.aide.event.FileDownLoadEvent;
import com.shareshow.aide.event.GetAdEvent;
import com.shareshow.aide.mvp.presenter.WorkPresenter;
import com.shareshow.aide.mvp.view.WorkView;
import com.shareshow.aide.retrofit.entity.AmplyNotify;
import com.shareshow.aide.retrofit.entity.StudyMaterialsVisitingMaterials;
import com.shareshow.aide.service.HeartBeatServer;
import com.shareshow.aide.util.AndroidUtil;
import com.shareshow.aide.widget.FileItemDecoration;
import com.shareshow.aide.util.CacheConfig;
import com.shareshow.aide.util.CacheUserInfo;
import com.shareshow.airpc.activity.ControlActivity;
import com.shareshow.airpc.activity.QuiteFileActivity;
import com.shareshow.airpc.model.RootPoint;
import com.shareshow.airpc.ports.PositionListener;
import com.shareshow.airpc.socket.common.QMDevice;
import com.shareshow.airpc.util.DebugLog;
import com.shareshow.airpc.util.NetworkUtils;
import com.shareshow.airpc.util.QMDialog;
import com.shareshow.db.GreenDaoManager;
import com.shareshow.db.StudyMaterialsVisitingMaterialsDao;
import com.socks.library.KLog;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


/**
 * Created by xiongchengguang on 2017/12/7.
 */

public class WorkFragment extends BaseFragment<WorkView, WorkPresenter> implements WorkView, SwipeRefreshLayout.OnRefreshListener {

    @BindView(R.id.flipper)
    public ViewFlipper flipper;
    @BindView(R.id.more_affiche_notify)
    public View moreAfficheNotify;
    @BindView(R.id.refreshLayout)
    public SwipeRefreshLayout refreshLayout;
    @BindView(R.id.recycler)
    public RecyclerView recycler;
    @BindView(R.id.emputy_state)
    public View emputyState;
    @BindView(R.id.tv_visit_count)
    public TextView visitCountText;
    @BindView(R.id.tv_morning_track)
    public TextView morningTrack;
    @BindView(R.id.device_group_view)
    public View deviceGroupView;
    @BindView(R.id.my_device_state)
    public TextView deviceStateText;
    @BindView(R.id.guide_text)
    public TextView guideText;
    @BindView(R.id.screen_projection)
    public View screenProjection;
    @BindView(R.id.work_file)
    public View workFile;
    @BindView(R.id.remote_control)
    public View remoteControl;
    @BindView(R.id.screen_projection_state_text)
    public TextView screenProjectionStateText;

    public DatumAdapter adapter;

    public List<StudyMaterialsVisitingMaterials> listSmvms = new ArrayList<>();

    private SystemDialogsReceiver systemDialogsReceiver = null;

    private SpannableString guideSpannableString;
    private SpannableString deviceStateSpannableString1;
    private SpannableString deviceStateSpannableString2;
    public static final int code1 = 223;// Activity回调
    private boolean isResume = false;
    private boolean isCreate = false;

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        ButterKnife.bind(this, view);
        initView();
        refurbishData();
        updateFileList();
    }

    /**
     * 刷新数据
     */
    public void refurbishData(){
        flipper.removeAllViews();
        presenter.onRefurbishData();
        presenter.startFlipping();
        EventBus.getDefault().post(new GetAdEvent());
    }

    /**
     * 更新文件列表
     */
    public void updateFileList(){
        List<StudyMaterialsVisitingMaterials> daoListSmvms = GreenDaoManager
                .getDaoSession()
                .getStudyMaterialsVisitingMaterialsDao()
                .queryBuilder()
                .orderDesc(StudyMaterialsVisitingMaterialsDao.Properties.Date)
                .list();
        listSmvms.clear();
        listSmvms.addAll(daoListSmvms);
        adapter.notifyDataSetChanged();
        if (daoListSmvms.size() == 0) {
            emputyState.setVisibility(View.VISIBLE);
        } else {
            emputyState.setVisibility(View.GONE);
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser){
        super.setUserVisibleHint(isVisibleToUser);
        if (isCreate && isVisibleToUser) {
            setViewMorningRegister(false);
            presenter.checkNetwokrBindDevice();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(DownloadRefurbish event){
        if (event.getFlag() == DownloadRefurbish.UPDATE){
            updateFileList();
        }else if (event.getFlag() == DownloadRefurbish.OVER){
            refreshLayout.setRefreshing(false);
            EventBus.getDefault().post(new FileDownLoadEvent());
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        DebugLog.showLog(this, "onActivityResult");
        super.onActivityResult(requestCode, resultCode, data);
        presenter.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState){
        DebugLog.showLog(this, "onCreate");
        super.onCreate(savedInstanceState);
        initOtherCast();
        initSpannable();
    }

    @Override
    public void onResume(){
        super.onResume();
        this.isResume = true;
        presenter.endStudy();
        if (checkNetworkDisabled()) {
            presenter.checkNetwokrBindDevice();
        } else {
            setBindDeviceView(presenter.isBindDev());
        }
        if (presenter.isBindDev()) {
            Intent intent = new Intent(getActivity(), HeartBeatServer.class);
            getActivity().startService(intent);
        }

        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
        setVisitAndStudyCount(presenter.getVisitCount(), presenter.getStudycount());
        setDefaultFliper();
    }

    @Override
    public void onPause(){
        this.isResume = false;
        super.onPause();
        presenter.startStudy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        presenter.reasle();
        if (systemDialogsReceiver != null){
            getActivity().unregisterReceiver(systemDialogsReceiver);
        }
    }

    @OnClick(R.id.more_affiche_notify)
    public void moreNotification(){
        Intent intent = new Intent(getActivity(), NotificationActivity.class);
        Bundle bundle = new Bundle();
        intent.putExtras(bundle);
        startActivity(intent);
    }

    @OnClick(R.id.screen_projection)
    public void openScreenProjection(){
        if (presenter.isScreenProjection()){
            presenter.closeScreenProjection();
        } else {
            //开启投屏
            presenter.openScreenProjection();
        }
    }

    @OnClick(R.id.work_file)
    public void openWorkFile(){
        RootPoint rootPoint = presenter.getRootPoint();
        if (rootPoint == null) {
            DebugLog.showLog(this, "没有设备！");
        } else {
            Intent intent = new Intent(getActivity(), QuiteFileActivity.class);
            startActivity(intent);
        }
    }

    @OnClick(R.id.remote_control)
    public void openRemoteControl(){
        if (presenter.isScreenProjection()){
            close_TP(R.string.close_tp_tip2);
        } else {
            if(presenter.getRootPoint() != null){
                //开启远程控制
                Intent intent = new Intent(getActivity(), ControlActivity.class);
                intent.putExtra("rootPoint", presenter.getRootPoint());
                startActivityForResult(intent, code1);
            }
        }
    }

    private void close_TP(int id){
        new QMDialog(getActivity(), id, R.string.yes, new PositionListener() {
            @Override
            public void method(int position) {
                presenter.closeScreenProjection();
                if (presenter.getRootPoint() != null) {
                    //开启远程控制
                    Intent intent = new Intent(getActivity(), ControlActivity.class);
                    intent.putExtra("rootPoint", presenter.getRootPoint());
                    startActivityForResult(intent, code1);
                }
            }

            @Override
            public void cancel() {

            }

        });
    }


    @Override
    public void onRefresh() {
        refreshLayout.setRefreshing(true);
        refurbishData();

    }

    @Override
    public int getLayoutResource() {
        return R.layout.fragment_work;
    }


    @Override
    public WorkPresenter createPresenter() {
        return new WorkPresenter();
    }


    @Override
    public void onViewSmvmCompile(){
        refreshLayout.setRefreshing(false);
    }

    @Override
    public void onViewNotificationListener(AmplyNotify ans) {

    }

    @Override
    public void onViewNotificationListener(List<AmplyNotify> notifyList) {
        setFlipper(notifyList);
    }

    @Override
    public void onToastShow(String msg) {
        Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onViewNotificationListener(String msg) {
        if (msg == null || msg.isEmpty()) {
            return;
        }
        flipper.removeAllViews();
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.item_flipper, null);
        TextView value = view.findViewById(R.id.flipper_value);
        value.setText(msg);
        flipper.addView(view);
        moreAfficheNotify.setVisibility(View.VISIBLE);
        flipper.setClickable(false);
        flipper.setOnClickListener(null);
    }

    @Override
    public void onViewMorningRegister(boolean whether) {
        setViewMorningRegister(whether);
    }

    @Override
    public void onVisitCountChange(int count) {
        setVisitAndStudyCount(count, presenter.getStudycount());
    }

    @Override
    public void onStudycountChange(int count) {
        setVisitAndStudyCount(presenter.getVisitCount(), count);
    }

    @Override
    public void onIsBindDevChange(boolean isBind, String ids) {
        setBindDeviceView(isBind);
        if (isBind && ids != null) {
            Intent intent = new Intent(getActivity(), HeartBeatServer.class);
            intent.putExtra(HeartBeatServer.DEV_IDS, ids);
            getActivity().startService(intent);
        }
    }

    @Override
    public void onScreenProjectionChang(boolean isScreenProjection) {
        setScreenProjectionState(isScreenProjection);
    }

    @Override
    public Activity getBindActivity() {
        return getActivity();
    }

    public boolean isResume() {
        return this.isResume;
    }

    @Override
    public void onBindDevOnlineChange(boolean isOnline) {
        setBindDeviceState(isOnline);
    }

    public boolean checkNetworkDisabled() {
        boolean disabled = NetworkUtils.isNetworkConnected();
        KLog.d("网络是否连接:" + disabled);
        if (disabled) {
//            networkDisabled.setVisibility(View.GONE);
        } else {
//            networkDisabled.setVisibility(View.VISIBLE);
//            donwloading.setVisibility(View.GONE);
        }
        return disabled;
    }

    private void setBindDeviceView(boolean isBind) {
        if (isBind) {
            deviceGroupView.setVisibility(View.VISIBLE);
        } else {
            //不存在设备绑定
            deviceGroupView.setVisibility(View.GONE);
        }
    }

    private void setBindDeviceState(boolean isOnline){
        if (isOnline) {
            guideText.setVisibility(View.GONE);
            screenProjection.setVisibility(View.VISIBLE);
            workFile.setVisibility(View.VISIBLE);
            remoteControl.setVisibility(View.VISIBLE);
            deviceStateText.setText(deviceStateSpannableString1);
        } else {
            guideText.setVisibility(View.VISIBLE);
            screenProjection.setVisibility(View.GONE);
            workFile.setVisibility(View.GONE);
            remoteControl.setVisibility(View.GONE);
            deviceStateText.setText(deviceStateSpannableString2);
        }
    }


    private void setVisitAndStudyCount(int visitCount, int studycount){
        SpannableString spannableString = new SpannableString("客户拜访 " + visitCount + " 次，培训学习 " + studycount + " 次");

//        spannableString.setSpan(new ClickableSpan() {
//            @Override
//            public void onClick(View widget) {
//            }
//            @Override
//            public void updateDrawState(TextPaint ds) {
//                ds.setColor(getResources().getColor(R.color.blue));
//            }
//        },4,5, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
//        spannableString.setSpan(new ClickableSpan() {
//            @Override
//            public void onClick(View widget) {
//            }
//            @Override
//            public void updateDrawState(TextPaint ds) {
//                ds.setColor(getResources().getColor(R.color.blue));
//            }
//        },11,12, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        visitCountText.setText(spannableString);
    }


    private void setViewMorningRegister(boolean isMorningRegister) {
        if (isMorningRegister || CacheConfig.get().getMorningState()) {
            morningTrack.setText("晨会签到已经完成");
        } else {
            morningTrack.setText("晨会未签到");
        }
    }

    private void setScreenProjectionState(boolean isScreenProjection) {
        if (isScreenProjection) {
            screenProjectionStateText.setText("关闭");
        } else {
            screenProjectionStateText.setText("投屏");
        }
    }

    private void setFlipper(List<AmplyNotify> afficheNotifyList){
        if (afficheNotifyList.size() > 0){
            flipper.removeAllViews();
            View view = LayoutInflater.from(getActivity()).inflate(R.layout.item_flipper, null);
            TextView value = view.findViewById(R.id.flipper_value);
            value.setText(afficheNotifyList.get(0).getNosTitle());
            flipper.addView(view);
            moreAfficheNotify.setVisibility(View.VISIBLE);
            flipper.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int index = flipper.getDisplayedChild();
                    AmplyNotifyDialog.newInstance(afficheNotifyList.get(index)).show(getActivity().getFragmentManager(), "NotifyDetailedDialog");
                }
            });
        }else {
            setDefaultFliper();
        }
    }

    public void setDefaultFliper() {
        if(flipper.getChildCount()==0){
            flipper.removeAllViews();
            View view = LayoutInflater.from(getActivity()).inflate(R.layout.item_flipper, null);
            TextView value = view.findViewById(R.id.flipper_value);
            value.setText("暂时无通知");
            flipper.addView(view);
            moreAfficheNotify.setVisibility(View.GONE);
        }

    }

    private void initView(){
        refreshLayout.setProgressBackgroundColorSchemeResource(android.R.color.white);
        refreshLayout.setColorSchemeResources(R.color.colorAccent, R.color.colorPrimary, R.color.colorPrimaryDark);
        refreshLayout.setOnRefreshListener(this);

        LinearLayoutManager manager = new LinearLayoutManager(getActivity());
        adapter = new DatumAdapter(getActivity(), listSmvms);
        recycler.setLayoutManager(manager);
        recycler.addItemDecoration(new FileItemDecoration(getActivity()));
        recycler.setAdapter(adapter);
        recycler.addOnScrollListener(adapter.getOnScrollListener());
        guideText.setHighlightColor(getResources().getColor(android.R.color.transparent));
        setBindDeviceView(presenter.isBindDev());
        setBindDeviceState(presenter.isBindDevOnline());
        setViewMorningRegister(presenter.isMorningRegister());
        guideText.setText(guideSpannableString);
        guideText.setMovementMethod(LinkMovementMethod.getInstance());
        setScreenProjectionState(presenter.isScreenProjection());
        isCreate = true;
    }

    private void initOtherCast() {
        // 注册接收消息广播
        systemDialogsReceiver = new WorkFragment.SystemDialogsReceiver();
        IntentFilter intentFilter = new IntentFilter(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
        getActivity().registerReceiver(systemDialogsReceiver, intentFilter);
    }


    private void initSpannable() {

        guideSpannableString = new SpannableString("请将手机和云微投连接同一WiFi或开启手机热点,将云微投连接至手机热点。");
        guideSpannableString.setSpan(new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS)); //直接进入手机中的wifi网络设置界面
            }

            @Override
            public void updateDrawState(TextPaint ds) {
                ds.setColor(getResources().getColor(R.color.blue));
            }
        }, 12, 16, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        guideSpannableString.setSpan(new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                if(AndroidUtil.isPad(getActivity())){
                    Toast.makeText(App.getApp(),"pad设备没有热点可以设置！",Toast.LENGTH_SHORT).show();
                    return;
                 }
                ComponentName cm = new ComponentName("com.android.settings", "com.android.settings.TetherSettings");
                Intent intent = new Intent("android.intent.action.VIEW");
                intent.setComponent(cm);
                getActivity().startActivity(intent);
            }

            @Override
            public void updateDrawState(TextPaint ds) {
                ds.setColor(getResources().getColor(R.color.blue));
            }
        }, 19, 23, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(View widget) {
            }

            @Override
            public void updateDrawState(TextPaint ds) {
                ds.setTextSize(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 12, getActivity().getResources().getDisplayMetrics()));
                ds.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
            }

        };
        String deviceStateString = "我的云微投";
        deviceStateSpannableString1 = new SpannableString(deviceStateString + " ( 在线 )");
        deviceStateSpannableString2 = new SpannableString(deviceStateString + " ( 离线 )");
        deviceStateSpannableString1.setSpan(clickableSpan, 5, 12, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        deviceStateSpannableString2.setSpan(clickableSpan, 5, 12, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
    }

    private class SystemDialogsReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (Intent.ACTION_CLOSE_SYSTEM_DIALOGS.equals(action)) {
                String reason = intent.getStringExtra("reason");
                if (reason != null) {
                    if (reason.equals("homekey")) {
                        presenter.endStudy();
                    } else if (reason.equals("recentapps")) {
                        presenter.endStudy();
                    }
                }
            }
        }
    }

}

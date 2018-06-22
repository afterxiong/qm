package com.shareshow.aide.fragment;

import android.app.FragmentManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.bigkoo.pickerview.TimePickerView;
import com.scwang.smartrefresh.header.MaterialHeader;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.footer.ClassicsFooter;
import com.shareshow.App;
import com.shareshow.aide.R;
import com.shareshow.aide.adapter.AudioAdapter;
import com.shareshow.aide.dialog.FilterMorningDialog;
import com.shareshow.aide.dialog.FilterVisitDialog;
import com.shareshow.aide.dialog.PermissSettingDialog;
import com.shareshow.aide.event.MessageEvent;
import com.shareshow.aide.event.UpdateMorningDataEvent;
import com.shareshow.aide.impl.OnRecycleviewListener;
import com.shareshow.aide.mvp.presenter.MoriningPresenter;
import com.shareshow.aide.mvp.view.MroiningView;
import com.shareshow.aide.retrofit.entity.Team;
import com.shareshow.aide.retrofit.entity.TeamMorningData;
import com.shareshow.aide.service.AudioRecordService;
import com.shareshow.aide.service.PlayService;
import com.shareshow.aide.util.CacheConfig;
import com.shareshow.aide.util.CacheUserInfo;
import com.shareshow.aide.util.PermissionUtil;
import com.shareshow.aide.widget.CoustomItemDecoration;
import com.shareshow.airpc.float_window.WindowUtil;
import com.shareshow.airpc.util.DebugLog;
import com.shareshow.db.GreenDaoManager;
import com.shareshow.db.TeamMorningDataDao;
import com.tbruyelle.rxpermissions2.RxPermissions;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import audio.shareshow.com.Utils.AudioContent;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 晨会签到
 * Created by xiongchengguang on 2017/12/7.
 * 1,晨会签到列表获取是服务器下载的数据；
 * 2,本地数据上传成功后，将文件移到远程文件目录；
 * 3,每次重启APP删除所有远程目录的文件以及数据;
 * 4,如果签到失败，存入数据库，等待网络切换或者重启APP，重新上传;
 * 5,晨会签到上传的过程中，发现有上传未成功的数据，将其本地文件和数据移除;
 * 6,如果再获取服务器列表过程中，如果有未上传成功的本地数据,以本地未上传成功的为准。
 *
 */

public class MorningFragment extends BaseFragment<MroiningView, MoriningPresenter> implements MroiningView, AudioRecordService.TimeListener, PlayService.PlayListener {
    @BindView(R.id.audio_start_and_compile)
    public RelativeLayout startAndCompile;
    @BindView(R.id.audio_time)
    public TextView audioTime;
    @BindView(R.id.audio_tip)
    public TextView audioTip;
    @BindView(R.id.recorder_list)
    public RecyclerView recycler;
    @BindView(R.id.tv_select_time)
    public TextView tv_select_time;
    @BindView(R.id.swipeRefresh)
    SmartRefreshLayout swipeRefresh;
    @BindView(R.id.morning_submit)
    ImageView morningSubmit;
    @BindView(R.id.morning_delete)
    ImageView morningDelete;
    @BindView(R.id.tv_select_user)
    ImageView selectUser;
    @BindView(R.id.morning_state)
    TextView morningState;
    @BindView(R.id.audio_pause)
    LinearLayout audioPause;
    @BindView(R.id.morning_line)
    View morningLine;
    private LinearLayoutManager manager;
    private AudioAdapter adapter;
    private List<TeamMorningData> morningData = new ArrayList<TeamMorningData>();
    private AudioRecordService.RecordBinder binder;
    private PlayService.PlayBinder playBinder;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    private boolean isMoringFragmentCreate;
    private FragmentManager fragmentManager;
    private boolean isMorningDataFinish = true;
    private RefreshLayout localLayout;
    private String selectTime = null;
    private int localFlag = -1;
    private boolean isGetData = false;
    private String filePath;
    private String trackState = AudioContent.IDLE_RECORDER;
    private Set<String> defaultUserIds = new HashSet<String>();
    private List<Team.TeamMember> memberList = new ArrayList<Team.TeamMember>();
    private String userId = null;
    private boolean isSelect;
    private TeamMorningData playMorningData;
    private FilterMorningDialog dialog;
    private TeamMorningData localFirstData;
    private Handler mHandler = new Handler(Looper.getMainLooper());

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        DebugLog.showLog(this, "onCreate");
        if (!EventBus.getDefault().isRegistered(this)){
             EventBus.getDefault().register(this);
        }
        initUerId();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        ButterKnife.bind(this, view);
        initView();
        initData();
    }

    //在此初始化userId方便下拉刷新时候获取数据，这个userId也是人员筛选
    private void initUerId(){
        userId = CacheUserInfo.get().getUserId();
    }

    private void initData(){
        checkUpdata();
        if(isGetSdPermission()){
            presenter.clearAllDatas();
        }
        if (!isGetData){
            List<TeamMorningData> datas = GreenDaoManager.getDaoSession().getTeamMorningDataDao().queryBuilder().where(TeamMorningDataDao.Properties.PhoneNum.eq(CacheUserInfo.get().getUserPhone())
                    , TeamMorningDataDao.Properties.Day.eq(dateFormat.format(new Date(System.currentTimeMillis())))).list();
            if (datas != null && datas.size() > 0) {
                morningData.add(datas.get(0));
            }
            adapter.notifyUpdate(recycler, morningData);
            tv_select_time.setText("");
            selectTime = null;
            isSelect = false;
            swipeRefresh.autoRefresh();
            clearPauseAudio();
        }
    }

    private void clearPauseAudio(){
        TeamMorningDataDao dataDao = GreenDaoManager.getDaoSession().getTeamMorningDataDao();
        List<TeamMorningData> data = dataDao.queryBuilder().where(TeamMorningDataDao.Properties.PhoneNum.eq(CacheUserInfo.get().getUserPhone())
                , TeamMorningDataDao.Properties.IsNeedUpload.eq(false)).list();
        if (data == null || data.size() == 0) {
            return;
        }
        for (TeamMorningData datum : data) {
            dataDao.delete(datum);
            deleteFile(datum);
        }
    }

    private void setTrackState(){
        if (CacheConfig.get().getMorningState()){
            morningState.setText(getResources().getString(R.string.morningFragment_finish_track));
        } else {
            morningState.setText(getResources().getString(R.string.morningFragment_unfinish_track));
        }
    }

    //核查是否有前一天的数据没有上传
    private void checkUpdata(){
        List<TeamMorningData> datas = GreenDaoManager.getDaoSession().getTeamMorningDataDao().queryBuilder().where(TeamMorningDataDao.Properties.IsUpload.eq(false)).list();
        if (datas != null){
            for (TeamMorningData data : datas){
                if (!data.getDay().equals(dateFormat.format(System.currentTimeMillis()))){
                    Toast.makeText(App.getApp(), getResources().getString(R.string.morningFragment_link_internet), Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        }
    }

    private void initView(){
        isMoringFragmentCreate = true;
        fragmentManager = getActivity().getFragmentManager();
        manager = new LinearLayoutManager(getActivity());
        recycler.setLayoutManager(manager);
        recycler.addItemDecoration(new CoustomItemDecoration(getActivity(), CoustomItemDecoration.VERTICAL_LIST, R.drawable.morningline, WindowUtil.dip2px(getActivity(), 14)));
        adapter = new AudioAdapter(morningData, getActivity());
        recycler.setAdapter(adapter);
        swipeRefresh.setRefreshHeader(new MaterialHeader(getActivity()));//设置Header
        swipeRefresh.setRefreshFooter(new ClassicsFooter(getActivity()));
        swipeRefresh.setDragRate(0.8f);
        swipeRefresh.setOnLoadMoreListener(this);
        swipeRefresh.setOnRefreshLoadMoreListener(this);
        setTrackState();
        setSelectView();
        adapter.setOnItemClickListener(new OnRecycleviewListener(){

            @Override
            public void onItemClick(int position, Object obj){
                if(trackState != AudioContent.IDLE_RECORDER){
                    Toast.makeText(getActivity(), getActivity().getResources().getString(R.string.morningFragment_recoding), Toast.LENGTH_SHORT).show();
                    return;
                }
                TeamMorningData data = ((TeamMorningData) obj);
                if (playMorningData != null && playMorningData.getUuid().equals(data.getUuid())) {
                    DebugLog.showLog(this,"播放相同的录音!");
                    return;
                }
                if (data.getPath() != null && new File(data.getPath()).exists()){
                    //本地目录下面查找
                    File param = new File(data.getPath());
                    adapter.setProgressInVisible();
                    setPlayMorningData(position);
                    if (playBinder != null){
                        playBinder.start(param.getPath(), morningData.get(position).getUuid());
                    }
                }else if (presenter.getCacheFile(data).exists()){
                    //缓存目录有没有
                    data.setPath(presenter.getCacheFile(data).getPath());
                    adapter.setProgressInVisible();
                    setPlayMorningData(position);
                    if (playBinder != null){
                        playBinder.start(data.getPath(), morningData.get(position).getUuid());
                    }
                } else{
                    //服务器中下载
                    adapter.setProgressVisible(position);
                    presenter.downloadFile(data, position);
                }
            }

            @Override
            public void OnPlay(TeamMorningData data, int flag){
                if (playBinder != null){
                    if (flag == 0){
                         playBinder.start(data.getPath(), data.getUuid());
                    } else if (flag == 1) {
                        playBinder.pause();
                    } else {
                        playBinder.stop();
                        playMorningData=null;
                    }
                }
            }

            @Override
            public void OnSeekBar(int progress){
                if (playMorningData != null) {
                    playMorningData.setProgress(progress);
                    adapter.setPlayItem(playMorningData);
                }
                if (playBinder != null) {
                    playBinder.seekTo(progress);
                }
            }
        });

        startAndCompile.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                if (Build.VERSION.SDK_INT >= 23 && CacheConfig.get().getPermission(PermissionUtil.AUDIO_PERMISSION) == -1) {
                    getAudioPermission();
                    return;
                } else if (Build.VERSION.SDK_INT < 23) {
                    //5.0手机的适配

                }

                if (!isPremission(PermissionUtil.AUDIO_PERMISSION)){
                    showPermissionDialog(getResources().getString(R.string.morningFragment_audio));
                    return;
                }
                audioStart();
            }
        });
    }

    private void setPlayMorningData(int position){
        for(int i = 0; i < morningData.size(); i++){
            if (i == position) {
                playMorningData = morningData.get(position);
                playMorningData.setIsPlayItem(true);
                playMorningData.setIsPlay(false);
                adapter.setPlayItem(playMorningData);
                adapter.setItemPosition(position);
            } else {
                morningData.get(i).clear();
            }
        }
        adapter.notifyDataSetChanged();
    }

    //如果第一次进来录音就申请权限
    private void getAudioPermission(){
        PermissionUtil.getPermission(getActivity(), PermissionUtil.AUDIO_PERMISSION, new PermissionUtil.PermissionListener() {
            @Override
            public void Success() {
                CacheConfig.get().setPermission(PermissionUtil.AUDIO_PERMISSION, 2);
                audioStart();
            }

            @Override
            public void Fail() {
                CacheConfig.get().setPermission(PermissionUtil.AUDIO_PERMISSION, 1);
            }
        });
    }

    //fragment可见的时候可以
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser){
        super.setUserVisibleHint(isVisibleToUser);
        if (isMoringFragmentCreate && isVisibleToUser){
            setTrackState();
            if(selectTime != null){
                morningData.clear();
                adapter.notifyUpdate(recycler, morningData);
                tv_select_time.setText("");
                selectTime = null;
                isSelect = false;
                swipeRefresh.autoRefresh();
            }
        } else {
                setRefreshState(false);
        }
    }

    public boolean isPremission(String permission){
        RxPermissions rxPermissions = new RxPermissions(getActivity());
        return rxPermissions.isGranted(permission);
    }

    public void audioStart(){
        isSelect = false;
        if (binder != null){
            binder.setListener(this);
        }
        if (trackState == AudioContent.IDLE_RECORDER){
            morningDelete.setVisibility(View.VISIBLE);
            morningSubmit.setVisibility(View.VISIBLE);
            audioPause.setVisibility(View.GONE);
            audioTip.setVisibility(View.GONE);
            audioTime.setVisibility(View.VISIBLE);
            morningState.setText(getResources().getString(R.string.morningFragment_tracking));
            audioTime.setText("00:00:00");
            if (binder != null) {
                binder.startRecord();
                trackState = AudioContent.START_RECORDER;
                adapter.setRecord(true);
            }
        } else if (trackState == AudioContent.START_RECORDER){
            audioPause.setVisibility(View.VISIBLE);
            if (binder != null) {
                binder.pauseRecord();
                String duration = audioTime.getText().toString().trim();
                presenter.getMorningData(duration, binder.getFilePath(), false);
                trackState = AudioContent.PAUSE_RECORDER;
                adapter.setRecord(true);
            }
        } else if (trackState == AudioContent.PAUSE_RECORDER){
            audioPause.setVisibility(View.GONE);
            if (binder != null) {
                String time = audioTime.getText().toString().trim();
                binder.reStartRecord(time);
                trackState = AudioContent.START_RECORDER;
                adapter.setRecord(true);
            }
        }
    }

    private void notifyLocalData(TeamMorningData data){
        if (selectTime == null){
            if (morningData.size() != 0) {
                for (TeamMorningData morningDatum : morningData) {
                    if (morningDatum.getPhoneNum().equals(data.getPhoneNum()) && morningDatum.getDay().equals(data.getDay())) {
                        morningData.remove(morningDatum);
                        break;
                    }
                }
            }
            morningData.add(0, data);
            adapter.notifyUpdate(recycler, morningData);
        } else {
            tv_select_time.setText("");
            selectTime = null;
            getMorningData();
        }
    }

    @Override
    public void onPause(){
        super.onPause();
    }

    private boolean isGetSdPermission(){
        boolean isPermission = PermissionUtil.isPremission(getActivity(), new String[]{PermissionUtil.SD_WRITE_PERMISSION,
                PermissionUtil.SD_READ_PERMISSION});
        if (!isPermission) {
            showPermissionDialog(getResources().getString(R.string.morningFragment_sd));
            return false;
        }else{
            return true;
        }
    }

    private void showPermissionDialog(String permission){
        PermissSettingDialog dialog = PermissSettingDialog.get(permission, fragmentManager);
        dialog.setCancelable(false);
        dialog.show(fragmentManager, "dialog");
    }

    private void deleteFile(TeamMorningData teamMorningData){
        if (teamMorningData.getPath() != null){
            File teamFile = new File(teamMorningData.getPath());
            if (teamFile.exists()){
                teamFile.delete();
            }
        }
    }

    public void getMorningData(){
        morningData.clear();
        adapter.notifyUpdate(recycler, morningData);
        swipeRefresh.autoRefresh();
    }

    @OnClick(R.id.tv_select_time)
    public void selectTime(){
        Calendar startDate = Calendar.getInstance();
        Calendar endDate = Calendar.getInstance();
        int year = endDate.get(Calendar.YEAR);
        int month = endDate.get(Calendar.MONTH);
        int day = endDate.get(Calendar.DAY_OF_MONTH);
        startDate.set(2017, 11, 01);
        endDate.set(year, month, day);
        TimePickerView pvTime = new TimePickerView.Builder(getActivity(), new TimePickerView.OnTimeSelectListener() {
            @Override
            public void onTimeSelect(Date date, View v) {//选中事件回调
                String time = dateFormat.format(date);
                getMorningData();
                tv_select_time.setText(time);
                selectTime = time;
                adapter.notifyUpdate(recycler, morningData);
            }
        }).setRangDate(startDate, endDate).isCenterLabel(false).setType(new boolean[]{true, true, true, false, false, false}).build();
        Calendar calendar = Calendar.getInstance();
        pvTime.setDate(calendar);
        pvTime.show();
    }


    @Override
    public MoriningPresenter createPresenter(){
        return new MoriningPresenter();
    }

    @Override
    public int getLayoutResource(){
        return R.layout.fragment_morning;
    }

    @Override
    public void notifyTeamData(ArrayList<TeamMorningData> datas, int flag){
        if (!isGetData) {
            isGetData = true;
            morningData.clear();
        }
        setRefreshState(true);
        if(flag == 0){
            //下拉
            morningData.addAll(datas);
        }else{
            //上拉
            morningData.addAll(0, datas);
            if(localFirstData!=null&&morningData!=null&&morningData.size()>0){
                TeamMorningData data =  morningData.get(0);
                 if(!localFirstData.getDay().equals(data.getDay())){
                     morningData.add(0,localFirstData);
                     localFirstData=null;
                 }
            }
        }
        adapter.notifyUpdate(recycler, morningData);
        isMorningDataFinish = true;
        if(morningData.size()==0&&selectTime!=null){
            Toast.makeText(App.getApp(),getActivity().getResources().getString(R.string.morningFragment_no_record_file),Toast.LENGTH_SHORT).show();
        }
    }

    private void setRefreshState(boolean state){
        if (localLayout != null){
            if (localFlag == 0) {
                localLayout.finishLoadMore(state);
            } else {
                localLayout.finishRefresh(state);
            }
        }
        localLayout = null;
        localFlag = -1;
    }


    @Override
    public void getMorningDataError(int flag){
        if (!isGetData){
            isGetData = true;
            morningData.clear();
        }
        setRefreshState(false);
        if (selectTime == null || selectTime.equals(dateFormat.format(new Date(System.currentTimeMillis())))) {
            List<TeamMorningData> datas = GreenDaoManager.getDaoSession().getTeamMorningDataDao().queryBuilder().where(TeamMorningDataDao.Properties.PhoneNum.eq(CacheUserInfo.get().getUserPhone()),
                    TeamMorningDataDao.Properties.Day.eq(dateFormat.format(new Date(System.currentTimeMillis())))).list();
            if (datas != null && datas.size() != 0){
                TeamMorningData data = datas.get(datas.size() - 1);
                for (TeamMorningData morningDatum : morningData){
                    if (morningDatum.getPhoneNum().equals(CacheUserInfo.get().getUserPhone())
                            && morningDatum.getDay().equals(dateFormat.format(new Date(System.currentTimeMillis())))) {
                        morningData.remove(morningDatum);
                        break;
                    }
                }
                morningData.add(data);
            }
        }
        Toast.makeText(App.getApp(), "网络连接不可用，请稍后重试!", Toast.LENGTH_SHORT).show();
        adapter.notifyUpdate(recycler, morningData);
        isMorningDataFinish = true;
    }

    @Override
    public void notifyUpSuccess(TeamMorningData data){
        for (TeamMorningData morningDatum : morningData){
            if (morningDatum.getPhoneNum().equals(data.getPhoneNum())
                    && morningDatum.getDay().equals(data.getDay())) {
                morningDatum.setPath(data.getPath());
                DebugLog.showLog(this, "录音上传成功更新录音路径：" + data.getPath());
                break;
            }
        }
        adapter.notifyUpdate(recycler, morningData);
    }


    @Override
    public void downLoadFileSuccess(TeamMorningData data, int position){
        if (adapter.getItemPosition() == position) {
            setPlayMorningData(position);
            if (playBinder != null) {
                playBinder.start(data.getPath(), morningData.get(position).getUuid());
            }
            adapter.setProgressInVisible();
        }

        for (TeamMorningData morningDatum : morningData) {
            if (morningDatum.getPhoneNum().equals(data.getPhoneNum()) && morningDatum.getDay().equals(data.getDay())) {
                morningDatum.setPath(data.getPath());
                break;
            }
        }
    }

    @Override
    public void downLoadFileFail(TeamMorningData data, int position) {
        if (adapter.getItemPosition() == position) {
            Toast.makeText(App.getApp(), getResources().getString(R.string.morningFragment_download_fail), Toast.LENGTH_SHORT).show();
            adapter.setProgressInVisible();
        }

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageResultEvent(MessageEvent event) {
        if (event.getSign() == MessageEvent.EVENT_PERMISSION_SETTING) {
            PermissionUtil.goAppDetailSettingIntent(getActivity());
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onUpdateMorningData(UpdateMorningDataEvent event){
        TeamMorningData data = event.getEvent();
        if (data != null){
            notifyUpSuccess(data);
        }
    }

    public void getTeamAudioData(String selectTime, String userId, int flag, String chmId) {
        if (!PermissionUtil.isPremission(getActivity(), new String[]{PermissionUtil.SD_READ_PERMISSION,
                PermissionUtil.SD_WRITE_PERMISSION})) {
        }
        if (!isMorningDataFinish) {
            return;
        }
        isMorningDataFinish = false;
        presenter.getTeamAudioData(selectTime, userId, flag, chmId, isSelect);
    }

    @Override
    public void onLoadMore(RefreshLayout refreshLayout) {
        localLayout = refreshLayout;
        localFlag = 0;
        //下拉加载
        if (morningData.size() != 0){
            getTeamAudioData(selectTime, userId, 0, morningData.get(morningData.size() - 1).getChmId());
        } else {
            getTeamAudioData(selectTime, userId, 0, null);
        }
    }

    @Override
    public void onRefresh(RefreshLayout refreshLayout){
        //上拉刷新
        localLayout = refreshLayout;
        localFlag = 1;
        if (morningData.size() != 0){
            if (morningData.get(0).getChmId().equals("-1")){
                localFirstData = morningData.remove(0);
                if (morningData.size() != 0){
                    getTeamAudioData(selectTime, userId, 1, morningData.get(0).getChmId());
                } else {
                    getTeamAudioData(selectTime, userId, 0, null);
                }
            } else {
                getTeamAudioData(selectTime, userId, 1, morningData.get(0).getChmId());
            }
        } else {
            getTeamAudioData(selectTime, userId, 0, null);
        }
    }

    //不能按照上传的时间或者服务器存储时间来排序，因为刷新告诉服务器的是序号
    private void sortByTime(List<TeamMorningData> al){
        Collections.sort(al, new Comparator<TeamMorningData>() {
            @Override
            public int compare(TeamMorningData lhs, TeamMorningData rhs) {
                if (Integer.parseInt(lhs.getChmId()) > Integer.parseInt(rhs.getChmId()))
                    return -1;
                if (Integer.parseInt(lhs.getChmId()) == Integer.parseInt(rhs.getChmId()))
                    return 0;

                return 1;
            }
        });
    }


    @OnClick({R.id.morning_submit, R.id.morning_delete, R.id.tv_select_user})
    public void onViewClicked(View view) {
        switch (view.getId()){
            case R.id.morning_submit:
                trackState = AudioContent.IDLE_RECORDER;
                audioTip.setVisibility(View.VISIBLE);
                audioTime.setVisibility(View.GONE);
                audioPause.setVisibility(View.GONE);
                morningDelete.setVisibility(View.GONE);
                morningSubmit.setVisibility(View.GONE);
                adapter.setRecord(false);
                if (binder != null) {
                    binder.stopRecord();
                    binder.setListener(null);
                    filePath = binder.getFilePath();
                }
                DebugLog.showLog(this, "提交录音");
                audioTip.setText(getResources().getString(R.string.morningFragment_track));
                String duration = audioTime.getText().toString().trim();
                TeamMorningData data = presenter.getMorningData(duration, filePath, true);
                morningState.setText(getResources().getString(R.string.morningFragment_finish_track));
                notifyLocalData(data);
                presenter.uploadAudio(data);
                CacheConfig.get().saveMorningState(data.getDay(), true);
                break;
            case R.id.morning_delete:
                deleteAudio();
                break;
            case R.id.tv_select_user:
                DebugLog.showLog(this, "人员筛选");
                if(dialog==null){
                    dialog = new FilterMorningDialog();
                }
                dialog.setData(memberList);
                dialog.show(getActivity().getFragmentManager(), "1");
                dialog.setOnSelectListener(defaultUserIds, new FilterMorningDialog.OnSelectListener() {
                    @Override
                    public void select(Set<String> selectUserId, boolean checkAll){
                          getFilterUser(selectUserId, checkAll);
                    }
                });

                break;
        }
    }

    private void deleteAudio(){
        trackState = AudioContent.IDLE_RECORDER;
        adapter.setRecord(false);
        DebugLog.showLog(this, "删除录音");
        if (binder != null) {
            binder.stopRecord();
            binder.setListener(null);
            binder.deleteRecord();
        }
        TeamMorningDataDao dataDao = GreenDaoManager.getDaoSession().getTeamMorningDataDao();
        List<TeamMorningData> dataList = dataDao.queryBuilder().where(TeamMorningDataDao.Properties.PhoneNum.eq(CacheUserInfo.get().getUserPhone())
                , TeamMorningDataDao.Properties.Day.eq(dateFormat.format(new Date(System.currentTimeMillis()))), TeamMorningDataDao.Properties.IsNeedUpload.eq(false)).list();
        if (dataList != null && dataList.size() > 0){
            for (TeamMorningData teamMorningData : dataList) {
                dataDao.delete(teamMorningData);
                deleteFile(teamMorningData);
            }
        }
        morningDelete.setVisibility(View.GONE);
        morningSubmit.setVisibility(View.GONE);
        audioPause.setVisibility(View.GONE);
        audioTip.setVisibility(View.VISIBLE);
        audioTime.setVisibility(View.GONE);
        setTrackState();
    }

    @Override
    public void TeamMember(List<Team.TeamMember> teams){
            if(teams!=null){
               memberList.clear();
               memberList.addAll(teams);
               defaultUserIds.clear();
                for (Team.TeamMember team : teams){
                     defaultUserIds.add(team.getUserId());
                }
            }
           setSelectView();
    }

    private void setSelectView(){
        if(memberList.size()==0){
            selectUser.setVisibility(View.GONE);
            morningLine.setVisibility(View.GONE);
        } else {
            selectUser.setVisibility(View.VISIBLE);
            morningLine.setVisibility(View.VISIBLE);
        }
    }

    public void getFilterUser(Set<String> selectUserId, boolean checkAll) {
        morningData.clear();
        adapter.notifyUpdate(recycler, morningData);
        defaultUserIds = selectUserId;
        StringBuffer buffer = new StringBuffer();
        ArrayList list = new ArrayList<String>(selectUserId);
        for (int i = 0; i < list.size(); i++) {
            if (i != list.size() - 1) {
                buffer.append(list.get(i));
                buffer.append(",");
            } else {
                buffer.append(list.get(i));
            }
        }
        userId = buffer.toString();
        isSelect = true;
        swipeRefresh.autoRefresh();
        DebugLog.showLog(this, "userIds:" + userId);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        DebugLog.showLog(this, "onDetach");
    }

    @Override
    public void OnTime(String s){
        if (trackState == AudioContent.PAUSE_RECORDER){
            DebugLog.showLog(this, "已经是暂停状态!");
            return;
        }
        audioTime.setText(s);
        audioTip.setVisibility(View.GONE);
        audioPause.setVisibility(View.GONE);
        audioTime.setVisibility(View.VISIBLE);
        morningDelete.setVisibility(View.VISIBLE);
        morningSubmit.setVisibility(View.VISIBLE);
    }


    @Override
    public void OnRecordFail(){
        mHandler.post(new Runnable() {
            @Override
            public void run(){
                Toast.makeText(getActivity(),"请检查是否有其他录音正在进行或者重启APP",Toast.LENGTH_SHORT).show();
                deleteAudio();
            }
        });
    }

    @Override
    public void onStart(){
        super.onStart();
        DebugLog.showLog(this, "绑定录音服务");
        startAudioService();
        startPlayService();
    }


    @Override
    public void onStop(){
        super.onStop();
        DebugLog.showLog(this, "解绑录音服务");
        stopAudioService();
        stopPlayService();
    }

    private void startPlayService() {
        Intent intent = new Intent(getActivity(), PlayService.class);
        getActivity().bindService(intent, playConnection, Context.BIND_AUTO_CREATE);
    }

    private void stopPlayService(){
        if (playBinder != null) {
            playBinder.setListener(null);
        }
        getActivity().unbindService(playConnection);
    }


    private ServiceConnection playConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            playBinder = (PlayService.PlayBinder) service;
            DebugLog.showLog(this, "绑定成功!");
            if (playBinder != null) {
                playBinder.setListener(MorningFragment.this);
            }

        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    private void startAudioService() {
        Intent intent = new Intent(getActivity(), AudioRecordService.class);
        getActivity().bindService(intent, audioConnection, Context.BIND_AUTO_CREATE);
    }

    private void stopAudioService() {
        if (binder != null) {
            binder.setListener(null);
            binder.setAudioState(trackState);
            binder.setTime(audioTime.getText().toString().trim());
        }
        getActivity().unbindService(audioConnection);
    }


    private ServiceConnection audioConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service){
            binder = (AudioRecordService.RecordBinder) service;
            DebugLog.showLog(this, "绑定成功!");
            if (binder != null) {
                binder.setListener(MorningFragment.this);
                trackState = binder.getAudioState();
                String time = binder.getTime();
                if (trackState == AudioContent.PAUSE_RECORDER) {
                    audioTip.setVisibility(View.GONE);
                    audioPause.setVisibility(View.VISIBLE);
                    morningDelete.setVisibility(View.VISIBLE);
                    morningSubmit.setVisibility(View.VISIBLE);
                    audioTime.setText(time);
                    audioTime.setVisibility(View.VISIBLE);
                }
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    @Override
    public void onMax(int max) {
        DebugLog.showLog(this, "max:" + max);
        if (playMorningData != null) {
            playMorningData.setMax(max);
            playMorningData.setIsPlay(false);
            adapter.setPlayItem(playMorningData);
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onProgress(int progress) {
        if (playMorningData != null) {
            playMorningData.setIsPlay(false);
            playMorningData.setProgress(progress);
            adapter.setPlayItem(playMorningData);
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onState(int state) {
        if (playMorningData == null) {
            return;
        }
        if (state == PlayService.PLAYING) {
            // adapter.setPlay(true);
            playMorningData.setIsPlay(true);
        } else if (state == PlayService.PAUSE || state == PlayService.STOP){
            // adapter.setPlay(false);
            playMorningData.setIsPlay(false);
        }
        adapter.setPlayItem(playMorningData);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onComplete(int position) {
        // adapter.setProgress(position);
        if (playMorningData != null){
            playMorningData.setProgress(position);
            adapter.setPlayItem(playMorningData);
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        DebugLog.showLog(this, "录音界面消失了..");
        fragmentManager = null;
        isGetData = false;
        isMoringFragmentCreate = false;
        if (swipeRefresh != null) {
            swipeRefresh.setOnRefreshListener(null);
        }
        EventBus.getDefault().unregister(this);
    }
}

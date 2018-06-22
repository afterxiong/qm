package com.shareshow.aide.activity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.bigkoo.pickerview.TimePickerView;
import com.shareshow.App;
import com.shareshow.aide.R;
import com.shareshow.aide.adapter.PersonAudioAdapter;
import com.shareshow.aide.fragment.MorningFragment;
import com.shareshow.aide.impl.OnItemClickListener;
import com.shareshow.aide.impl.OnRecycleviewListener;
import com.shareshow.aide.mvp.presenter.PersonAudioPresenter;
import com.shareshow.aide.mvp.view.PersonAudioView;
import com.shareshow.aide.retrofit.entity.TeamAudioData;
import com.shareshow.aide.retrofit.entity.TeamMorningData;
import com.shareshow.aide.service.PlayService;
import com.shareshow.aide.util.CacheUserInfo;
import com.shareshow.aide.util.CopyFile;
import com.shareshow.aide.util.Fixed;
import com.shareshow.airpc.util.DebugLog;
import com.shareshow.airpc.widget.ProgressWheel;
import com.shareshow.db.GreenDaoManager;
import com.shareshow.db.TeamMorningDataDao;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import jp.sourceforge.qrcode.util.Color;

/**
 * Created by FENGYANG on 2018/3/16.
 * 这个类业务方面与morningfragment很相似
 */

public class PersonAudioActivity extends BaseActivity<PersonAudioView, PersonAudioPresenter> implements PersonAudioView,OnRecycleviewListener, PlayService.PlayListener {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.startTime)
    TextView startTime;
    @BindView(R.id.endTime)
    TextView endTime;
    @BindView(R.id.track_totle)
    TextView trackTotle;
    @BindView(R.id.audio_recylerview)
    RecyclerView audioRecylerview;
    @BindView(R.id.title)
    TextView title;
    @BindView(R.id.progress_wheel)
    ProgressWheel progressWheel;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    private ArrayList<TeamMorningData> morningData =new ArrayList<TeamMorningData>();
    private PersonAudioAdapter adapter;
    private String userId;
    private String userPhone;
    private TeamMorningData playMorningData;
    private static final int START_TIME_SELECT =1;
    private static final int END_TIME_SELECT =2;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person_audio);
        ButterKnife.bind(this);
        initToolbar();
        initView();
        initData();
    }

    public void initToolbar() {
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowTitleEnabled(false);
        }
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }


    private void initData(){
        userId = getIntent().getStringExtra(Fixed.USER_ID);
        userPhone = getIntent().getStringExtra(Fixed.USER_PHONE);
        getAudioData();
        getMonthAudioTotelData();
    }

    private void getMonthAudioTotelData(){
        presenter.getMonthAudioMum(userId);
    }

    private void getAudioData(){
        progressWheel.setVisibility(View.VISIBLE);
        presenter.getPersonAudio(userId,
                startTime.getText().toString().trim(),
                endTime.getText().toString().trim());
    }


    private void initView(){
        String username = getIntent().getStringExtra(Fixed.USER_NAME);
        title.setText(username + "晨会录音");
        String date = dateFormat.format(System.currentTimeMillis());
        endTime.setText(date);
        startTime.setText(CopyFile.getOldDate(-7));
        progressWheel.setVisibility(View.VISIBLE);
        adapter = new PersonAudioAdapter(morningData);
        audioRecylerview.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        audioRecylerview.setLayoutManager(new LinearLayoutManager(this));
        audioRecylerview.setAdapter(adapter);
        adapter.setOnItemClickListener(this);
    }

    @Override
    public Toolbar getToolbar(){
        return toolbar;
    }

    @Override
    public PersonAudioPresenter createPresenter(){
        return new PersonAudioPresenter();
    }


    @OnClick(R.id.startTime)
    public void startTime() {
        selectTime(START_TIME_SELECT);
    }

    @OnClick(R.id.endTime)
    public void endTime() {
        selectTime(END_TIME_SELECT);
    }

    public void selectTime(int type){
        //时间选择器
        Calendar startDate = Calendar.getInstance();
        startDate.set(2017, 11, 01);
        Calendar endDate = Calendar.getInstance();
        int year = endDate.get(Calendar.YEAR);
        int month = endDate.get(Calendar.MONTH);
        int day = endDate.get(Calendar.DAY_OF_MONTH);
        endDate.set(year, month, day);
        TimePickerView pvTime = new TimePickerView.Builder(this, new TimePickerView.OnTimeSelectListener() {
            @Override
            public void onTimeSelect(Date date, View v) {//选中事件回调
                try{
                    String start = startTime.getText().toString().trim();
                    String end = endTime.getText().toString().trim();
                    DebugLog.showLog(this,"start:"+start+"end:"+end);
                    String time = dateFormat.format(date);
                    date=dateFormat.parse(dateFormat.format(date));
                    if(type==START_TIME_SELECT){
                         Date endtime= dateFormat.parse(end);
                        if(date.getTime()>(endtime.getTime())){
                            Toast.makeText(PersonAudioActivity.this,getResources().getString(R.string.person_audio_starttime),Toast.LENGTH_SHORT).show();
                            return;
                        }

                        if(CopyFile.isPassNinety(date,endtime)){
                            Toast.makeText(PersonAudioActivity.this,getResources().getString(R.string.person_audio_90_day),Toast.LENGTH_SHORT).show();
                            return;
                        }

                        startTime.setText(time);
                    }else{
                        Date starttime= dateFormat.parse(start);
                        if(date.getTime()<(starttime.getTime())){
                            Toast.makeText(PersonAudioActivity.this,getResources().getString(R.string.person_audio_endtime),Toast.LENGTH_SHORT).show();
                            return;
                        }

                        if(CopyFile.isPassNinety(starttime,date)){
                            Toast.makeText(PersonAudioActivity.this,getResources().getString(R.string.person_audio_90_day),Toast.LENGTH_SHORT).show();
                            return;
                        }
                        endTime.setText(time);
                    }
                    getAudioData();
               }catch (Exception e){
                    e.printStackTrace();
                }

            }
        })
                .setTitleText(type == START_TIME_SELECT ? "开始日期" : "结束日期")
                .setRangDate(startDate, endDate)
                .isCenterLabel(false)
                .setType(new boolean[]{true, true, true, false, false, false})
                .build();
        pvTime.setDate(Calendar.getInstance());//注：根据需求来决定是否使用该方法（一般是精确到秒的情况），此项可以在弹出选择器的时候重新设置当前时间，避免在初始化之后由于时间已经设定，导致选中时间与当前时间不匹配的问题。
        pvTime.show();
    }


    @Override
    public void setPersonAudioData(List<TeamMorningData> datasBeans){
        morningData.clear();
        if(datasBeans!=null){
            morningData.addAll(datasBeans);
        }
        adapter.notifyDataSetChanged();
        progressWheel.setVisibility(View.GONE);
    }

    @Override
    public void setPersonDataError(){
        morningData.clear();
        progressWheel.setVisibility(View.GONE);
        Toast.makeText(PersonAudioActivity.this,"获取录音数据失败!",Toast.LENGTH_SHORT).show();
    }

    @Override
    public void setPersonAudioMum(int totleTime, int index){
        if(index==-1){
              DebugLog.showLog(this,"获取总签到失败!");
        }else{
              trackTotle.setText(Html.fromHtml("本月签到"+index+"次，未签到<font color='#ff0000'>"+(totleTime-index)+"</font>次"));
        }
    }

    @Override
    public void downLoadFileSuccess(TeamMorningData data,int position){
        if(adapter.getItemPosition()==position){
            setPlayMorningData(position);
            if(playBinder!=null){
                playBinder.start(data.getPath(),morningData.get(position).getUuid());
            }
            adapter.setProgressInVisible();
        }

        for(TeamMorningData morningDatum : morningData){
            if (morningDatum.getPhoneNum().equals(data.getPhoneNum()) && morningDatum.getDay().equals(data.getDay())) {
                morningDatum.setPath(data.getPath());
                break;
            }
        }
    }

    @Override
    public void downLoadFileFail(TeamMorningData data,int position){
        if(adapter.getItemPosition()==position){
            Toast.makeText(App.getApp(), getResources().getString(R.string.morningFragment_download_fail), Toast.LENGTH_SHORT).show();
            adapter.setProgressInVisible();
        }
    }

    @Override
    public void onItemClick(int position, Object obj){
        TeamMorningData datasBean = (TeamMorningData) obj;
        File playFile = getCacheFile(datasBean);
        if(playFile.exists()){
            setPlayMorningData(position);
            if(playBinder!=null){
                playBinder.start(playFile.getPath(),morningData.get(position).getUuid());
            }
        }else{
            //重新下载
            adapter.setProgressVisible(position);
            presenter.downPersonAudio(datasBean,position);
        }
    }

    @Override
    public void OnPlay(TeamMorningData data, int flag){
        if(playBinder!=null){
            if(flag==0){
                playBinder.start(data.getPath(),data.getUuid());
            }else if(flag==1){
                playBinder.pause();
            }else{
                playBinder.stop();
            }
        }

    }

    @Override
    public void OnSeekBar(int progress){
        if(playMorningData!=null){
            playMorningData.setProgress(progress);
            adapter.setPlayItem(playMorningData);
        }
        if(playBinder!=null){
            playBinder.seekTo(progress);
        }
    }


    @NonNull
    private File getCacheFile(TeamMorningData data){
        return new File(Fixed.getRemouteMoningFile() + File.separator + data.getDay() + File.separator + data.getPhoneNum()+"_"+CopyFile.getTime(data.getTime())+ ".aac");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    private void setPlayMorningData(int position){
        for (int i = 0; i < morningData.size(); i++){
            if(i==position){
                playMorningData = morningData.get(position);
                playMorningData.setIsPlayItem(true);
                playMorningData.setIsPlay(false);
                adapter.setPlayItem(playMorningData);
                adapter.setItemPosition(position);
            }else{
                morningData.get(i).clear();
            }
        }
        adapter.notifyDataSetChanged();
    }

    @Override
    protected void onStart() {
        super.onStart();
        startPlayService();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(playBinder!=null){
            playBinder.stop();
        }
        if(playMorningData!=null&&playMorningData.getIsPlay()){
            Toast.makeText(this,getResources().getString(R.string.person_audio_stop),Toast.LENGTH_SHORT).show();
        }
        stopPlayService();
    }

    private void startPlayService(){
        Intent intent = new Intent(this, PlayService.class);
        this.bindService(intent, playConnection, Context.BIND_AUTO_CREATE);
    }

    private void stopPlayService() {
        if (playBinder != null) {
            playBinder.setListener(null);
        }
        unbindService(playConnection);
    }


    private PlayService.PlayBinder playBinder;
    private ServiceConnection playConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service){
            playBinder = (PlayService.PlayBinder) service;
            DebugLog.showLog(this,"绑定成功!");
            if(playBinder !=null){
                playBinder.setListener(PersonAudioActivity.this);
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };


    @Override
    public void onMax(int max){
        if(playMorningData!=null){
            playMorningData.setMax(max);
            playMorningData.setIsPlay(false);
            adapter.setPlayItem(playMorningData);
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onProgress(int progress){
        if(playMorningData!=null){
            playMorningData.setProgress(progress);
            adapter.setPlayItem(playMorningData);
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onState(int state){
        if(playMorningData==null){
            return;
        }
        if(state==PlayService.PLAYING){
            playMorningData.setIsPlay(true);
        }else if(state==PlayService.PAUSE||state==PlayService.STOP){
            playMorningData.setIsPlay(false);
        }
        adapter.setPlayItem(playMorningData);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onComplete(int position) {
        if(playMorningData!=null){
            playMorningData.setProgress(position);
            adapter.setPlayItem(playMorningData);
            adapter.notifyDataSetChanged();
        }

    }
}

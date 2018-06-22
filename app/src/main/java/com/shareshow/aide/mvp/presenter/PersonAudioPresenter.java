package com.shareshow.aide.mvp.presenter;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.v4.content.FileProvider;

import com.shareshow.aide.mvp.model.PersonAudioModel;
import com.shareshow.aide.mvp.view.PersonAudioView;
import com.shareshow.aide.retrofit.entity.TeamAudioData;
import com.shareshow.aide.retrofit.entity.TeamMorningData;
import com.socks.library.KLog;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

/**
 * Created by FENGYANG on 2018/3/16.
 */

public class PersonAudioPresenter extends BasePresenter<PersonAudioView> {

    private  PersonAudioModel model;

    public PersonAudioPresenter(){
         model = new PersonAudioModel(this);
    }

    public void getPersonAudio(String userId, String startTime, String endTime){
         model.getDateFilterMorningRecord(userId,startTime,endTime);
    }

    public void personAudioData(List<TeamAudioData.DatasBean> datasBeans){
        if(datasBeans==null){
            if(isViewAttached()){
                getView().setPersonAudioData(null);
            }
            return;
        }
        ArrayList<TeamMorningData>  datas = new ArrayList<TeamMorningData>();
        for(TeamAudioData.DatasBean teamData : datasBeans){
            TeamMorningData morningData = new TeamMorningData();
            morningData.setChmId(teamData.getChmId());
            morningData.setUserName(teamData.getChmFilename());
            morningData.setDay(teamData.getChmDay());
            morningData.setAuthor(teamData.getChmPhone());
            morningData.setDuration(teamData.getChmAudiotime());
            morningData.setIsUpload(true);
            morningData.setTime(teamData.getChmTime());
            morningData.setIsRemoteAudio(true);
            morningData.setUrl(teamData.getChmFilepath());
            morningData.setPath(null);
            morningData.setPhoneNum(teamData.getChmPhone());
            morningData.setUuid(UUID.randomUUID().toString());
            morningData.setIsPlay(false);
            morningData.setIsPlayItem(false);
            morningData.setMax(0);
            morningData.setProgress(0);
            datas.add(morningData);
        }
        sortByTime(datas);
        if(isViewAttached()){
            getView().setPersonAudioData(datas);
        }
    }

    private void sortByTime(List<TeamMorningData> al){
        Collections.sort(al,new Comparator<TeamMorningData>(){
            @Override
            public int compare(TeamMorningData lhs, TeamMorningData rhs) {
                if(getTime(lhs)>getTime(rhs))
                    return -1;
                if(getTime(lhs)==getTime(rhs))
                    return 0;

                return 1;
            }
        });
    }

    public long getTime(TeamMorningData data){
        SimpleDateFormat timeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            return timeFormat.parse(data.getTime()).getTime();
        } catch (ParseException e) {
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * 播放录音文件
     *
     * @param activity
     * @param param
     */
    public void mediaAudio(Activity activity, File param){
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        Uri uri;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            uri = FileProvider.getUriForFile(activity, "com.shareshow.aide.fileProvider", param);
        } else {
            uri = Uri.fromFile(param);
        }
        KLog.d(uri);
        intent.setDataAndType(uri, "audio/*");
        activity.startActivity(intent);
    }

    public void personDataError(){
        if(isViewAttached()){
            getView().setPersonDataError();
        }

    }

    public void getMonthAudioMum(String userId) {
        model.getMonthAudioData(userId);
    }

    public void setPersonAudioMum(int totleTime, int i) {
        if(isViewAttached()){
            getView().setPersonAudioMum(totleTime,i);
        }
    }

    public void downPersonAudio(TeamMorningData datasBean, int position) {
        model.downAudioFile(datasBean,position);
    }

    public void downloadComplite(boolean b, TeamMorningData data, int position){
        if(b){
            if(isViewAttached()){
                getView().downLoadFileSuccess(data,position);
            }
        }else{
            if(isViewAttached()){
                getView().downLoadFileFail(data,position);
            }
        }
    }
}

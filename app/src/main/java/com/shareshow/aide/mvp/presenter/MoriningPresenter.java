package com.shareshow.aide.mvp.presenter;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;

import com.shareshow.aide.mvp.model.MorningModel;
import com.shareshow.aide.mvp.view.MroiningView;
import com.shareshow.aide.retrofit.entity.Team;
import com.shareshow.aide.retrofit.entity.TeamAudioData;
import com.shareshow.aide.retrofit.entity.TeamMorningData;
import com.shareshow.aide.util.CacheUserInfo;
import com.shareshow.aide.util.CopyFile;
import com.shareshow.aide.util.Fixed;
import com.shareshow.airpc.util.DebugLog;
import com.shareshow.airpc.util.FileReNameUtils;
import com.shareshow.db.GreenDaoManager;
import com.shareshow.db.TeamMorningDataDao;
import com.socks.library.KLog;
import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * Created by xiongchengguang on 2017/12/8.
 */

public class MoriningPresenter extends BasePresenter<MroiningView> {

    private MorningModel model;

    private SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

    private SimpleDateFormat timeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public MoriningPresenter(){
        model = new MorningModel(this);
    }

    public void uploadAudio(TeamMorningData data){
        File file = new File(data.getPath());
        if (file.exists()) {
            new Thread(new Runnable(){
                @Override
                public void run(){
                    //解决部分机型，上传数据出现收到数据和预期数据大小不一致问题
                    SystemClock.sleep(1000);
                    model.uploadAudio(data,file);
                }
            }).start();
        }
    }


    public void clearAllDatas() {
        //分别保存本地的七天的录音和点击下载后三天的录音
        TeamMorningDataDao dataDao = GreenDaoManager.getDaoSession().getTeamMorningDataDao();
        List<TeamMorningData> dataList = dataDao.loadAll();
        String today = format.format(System.currentTimeMillis());
        String beforeDay = CopyFile.getOldDate(-1);
        String beforeTwoDay = CopyFile.getOldDate(-2);
        String beforeThreeDay = CopyFile.getOldDate(-3);
        for (TeamMorningData teamMorningData : dataList) {
            if (!teamMorningData.getIsUpload()) {
                continue;
            }
            if ((teamMorningData.getDay().equals(today)
                    || teamMorningData.getDay().equals(beforeDay)
                    || teamMorningData.getDay().equals(beforeTwoDay)
                    || teamMorningData.getDay().equals(beforeThreeDay))
                    && teamMorningData.getPhoneNum().equals(CacheUserInfo.get().getUserPhone())) {
                continue;
            }
            dataDao.delete(teamMorningData);
            deleteFile(teamMorningData);
        }
        File dirFile = new File(Fixed.getRemouteMoningFile());
        if (dirFile.exists()) {
            for (File file : dirFile.listFiles()) {
                if (file.getName().equals(today)
                        || file.getName().equals(beforeDay)
                        || file.getName().equals(beforeTwoDay)
                        || file.getName().equals(beforeThreeDay)) {
                    continue;
                }
                if (file.exists()) {
                    FileReNameUtils.delAllFile(file.getPath());
                    file.delete();
                }
            }
        } else {
            return;
        }
    }

    private void deleteFile(TeamMorningData teamMorningData) {
        if (teamMorningData.getPath() != null) {
            File teamFile = new File(teamMorningData.getPath());
            if (teamFile.exists()) {
                teamFile.delete();
            }
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

    public void getTeamAudioData(String localTime, String userId,int i, String chmId,boolean isSelect){
        if(TextUtils.isEmpty(CacheUserInfo.get().getTeamId())){
            List<Team.TeamMember> members = new ArrayList<>();
            TeamMembers(members);
        }
        model.getTeamAudioData(localTime,userId,i,chmId,isSelect);
    }

    public void teamAudioData(List<TeamAudioData.DatasBean> teamDatas, int flag){
        if(teamDatas==null){
            if(isViewAttached()){
                getView().notifyTeamData(null,flag);
            }
            return;
        }
        ArrayList<TeamMorningData>  datas = new ArrayList<TeamMorningData>();
        TeamMorningDataDao dataDao = GreenDaoManager.getDaoSession().getTeamMorningDataDao();
        for(TeamAudioData.DatasBean teamData : teamDatas){
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
                List<TeamMorningData> morningDataList = dataDao.queryBuilder().where(TeamMorningDataDao.Properties.PhoneNum.eq(morningData.getPhoneNum()),
                        TeamMorningDataDao.Properties.Day.eq(morningData.getDay()),TeamMorningDataDao.Properties.IsNeedUpload.eq(true)).list();
                if(morningDataList != null){
                    for (TeamMorningData teamMorningData : morningDataList){
                        if (!teamMorningData.getIsUpload()){
                            //如果有上传没有成功的，以上传未成功的为准！
                            DebugLog.showLog(this,"如果还没有上传以未上传成功的为准!");
                            morningData.setPath(teamMorningData.getPath());
                            morningData.setDuration(teamMorningData.getDuration());
                            morningData.setTime(teamMorningData.getTime());
                            break;
                        } else {
                            //如果有上传成功的，就要修改存储为准，以免下次从服务器下载
                            morningData.setPath(teamMorningData.getPath());
                            break;
                        }
                    }
                }
                datas.add(morningData);
            }
        if(isViewAttached()){
            getView().notifyTeamData(datas, flag);
        }
    }

    public TeamMorningData getMorningData(String duration, String dstFolder,boolean isNeetUpload){
        TeamMorningData entry = new TeamMorningData();
        entry.setPhoneNum(CacheUserInfo.get().getUserPhone());
        entry.setAuthor(CacheUserInfo.get().getUserPhone());
        entry.setUserName(CacheUserInfo.get().getUserName());
        entry.setIsUpload(false);
        entry.setIsNeedUpload(isNeetUpload);
        entry.setId(System.currentTimeMillis());
        entry.setDuration(duration);
        entry.setTime(timeFormat.format(System.currentTimeMillis()));
        entry.setPath(dstFolder);
        entry.setChmId("-1");
        entry.setDay(format.format(System.currentTimeMillis()));
        entry.setUuid(UUID.randomUUID().toString());
        entry.setIsPlay(false);
        entry.setIsPlayItem(false);
        entry.setMax(0);
        entry.setProgress(0);
        TeamMorningDataDao dataDao = GreenDaoManager.getDaoSession().getTeamMorningDataDao();
        List<TeamMorningData> datas=null;
        if(isNeetUpload){
            datas = dataDao.queryBuilder().where(TeamMorningDataDao.Properties.Day.eq(entry.getDay()),TeamMorningDataDao.Properties.PhoneNum.eq(entry.getPhoneNum())
                   ).list();
        }else{
            datas = dataDao.queryBuilder().where(TeamMorningDataDao.Properties.Day.eq(entry.getDay()),TeamMorningDataDao.Properties.PhoneNum.eq(entry.getPhoneNum())
                    ,TeamMorningDataDao.Properties.IsNeedUpload.eq(isNeetUpload)).list();
        }
        if(datas!=null){
            for(TeamMorningData data : datas){
                 deletFile(data.getPath(),dstFolder);
                 dataDao.delete(data);
            }
        }
        List<TeamMorningData> dataList=dataDao.loadAll();
        dataDao.insert(entry);
        return entry;
    }

    private void deletFile(String path,String dstFolder){
        if(path==null||path.equals(dstFolder)){
            return;
        }
        File file = new File(path);
        if(file.exists()){
            file.delete();
        }
    }

    public void downloadFile(TeamMorningData data, int position){
        DebugLog.showLog(this,"开始下载文件");
        model.downloadFile(data,position);
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

    public void getMorningDataError(int flag){
        if(isViewAttached()){
            getView().getMorningDataError(flag);
        }
    }

    private void sortByTime(ArrayList<TeamMorningData> al){
        Collections.sort(al,new Comparator<TeamMorningData>(){
            @Override
            public int compare(TeamMorningData lhs, TeamMorningData rhs) {
                if(Integer.parseInt(lhs.getChmId())>Integer.parseInt(rhs.getChmId()))
                    return -1;
                if(Integer.parseInt(lhs.getChmId())==Integer.parseInt(rhs.getChmId()))
                    return 0;

                return 1;
            }
        });
    }


    public long getTime(TeamMorningData data){
        try {
            return timeFormat.parse(data.getTime()).getTime();
        } catch (ParseException e) {
            e.printStackTrace();
            return 0;
        }
    }


    public void morningDataUpSuccess(TeamMorningData data){
        if(isViewAttached()){
            getView().notifyUpSuccess(data);
        }
    }

    @NonNull
    public File getCacheFile(TeamMorningData data){
        return new File(Fixed.getRemouteMoningFile() + File.separator + data.getDay() + File.separator + data.getPhoneNum() + "_" + CopyFile.getTime(data.getTime()) + ".aac");
    }


    public void TeamMembers(List<Team.TeamMember> teams){
        if(isViewAttached()){
            getView().TeamMember(teams);
        }
    }
}

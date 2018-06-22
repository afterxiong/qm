package com.shareshow.aide.mvp.model;

import android.text.TextUtils;

import com.alibaba.sdk.android.oss.ClientConfiguration;
import com.alibaba.sdk.android.oss.OSS;
import com.alibaba.sdk.android.oss.OSSClient;
import com.alibaba.sdk.android.oss.common.auth.OSSAuthCredentialsProvider;
import com.alibaba.sdk.android.oss.common.auth.OSSCredentialProvider;
import com.alibaba.sdk.android.oss.model.PutObjectRequest;
import com.shareshow.App;
import com.shareshow.aide.mvp.presenter.MoriningPresenter;
import com.shareshow.aide.retrofit.Api;
import com.shareshow.aide.retrofit.RetrofitProvider;
import com.shareshow.aide.retrofit.entity.Team;
import com.shareshow.aide.retrofit.entity.TeamAudioData;
import com.shareshow.aide.retrofit.entity.TeamMorningData;
import com.shareshow.aide.retrofit.entity.UserInfo;
import com.shareshow.aide.util.CacheUserInfo;
import com.shareshow.aide.util.CopyFile;
import com.shareshow.aide.util.Fixed;
import com.shareshow.airpc.util.DebugLog;
import com.shareshow.db.GreenDaoManager;
import com.shareshow.db.TeamMorningDataDao;
import com.socks.library.KLog;

import java.io.File;
import java.io.FileOutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSink;
import okio.BufferedSource;
import okio.Okio;

/**
 * Created by xiongchengguang on 2017/12/8.
 */

public class MorningModel implements BaseModel {

    private MoriningPresenter presenter;

    private Api api;

    private SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

    public MorningModel(MoriningPresenter presenter) {
        this.presenter = presenter;
        api = RetrofitProvider.getApi();
    }

    private static String endpoint = "http://oss-cn-hangzhou.aliyuncs.com";
    private static String bucketName = "qmkj-20180416-oss";
    private String stsServer = "http://10.42.0.95:8080/OssToken/AppToken";

    //上传录音至OSS 录音信息至平台服务器
    public void uploadAudio(TeamMorningData data, File file) {
        OSSCredentialProvider credentialProvider = new OSSAuthCredentialsProvider(stsServer);
        ClientConfiguration conf = new ClientConfiguration();
        conf.setConnectionTimeout(15 * 1000); // 连接超时，默认15秒
        conf.setSocketTimeout(15 * 1000); // socket超时，默认15秒
        conf.setMaxConcurrentRequest(5); // 最大并发请求数，默认5个
        conf.setMaxErrorRetry(2); // 失败后最大重试次数，默认2次
        final OSS oss = new OSSClient(App.getApp(), endpoint, credentialProvider, conf);

        Observable.just(file)
                .map(new Function<File, String>() {
                    @Override
                    public String apply(File file) throws Exception {
                        String objectKey = "android_morning" + CacheUserInfo.get().getUserPhone() + System.currentTimeMillis() + new Random().nextInt(999) + file.getName();
                        PutObjectRequest request = new PutObjectRequest(bucketName, objectKey, file.getPath());
                        oss.putObject(request);
                        return request.getObjectKey();
                    }
                })
                .flatMap(new Function<String, ObservableSource<UserInfo>>() {
                    @Override
                    public ObservableSource<UserInfo> apply(String s) throws Exception {
                        return api.uploadFileMorning(data.getDay(), CacheUserInfo.get().getUserId(), data.getDuration(), CacheUserInfo.get().getUserPhone(), s, file.getName());
                    }
                })
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<UserInfo>() {
                    @Override
                    public void accept(UserInfo userInfo) throws Exception {
                        if (userInfo.getReturnCode()==1) {
                            KLog.d("晨会签到录音上传成功");
                            TeamMorningDataDao teamDao = GreenDaoManager.getDaoSession().getTeamMorningDataDao();
                            data.setIsUpload(true);
                            data.setIsRemoteAudio(true);
                            teamDao.update(data);
                        } else {
                            KLog.d("晨会签到录音上传失败");
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        throwable.printStackTrace();
                        KLog.d("晨会签到录音上传失败");
                    }
                });

        /*RequestBody body = RequestBody.create(MediaType.parse("application/otcet-stream"), file);
        MultipartBody.Part part = MultipartBody.Part.createFormData("file", file.getName(), body);
        String chchePhone = CacheUserInfo.get().getUserPhone();
        RequestBody phone = RequestBody.create(null, chchePhone);
        RequestBody userId =RequestBody.create(null,CacheUserInfo.get().getUserId());
        RequestBody duration =RequestBody.create(null,data.getDuration());
        RequestBody date = RequestBody.create(null,data.getDay());
//             api.uploadFilePlayeRecoed(date,part,phone,userId,duration)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.newThread())
                .subscribe(new Consumer<ResponseBody>(){
                    @Override
                    public void accept(ResponseBody responseBody) throws Exception{
                        String string = responseBody.string();
                        if(string.equals("1")){
                            DebugLog.showLog(this,"文件上传成功!");
                            TeamMorningDataDao teamDao = GreenDaoManager.getDaoSession().getTeamMorningDataDao();
                            data.setIsUpload(true);
                            data.setIsRemoteAudio(true);
                            List<TeamMorningData> teamMorningDatas = teamDao.queryBuilder().where(TeamMorningDataDao.Properties.Day.eq(data.getDay()),TeamMorningDataDao.Properties.PhoneNum.eq(data.getPhoneNum())).list();
                            if(teamMorningDatas!=null){
                                for(TeamMorningData teamMorningData : teamMorningDatas){
                                     teamDao.delete(teamMorningData);
                                }
                            }
                            teamDao.insertOrReplace(data);
                        }else{
                            DebugLog.showLog(this,"文件上传失败!"+data.getTime());
                        }
                    }
                }, new Consumer<Throwable>(){
                    @Override
                    public void accept(Throwable throwable) throws Exception{
                        throwable.printStackTrace();
                        DebugLog.showLog(this,"文件上传失败!"+data.getTime());
                    }
                });*/
    }

    //上传成功就清空所有的本地的录音文件
    private void clearMorningData(boolean isSuccess, String srcPath) {
        if (isSuccess) {
            new File(srcPath).delete();
        }
    }

    private void deletFile(String path) {
        File file = new File(path);
        if (file.exists()) {
            file.delete();
        }
    }

    public void getTeamAudioData(String time, String userId, int flag, String chmId, boolean isSelect) {
        DebugLog.showLog(this, "TeamId:" + CacheUserInfo.get().getTeamId() + "isSelect:" + isSelect + "userId:" + userId);
        if (TextUtils.isEmpty(CacheUserInfo.get().getTeamId()) || isSelect) {
            getTeamMorningData(time, userId, flag, chmId);
            return;
        }
        api.teamMember(CacheUserInfo.get().getTeamId())
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Team>() {
                    @Override
                    public void accept(Team team) throws Exception {
                        if (team.getReturnCode() == 1) {
                            List<Team.TeamMember> teamMembers = team.getDatas();
                            if (teamMembers.get(0).getUserId().equals(CacheUserInfo.get().getUserId())) {
                                //如果是团队长，就需要获取所有人的录音
                                StringBuffer teambuffer = new StringBuffer();
                                for (int i = 0; i < teamMembers.size(); i++) {
                                    teambuffer.append(teamMembers.get(i).getUserId());
                                    if (i != teamMembers.size() - 1) {
                                        teambuffer.append(",");
                                    }
                                }
                                getTeamMorningData(time, teambuffer.toString(), flag, chmId);
                                presenter.TeamMembers(teamMembers);
                            } else {
                                //如果是个人就只需要获取自己的录音
                                List<Team.TeamMember> members = new ArrayList<>();
                                getTeamMorningData(time, CacheUserInfo.get().getUserId(), flag, chmId);
                                presenter.TeamMembers(members);
                            }
                        } else {
                            DebugLog.showLog(this, "获取团队成员数据失败!");
                            presenter.getMorningDataError(flag);
                            presenter.TeamMembers(null);
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        throwable.printStackTrace();
                        DebugLog.showLog(this, "获取团队成员数据失败!");
                        presenter.TeamMembers(null);
                        presenter.getMorningDataError(flag);
                    }
                });
    }

    private void getTeamMorningData(String time, String userId, int flag, String chmId) {
        api.devListCheckMoring(userId, time, chmId, flag, 6)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<TeamAudioData>() {
                    @Override
                    public void accept(TeamAudioData teamAudioData) throws Exception {
                        if (teamAudioData.getReturnCode() == 1) {
                            List<TeamAudioData.DatasBean> datasBeans = teamAudioData.getDatas();
                            DebugLog.showLog(this, "获取录音文件数据：" + datasBeans.toString());
                            presenter.teamAudioData(datasBeans, flag);
                        } else {
                            presenter.getMorningDataError(flag);
                            DebugLog.showLog(this, "返回数据失败:" + teamAudioData.getReturnCode());
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        throwable.printStackTrace();
                        DebugLog.showLog(this, "返回数据失败:" + throwable.getMessage());
                        presenter.getMorningDataError(flag);
                    }
                });
    }

    public void downloadFile(TeamMorningData data, int position) {
        api.donwnload(data.getUrl())
                .subscribeOn(Schedulers.io())
                .map(new Function<ResponseBody, File>() {
                    @Override
                    public File apply(ResponseBody responseBody) throws Exception {
                        File file = new File(Fixed.getRemouteMoningFile() + File.separator + data.getDay() + File.separator + CacheUserInfo.get().getUserPhone() + "_" + CopyFile.getTime(data.getTime()) + ".aac");
                        if (!file.exists()) {
                            file.getParentFile().mkdirs();
                            file.createNewFile();
                            BufferedSource source = responseBody.source();
                            BufferedSink bufferedSink = Okio.buffer(Okio.sink(file));
                            Buffer buffer = new Buffer();
                            long total = 0;
                            while (!source.exhausted()) {
                                long count = source.read(buffer, 1024);
                                total += count;
                                bufferedSink.write(buffer, count);
                            }
                            bufferedSink.flush();
                            bufferedSink.close();
                            source.close();
                            return file;
                        } else {
                            return file;
                        }
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<File>() {
                    @Override
                    public void accept(File file) throws Exception {
                        data.setPath(file.getPath());
                        presenter.downloadComplite(true, data, position);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        throwable.printStackTrace();
                        presenter.downloadComplite(false, data, position);
                    }
                });
    }

}

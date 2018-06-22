package com.shareshow.aide.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.SystemClock;
import android.support.annotation.Nullable;

import com.alibaba.sdk.android.oss.ClientConfiguration;
import com.alibaba.sdk.android.oss.OSS;
import com.alibaba.sdk.android.oss.OSSClient;
import com.alibaba.sdk.android.oss.common.auth.OSSAuthCredentialsProvider;
import com.alibaba.sdk.android.oss.common.auth.OSSCredentialProvider;
import com.alibaba.sdk.android.oss.model.PutObjectRequest;
import com.google.gson.GsonBuilder;
import com.shareshow.App;
import com.shareshow.aide.retrofit.Api;
import com.shareshow.aide.retrofit.RetrofitProvider;
import com.shareshow.aide.retrofit.entity.TeamMorningData;
import com.shareshow.aide.retrofit.entity.UserInfo;
import com.shareshow.aide.retrofit.entity.VisitData;
import com.shareshow.aide.util.CacheUserInfo;
import com.shareshow.aide.util.VisitCacheData;
import com.shareshow.airpc.util.DebugLog;
import com.shareshow.db.GreenDaoManager;
import com.shareshow.db.TeamMorningDataDao;
import com.shareshow.db.VisitDataDao;
import com.socks.library.KLog;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;

/**
 * 定时提交任务
 * Created by xiongchengguang on 2018/2/7.
 */

public class TimerService extends Service {
    private Api api;
    private VisitDataDao dataDao = GreenDaoManager.getDaoSession().getVisitDataDao();

    @Override
    public void onCreate() {
        super.onCreate();
        api = RetrofitProvider.getApi();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Observable
                .interval(0, 60, TimeUnit.SECONDS)
                .subscribeOn(Schedulers.newThread())
                .flatMap(new Function<Long, ObservableSource<VisitData>>() {
                    @Override
                    public ObservableSource<VisitData> apply(Long aLong) throws Exception {
                        List<VisitData> daoList = dataDao.queryBuilder().list();
                        return Observable.fromIterable(daoList);
                    }
                })
                .subscribe(new Consumer<VisitData>() {
                    @Override
                    public void accept(VisitData visitData) throws Exception {
                        long gtId = dateFormat.parse("2018-01-01").getTime();
                        long vrId = Long.parseLong(visitData.getVrId());
                        if (vrId > gtId) {
                            KLog.d("未上传数据的ID:" + vrId);
                            commitVisitData(visitData);
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        throwable.printStackTrace();
                    }
                });
        updateMorningDataToServer();
    }

    //提交晨会录音记录到服务器
    private void updateMorningDataToServer() {
        Observable.interval(0, 60, TimeUnit.SECONDS)
                .subscribeOn(Schedulers.newThread())
                .flatMap(new Function<Long, ObservableSource<TeamMorningData>>() {
                    @Override
                    public ObservableSource<TeamMorningData> apply(Long aLong) throws Exception {
                        List<TeamMorningData> datas = GreenDaoManager.getDaoSession().getTeamMorningDataDao().queryBuilder().where(TeamMorningDataDao.Properties.IsUpload.eq(false)).list();
                        return Observable.fromIterable(datas);
                    }
                })
                .subscribe(new Consumer<TeamMorningData>() {
                    @Override
                    public void accept(TeamMorningData data) throws Exception {
                        File file = new File(data.getPath());
                        if (file.exists()) {
                            SystemClock.sleep(1000);
                            commitMorningData(data, file);
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        throwable.printStackTrace();
                    }
                });
    }

    //上传录音至OSS 录音信息至平台服务器
    private void commitMorningData(TeamMorningData data, File file) {
        api = RetrofitProvider.getApi();
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
                            KLog.d("后台服务:晨会签到录音上传成功");
                            TeamMorningDataDao teamDao = GreenDaoManager.getDaoSession().getTeamMorningDataDao();
                            data.setIsUpload(true);
                            data.setIsRemoteAudio(true);
                            teamDao.update(data);
                        } else {
                            KLog.d("后台服务:晨会签到录音上传失败");
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        throwable.printStackTrace();
                        KLog.d("后台服务:晨会签到录音上传失败");
                    }
                });


//        RequestBody body = RequestBody.create(MediaType.parse("application/otcet-stream"), file);
//        MultipartBody.Part part = MultipartBody.Part.createFormData("file", file.getName(), body);
//        String chchePhone = CacheUserInfo.get().getUserPhone();
//        RequestBody phone = RequestBody.create(null, chchePhone);
//        RequestBody userId = RequestBody.create(null, CacheUserInfo.get().getUserId());
//        RequestBody duration = RequestBody.create(null, data.getDuration());
//        RequestBody date = RequestBody.create(null, data.getDay());
//        api.uploadFilePlayeRecoed(date, part, phone, userId, duration)
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribeOn(Schedulers.newThread())
//                .subscribe(new Consumer<ResponseBody>() {
//                    @Override
//                    public void accept(ResponseBody responseBody) throws Exception {
//                        if (responseBody.string().equals("1")) {
//                            DebugLog.showLog(this, "上传文件成功!");
//                            TeamMorningDataDao teamDao = GreenDaoManager.getDaoSession().getTeamMorningDataDao();
//                            data.setIsUpload(true);
//                            data.setIsRemoteAudio(true);
//                            List<TeamMorningData> teamMorningDatas = teamDao.queryBuilder().where(TeamMorningDataDao.Properties.Day.eq(data.getDay()), TeamMorningDataDao.Properties.PhoneNum.eq(data.getPhoneNum())).list();
//                            if (teamMorningDatas != null) {
//                                for (TeamMorningData teamMorningData : teamMorningDatas) {
//                                    teamDao.delete(teamMorningData);
//                                }
//                            }
//                            teamDao.insertOrReplace(data);
//                        }
//                    }
//                }, new Consumer<Throwable>() {
//                    @Override
//                    public void accept(Throwable throwable) throws Exception {
//                        throwable.printStackTrace();
//                    }
//                });
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    private static String endpoint = "http://oss-cn-hangzhou.aliyuncs.com";
    private static String bucketName = "qmkj-20180416-oss";
    private String stsServer = "http://10.42.0.95:8080/OssToken/AppToken";

    public void commitVisitData(VisitData data) {
        List<File> files = new ArrayList<>();
        if (data.getVrPicurls() != null) {
            for (String string : data.getVrPicurls()) {
                File file = new File(string);
                if (file.exists()) {
                    files.add(file);
                }
            }
        }
        if (!data.getAudioPath().isEmpty()) {
            File file = new File(data.getAudioPath());
            if (file.exists()) {
                files.add(file);
            }
        }

        OSSCredentialProvider credentialProvider = new OSSAuthCredentialsProvider(stsServer);
        ClientConfiguration conf = new ClientConfiguration();
        conf.setConnectionTimeout(15 * 1000); // 连接超时，默认15秒
        conf.setSocketTimeout(15 * 1000); // socket超时，默认15秒
        conf.setMaxConcurrentRequest(5); // 最大并发请求数，默认5个
        conf.setMaxErrorRetry(2); // 失败后最大重试次数，默认2次
        final OSS oss = new OSSClient(App.getApp(), endpoint, credentialProvider, conf);

        final List<String> keyLists = new ArrayList<>();
        Observable.fromIterable(files)
                .map(new Function<File, String>() {
                    @Override
                    public String apply(File file) throws Exception {
                        String objectKey = "android" + CacheUserInfo.get().getUserPhone() + System.currentTimeMillis() + new Random().nextInt(999) + file.getName();
                        PutObjectRequest request = new PutObjectRequest(bucketName, objectKey, file.getPath());
                        oss.putObject(request);
                        return request.getObjectKey();
                    }
                })
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<String>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(String s) {
                        keyLists.add(s);
                    }

                    @Override
                    public void onError(Throwable e) {
                        KLog.d("后台自动云存储OSS 出现异常");
                    }

                    @Override
                    public void onComplete() {
                        KLog.d("后台自动OSS上传成功");
                        String keys = new GsonBuilder().serializeNulls().create().toJson(keyLists);
                        setVisitInfoCommit(data, keys);
                    }
                });
    }


    private void setVisitInfoCommit(VisitData data, String keys) {
        api.visitRecord(data.getVrUrId(),
                data.getVrPhone(),
                Long.parseLong(data.getVrTimestart()),
                Long.parseLong(data.getVrTimeend()),
                data.getVrGuestname(),
                data.getVrAddresss(),
                data.getVrGps(),
                data.getVrContent(),
                keys)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<UserInfo>() {
                    @Override
                    public void accept(UserInfo userInfo) throws Exception {
                        if (userInfo.getReturnCode() == 1) {
                            KLog.d("后台服务提交成功:" + data.getVrContent() + "  " + data.getVrGuestname() + "   " + data.getVrId());
                            GreenDaoManager.getDaoSession().getVisitDataDao().delete(data);
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        throwable.printStackTrace();
                        KLog.d("后台服务提交失败");
                    }
                });
    }

//    public void commitVisitData(VisitData data) {
//        api = RetrofitProvider.getApi();
//        List<MultipartBody.Part> parts = getMultipartBodyParts(data.getVrPicurls());
//        api.uploadFile(parts)

//                .flatMap(new Function<UploadIds, ObservableSource<UploadIds>>() {
//                    @Override
//                    public ObservableSource<UploadIds> apply(UploadIds uploadIds) throws Exception {
//                        if (uploadIds.getReturnCode() == 1) {
//                            String userId = CacheUserInfo.get().getUserId();
//                            String phone = CacheUserInfo.get().getUserPhone();
//                            Long timeStart = Long.parseLong(VisitCacheData.get().getVrTimestart());
//                            Long timeEnd = Long.parseLong(VisitCacheData.get().getVrTimeend());
//                            String name = VisitCacheData.get().getVrGuestname();
//                            String addresss = VisitCacheData.get().getVrAddresss();
//                            String gps = VisitCacheData.get().getVrGps();
//                            String content = VisitCacheData.get().getVrContent();
//                            String url = new Gson().toJson(uploadIds.getDatas());
//                            return api.setSubmitVisitData(userId, phone, timeStart, timeEnd, name, addresss, gps, content, url);
//                        }
//                        return Observable.empty();
//                    }
//                })
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribeOn(Schedulers.newThread())
//                .subscribe(new Consumer<UploadIds>() {
//                    @Override
//                    public void accept(UploadIds uploadIds) throws Exception {
//                        if (uploadIds.getReturnCode() == 1) {
//                            dataDao.delete(data);
//                            KLog.d("上传成功");
//                        }
//                    }
//                }, new Consumer<Throwable>() {
//                    @Override
//                    public void accept(Throwable throwable) throws Exception {
//                        throwable.printStackTrace();
//                    }
//                });
//    }

//    public void commitVisitData(VisitData data) {
//        List<File> list = new ArrayList<>();
//        for (String string : data.getVrPicurls()) {
//            File file = new File(string);
//            if (file.exists()) {
//                list.add(file);
//            }
//        }
//        if (!data.getAudioPath().isEmpty()) {
//            File file = new File(data.getAudioPath());
//            if (file.exists()) {
//                list.add(file);
//            }
//        }
//
//        List<MultipartBody.Part> parts = getMultipartBodyParts(list);
//        String id = CacheUserInfo.get().getUserId();
//        String userPhone = CacheUserInfo.get().getUserPhone();
//        String visitContent = data.getVrContent();
//        MediaType mediaType = MediaType.parse("multipart/form-data");
//        RequestBody userId = RequestBody.create(mediaType, id);
//        RequestBody phone = RequestBody.create(mediaType, userPhone);
//        RequestBody timeStart = RequestBody.create(mediaType, String.valueOf(data.getVrTimestart()));
//        RequestBody timeEnd = RequestBody.create(mediaType, String.valueOf(data.getVrTimeend()));
//        RequestBody guestname = RequestBody.create(mediaType, data.getVrGuestname());
//        RequestBody addresss = RequestBody.create(mediaType, data.getVrAddresss());
//        RequestBody gps = RequestBody.create(mediaType, data.getVrGps());
//        RequestBody content = RequestBody.create(mediaType, visitContent);
//
//        Map<String, RequestBody> map = new HashMap<>();
//        map.put("userId", userId);
//        map.put("phone", phone);
//        map.put("timeStart", timeStart);
//        map.put("timeEnd", timeEnd);
//        map.put("guestname", guestname);
//        map.put("addresss", addresss);
//        map.put("gps", gps);
//        map.put("content", content);
//        api.submitVisitRecord(map, parts)
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribeOn(Schedulers.newThread())
//                .subscribe(new Consumer<UserInfo>() {
//                    @Override
//                    public void accept(UserInfo userInfo) throws Exception {
//                        if (userInfo.getReturnCode() == 1) {
//                            KLog.d("后台服务提交成功:" + data.getVrContent() + "  " + data.getVrGuestname());
//                            GreenDaoManager.getDaoSession().getVisitDataDao().delete(data);
//                        }
//                    }
//                }, new Consumer<Throwable>() {
//                    @Override
//                    public void accept(Throwable throwable) throws Exception {
//                        throwable.printStackTrace();
//                        KLog.d("后台服务提交失败");
//                    }
//                });
//    }


    public static List<MultipartBody.Part> getMultipartBodyParts(List<File> list) {
        List<MultipartBody.Part> parts = new ArrayList<>(list.size());
        for (File file : list) {
            RequestBody requestBody = RequestBody.create(MediaType.parse("application/otcet-stream"), file);
            MultipartBody.Part part = MultipartBody.Part.createFormData("aFile", file.getName(), requestBody);
            parts.add(part);
        }
        return parts;
    }

}

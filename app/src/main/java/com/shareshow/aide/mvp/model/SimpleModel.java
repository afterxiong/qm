package com.shareshow.aide.mvp.model;

import android.os.Handler;
import android.widget.Toast;

import com.alibaba.sdk.android.oss.ClientConfiguration;
import com.alibaba.sdk.android.oss.OSS;
import com.alibaba.sdk.android.oss.OSSClient;
import com.alibaba.sdk.android.oss.common.auth.OSSAuthCredentialsProvider;
import com.alibaba.sdk.android.oss.common.auth.OSSCredentialProvider;
import com.alibaba.sdk.android.oss.model.PutObjectRequest;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.shareshow.App;
import com.shareshow.aide.impl.OnResponseResultListener;
import com.shareshow.aide.retrofit.Api;
import com.shareshow.aide.retrofit.RetrofitProvider;
import com.shareshow.aide.retrofit.entity.UploadIds;
import com.shareshow.aide.retrofit.entity.UserInfo;
import com.shareshow.aide.retrofit.entity.VisitData;
import com.shareshow.aide.util.CacheUserInfo;
import com.shareshow.aide.util.VisitCacheData;
import com.shareshow.db.GreenDaoManager;
import com.socks.library.KLog;

import java.io.File;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.OptionalLong;
import java.util.Random;

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
 * Created by xiongchengguang on 2018/3/30.
 */

public class SimpleModel implements BaseModel {

    private Api api;

    /**
     * 验证码
     */
    public static final int TAG_PROOF_CODE = 0;

    /**
     * 修改验证码结果
     */
    public static final int TAG_UPDATE_PHONE_RESULT = 1;

    public SimpleModel() {
        api = RetrofitProvider.getApi();
    }

    public void getProofCode(String phone, OnResponseResultListener listener) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                listener.result("123123");
            }
        }, 2000);
//TODO   节约短信条数 此处返回一个固定的验证码  正式发布  注释即可
//        api.getTestCode(phone)
//                .subscribeOn(Schedulers.newThread())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(new Consumer<ResponseBody>() {
//                    @Override
//                    public void accept(ResponseBody responseBody) throws Exception {
//                        String code = responseBody.string();
//                        listener.result(code);
//                    }
//                }, new Consumer<Throwable>() {
//                    @Override
//                    public void accept(Throwable throwable) throws Exception {
//                        throwable.printStackTrace();
//                        listener.error();
//                    }
//                });
    }

    public void updateUserPhone(String oldPhone, String newPhone, OnResponseResultListener listener) {
        api.changeUserPhone(oldPhone, newPhone)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .flatMap(new Function<UserInfo, ObservableSource<UserInfo>>() {
                    @Override
                    public ObservableSource<UserInfo> apply(UserInfo userInfo) throws Exception {
                        if (userInfo.getReturnCode() == 1) {
                            String userId = CacheUserInfo.get().getUserId();
                            Toast.makeText(App.getApp(), "修改成功", Toast.LENGTH_LONG).show();
                            return api.getRegisterUserInfo(userId).subscribeOn(Schedulers.newThread());
                        } else if (userInfo.getReturnCode() == 3) {
                            Toast.makeText(App.getApp(), "修改的手机号码已被注册", Toast.LENGTH_LONG).show();
                            return Observable.error(new RuntimeException("修改的手机号码已被注册"));
                        } else {
                            Toast.makeText(App.getApp(), "手机号码修改失败", Toast.LENGTH_LONG).show();
                            return Observable.error(new RuntimeException("手机号码修改失败"));
                        }
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<UserInfo>() {
                    @Override
                    public void accept(UserInfo userInfo) throws Exception {
                        listener.result(userInfo);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        throwable.printStackTrace();
                        if (throwable instanceof UnknownHostException) {
                            Toast.makeText(App.getApp(), "网络请求超时", Toast.LENGTH_SHORT).show();
                        }
                        listener.error();
                    }
                });
    }

    private static String endpoint = "http://oss-cn-hangzhou.aliyuncs.com";
    private static String bucketName = "qmkj-20180416-oss";
    private String stsServer = "http://10.42.0.95:8080/OssToken/AppToken";

    //阿里云存储OSS
    public void setVisitCommit() {
        VisitData data = VisitCacheData.get().getVisitData();
        List<File> files = new ArrayList<>();
        if (data.getVrPicurls() != null) {
            for (String string : data.getVrPicurls()) {
                File file = new File(string);
                if (file.exists()) {
                    files.add(file);
                }
            }
        }
        if (data.getAudioPath() != null) {
            File file = new File(data.getAudioPath());
            if (file.exists()) {
                files.add(file);
            }
        }

        OSSCredentialProvider credentialProvider = new OSSAuthCredentialsProvider(stsServer);
        ClientConfiguration conf = new ClientConfiguration();
        conf.setConnectionTimeout(15 * 1000);
        conf.setSocketTimeout(15 * 1000);
        conf.setMaxConcurrentRequest(5);
        conf.setMaxErrorRetry(2);
        final OSS oss = new OSSClient(App.getApp(), endpoint, credentialProvider, conf);
        final List<String> keyLists = new ArrayList<>();

        Observable.fromIterable(files)
                .map(new Function<File, String>() {
                    @Override
                    public String apply(File file) throws Exception {
                        String objectKey = "android_visit" + CacheUserInfo.get().getUserPhone() + System.currentTimeMillis() + new Random().nextInt(999) + file.getName();
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
                        KLog.d("上传成功:" + s);
                    }

                    @Override
                    public void onError(Throwable e) {
                        failSave();
                        KLog.d("云存储OSS 出现异常");
                    }

                    @Override
                    public void onComplete() {
                        KLog.d("OSS上传成功");
                        String keys = new GsonBuilder().serializeNulls().create().toJson(keyLists);
                        setVisitInfoCommit(keys);
                    }
                });
    }

    private void setVisitInfoCommit(String keys) {
        VisitData data = VisitCacheData.get().getVisitData();
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
                            KLog.d("提交成功:" + data.getVrContent() + "  " + data.getVrGuestname());
                            VisitCacheData.get().clear();
                        } else {
                            failSave();
                            KLog.d("上传本地服务器失败");
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        throwable.printStackTrace();
                        failSave();
                        KLog.d("上传本地服务器失败");
                    }
                });
    }

    /**
     * 上传失败，存入数据库，下次上传
     */

    private void failSave() {
        VisitData data = VisitCacheData.get().getVisitData();
        GreenDaoManager.getDaoSession().getVisitDataDao().insert(data);
        VisitCacheData.get().clear();
    }


    //提交拜访记录
//    public void setVisitCommit() {
//        VisitData data = VisitCacheData.get().getVisitData();
//        List<File> list = new ArrayList<>();
//        if (data.getVrPicurls() != null) {
//            for (String string : data.getVrPicurls()) {
//                File file = new File(string);
//                if (file.exists()) {
//                    list.add(file);
//                }
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
//                            KLog.d("提交成功:" + data.getVrContent() + "  " + data.getVrGuestname());
//                            VisitCacheData.get().clear();
//                        } else {
//                            failSave();
//                        }
//                    }
//                }, new Consumer<Throwable>() {
//                    @Override
//                    public void accept(Throwable throwable) throws Exception {
//                        throwable.printStackTrace();
//                        KLog.d("提交失败");
//                        failSave();
//                    }
//                });
//    }

    /**
     * 拜访文件上传   信息和文件分离
     */
    /*public void setVisitCommit() {
        VisitData data = VisitCacheData.get().getVisitData();
        List<MultipartBody.Part> parts = getMultipartBodyParts(data.getVrPicurls());
        api.uploadFile(parts)
                .flatMap(new Function<UploadIds, ObservableSource<UploadIds>>() {
                    @Override
                    public ObservableSource<UploadIds> apply(UploadIds uploadIds) throws Exception {
                        if (uploadIds.getReturnCode() == 1) {
                            String userId = CacheUserInfo.get().getUserId();
                            String phone = CacheUserInfo.get().getUserPhone();
                            Long timeStart = Long.parseLong(VisitCacheData.get().getVrTimestart());
                            Long timeEnd = Long.parseLong(VisitCacheData.get().getVrTimeend());
                            String name = VisitCacheData.get().getVrGuestname();
                            String addresss = VisitCacheData.get().getVrAddresss();
                            String gps = VisitCacheData.get().getVrGps();
                            String content = VisitCacheData.get().getVrContent();
                            String url = new Gson().toJson(uploadIds.getDatas());
                            return api.setSubmitVisitData(userId, phone, timeStart, timeEnd, name, addresss, gps, content, url);
                        }
                        return Observable.empty();
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.newThread())
                .subscribe(new Consumer<UploadIds>() {
                    @Override
                    public void accept(UploadIds uploadIds) throws Exception {
                        if (uploadIds.getReturnCode() == 1) {
                            VisitCacheData.get().clear();
                            KLog.d("上传成功");
                        } else {
                            failSave();
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        throwable.printStackTrace();
                        failSave();
                    }
                });
    }*/
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






package com.shareshow.aide.mvp.model;

import com.shareshow.aide.mvp.presenter.WorkPresenter;
import com.shareshow.aide.retrofit.Api;
import com.shareshow.aide.retrofit.RetrofitProvider;
import com.shareshow.aide.retrofit.entity.AmplyNotify;
import com.shareshow.aide.retrofit.entity.StudyMaterialsVisitingMaterials;
import com.shareshow.aide.retrofit.entity.TeamAudioData;
import com.shareshow.aide.retrofit.entity.VisitRecord;
import com.shareshow.aide.util.CacheUserInfo;
import com.shareshow.aide.util.download.SmvmFileUtils;
import com.shareshow.airpc.util.Cache;
import com.shareshow.db.GreenDaoManager;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;

/**
 * Created by xiongchengguang on 2017/12/7.
 */

public class WorkModel implements BaseModel {

    public WorkPresenter presenter;
    private Api api;

    public WorkModel(WorkPresenter presenter) {
        this.presenter = presenter;
        api = RetrofitProvider.getApi();
    }

    public void getDevGetNotification(String cachePhone) {
        api.devGetNotification(cachePhone)
                .subscribeOn(Schedulers.newThread())
                .flatMap(new Function<List<AmplyNotify>, ObservableSource<AmplyNotify>>() {
                    @Override
                    public ObservableSource<AmplyNotify> apply(List<AmplyNotify> listAns) throws Exception {
                        return Observable.fromIterable(listAns);
                    }
                })
                .subscribe(new Consumer<AmplyNotify>() {
                    @Override
                    public void accept(AmplyNotify ans) throws Exception {
                        obtainResponse(ans.getNosId());
                        if (GreenDaoManager.getDaoSession().getAmplyNotifyDao().load(ans.getNosId()) == null) {
                            GreenDaoManager.getDaoSession().getAmplyNotifyDao().insert(ans);
                            presenter.onPresenterNotification(ans);
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        throwable.printStackTrace();
                    }
                });


    }

    private void obtainResponse(String sId) {
        String phone = CacheUserInfo.get().getUserPhone();
        api.devSetNotification(phone, sId)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<ResponseBody>() {
            @Override
            public void accept(ResponseBody responseBody) throws Exception {
                String string = responseBody.string();
            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {
                throwable.printStackTrace();
            }
        });
    }

    public void getStudyAndVisitData(String cachePhone) {
        api.devGetStudyAndVisit(cachePhone)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.newThread())
                .map(new Function<List<StudyMaterialsVisitingMaterials>, List<StudyMaterialsVisitingMaterials>>() {
                    @Override
                    public List<StudyMaterialsVisitingMaterials> apply(List<StudyMaterialsVisitingMaterials> newSmvms) throws Exception {
                        List<StudyMaterialsVisitingMaterials> daoSmvmList = GreenDaoManager.getDaoSession().getStudyMaterialsVisitingMaterialsDao().loadAll();
                        for(StudyMaterialsVisitingMaterials daoSmvm : daoSmvmList){
                            if (!newSmvms.contains(daoSmvm)){
                                SmvmFileUtils.deleteDatabaseAndFile(daoSmvm);
                            }
                        }
                        return newSmvms;
                    }
                })
                .subscribe(new Consumer<List<StudyMaterialsVisitingMaterials>>() {
                    @Override
                    public void accept(List<StudyMaterialsVisitingMaterials> smvms) throws Exception {
                        presenter.onPresenterSmvmCompile();
                        SmvmFileUtils.get().download(smvms);
                    }
                }, new Consumer<Throwable>(){
                    @Override
                    public void accept(Throwable throwable) throws Exception{
                        throwable.printStackTrace();
                        presenter.onPresenterSmvmCompile();
                    }
                });


//        api.devGetStudyAndVisit(cachePhone)
//                .flatMap(new Function<List<StudyAndVisit>, ObservableSource<StudyAndVisit>>() {
//                    @Override
//                    public ObservableSource<StudyAndVisit> apply(List<StudyAndVisit> datas) throws Exception {
//                        for (StudyAndVisit data : datas){
//                            String json = new GsonBuilder().serializeNulls().create().toJson(data);
//                            KLog.json("xiongchengguang", json);
//                        }
//                        return Observable.fromIterable(datas);
//                    }
//                })
//                .doOnNext(new Consumer<StudyAndVisit>(){
//                    @Override
//                    public void accept(StudyAndVisit data) throws Exception{
//                        String planId = data.getSfSpid();
//                        String planType = data.getSfType();
//                        api.devUpdateVisitAndStudyFileReceive(cachePhone, planId, planType)
//                                .subscribeOn(Schedulers.newThread())
//                                .observeOn(AndroidSchedulers.mainThread())
//                                .subscribe(new Consumer<ResponseBody>(){
//                                    @Override
//                                    public void accept(ResponseBody responseBody) throws Exception {
//                                        String string = responseBody.string();
//                                        KLog.d(string);
//                                    }
//                                }, new Consumer<Throwable>(){
//                                    @Override
//                                    public void accept(Throwable throwable) throws Exception {
//                                        throwable.printStackTrace();
//                                    }
//                                });
//                    }
//                })
//                .flatMap(new Function<StudyAndVisit, ObservableSource<ResponseBody>>() {
//                    @Override
//                    public ObservableSource<ResponseBody> apply(StudyAndVisit data) throws Exception {
//                        sfId = data.getSfId();
//                        sfFilename = data.getSfFilename();
//                        sfVpId = data.getSfSpid();
//                        sType = data.getSfType();
//                        String url = data.getSfUrl();
//                        return api.donwnload(url);
//                    }
//                })
//                .map(new Function<ResponseBody, File>() {
//                    @Override
//                    public File apply(ResponseBody responseBody) throws Exception{
//                        KLog.d(sfFilename);
//                        String name = sfId + "<#>" + sfVpId + "<#>" + sType + "<#>" + sfFilename;
//                        String cachePhone = CacheUserInfo.get().getUserPhone();
//                        String pathname = Fixed.getRootPath() + File.separator + cachePhone + File.separator + "StudyVisitData" + File.separator + name;
//                        File file = new File(pathname);
//                        File parent = file.getParentFile();
//                        if (!parent.exists()) {
//                            parent.mkdirs();
//                        }
//                        if (file.exists()){
//                            long len1 = responseBody.contentLength();
//                            long len2 = file.length();
//                            if (len1 == len2) {
//                                KLog.d("文件存在");
//                                return file;
//                            } else {
//                                file.delete();
//                            }
//                        }
//                        BufferedSource source = responseBody.source();
//                        BufferedSink bufferedSink = Okio.buffer(Okio.sink(file));
//                        Buffer buffer = new Buffer();
//                        long total = 0;
//                        while (!source.exhausted()) {
//                            long count = source.read(buffer, 1024);
//                            total += count;
//                            bufferedSink.write(buffer, count);
//                        }
//                        bufferedSink.flush();
//                        return file;
//                    }
//                })
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(new Observer<File>() {
//                    @Override
//                    public void onSubscribe(Disposable d) {
//
//                    }
//
//                    @Override
//                    public void onNext(File file) {
//                        KLog.d(file.getName() + " 下载完毕");
//                        presenter.addFile(file);
//                    }
//
//                    @Override
//                    public void onError(Throwable e) {
//                        presenter.error(e);
//                    }
//
//                    @Override
//                    public void onComplete() {
//                        presenter.onComplete();
//                    }
//                });
    }


    public void getVisitTrack(String data, String userId) {
        api.getVisitTrack(userId, data)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.newThread())
                .subscribe(new Consumer<VisitRecord>() {
                    @Override
                    public void accept(VisitRecord visitRecord) throws Exception {
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        throwable.printStackTrace();
                    }
                });

    }

    public void getTeamAudioData(String date) {
        String userId=CacheUserInfo.get().getUserId();
        api.getDateFilterMorningRecord(userId,date,date)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .map(new Function<TeamAudioData, Boolean>() {
                    @Override
                    public Boolean apply(TeamAudioData teamAudioData) throws Exception {
                        if(teamAudioData.getDatas().size()>0){
                            return true;
                        }else {
                            return false;
                        }
                    }
                })
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean bool) throws Exception {
                        presenter.onPresenterMorningRegister(bool);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        throwable.printStackTrace();
                    }
                });
    }


}


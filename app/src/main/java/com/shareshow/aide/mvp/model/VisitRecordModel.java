package com.shareshow.aide.mvp.model;

import com.shareshow.aide.impl.OnResponseResultListener;
import com.shareshow.aide.mvp.presenter.VisitRecordFilterPresenter;
import com.shareshow.aide.retrofit.Api;
import com.shareshow.aide.retrofit.RetrofitProvider;
import com.shareshow.aide.retrofit.entity.VisitData;
import com.shareshow.aide.retrofit.entity.VisitRecord;
import com.shareshow.aide.util.CacheConfig;
import com.shareshow.aide.util.CacheUserInfo;
import com.shareshow.aide.util.VisitCacheData;
import com.shareshow.db.GreenDaoManager;
import com.shareshow.db.VisitDataDao;

import java.text.SimpleDateFormat;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by xiongchengguang on 2018/3/16.
 */

public class VisitRecordModel implements BaseModel {
    private VisitRecordFilterPresenter presenter;
    private Api api;

    public VisitRecordModel(VisitRecordFilterPresenter presenter) {
        this.presenter = presenter;
        api = RetrofitProvider.getApi();
    }

    public void getDateFilterVisitRecord(String userId, String tvStartDateText, String tvEndDateText) {
        api.getSyncVisitRecord(userId,tvStartDateText,tvEndDateText)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<VisitRecord>() {
                    @Override
                    public void accept(VisitRecord visitRecord) throws Exception {
                        presenter.onDateFilterVisitRecordListener(visitRecord);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        throwable.printStackTrace();
                        presenter.onDateFilterVisitRecordListener(null);
                    }
                });

    }
    public void getSyncVisitRecord(OnResponseResultListener listener) {
        String userId = CacheUserInfo.get().getUserId();
        String startDate = VisitCacheData.get().getLastDate();
        api.getSyncVisitRecord(userId, startDate, getToDay())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<VisitRecord>() {
                    @Override
                    public void accept(VisitRecord record) throws Exception {
                        if (record.getReturnCode() == 1) {
                            listener.result(record);
                            VisitCacheData.get().saveLastDate(getToDay());
                            List<VisitData> visitDatas = record.getDatas();
                            VisitDataDao dao = GreenDaoManager.getDaoSession().getVisitDataDao();
                            for (VisitData data : visitDatas) {
                                if (dao.load(data.getVrId()) == null) {
                                    dao.insert(data);
                                }
                            }
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        listener.result(null);
                        throwable.printStackTrace();
                    }
                });
    }

    public String getToDay() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        return dateFormat.format(System.currentTimeMillis());
    }
}

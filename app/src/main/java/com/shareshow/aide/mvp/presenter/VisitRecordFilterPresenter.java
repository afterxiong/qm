package com.shareshow.aide.mvp.presenter;

import com.shareshow.aide.impl.OnResponseResultListener;
import com.shareshow.aide.mvp.model.VisitRecordModel;
import com.shareshow.aide.mvp.view.VisitRecordFilterView;
import com.shareshow.aide.retrofit.entity.VisitRecord;

import java.text.SimpleDateFormat;

/**
 * Created by xiongchengguang on 2018/3/16.
 */

public class VisitRecordFilterPresenter extends BasePresenter<VisitRecordFilterView> {
    private VisitRecordModel model;

    public VisitRecordFilterPresenter() {
        model = new VisitRecordModel(this);
    }

    public void getDateFilterVisitRecord(String userId, String tvStartDate, String tvEndDate) {
        model.getDateFilterVisitRecord(userId, tvStartDate, tvEndDate);
    }

    public void onDateFilterVisitRecordListener(VisitRecord record) {
        if (isViewAttached()) {
            getView().onDateFilterVisitRecordListener(record);
        }
    }

    public void getSyncVisitRecord() {
        model.getSyncVisitRecord(new OnResponseResultListener() {
            @Override
            public void result(Object obj) {
                VisitRecord record = (VisitRecord) obj;
                if (isViewAttached()) {
                    getView().onViewSyncVisitData(record.getDatas());
                }
            }

            @Override
            public void error() {

            }
        });
    }

    public String getToDay() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        return dateFormat.format(System.currentTimeMillis());
    }
}

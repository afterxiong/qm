package com.shareshow.aide.mvp.view;

import com.shareshow.aide.retrofit.entity.VisitData;
import com.shareshow.aide.retrofit.entity.VisitRecord;

import java.util.List;

/**
 * Created by xiongchengguang on 2018/3/16.
 */

public interface VisitRecordFilterView extends BaseView {

    public void showLoading();

    public void hideLoading();
    public void onDateFilterVisitRecordListener(VisitRecord record);

    public  void onViewSyncVisitData(List<VisitData> datas);
}


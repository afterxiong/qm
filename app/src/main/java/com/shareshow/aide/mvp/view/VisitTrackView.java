package com.shareshow.aide.mvp.view;

import com.shareshow.aide.retrofit.entity.VisitData;
import com.shareshow.aide.retrofit.entity.VisitRecord;

import java.util.List;

/**
 * Created by xiongchengguang on 2017/12/26.
 */

public interface VisitTrackView extends BaseView {

    public void initView();

    public void onViewSyncVisitData(List<VisitData> datas);
}

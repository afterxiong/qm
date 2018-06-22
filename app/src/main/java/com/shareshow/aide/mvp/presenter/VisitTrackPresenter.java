package com.shareshow.aide.mvp.presenter;

import com.shareshow.aide.impl.OnResponseResultListener;
import com.shareshow.aide.mvp.model.VisitTrackModel;
import com.shareshow.aide.mvp.view.VisitTrackView;
import com.shareshow.aide.retrofit.entity.VisitRecord;
import com.socks.library.KLog;

/**
 * Created by xiongchengguang on 2017/12/26.
 */

public class VisitTrackPresenter extends BasePresenter<VisitTrackView> {
    private VisitTrackModel model;

    public VisitTrackPresenter() {
        model = new VisitTrackModel(this);
    }


    /**
     * 下拉刷新，日期筛选
     *
     * @param date
     */
    public void getNumDownVisitTrack(String tramIds, String date) {
        int flag = 0;
        int num =10;
        if(tramIds==null){
            model.getNumDownVisitTrack(date, null, flag, num);
        }else{
            if(tramIds.length()<=2){
                model.getNumDownVisitTrack(date, null, flag, num);
            }else{
                model.getNumDownVisitTrack(tramIds,date, null, flag, num);
            }
        }

    }

    public void getNumUpVisitTrack(String tramIds, String date,String vrId) {
        int flag = 0;
        int num = 10;
        if(tramIds==null){
            model.getNumUpVisitTrack(date,vrId,flag,num);
        }else {
            if(tramIds.length()<=2){
                model.getNumUpVisitTrack(date,vrId,flag,num);
            }else{
                model.getNumUpVisitTrack(tramIds,date,vrId,flag,num);
            }
        }
    }

    public void onVisitErrorListener() {
        if (isViewAttached()) {
        }
    }


    public void getSyncVisitRecord() {
        model.getSyncVisitRecord(new OnResponseResultListener() {
            @Override
            public void result(Object obj) {
                VisitRecord record= (VisitRecord) obj;
                if(isViewAttached()){
                    getView().onViewSyncVisitData(record.getDatas());
                }
            }

            @Override
            public void error() {
                KLog.d("拜访记录获取失败");
            }
        });
    }
}

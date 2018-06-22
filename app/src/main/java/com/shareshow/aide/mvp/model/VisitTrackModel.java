package com.shareshow.aide.mvp.model;

import com.google.gson.GsonBuilder;
import com.shareshow.aide.impl.OnResponseResultListener;
import com.shareshow.aide.mvp.presenter.VisitTrackPresenter;
import com.shareshow.aide.retrofit.Api;
import com.shareshow.aide.retrofit.RetrofitProvider;
import com.shareshow.aide.retrofit.entity.Team;
import com.shareshow.aide.retrofit.entity.VisitData;
import com.shareshow.aide.retrofit.entity.VisitRecord;
import com.shareshow.aide.util.CacheConfig;
import com.shareshow.aide.util.CacheUserInfo;
import com.shareshow.aide.util.VisitCacheData;
import com.shareshow.db.GreenDaoManager;
import com.shareshow.db.VisitDataDao;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by xiongchengguang on 2017/12/26.
 */

public class VisitTrackModel implements BaseModel {
    private VisitTrackPresenter presenter;
    private Api api;

    public VisitTrackModel(VisitTrackPresenter presenter) {
        this.presenter = presenter;
        api = RetrofitProvider.getApi();
    }

    /***
     * 下拉刷新全部
     * @param date
     * @param vrId
     * @param flag
     * @param num
     */
    public void getNumDownVisitTrack(String date, String vrId, Integer flag, int num) {
        String teamId = CacheUserInfo.get().getTeamId();
        api.teamMember(teamId)
                .flatMap(new Function<Team, ObservableSource<VisitRecord>>() {
                    @Override
                    public ObservableSource<VisitRecord> apply(Team team) throws Exception {
                        if (team.getReturnCode() == 1) {
                            List<String> selectUserId = new ArrayList<>();
                            for (Team.TeamMember teamMember : team.getDatas()) {
                                selectUserId.add(teamMember.getUserId());
                            }
                            String userIds = new GsonBuilder().serializeNulls().create().toJson(selectUserId);
                            return api.getDevVistiTrackList(userIds, date, vrId, flag, num);
                        } else {
                            return Observable.error(new RuntimeException("未查询到团队成员"));
                        }
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<VisitRecord>() {
                    @Override
                    public void accept(VisitRecord visitRecord) throws Exception {
//                        presenter.onRefreshCompile(visitRecord);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        throwable.printStackTrace();
//                        presenter.onRefreshCompile(null);
                    }
                });

    }


    /**
     * 下拉刷新部分
     *
     * @param tramIds
     * @param date
     * @param vrId
     * @param flag
     * @param num
     */
    public void getNumDownVisitTrack(String tramIds, String date, String vrId, Integer flag, int num) {
        api.getDevVistiTrackList(tramIds, date, vrId, flag, num)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<VisitRecord>() {
                    @Override
                    public void accept(VisitRecord visitRecord) throws Exception {
//                        presenter.onRefreshCompile(visitRecord);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        throwable.printStackTrace();
//                        presenter.onRefreshCompile(null);
                    }
                });
    }

    /**
     * 上拉加载全部
     *
     * @param date
     * @param vrId
     * @param flag
     * @param num
     */
    public void getNumUpVisitTrack(String date, String vrId, Integer flag, int num) {
        String teamId = CacheUserInfo.get().getTeamId();
        api.teamMember(teamId)
                .flatMap(new Function<Team, ObservableSource<VisitRecord>>() {
                    @Override
                    public ObservableSource<VisitRecord> apply(Team team) throws Exception {
                        if (team.getReturnCode() == 1) {
                            List<String> selectUserId = new ArrayList<>();
                            for (Team.TeamMember teamMember : team.getDatas()) {
                                selectUserId.add(teamMember.getUserId());
                            }
                            String userIds = new GsonBuilder().serializeNulls().create().toJson(selectUserId);
                            return api.getDevVistiTrackList(userIds, date, vrId, flag, num);
                        } else {
                            return Observable.error(new RuntimeException("未查询到团队成员"));
                        }
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<VisitRecord>() {
                    @Override
                    public void accept(VisitRecord visitRecord) throws Exception {
//                        presenter.onLoadMoreCompile(visitRecord);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        throwable.printStackTrace();
//                        presenter.onLoadMoreCompile(null);
                    }
                });
    }


    /**
     * 上拉加载部分
     *
     * @param tramIds
     * @param date
     * @param vrId
     * @param flag
     * @param num
     */
    public void getNumUpVisitTrack(String tramIds, String date, String vrId, Integer flag, int num) {
        api.getDevVistiTrackList(tramIds, date, vrId, flag, num)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<VisitRecord>() {
                    @Override
                    public void accept(VisitRecord visitRecord) throws Exception {
//                        presenter.onLoadMoreCompile(visitRecord);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        throwable.printStackTrace();
//                        presenter.onLoadMoreCompile(null);
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
                        listener.error();
                        throwable.printStackTrace();
                    }
                });
    }

    public String getToDay() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        return dateFormat.format(System.currentTimeMillis());
    }
}

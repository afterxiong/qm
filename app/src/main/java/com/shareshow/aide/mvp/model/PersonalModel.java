package com.shareshow.aide.mvp.model;

import android.widget.Toast;

import com.shareshow.App;
import com.shareshow.aide.impl.OnResponseResultListener;
import com.shareshow.aide.mvp.presenter.MinePresenter;
import com.shareshow.aide.mvp.presenter.PersonalPresenter;
import com.shareshow.aide.nettyfile.DownLoadListener;
import com.shareshow.aide.retrofit.Api;
import com.shareshow.aide.retrofit.RetrofitProvider;
import com.shareshow.aide.retrofit.entity.Team;
import com.shareshow.aide.retrofit.entity.UserInfo;
import com.shareshow.aide.util.CacheConfig;
import com.shareshow.aide.util.CacheUserInfo;
import com.socks.library.KLog;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by xiongchengguang on 2018/3/29.
 */

public class PersonalModel implements BaseModel{

    private PersonalPresenter presenter;
    private Api api;

    public PersonalModel(PersonalPresenter presenter) {
        this.presenter = presenter;
        api = RetrofitProvider.getApi();
    }

    public void getUserInfo(String userId,OnResponseResultListener listener) {
        api.getRegisterUserInfo(userId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.newThread())
                .subscribe(new Consumer<UserInfo>() {
                    @Override
                    public void accept(UserInfo userInfo) throws Exception {
                        listener.result(userInfo);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        throwable.printStackTrace();
                        listener.error();
                    }
                });

//        BoxDataModel.getBoxDataModel().getBinds(new DownLoadListener() {
//            @Override
//            public void OnSuccess(int index, int isUpdate) {
//                presenter.setBindName(CacheConfig.get().getBindDeviceName(CacheUserInfo.get().getUserPhone()));
//            }
//
//            @Override
//            public void OnFail(int index) {
//                presenter.setBindName(CacheConfig.get().getBindDeviceName(CacheUserInfo.get().getUserPhone()));
//            }
//        });
    }

    public void addTeam(String userId, String teamId,OnResponseResultListener listener) {
        KLog.d("开始加入团队");
        api.addTeam(userId, teamId)
                .subscribeOn(Schedulers.newThread())
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
                        listener.error();
                    }
                });
    }


    public void getDevUserRegisterBandOrg(String userId, String orgId, String deptId, String qrCodeUserId,OnResponseResultListener listener) {
        api.getDevUserRegisterBandOrg(userId, orgId, deptId, qrCodeUserId)
                .subscribeOn(Schedulers.newThread())
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
                        listener.error();
                    }
                });
    }


    public void updateTeam(String oldTeamId, String newTeamId,OnResponseResultListener listener) {
        Observable.just(CacheUserInfo.get().getUserTeanCreate())
                .flatMap(new Function<Boolean, ObservableSource<Team>>() {
                    @Override
                    public ObservableSource<Team> apply(Boolean aBoolean) throws Exception {
                        if (aBoolean) {
                            //解散团队
                            return api.deleteTeam(oldTeamId);
                        } else {
                            //退出团队
                            String userId = CacheUserInfo.get().getUserId();
                            return api.leftTeam(userId);
                        }
                    }
                })
                .flatMap(new Function<Team, ObservableSource<UserInfo>>() {
                    @Override
                    public ObservableSource<UserInfo> apply(Team team) throws Exception {
                        if (team.getReturnCode() == 1) {
                            String userId = CacheUserInfo.get().getUserId();
                            return api.addTeam(userId, newTeamId);
                        } else {
                            return Observable.empty();
                        }
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<UserInfo>() {
                    @Override
                    public void accept(UserInfo userInfo) throws Exception {
                        if (userInfo.getReturnCode() == 0) {
                            Toast.makeText(App.getApp(), userInfo.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                        listener.result(userInfo);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        throwable.printStackTrace();
                        listener.error();
                    }
                });
    }
}

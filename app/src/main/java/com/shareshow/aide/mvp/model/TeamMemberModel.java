package com.shareshow.aide.mvp.model;

import android.widget.Toast;

import com.shareshow.App;
import com.shareshow.aide.impl.OnResponseResultListener;
import com.shareshow.aide.mvp.presenter.TeamMemberPresenter;
import com.shareshow.aide.retrofit.Api;
import com.shareshow.aide.retrofit.RetrofitProvider;
import com.shareshow.aide.retrofit.entity.Team;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by xiongchengguang on 2018/2/2.
 */

public class TeamMemberModel implements BaseModel {
    private TeamMemberPresenter presenter;
    private String exitTeam;
    private Api api;

    public TeamMemberModel(TeamMemberPresenter presenter) {
        this.presenter = presenter;
        api = RetrofitProvider.getApi();
    }

    /**
     * 团队成员列表
     *
     * @param teamId
     * @param listener
     */
    public void getTeamMemberList(String teamId, OnResponseResultListener listener) {
        api.teamMember(teamId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Team>() {
                    @Override
                    public void accept(Team team) throws Exception {
                        listener.result(team);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        throwable.printStackTrace();
                        listener.error();
                    }
                });
    }

    public void getDismiisTeam(String teamId, OnResponseResultListener listener) {
        api.deleteTeam(teamId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Team>() {
                    @Override
                    public void accept(Team team) throws Exception {
                        listener.result(team);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        throwable.printStackTrace();
                        listener.error();
                    }
                });
    }

    public void setExitTeam(String userId, OnResponseResultListener listener) {
        api.leftTeam(userId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Team>() {
                    @Override
                    public void accept(Team team) throws Exception {
                        listener.result(team);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        throwable.printStackTrace();
                        listener.error();
                        Toast.makeText(App.getApp(), "退出失败", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public void getRemoveTeam(String userId, OnResponseResultListener listener) {
        api.leftTeam(userId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Team>() {
                    @Override
                    public void accept(Team team) throws Exception {
                        listener.result(team);
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

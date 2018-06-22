package com.shareshow.aide.mvp.presenter;

import android.app.Activity;
import android.widget.Toast;

import com.shareshow.App;
import com.shareshow.aide.impl.OnResponseResultListener;
import com.shareshow.aide.mvp.model.TeamMemberModel;
import com.shareshow.aide.mvp.view.TeamMemberView;
import com.shareshow.aide.retrofit.entity.Team;
import com.shareshow.aide.util.CacheUserInfo;
import com.shareshow.aide.util.Fixed;

/**
 * Created by xiongchengguang on 2018/2/2.
 */

public class TeamMemberPresenter extends BasePresenter<TeamMemberView> {
    private TeamMemberModel model;
    private Activity activity;

    public TeamMemberPresenter(Activity activity) {
        this.activity = activity;
        model = new TeamMemberModel(this);
    }

    /**
     * 团队成员列表
     */
    public void getTeamMemberList() {
        String teamId = CacheUserInfo.get().getTeamId();
        model.getTeamMemberList(teamId, new OnResponseResultListener() {
            @Override
            public void result(Object obj) {
                Team team = (Team) obj;
                if (team.getReturnCode() == 1) {
                    getView().onViewTeamMenberList(team);
                } else {
                    Toast.makeText(App.getApp(), "加载失败,刷新重试", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void error() {
                if (isViewAttached()) {
                    getView().onViewError(0);
                    Toast.makeText(App.getApp(), "加载失败,刷新重试", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    /**
     * 解散团队
     */
    public void getDismiisTeam() {
        String teamId = CacheUserInfo.get().getTeamId();
        model.getDismiisTeam(teamId, new OnResponseResultListener() {
            @Override
            public void result(Object obj) {
                Team team = (Team) obj;
                if (team.getReturnCode() == 1) {
                    CacheUserInfo.get().setTeamId("");
                    Toast.makeText(App.getApp(), "解散成功", Toast.LENGTH_SHORT).show();
                    activity.setResult(Fixed.UPDATE_USER_INFO);
                    activity.finish();
                } else {
                    Toast.makeText(App.getApp(), "解散失败", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void error() {
                Toast.makeText(App.getApp(), "解散失败", Toast.LENGTH_LONG);
            }
        });
    }


    /**
     * 退出团队
     */
    public void getExitTeam() {
        String userId = CacheUserInfo.get().getUserId();
        model.setExitTeam(userId, new OnResponseResultListener() {
            @Override
            public void result(Object obj) {
                Team team = (Team) obj;
                if (team.getReturnCode() == 1) {
                    getTeamMemberList();
                } else {
                    Toast.makeText(App.getApp(), "退出失败", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void error() {
                Toast.makeText(App.getApp(), "退出失败", Toast.LENGTH_LONG).show();
            }
        });
    }

    public void getRemoveTeam(String userId) {
        model.getRemoveTeam(userId, new OnResponseResultListener() {
            @Override
            public void result(Object obj) {
                Team team = (Team) obj;
                if (isViewAttached()) {
                }
            }

            @Override
            public void error() {
                Toast.makeText(App.getApp(), "退出失败", Toast.LENGTH_SHORT).show();
            }
        });
    }
}

package com.shareshow.aide.mvp.presenter;

import android.widget.Toast;

import com.shareshow.App;
import com.shareshow.aide.impl.OnResponseResultListener;
import com.shareshow.aide.mvp.model.PersonalModel;
import com.shareshow.aide.mvp.view.PersonalView;
import com.shareshow.aide.retrofit.entity.UserInfo;
import com.shareshow.aide.util.CacheConfig;
import com.shareshow.aide.util.CacheUserInfo;
import com.socks.library.KLog;

/**
 * Created by xiongchengguang on 2018/3/29.
 */

public class PersonalPresenter extends BasePresenter<PersonalView> {

    private PersonalModel model;

    public PersonalPresenter() {
        model = new PersonalModel(this);
    }

    public void getUserInfo() {
        String userId = CacheUserInfo.get().getUserId();
        model.getUserInfo(userId, new OnResponseResultListener() {
            @Override
            public void result(Object obj) {
                if (isViewAttached()) {
                    UserInfo userInfo = (UserInfo) obj;
                    if (userInfo.getReturnCode() == 1 || userInfo.getReturnCode() == 2) {
                        CacheUserInfo.get().setUserInfo(userInfo);
                    }
                    getView().onViewRefurbishUserInfo();
                }
            }

            @Override
            public void error() {
                KLog.d("个人信息获取失败!");
            }
        });
    }

    public void getDevUserRegisterBandOrg(String orgId, String deptId, String qrCodeUserId) {
        String userId= CacheUserInfo.get().getUserId();
        model.getDevUserRegisterBandOrg(userId,orgId, deptId , qrCodeUserId, new OnResponseResultListener() {
            @Override
            public void result(Object obj) {
                UserInfo userInfo = (UserInfo) obj;
                if (userInfo.getReturnCode() == 1) {
                    getUserInfo();
                }else {
                    Toast.makeText(App.getApp(), "加入组织失败", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void error() {
                Toast.makeText(App.getApp(), "加入组织失败", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * 加入团队
     *
     * @param teamId 加入的团队Id
     */
    public void addTeam(String teamId) {
        String userId = CacheUserInfo.get().getUserId();
        model.addTeam(userId, teamId, new OnResponseResultListener() {
            @Override
            public void result(Object obj) {
                UserInfo userInfo = (UserInfo) obj;
                if (userInfo.getReturnCode() == 1) {
                    getUserInfo();
                }
            }

            @Override
            public void error() {
                Toast.makeText(App.getApp(), "加入失败", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void updateTeam(String newTeamId) {
        String oldTeamId = CacheUserInfo.get().getTeamId();
        model.updateTeam(oldTeamId, newTeamId, new OnResponseResultListener() {
            @Override
            public void result(Object obj) {
                UserInfo userInfo = (UserInfo) obj;
                if (userInfo.getReturnCode() == 1) {
                    getUserInfo();
                }
            }

            @Override
            public void error() {
                Toast.makeText(App.getApp(), "更换失败", Toast.LENGTH_SHORT).show();
            }
        });
    }

}

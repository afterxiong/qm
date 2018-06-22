package com.shareshow.aide.mvp.presenter;

import com.shareshow.aide.mvp.model.MineModel;
import com.shareshow.aide.mvp.view.MineView;
import com.shareshow.aide.retrofit.entity.UserInfo;
import com.shareshow.aide.util.CacheUserInfo;

/**
 * Created by xiongchengguang on 2018/1/23.
 */

public class MinePresenter extends BasePresenter<MineView> {

    private MineModel model;

    public MinePresenter() {
        model = new MineModel(this);
    }

    public void getUserInfo() {
        if (isViewAttached()) {
            getView().showLoading();
        }
        String userId = CacheUserInfo.get().getUserId();
        model.getUserInfo(userId);
    }

    public void setUserInfo(UserInfo userInfo) {
        if (isViewAttached()) {
            getView().setUserInfo(userInfo);
            getView().hideLoading();
        }
    }

    public void addTeam(String teamId) {
        if (isViewAttached()) {
            String userId = CacheUserInfo.get().getUserId();
            getView().showLoading();
            model.addTeam(userId, teamId);
        }
    }

    public void onPresenterAddTeamListener(UserInfo userInfo) {
        if (isViewAttached()) {
            getView().hideLoading();
            getUserInfo();
        }
    }


    public void onPresenterAddOrgDeptListener(UserInfo userInfo) {
        if (isViewAttached()) {
            getView().hideLoading();
            getUserInfo();
        }
    }

    public void getDevUserRegisterBandOrg(String orgId, String deptId, String qrCodeUserId) {
        if (isViewAttached()) {
            getView().showLoading();
            String userId = CacheUserInfo.get().getUserId();
            model.getDevUserRegisterBandOrg(userId, orgId, deptId, qrCodeUserId);
        }
    }

    public void updateTeam(String newTeamId) {
        if (isViewAttached()) {
            getView().showLoading();
            String oldTeamId = CacheUserInfo.get().getTeamId();
            model.updateTeam(oldTeamId, newTeamId);
        }
    }

    public void onErrorListener() {
        if (isViewAttached()) {
            getView().hideLoading();
        }
    }


    public void setBindName(String bindDeviceName) {
        if (isViewAttached()) {
            getView().setBindName(bindDeviceName);
        }
    }
}

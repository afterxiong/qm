package com.shareshow.aide.mvp.presenter;

import com.shareshow.aide.mvp.model.DeptMemberModel;
import com.shareshow.aide.mvp.view.DeptMemberView;
import com.shareshow.aide.retrofit.entity.DeptMember;
import com.shareshow.aide.retrofit.entity.UserInfo;
import com.shareshow.aide.util.CacheUserInfo;

/**
 * Created by xiongchengguang on 2018/3/20.
 */

public class DeptMemberPresenter extends BasePresenter<DeptMemberView> {
    private DeptMemberModel model;

    public DeptMemberPresenter() {
        model = new DeptMemberModel(this);
    }

    public void getDeptMemberList() {
        String deptId= CacheUserInfo.get().getDeptId();
        model.getDeptMemberList(deptId);
    }

    public void setOnDeptMemberList(DeptMember deptMember) {
        if(isViewAttached()){
            getView().setOnDeptMemberList(deptMember);
        }
    }

    public void userCheck(String urbrId, int i) {
        model.userCheck(urbrId,i);
    }

    public void userCheckListener(UserInfo userInfo) {
        if (isViewAttached()){
            getView().setUserCheckListener(userInfo);
        }
    }
}

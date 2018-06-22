package com.shareshow.aide.mvp.view;

import com.shareshow.aide.retrofit.entity.DeptMember;
import com.shareshow.aide.retrofit.entity.UserInfo;

/**
 * Created by xiongchengguang on 2018/3/20.
 */

public interface DeptMemberView extends BaseView{

    public void setOnDeptMemberList(DeptMember deptMember);
    public void setUserCheckListener(UserInfo userInfo);
}

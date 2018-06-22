package com.shareshow.aide.mvp.view;

import com.shareshow.aide.retrofit.entity.UserInfo;

/**
 * Created by xiongchengguang on 2017/11/14.
 */

public interface MainView extends BaseView {
    public void initView();

    public void setUserLogin(UserInfo userInfo);
}

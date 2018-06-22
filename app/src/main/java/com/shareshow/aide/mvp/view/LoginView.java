package com.shareshow.aide.mvp.view;

import com.shareshow.aide.retrofit.entity.UserInfo;

/**
 * Created by xiongchengguang on 2018/1/2.
 */

public interface LoginView extends BaseView {
    public void setTestCode(String code);
    public void showLoading();
    public void hideLoading();

    public void setLoginCompile(UserInfo userInfo);
}

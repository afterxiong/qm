package com.shareshow.aide.mvp.view;

import com.shareshow.aide.retrofit.entity.UserInfo;

/**
 * Created by xiongchengguang on 2018/1/23.
 */

public interface MineView extends BaseView {
    public void showLoading();
    public void hideLoading();
    public void setUserInfo(UserInfo userInfo);
    public void setBindName(String bindDeviceName);
}

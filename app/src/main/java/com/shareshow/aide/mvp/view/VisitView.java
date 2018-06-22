package com.shareshow.aide.mvp.view;

import com.amap.api.location.AMapLocation;

/**
 * Created by xiongchengguang on 2017/12/11.
 */

public interface VisitView extends BaseView {

    public void setLocationText(AMapLocation amapLocation);

    public void setSelectorContacts(String usernumber);

//    public void onVisitListener(UserInfo userInfo);
//
//    public void onVisitCommitErrorListener();
}

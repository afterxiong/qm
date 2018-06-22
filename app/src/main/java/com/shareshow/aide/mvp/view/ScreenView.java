package com.shareshow.aide.mvp.view;

import com.shareshow.airpc.model.RootPoint;

/**
 * Created by TEST on 2017/12/11.
 */

public interface ScreenView extends BaseView {

    void exitTP(RootPoint rp, int type, int id);

    void screenSuccessBox(RootPoint rootPoint);

}

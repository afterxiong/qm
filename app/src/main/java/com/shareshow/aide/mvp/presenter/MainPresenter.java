package com.shareshow.aide.mvp.presenter;

import com.shareshow.aide.mvp.model.MainModel;
import com.shareshow.aide.mvp.view.MainView;

/**
 * Created by xiongchengguang on 2017/12/5.
 */


public class MainPresenter extends BasePresenter<MainView> {


    private MainModel model;

    public MainPresenter() {
        model = new MainModel();
    }

}

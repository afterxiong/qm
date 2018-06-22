package com.shareshow.aide.activity;

import android.support.v7.widget.Toolbar;

import com.shareshow.aide.mvp.presenter.SimlePresenter;
import com.shareshow.aide.mvp.view.SimpleView;

/**
 * Created by xiongchengguang on 2018/3/30.
 */

public class SimpleActivity extends BaseActivity<SimpleView,SimlePresenter> implements SimpleView {

    @Override
    public Toolbar getToolbar() {
        return null;
    }

    @Override
    public SimlePresenter createPresenter() {
        return new SimlePresenter();
    }


    @Override
    public void getResult(int tga, Object obj) {

    }

    @Override
    public void error(int tag) {

    }

}

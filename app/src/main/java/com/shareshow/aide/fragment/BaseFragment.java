package com.shareshow.aide.fragment;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.shareshow.aide.mvp.presenter.BasePresenter;
import com.shareshow.aide.mvp.view.BaseView;

/**
 * Created by xiongchengguang on 2017/12/7.
 */

public abstract class BaseFragment<V extends BaseView, P extends BasePresenter<V>> extends Fragment {
    protected P presenter;
    protected View view;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        presenter = createPresenter();
    }

    @Nullable
    @Override
    public final View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(getLayoutResource(), container, false);
        presenter.attachView((V) this);
        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        presenter.detachView();
    }

    @Override
    public abstract void onViewCreated(View view, @Nullable Bundle savedInstanceState);

    public abstract P createPresenter();

    public abstract int getLayoutResource();


}

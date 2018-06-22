package com.shareshow.aide.mvp.presenter;

import com.shareshow.aide.mvp.view.BaseView;

import java.lang.ref.Reference;
import java.lang.ref.WeakReference;

/**
 * Created by xiongchengguang on 2017/12/5.
 */

public abstract class BasePresenter<V extends BaseView> {

    public Reference<V> reference;

    public void attachView(V view) {
        reference = new WeakReference<V>(view);
    }

    protected V getView() {
        return reference.get();
    }

    public boolean isViewAttached() {
        return reference != null && reference.get() != null;
    }

    public void detachView() {
        if (reference != null) {
            reference.clear();
            reference = null;
        }
    }


}

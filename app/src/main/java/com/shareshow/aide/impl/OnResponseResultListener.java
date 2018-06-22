package com.shareshow.aide.impl;

/**
 * 网络请求结果
 * Created by xiongchengguang on 2018/3/29.
 */

public interface OnResponseResultListener {
    public void result(Object obj);

    public void error();
}

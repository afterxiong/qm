package com.shareshow.aide.impl;

/**
 * Created by xiongchengguang on 2018/3/21.
 */

public interface OnUserCheckListener {
    public void agree(String urbrId,int position);

    public void refuse(String urbrId,int position);
}

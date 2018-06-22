package com.shareshow.aide.event;

import retrofit2.http.PUT;

/**
 * Created by xiongchengguang on 2018/3/27.
 */

public class DownloadRefurbish {
    public static final int UPDATE=1;
    public static final int OVER=2;

    private int flag;

    public int getFlag() {
        return flag;
    }

    public DownloadRefurbish(int flag) {
        this.flag = flag;
    }
}

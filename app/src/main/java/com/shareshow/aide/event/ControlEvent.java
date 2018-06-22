package com.shareshow.aide.event;

import android.app.Activity;

import com.shareshow.airpc.record.RecodListener;

/**
 * Created by hzk on 2018/3/28 0028.
 */

public class ControlEvent {

    public static final int START_TP = 1;
    public static final int CLOSE_TP = 2;

    public int controlCod = 0;
    public Activity activity = null;
    public RecodListener listener = null;
    public Runnable runnable = null;
}

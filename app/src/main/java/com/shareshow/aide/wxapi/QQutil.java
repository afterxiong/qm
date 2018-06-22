package com.shareshow.aide.wxapi;

import android.content.Context;

import com.tencent.tauth.Tencent;

/**
 * Created by Administrator on 2018/3/23 0023.
 */

public class QQutil {

    private static final String APP_ID = "1106645553";
    private static Tencent tencent = null;

    public static Tencent getTencent (Context context) {
        if (tencent == null) {
            tencent =  Tencent.createInstance(APP_ID,context.getApplicationContext());
        }
        return  tencent;
    }

}

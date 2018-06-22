package com.shareshow.aide.wxapi;

import android.content.Context;

import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

/**
 * Created by Administrator on 2018/3/23 0023.
 */

public class WXutil {

    private static IWXAPI api;

    private static final String  APP_ID = "wx6dd7b75b3d862bd8";

    public static IWXAPI getApi(Context context){
        if (api == null) {
            api = WXAPIFactory.createWXAPI(context.getApplicationContext(), APP_ID);
            api.registerApp(APP_ID);
        }
        return api;
    }

}

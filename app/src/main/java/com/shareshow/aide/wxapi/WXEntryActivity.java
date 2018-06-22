package com.shareshow.aide.wxapi;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.shareshow.airpc.util.DebugLog;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;

/**
 * Created by Administrator on 2018/3/19 0019.
 */

public class WXEntryActivity extends Activity implements IWXAPIEventHandler {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DebugLog.showLog(this,"onCreate");
        WXutil.getApi(this).handleIntent(getIntent(),this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        WXutil.getApi(this).handleIntent(intent,this);

    }

    @Override
    public void onReq(BaseReq baseReq) {
        DebugLog.showLog(this,"onReq");
    }

    @Override
    public void onResp(BaseResp baseResp) {
        DebugLog.showLog(this,"onResp"+baseResp.errCode);
    }
}

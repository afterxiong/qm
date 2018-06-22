package com.shareshow;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.os.Debug;
import android.os.Process;
import android.support.multidex.MultiDex;
import com.liulishuo.filedownloader.FileDownloader;
import com.liulishuo.filedownloader.services.FileDownloadService;
import com.shareshow.airpc.util.DebugLog;
import com.shareshow.db.GreenDaoManager;

import java.util.List;


/**
 * Created by xiongchengguang on 2017/12/5.
 */

public class App extends Application{

    public static boolean isBindAudioService =false;

    private static Application app;

    public static Application getApp(){
        return app;
    }

    @Override
    public void onCreate(){
        super.onCreate();
        app = this;
        FileDownloader.setup(this);
        if(shouldInit()){
            //在子线程中完成其他初始化,解决启动白屏问题
          InitializeService.start(this);
        }
    }

    @Override
    protected void attachBaseContext(Context base){
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    private boolean shouldInit(){
        ActivityManager am = ((ActivityManager) getSystemService(Context.ACTIVITY_SERVICE));
        List<ActivityManager.RunningAppProcessInfo> processInfos = am.getRunningAppProcesses();
        String mainProcessName = getPackageName();
        int myPid = android.os.Process.myPid();
        for (ActivityManager.RunningAppProcessInfo info : processInfos){
            if (info.pid == myPid && mainProcessName.equals(info.processName)) {
                return true;
            }
        }
        return false;
    }

}

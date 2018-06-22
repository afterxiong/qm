package com.shareshow;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.Nullable;
import com.shareshow.aide.service.NettyService;
import com.shareshow.aide.service.TimerService;
import com.shareshow.airpc.ftpserver.FTPServerService;
import com.shareshow.airpc.util.CrashHandle;
import com.shareshow.airpc.util.DebugLog;
import com.shareshow.airpc.util.NetworkUtils;
import com.shareshow.db.GreenDaoManager;
import com.tencent.bugly.crashreport.CrashReport;
import com.xtmedia.xtview.GlRenderNative;
import java.net.DatagramSocket;
import java.net.SocketException;

/**
 * Created by xiongchengguang on 2018/1/17.
 */

public class InitializeService extends IntentService {

    private static final String ACTION_INIT = "initApplication";

    public InitializeService(){
        super("InitializeService");
    }

    public static void start(Context context){
        Intent intent = new Intent(context, InitializeService.class);
        intent.setAction(ACTION_INIT);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent){
        if (intent != null){
            final String action = intent.getAction();
            if (ACTION_INIT.equals(action)){
                initApplication();
            }
        }
    }

    private void initApplication(){
         iniService();
         iniJNIthread.start();
         GreenDaoManager.initGreenDao();
         CrashHandle.getInstance().init(App.getApp());
//        CrashReport.UserStrategy strategy = new CrashReport.UserStrategy(App.getApp());
//        strategy.setAppVersion(getPackageCode()+"_bugly");
//        CrashReport.initCrashReport(getApplicationContext(), "2db24625f0", false,strategy);
       // CrashReport.testJavaCrash();
        CrashReport.initCrashReport(getApplicationContext(), "2db24625f0", false);
        Intent timeIntent = new Intent(this, TimerService.class);
        startService(timeIntent);
    }

    private void iniService(){
        Intent intent =  new Intent(this,NettyService.class);
        startService(intent);
    }


    private String getPackageCode(){
            try {
                return getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
                return "";
            }
    }

    /**
     * 初始化JNI的
     */

    private Thread iniJNIthread = new Thread(){
        @Override
        public void run(){
            super.run();
            initPlatform();
            GlRenderNative.setPlayStatusEnable(new Object(
            ));
        }
    };

    /*
     * 采用递归去轮询所有的端口
     */
    private int isPortOccupy(int count, int port){
        //   long time=System.currentTimeMillis();
        for (int i = 0; i < count; i++) {
            try {
                DatagramSocket socket = new DatagramSocket(port + i);
                socket.close();
            } catch (SocketException e){
                e.printStackTrace();
                DebugLog.showLog(this, "阻塞port:" + (port + i));
                return isPortOccupy(count, port + count);
            }
        }
        DebugLog.showLog(this, "调用端口：" + port);
        //	DebugLog.showLog(this,"时间："+(System.currentTimeMillis()-time));
        return port;
    }


    /**
     * 初始化传输库和转发库
     */
    private void initPlatform(){
        // 打开丢包重传
        GlRenderNative.PutRouterCfg("config.sink_cfg.resend", "1");
        // 打开心跳检测 3秒检测一次,单位毫秒
        GlRenderNative.PutRouterCfg("config.rtsp_config.ping_timeout", "10000*10");
        GlRenderNative.mediaClientInit("0.0.0.0", 19901, 40000, 200);//分享的底层初始化库
//		GlRenderNative.mediaServerInit(32, "0.0.0.0", 50000, 20000, 1554, 20001,true, false);//投屏的底层初始化库
        //DebugLog.showLog(this,"occupy :"+isPortOccupy(4*3,50000));
        // 最大的端口号65535
        GlRenderNative.mediaServerInit(4, "0.0.0.0", isPortOccupy(4 * 3, 50000), 20000, 1554, 20001, true, false);//投屏的底层初始化库
        System.out.println("================初始化传输库和转发库===================");
    }

}

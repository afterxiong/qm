package com.shareshow.airpc.util;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.os.Looper;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2017/6/6 0006.
 */

public class CrashHandle implements Thread.UncaughtExceptionHandler {
    private static final String TAG = "CrashHandler";
    private Thread.UncaughtExceptionHandler mDefaultHandler;
    private static CrashHandle INSTANCE = new CrashHandle();
    private Context mContext;
    private Map<String, String> info = new HashMap<String, String>();
    private SimpleDateFormat format = new SimpleDateFormat(
            "yyyy-MM-dd-HH-mm-ss");

    private CrashHandle(){

    }

    public static CrashHandle getInstance() {
        return INSTANCE;
    }

    /**
     * @param context
     */
    public void init(Context context) {
        mContext = context;
        mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(this);
    }

    /**
     *
     */
    public void uncaughtException(Thread thread, Throwable ex){
        if(!handleException(ex) && mDefaultHandler != null){
            mDefaultHandler.uncaughtException(thread, ex);
        }else{
            try {
                thread.sleep(3000);
            } catch (InterruptedException e){
                e.printStackTrace();
            }
            android.os.Process.killProcess(android.os.Process.myPid());
            System.exit(1);
        }
    }

    /**
     * @param ex
     * @return
     */
    public boolean handleException(final Throwable ex){
        new Thread(){
            public void run(){
                Looper.prepare();
                Looper.loop();
            }
        }.start();
        if (ex == null) {
            return false;
        }

        // collectDeviceInfo(mContext);
        //saveCrashInfo2File(ex);
        String result = getCrashResult(ex);
        DebugLog.showLog(this, "储存异常信息:"+result);
       // saveCrashInfo2File(ex);
//        result= AndroidInfo.getPhoneInfo()+"\n"+result;
//        MobclickAgent.reportError(mContext,result);
        return true;
    }

    private String getCrashResult(final Throwable ex){
        Writer writer = new StringWriter();
        PrintWriter pw = new PrintWriter(writer);
        ex.printStackTrace(pw);
        Throwable cause = ex.getCause();

        while (cause != null){
            cause.printStackTrace(pw);
            cause = cause.getCause();
        }
        pw.close();
        String result = writer.toString();
        return result;
    }

    /**
     * @param context
     */
    public void collectDeviceInfo(Context context) {
        try {
            PackageManager pm = context.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(context.getPackageName(),
                    PackageManager.GET_ACTIVITIES);
            if (pi != null) {
                String versionName = pi.versionName == null ? "null"
                        : pi.versionName;
                String versionCode = pi.versionCode + "";
                info.put("versionName", versionName);
                info.put("versionCode", versionCode);
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        Field[] fields = Build.class.getDeclaredFields();
        for (Field field : fields){
            try {
                field.setAccessible(true);
                info.put(field.getName(), field.get("").toString());
                Log.d(TAG, field.getName() + ":" + field.get(""));
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    private String saveCrashInfo2File(Throwable ex){
        StringBuffer sb = new StringBuffer();
        for (Map.Entry<String, String> entry : info.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            sb.append(key + "=" + value + "\r\n");
        }
        String result = getCrashResult(ex);
        sb.append(result);
        long timetamp = System.currentTimeMillis();
        String time = format.format(new Date());
        String fileName = "crash-" + time + "-" + timetamp + ".log";
        if (Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)){
            try {
                File dir = new File(Environment.getExternalStorageDirectory()
                        + "/renyiping/crash");
                if (!dir.exists())
                    dir.mkdir();
                FileOutputStream fos = new FileOutputStream(dir +"/"+ fileName);
                fos.write(sb.toString().getBytes());
                fos.close();
                return fileName;
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
       }
    }



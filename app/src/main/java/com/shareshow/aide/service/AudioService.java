package com.shareshow.aide.service;

import android.app.Service;
import android.content.Intent;
import android.media.MediaRecorder;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import com.shareshow.App;
import com.shareshow.aide.util.CacheUserInfo;
import com.shareshow.aide.util.Fixed;
import com.socks.library.KLog;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by xiongchengguang on 2018/1/10.
 */

public class AudioService extends Service {

    public  static final String TIME_RECIEVER = "com.shareshow.AudioTime";
   // private static String dstPath;
    private MediaRecorder mediaRecorder;
    private static final int MAX_DURATION_MS = 1000*60*60*24;
    //最大录制值设置为一天
    private Disposable disposable;
    //private static String duration = "";

    @Override
    public void onCreate(){
        super.onCreate();
       // beginClock();
      //  startAudio();
    }


    private void startAudio(String dstPath){
        if (mediaRecorder == null){
            mediaRecorder = new MediaRecorder();
        }
        String cachePhone = CacheUserInfo.get().getUserPhone();
        if (cachePhone.isEmpty()){
            cachePhone = "cacheAudio";
        }
        //dstPath = Fixed.getMoningFile() + File.separator +CacheUserInfo.get().getUserPhone()+"_"+System.currentTimeMillis() + ".aac";
        File file = new File(dstPath);
        File parent = file.getParentFile();
        if (!parent.exists()){
            parent.mkdirs();
        }
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.DEFAULT);
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.HE_AAC);
        mediaRecorder.setOutputFile(dstPath);
        mediaRecorder.setMaxDuration(MAX_DURATION_MS);
        try {
            mediaRecorder.prepare();
            mediaRecorder.start();
        } catch(IOException e){
            e.printStackTrace();
        }
    }

    private void stopAudio() {
        if (mediaRecorder == null){
            return;
        }
        try {
            mediaRecorder.stop();
            mediaRecorder.reset();
            mediaRecorder.release();
            mediaRecorder = null;
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        String path = intent.getStringExtra("filepath");
        startAudio(path);
        App.isBindAudioService=true;
        return new AudioBinder();
    }

    @Override
    public boolean onUnbind(Intent intent){
        App.isBindAudioService=false;
        return super.onUnbind(intent);
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        KLog.d("停止录音");
        //endClock();
        stopAudio();
    }

    public static class AudioBinder extends Binder {

//        public String getDstFolder() {
//            return dstPath;
//        }
//
//        public String getDuration() {
//            return duration;
//        }
    }
    /**
     * 录音记时
     */
    public void beginClock(){
//        disposable = Observable.interval(0, 1000, TimeUnit.MILLISECONDS)
//                .subscribeOn(Schedulers.io())
//                .map(ms -> {
//                    SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");
//                    String hms = formatter.format(ms * 1000 - TimeZone.getDefault().getRawOffset());
//                    return hms;
//                })
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(string ->{
//                    duration = string;
//                });
    }

//    public void endClock(){
//        disposable.dispose();
//    }


    private String getNormalTime(){
        long value = System.currentTimeMillis();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String time = format.format(new Date(value));
        return time;
    }

}

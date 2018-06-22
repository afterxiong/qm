package com.shareshow.aide.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.support.annotation.Nullable;

import com.shareshow.airpc.util.DebugLog;

/**
 * Created by FENGYANG on 2018/3/26.
 * 音频播放器
 *
 *
 */


public class PlayService extends Service implements MediaPlayer.OnCompletionListener {

    private static final int IDLE =0*0001;

    public static final int PLAYING =0*0002;

    public static final int PAUSE =0*0003;

    public static final int STOP =0*0004;

    public static final int FAIL =0*0005;

    private MediaPlayer player;

    private int playState = IDLE;

    private boolean isPlay =false;

    private Object lock =new Object();

    private boolean isWorkRunning =true;

    private String mUUid="";

    private AudioManager mAudioManager;//管理音频焦点的

    private boolean needRestart;//是否暂时失去音频焦点

    private PlayListener mListener;

    private Handler mHandler =new Handler(Looper.getMainLooper());

    @Override
    public void onCreate(){
        super.onCreate();
        init();
    }

    private void init(){
        mAudioManager = (AudioManager)getSystemService(AUDIO_SERVICE);
        workThread.start();
        initBroadReceiver();
    }

    //监听耳机或者蓝牙耳机拔出后暂停
    private void initBroadReceiver(){
        IntentFilter filter =  new IntentFilter(AudioManager.ACTION_AUDIO_BECOMING_NOISY);
        registerReceiver(headphoneReceiver,filter);
    }

    private  BroadcastReceiver headphoneReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (AudioManager.ACTION_AUDIO_BECOMING_NOISY.equals(action)) {
                   if(playState==PLAYING){
                       pause();
                }
            }
        }
    };


    @Nullable
    @Override
    public IBinder onBind(Intent intent){

        return new PlayBinder();
    }

    public void start(String path, String uuid){
        if(player==null){
            this.mUUid =uuid;
            try{
                player=new MediaPlayer();
                player.setDataSource(path);
                player.prepare();
                isStartPlay();
                player.start();
                player.setOnCompletionListener(this);
                mHandler.post(new Runnable(){
                        @Override
                        public void run(){
                            if(mListener!=null){
                                mListener.onState(PLAYING);
                                mListener.onMax(player.getDuration());
                            }
                        }
                 });
            }catch (Exception e){
                e.printStackTrace();
                player.reset();
                player.release();
                player=null;
                playState=IDLE;
                if(mListener!=null){
                    mListener.onState(FAIL);
                }
            }
        }else if(mUUid.equals(uuid)&&player!=null){
            isStartPlay();
            player.start();
            if(mListener!=null){
                mListener.onState(PLAYING);
            }
        }else{
            stop();
            start(path,uuid);
        }
        playState=PLAYING;
        isPlay=true;
        synchronized(lock){
           lock.notify();
        }
    }

    public boolean isStartPlay(){
        int result = mAudioManager.requestAudioFocus(audioFocusChangeListener,AudioManager.STREAM_MUSIC,
                AudioManager.AUDIOFOCUS_GAIN);
        if(result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED){
            DebugLog.showLog(this,"音频获取成功!");
            return true;
        }else{
            DebugLog.showLog(this,"音频获取失败,释放资源!");
            return false;
        }
    }

    private AudioManager.OnAudioFocusChangeListener audioFocusChangeListener = new AudioManager.OnAudioFocusChangeListener() {
        @Override
        public void onAudioFocusChange(int focusChange) {
            switch (focusChange){
                //重新获取焦点
                case AudioManager.AUDIOFOCUS_GAIN:
                    //判断是否需要重新播放音乐
                    if (needRestart){
                        start(null,mUUid);
                        needRestart = false;
                    }
                    break;
                //暂时失去焦点
                case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                    //暂时失去焦点，暂停播放音乐（将needRestart设置为true）
                    if(playState==PLAYING){
                        pause();
                        needRestart = true;
                    }
                    break;
                //时期焦点
                case AudioManager.AUDIOFOCUS_LOSS:
                    //暂停播放音乐，不再继续播放
                    if(playState==PLAYING){
                        pause();
                    }
                    break;
            }
        }
    };

    public void pause(){
        if(player!=null){
            playState=PAUSE;
            player.pause();
            isPlay=false;
            if(mListener!=null){
                mListener.onState(PAUSE);
            }
        }
    }

    public void stop(){
        if(player!=null){
            playState=STOP;
            player.stop();
            player.reset();
            player.release();
            player=null;
            if(mListener!=null){
                mListener.onState(STOP);
            }
            realseAudioFocus();
        }
    }

    private void seekTo(int progress){
        if(player!=null){
            player.seekTo(progress);
        }
    }

    private Thread workThread = new Thread(new Runnable(){
        @Override
        public void run(){
            while (isWorkRunning){
                if(isPlay&&player!=null){
                    int position = player.getCurrentPosition();
                    mHandler.post(new Runnable(){
                        @Override
                        public void run(){
                            if(mListener!=null&&player!=null){
                                mListener.onProgress(position);
                            }
                        }
                    });
                    try{
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }else{
                    synchronized (lock){
                        DebugLog.showLog(this,"播放暂停中...");
                        try {
                            lock.wait();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }

            }
        }
    });

    @Override
    public void onDestroy(){
        super.onDestroy();
        isWorkRunning=false;
        mHandler.removeCallbacksAndMessages(null);
        mHandler=null;
        mUUid="";
        DebugLog.showLog(this,"playService结束了!");
        if(player!=null){
            player.reset();
            player.release();
            player=null;
        }
        workThread.interrupt();
        workThread=null;
        realseAudioFocus();
    }

    public void realseAudioFocus(){
        if (mAudioManager != null && audioFocusChangeListener != null) {
            mAudioManager.abandonAudioFocus(audioFocusChangeListener);
        }
    }

    //音频播放完毕
    @Override
    public void onCompletion(MediaPlayer mp){
        DebugLog.showLog(this,"音频播放完毕!");
        isPlay=false;
        if(mListener!=null){
            mListener.onComplete(mp.getDuration());
        }
        stop();
    }


    public class  PlayBinder extends Binder{

        public void setListener(PlayListener listener){
            mListener = listener;
        }

        public void start(String url, String  uuid){
           PlayService.this.start(url,uuid);
        }

        public void pause(){
            PlayService.this.pause();
        }

        public void seekTo(int progress) {
            PlayService.this.seekTo(progress);
        }

        public void stop() {
            PlayService.this.stop();
        }
    }


    public interface PlayListener{

        void onMax(int max);

        void onProgress(int progress);

        void onState(int state);

        void onComplete(int duration);

    }

}

package com.shareshow.aide.service;

/**
 * Created by xiongchengguang on 2018/4/8.
 */

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.shareshow.aide.util.CacheUserInfo;
import com.shareshow.aide.util.Fixed;
import com.shareshow.airpc.util.DebugLog;

import java.io.File;

import audio.shareshow.com.Encord.Encoder;
import audio.shareshow.com.Record.Recorder;
import audio.shareshow.com.Utils.AudioContent;
import audio.shareshow.com.Utils.AudioManager;
import audio.shareshow.com.Utils.BufferChunk;
import audio.shareshow.com.Utils.INotify;
import audio.shareshow.com.Utils.TimeThread;

/**
 *
 */
public class VisitAudioService extends Service implements INotify, TimeThread.TimeListener {

    private AudioManager audioManager;
    private TimeThread timeThread;
    private TimeListener listener;
    private File loclFile;
    private String audio_state = AudioContent.IDLE_RECORDER;
    private String time = "00:00:00";
    private android.media.AudioManager manager;

    @Override
    public void onCreate() {
        super.onCreate();
        initAudio();
    }

    //初始化录音管理器
    private void initAudio() {
        if (audioManager == null) {
            audioManager = new AudioManager(this);
        }
        if (timeThread == null) {
            timeThread = new TimeThread(this);
        }
        if (manager == null) {
            manager = (android.media.AudioManager) getSystemService(AUDIO_SERVICE);
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return new RecordBinder();
    }

    //开始录音
    public void startRecord() {
        if (audioManager != null && (audioManager.getmState() == AudioContent.IDLE_RECORDER
                || audioManager.getmState() == AudioContent.STOP_RECORDER) && createAudio()) {
            manager.requestAudioFocus(null, android.media.AudioManager.STREAM_MUSIC, android.media.AudioManager.AUDIOFOCUS_GAIN_TRANSIENT_EXCLUSIVE);
            audioManager.StartAudioTran();
            if (timeThread != null) {
                timeThread.startTime();
            }
        } else {
            if (listener != null) {
                listener.OnRecordFail();
            }
            DebugLog.showLog(this, "暂时无法录音" + audioManager.getmState());
        }
    }

    public boolean createAudio() {
        try {
            loclFile = new File(Fixed.getVisitAudioPath());
            File dir = loclFile.getParentFile();
            if (!dir.exists()) {
                dir.mkdirs();
            }
            loclFile.createNewFile();
            if (audioManager != null) {
                audioManager.createAudioFile(loclFile.getPath());
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    //暂停录音
    public void pauseRecord() {
        if (audioManager != null && audioManager.getmState() == AudioContent.START_RECORDER) {
            audioManager.StopAudioTran();
        }
        if (timeThread != null) {
            timeThread.pauseTime();
        }
    }

    //暂停后重新录音
    public void reStartRecord(String time) {
        if (audioManager != null && audioManager.getmState() == AudioContent.PAUSE_RECORDER) {
            audioManager.StartAudioTran();
        }
        if (timeThread != null) {
            timeThread.setTime(time);
            timeThread.startTime();
        }
    }


    //停止录音了
    public void stopRecord() {
        if (audioManager != null && (audioManager.getmState() == AudioContent.PAUSE_RECORDER || audioManager.getmState() == AudioContent.START_RECORDER)) {
            audioManager.stopRecordFile();
        }
        if (timeThread != null) {
            timeThread.stopTime();
        }
    }


    private String getState() {
        if (audioManager == null) {
            return AudioContent.IDLE_RECORDER;
        }
        return audioManager.getmState();
    }

    @Override
    public void OnTime(String s) {
        if (listener != null) {
            listener.OnTime(s);
        }
    }


    @Override
    public void onDataComeing(Object context, byte[] data, int size) {
        if (audioManager == null) {
            return;
        }
        if (Recorder.class.isInstance(context)) {
            if (audioManager.getmDataPusher() != null) {
                BufferChunk bufferChunk = new BufferChunk(data, size,
                        System.nanoTime());
                audioManager.getmDataPusher().push(bufferChunk);
            }
        } else if (Encoder.class.isInstance(context)) {
            audioManager.saveInAACfile(data, size);
        }
    }

    @Override
    public void onStateChanged(Object o, int i) {

    }

    @Override
    public void onRecordSuccee(boolean b) {

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (audioManager != null) {
            audioManager.realse();
            audioManager = null;
        }
        if (timeThread != null) {
            timeThread.releaseThread();
            timeThread = null;
        }
    }

    public class RecordBinder extends Binder {

        public void setListener(TimeListener listener) {
            VisitAudioService.this.listener = listener;
        }

        public void startRecord() {
            setAudioState(AudioContent.START_RECORDER);
            VisitAudioService.this.startRecord();
        }

        public void reStartRecord(String time) {
            setAudioState(AudioContent.START_RECORDER);
            VisitAudioService.this.reStartRecord(time);
        }

        public void pauseRecord() {
            setAudioState(AudioContent.PAUSE_RECORDER);
            VisitAudioService.this.pauseRecord();

        }

        public void stopRecord() {
            setAudioState(AudioContent.STOP_RECORDER);
            VisitAudioService.this.stopRecord();
        }


        public String getFilePath() {
            if (loclFile == null) {
                return null;
            }
            return loclFile.getPath();
        }


        public String getState() {
            return VisitAudioService.this.getState();
        }


        public void setAudioState(String trackState) {
            audio_state = trackState;
        }

        public String getAudioState() {

            return audio_state;
        }

        public void setTime(String t) {
            time = t;
        }

        public String getTime() {

            return time;
        }
    }

    public interface TimeListener {

        void OnTime(String s);

        void OnRecordFail();
    }

}

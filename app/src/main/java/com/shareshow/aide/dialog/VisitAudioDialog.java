package com.shareshow.aide.dialog;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.shareshow.aide.R;
import com.shareshow.aide.service.VisitAudioService;

import audio.shareshow.com.Utils.AudioContent;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by xiongchengguang on 2018/4/8.
 */

public class VisitAudioDialog extends BaseDialog implements VisitAudioService.TimeListener {

    @BindView(R.id.visit_audio_time)
    public TextView visit_audio_time;
    @BindView(R.id.visit_audio_pause)
    public TextView visit_audio_pause;

    private VisitAudioService.RecordBinder binder;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = new Intent(getActivity(), VisitAudioService.class);
        getActivity().bindService(intent, audioConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    public View layout(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_visit_audio, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        DisplayMetrics dm = getResources().getDisplayMetrics();
        int width = (int) (dm.widthPixels * 0.8);
        getDialog().getWindow().setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    @OnClick(R.id.visit_audio_close)
    public void visit_audio_close() {
        this.dismiss();
        stopService();
        binder.stopRecord();
        if (listener != null) {
            listener.close();
        }
    }

    @OnClick(R.id.visit_audio_pause)
    public void visit_audio_pause() {
        String state = binder.getAudioState();
        if (state == AudioContent.START_RECORDER) {
            if (binder != null) {
                binder.pauseRecord();
                visit_audio_pause.setText("继续");
            }
        } else if (state == AudioContent.PAUSE_RECORDER) {
            if (binder != null) {
                String time = visit_audio_time.getText().toString().trim();
                binder.reStartRecord(time);
                visit_audio_pause.setText("暂停");
            }
        }
    }

    @OnClick(R.id.visit_audio_compile)
    public void visit_audio_compile() {
        this.dismiss();
        stopService();
        binder.stopRecord();
        if (listener != null) {
            listener.compile();
        }
    }

    private ServiceConnection audioConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            binder = (VisitAudioService.RecordBinder) service;
            binder.startRecord();
            if (binder != null) {
                binder.setListener(VisitAudioDialog.this);
                String time = binder.getTime();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    @Override
    public void OnTime(String s) {
        visit_audio_time.setText(s);
    }

    @Override
    public void OnRecordFail() {

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private void stopService() {
        getActivity().unbindService(audioConnection);
    }

    private OnVisitAudioListener listener;

    public void setOnVisitAudioListener(OnVisitAudioListener listener) {
        this.listener = listener;
    }

    public interface OnVisitAudioListener {
        public void close();

        public void compile();
    }
}


package com.shareshow.airpc.socket.common;

/**
 * Created by TEST on 2017/9/26.
 */

public interface ScreenStarteListener {

    void LoadingAnimate();

    void playSuccess();

    void playError(boolean isPlay, String host);

    void InterruptTimeOut();

    void backToMainActivity(int state);

    void showCloseActivity();
}

package com.shareshow.airpc.record;

/**
 * Created by TEST on 2017/9/8.
 */

public interface EncoderListener {


    void printTimeLog(String log);

    void StartTimer();

    void requestRate();
}

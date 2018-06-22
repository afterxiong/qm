package com.shareshow.airpc.socket.common;

/**
 * Created by TEST on 2017/11/9.
 */

public class HeatbeatEvent {

    private String result;

    public String getResult() {
        return this.result;
    }

    public HeatbeatEvent(String result) {
        this.result = result;
    }
}

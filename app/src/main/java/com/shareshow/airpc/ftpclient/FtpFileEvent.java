package com.shareshow.airpc.ftpclient;

/**
 * Created by TEST on 2017/8/17.
 */

public class FtpFileEvent {

    private boolean  state;

    public  FtpFileEvent(boolean msg){
        this.state=msg;
    }

    public boolean getMsg(){
        return state;
    }


}

package com.shareshow.airpc.ftpserver;

/**
 * Created by Administrator on 2017/7/7 0007.
 */
public class FtpEvent{

    private String mMsg;

    public  FtpEvent(String msg){
        this.mMsg=msg;
    }

    public String getMsg(){
        return mMsg;
    }

}

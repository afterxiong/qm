package com.shareshow.airpc.ftpserver;

import android.util.Log;

/**
 * Created by wulong on 2017/8/16 0016.
 */
public class CmdSTAT extends FtpCmd {

    public CmdSTAT(SessionThread sessionThread, String input){
        super(sessionThread, CmdSTAT.class.toString());
    }

    @Override
    public void run(){
      Log.i("test","STAT====");
        try{
            int version = Constans.VERSION;
            sessionThread.writeString("213 " + version + "\r\n");
        }catch (Exception e){
            // This shouldn't happen unless our input validation has failed
            myLog.l(Log.ERROR, "STAT canonicalize");
            sessionThread.closeSocket(); // should cause thread termination
        }
        myLog.l(Log.DEBUG, "STAT complete");
    }

}

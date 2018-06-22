package com.shareshow.airpc.record;

import com.shareshow.airpc.model.RootPoint;
import com.shareshow.airpc.socket.common.DisconnectListener;
import com.shareshow.airpc.util.DebugLog;


/**
 * Created by Administrator on 2017/6/10 0010.
 */


public class SendDisconStream{

    private static  RootPoint mRootPoint;

    private static SendDisconStream stream=null;

    private static DisconnectListener mListener =null;

    private  SendDisconStream(){

    }

    public  static SendDisconStream getStream(RootPoint rootPoint, DisconnectListener listener){
        if(stream==null){
            synchronized(SendDisconStream.class){
                if(stream==null){
                    stream=new SendDisconStream();
                }
            }
        }

        mRootPoint=rootPoint;

        DebugLog.showLog("SendDisconStream","rootPoint:"+mRootPoint);

        mListener=listener;
        return  stream;
    }
//    public SendDisconStream(RootPoint rootPoint){
//        mRootPoint=rootPoint;
//    }

    public int breaktrack(int a, int b){
        if(mListener!=null){
            mListener.disconnectStream(mRootPoint);
        }
      //  ScreenShareActivity.surface_layout.startPlay(mRootPoint);
        return 1;
    }


}

package com.shareshow.airpc.fragment;


import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.widget.Toast;

import com.shareshow.airpc.util.DebugLog;
import com.shareshow.airpc.util.QMDocumentFile;
import com.shareshow.airpc.util.QMFileType;
import com.shareshow.airpc.util.QMUtil;


/**
 * Created by TEST on 2017/7/18.
 */


public class WxFragment extends BaseWQFragment{


    public void getData(boolean isRefresh){
        if(isRefresh){
            wheel.setVisibility(View.VISIBLE);
        }
        QMUtil.getInstance().getQmDocumentFile().setFileWxFinished(false);
        getFileData(QMFileType.SDCARD+ QMFileType.WEIXIN_FILE,1);
        try {
            Thread.sleep(300);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        QMUtil.getInstance().getQmDocumentFile().setImgWxFinished(false);
        getFileData(QMFileType.SDCARD+QMFileType.WEIXIN_IMG,3);
         handler.postDelayed(TimeOutRunnable,3000);
    }

    public Runnable TimeOutRunnable =new Runnable(){
        @Override
        public void run(){
            //if(!QMUtil.getInstance().getQmDocumentFile().isImgQQFinished()||!QMUtil.getInstance().getQmDocumentFile().isFileQQFinished())
            handler.sendEmptyMessage(QQ_OUT_TIME);
        }
    };


    public Handler handler = new Handler(Looper.getMainLooper()){

        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case WX_FILE_SUCCESS:
                    wheel.setVisibility(View.GONE);
                    wxFile.clear();
                    wxFile.addAll(QMUtil.getInstance().getQmDocumentFile().getWx_document());
                    fileAdpter.notifyDataSetChanged();
                    if(list.getAdapter().equals(fileAdpter)){
                        al = wxFile;
                        setText(0);
                    }
                    break;
                case WX_IMG_VIDEO_SUCCESS:
                    wheel.setVisibility(View.GONE);
                    wxImg.clear();
                    wxImg.addAll(QMUtil.getInstance().getQmDocumentFile().getWx_img());
                    imgAdapter.notifyDataSetChanged();
                    wxVideo.clear();
                    wxVideo.addAll(QMUtil.getInstance().getQmDocumentFile().getWx_video());
                    videoAdapter.notifyDataSetChanged();
                    if(list.getAdapter().equals(imgAdapter)){
                        al = wxImg;
                        setText(1);
                    }else if(list.getAdapter().equals(videoAdapter)){
                        al = wxVideo;
                        setText(2);
                    }
                    break;
                case CONNECT_OK:
//                    if(callBackActivity!=null){
//                        callBackActivity.onDeviceName(QMDevice.getInstance().getSelectDevice());
//                    }
                    //startUpLoad();
                    break;
                case CONNECT_FAILED:
                    DebugLog.showLog(this, "ftp连接失败！");
                    break;
                case QQ_OUT_TIME:
                  //  Toast.makeText(getActivity(),"超时，未获取任何文件。。。", Toast.LENGTH_SHORT).show();
                    wheel.setVisibility(View.GONE);
                    if(list.getAdapter().equals(imgAdapter)){
                        setText(1);
                    }else if(list.getAdapter().equals(videoAdapter)){
                        setText(2);
                    }else{
                        setText(0);
                    }
                    break;

            }
        }
    };


    public void getFileData(String path, int pos){
        if(threadPool!=null){
            threadPool.submit(new DataThread(path,pos));
        }
   }


    public class  DataThread extends Thread {

       public String path;
       public  int pos;
       public DataThread(String path, int pos){
           this.path=path;
           this.pos=pos;
       }

       public void run(){
           QMUtil.getInstance().getQmDocumentFile().getDocumentFile(path, pos, new QMDocumentFile.FileDataListener() {
               @Override
               public void OnFileData(int type) {
                   if(type==1){
                       handler.sendEmptyMessage(WX_FILE_SUCCESS);
                   }else if(type==3){
                       handler.sendEmptyMessage(WX_IMG_VIDEO_SUCCESS);
                   }

                   handler.removeCallbacks(TimeOutRunnable);
               }
           });
//               boolean flag=true;
//               while(flag){
//                   if (QMUtil.getInstance().getQmDocumentFile().isFileWxFinished()) {
//                       flag = false;
//                       handler.sendEmptyMessage(WX_FILE_SUCCESS);
//                   }
//               }
//
//               boolean flag1= true;
//               while (flag1){
//                   if(QMUtil.getInstance().getQmDocumentFile().isImgWxFinished()){
//                       flag1 =false;
//                       handler.sendEmptyMessage(WX_IMG_VIDEO_SUCCESS);
//                   }
//               }
            }
       }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if(handler!=null){
            handler.removeCallbacksAndMessages(null);
        }
    }

}

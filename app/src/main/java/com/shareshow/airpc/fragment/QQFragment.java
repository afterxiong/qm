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
public class QQFragment extends BaseWQFragment {


    public void getData(boolean isRefresh){
        if(isRefresh){
            wheel.setVisibility(View.VISIBLE);
        }
        QMUtil.getInstance().getQmDocumentFile().setFileQQFinished(false);
        getFileData(QMFileType.SDCARD+QMFileType.QQ_FILE,2);
        try {
            Thread.sleep(300);
        } catch (InterruptedException e){
            e.printStackTrace();
        }
        QMUtil.getInstance().getQmDocumentFile().setImgQQFinished(false);
        getFileData(QMFileType.SDCARD+QMFileType.QQ_IMG,4);
        handler.postDelayed(TimeOutRunnable,8000);
       }

       public Runnable TimeOutRunnable =new Runnable(){
           @Override
           public void run(){
               //if(!QMUtil.getInstance().getQmDocumentFile().isImgQQFinished()||!QMUtil.getInstance().getQmDocumentFile().isFileQQFinished())
               handler.sendEmptyMessage(QQ_OUT_TIME);
           }
       };

        public Handler handler = new Handler(Looper.getMainLooper()){

            @Override
            public void handleMessage(Message msg){
                super.handleMessage(msg);
                switch (msg.what){
                    case QQ_FILE_SUCCESS:
                        wheel.setVisibility(View.GONE);
                        wxFile.clear();
                        wxFile.addAll(QMUtil.getInstance().getQmDocumentFile().getQq_document());
                        fileAdpter.notifyDataSetChanged();
                        wxVideo.clear();
                        wxVideo.addAll(QMUtil.getInstance().getQmDocumentFile().getQq_video());
                        videoAdapter.notifyDataSetChanged();
                        if(list.getAdapter().equals(videoAdapter)){
                            al = wxVideo;
                            setText(2);
                        }else if(list.getAdapter().equals(fileAdpter)){
                            al = wxFile;
                            setText(0);
                        }
                        break;
                    case QQ_IMG_VIDEO_SUCCESS:
                        wheel.setVisibility(View.GONE);
                        wxImg.clear();
                        wxImg.addAll(QMUtil.getInstance().getQmDocumentFile().getQq_img());
                        imgAdapter.notifyDataSetChanged();
                        if(list.getAdapter().equals(imgAdapter)){
                            al=wxImg;
                            setText(1);
                        }
                        break;

                    case CONNECT_OK:

                       // startUpLoad();
                        break;
                    case CONNECT_FAILED:
                        DebugLog.showLog(this, "ftp连接失败！");
                        break;
                    case QQ_OUT_TIME:
                      //  Toast.makeText(getActivity(),"超时，未获取任何文件。。。", Toast.LENGTH_SHORT).show();
                        wheel.setVisibility(View.GONE);
                        if(list.getAdapter().equals(videoAdapter)){
                            setText(2);
                        }else if(list.getAdapter().equals(fileAdpter)){
                            setText(0);
                        }else{
                            setText(1);
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

                    if(type==2){
                        handler.sendEmptyMessage(QQ_FILE_SUCCESS);
                    }else if(type==4){
                        handler.sendEmptyMessage(QQ_IMG_VIDEO_SUCCESS);
                    }

                    handler.removeCallbacks(TimeOutRunnable);

                }
            });
//               boolean flag2=true;
//                while (flag2){
//                    if(QMUtil.getInstance().getQmDocumentFile().isFileQQFinished()&&threadPool!=null){
//                        flag2 =false;
//                        handler.sendEmptyMessage(QQ_FILE_SUCCESS);
//                    }
//                }
//
//                boolean flag3=true;
//                while (flag3){
//                    if(QMUtil.getInstance().getQmDocumentFile().isImgQQFinished()&&threadPool!=null){
//                        flag3 =false;
//                        handler.sendEmptyMessage(QQ_IMG_VIDEO_SUCCESS);
//                    }
//                }
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



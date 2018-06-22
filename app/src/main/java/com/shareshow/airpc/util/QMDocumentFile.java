package com.shareshow.airpc.util;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.shareshow.App;
import com.shareshow.airpc.model.LancherFile;
import com.shareshow.airpc.model.QMLocalFile;

import org.xutils.ex.DbException;

import java.io.File;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 本地文件的读取 --除去相册和视频
 *
 * @author tanwei
 */
public class QMDocumentFile {

    private ExecutorService threadPool;
    //==================================文件共享中‘文档’、‘其他’文件的获取===============================
    private boolean fileDealFinished = false;//本地文件是否读取完成

    private ArrayList<String> listDir = new ArrayList<String>();//SD卡中根目录下需要遍历的文件夹

    private ArrayList<QMLocalFile> document = new ArrayList<QMLocalFile>();//文档类型的集合

    private ArrayList<QMLocalFile> wx_document= new ArrayList<>();//文档类型的集合

    private ArrayList<QMLocalFile> wx_img= new ArrayList<>();//文档类型的集合

    private ArrayList<QMLocalFile> wx_video= new ArrayList<>();//文档类型的集合

    private ArrayList<QMLocalFile> qq_document= new ArrayList<>();//文档类型的集合

    private ArrayList<QMLocalFile> qq_img= new ArrayList<>();//文档类型的集合

    private ArrayList<QMLocalFile> qq_video= new ArrayList<>();//文档类型的集合

    private ArrayList<QMLocalFile> other = new ArrayList<QMLocalFile>();//其他类型的集合

    private Object mLock = new Object();// lock

    private final int COUNT = 5;//每个线程读取最多根目录中5个文件夹

    private int threadCount;//开启的线程数据量

    private int threadCount2;//线程结束标志 从threadCount到0

    private static final int SD_OK = 999;//SD文件读取成功
    private boolean fileWxFinished;

    private boolean fileQQFinished;

    private boolean imgWxFinished;
    private boolean imgQQFinished;

    private FileDataListener mListener;

    public boolean isImgQQFinished() {
        return imgQQFinished;
    }

    public boolean isImgWxFinished() {
        return imgWxFinished;
    }

    public boolean isFileWxFinished() {
        return fileWxFinished;
    }

    public boolean isFileQQFinished() {
        return fileQQFinished;
    }


    public void setFileWxFinished(boolean fileWxFinished) {
        this.fileWxFinished = fileWxFinished;
    }

    public void setFileQQFinished(boolean fileQQFinished) {
        this.fileQQFinished = fileQQFinished;
    }

    public void setImgWxFinished(boolean imgWxFinished) {
        this.imgWxFinished = imgWxFinished;
    }

    public void setImgQQFinished(boolean imgQQFinished) {
        this.imgQQFinished = imgQQFinished;
    }

    public boolean isFileDealFinished() {
        return fileDealFinished;
    }

    public ArrayList<QMLocalFile> getDocument(){
        return document;
    }

    public ArrayList<QMLocalFile> getWx_document() {
        return wx_document;
    }

    public ArrayList<QMLocalFile> getWx_img() {
        return wx_img;
    }

    public ArrayList<QMLocalFile> getQq_document() {
        return qq_document;
    }

    public ArrayList<QMLocalFile> getQq_img() {
        return qq_img;
    }

    public ArrayList<QMLocalFile> getQq_video() {
        return qq_video;
    }

    public ArrayList<QMLocalFile> getWx_video() {
        return wx_video;
    }

    public ArrayList<QMLocalFile> getOther(){
        return other;
    }

    public QMDocumentFile(){
        if(threadPool==null){
            threadPool = Executors.newFixedThreadPool(5);
        }
    }

    private Handler handler = new Handler(Looper.getMainLooper()){
        @SuppressLint("ShowToast")
        @Override
        public void handleMessage(Message msg){
            super.handleMessage(msg);
            switch (msg.what){
                case SD_OK:
                    int pos = msg.arg2;
                    if(pos==0){
                        fileDealFinished = true;
                     }else if(pos==1){
                        fileWxFinished = true;
                        DebugLog.showLog(this,"微信文件完成!");
                     }else if(pos==2){
                        fileQQFinished = true;
                    }else if(pos == 3){
                        imgWxFinished=true;
                    }else if(pos == 4){
                        imgQQFinished=true;
                        DebugLog.showLog(this,"QQ文件完成!");
                    }
                    break;
            }
        }
    };

    public void getDocumentAndOtherData(){
        fileDealFinished = false;
        getAllDir(QMFileType.SDCARD,0);//先读取根目录下所有文件夹加入到集合中，有文件加到相应的集合中
    }

    public void getDocumentFile(String path, int pos, FileDataListener listener){
        if(pos==1){
             fileWxFinished = false;
        }else if(pos == 2){
             fileQQFinished = false;
        }else if(pos==3){
             imgWxFinished =false;
        }else if(pos==4){
             imgQQFinished =false;
        }
        this.mListener=listener;
        getAllDir(path,pos);
    }


    private void sortDocumentAndOther(ArrayList<QMLocalFile> documentAndother){
        if (documentAndother!=null)
        Collections.sort(documentAndother, new Comparator<QMLocalFile>(){

            @Override
            public int compare(QMLocalFile lhs, QMLocalFile rhs){
                if(lhs!=null&&rhs!=null){
                    if(lhs.getUpdate() > rhs.getUpdate()){
                        return 1;
                     }else if (lhs.getUpdate() == rhs.getUpdate()){
                        Collator instance = Collator.getInstance(Locale.CHINA);
                        return instance.compare(lhs.getName(), rhs.getName());
                     }else{
                        return -1;
                     }
                }
               return 0;
            }

        });
//        ArrayList<ArrayList<QMLocalFile>> mm = new ArrayList<ArrayList<QMLocalFile>>();
//        int old_type = -2;
        ArrayList<QMLocalFile> kk = null;
        if (documentAndother!=null)
            kk = new ArrayList<QMLocalFile>();
        for (int i = 0; i < documentAndother.size(); i++){
//            if (old_type != documentAndother.get(i).getType()) {

                QMLocalFile pp = documentAndother.get(i);
                kk.add(pp);
//                mm.add(kk);
//            } else {
//                QMLocalFile pp = documentAndother.get(i);
//                kk.add(pp);
//            }
//            old_type = documentAndother.get(i).getType();
        }

        documentAndother.clear();
//        for (int i = 0; i < mm.size(); i++) {
//            ArrayList<QMLocalFile> wx = mm.get(i);
//            Collections.sort(wx, new Comparator<QMLocalFile>() {
//
//                @Override
//                public int compare(QMLocalFile lhs, QMLocalFile rhs) {
//                    if (lhs.getUpdate() > rhs.getUpdate())
//                        return -1;
//                    if (lhs.getUpdate() == rhs.getUpdate())
//                        return 0;
//
//                    return 1;
//                }
//            });
//
            documentAndother.addAll(kk);
//        }
    }

    public int compare1(Long o1, Long o2){
        long val1 = o1.longValue();
        long val2 = o2.longValue();
        return (val1 < val2 ? -1 : (val1 == val2 ? 0 : 1));
    }


    private void getAllDir(String filePath, int pos){
        if(pos==0){
            document.clear();
            other.clear();
        }else if(pos ==1){
            wx_document.clear();
        }else if(pos ==2){
            qq_document.clear();
            qq_video.clear();
        }else if(pos ==3){
            wx_img.clear();
            wx_video.clear();
        }else if(pos==4){
            qq_img.clear();
        }
        listDir.clear();
        File root = new File(filePath);
        DebugLog.showLog(this,"path:"+filePath);
        if(root.exists()){
            DebugLog.showLog(this,"存在！");
        }else{
            DebugLog.showLog(this,"不存在！");
            return;
        }
        File[] files = root.listFiles();
        if(files!=null){
            for (File file : files){
                if (file.isDirectory()){
                    if (file.getPath().equals(QMFileType.ANYSCREEN))//从任盒下载或缓存的文件不用读取
                        continue;
                    listDir.add(file.getAbsolutePath());
                }else{
                    String name = file.getName();
                    long size = file.length();
                    if (size == 0)
                        continue;
                    int type = QMFileType.getType(file.getName());
                    if (type == 9 || type == 10 || type == 11 || type == 12 || type==5 || type ==6){
                        String path = file.getPath();
                        long update = file.lastModified();
                        QMLocalFile localFile = new QMLocalFile(name, path, size, update, type);
                        if(pos==0){
                            other.add(localFile);
                        }else if(pos==1){
                            wx_document.add(localFile);
                        }else if(pos == 2){
                            qq_document.add(localFile);
                        }
                    }

                    if (type <= 4 && type > 0){
                        String path = file.getPath();
                        long update = file.lastModified();
                        QMLocalFile localFile = new QMLocalFile(name, path, size, update, type);
                        if(pos==0){
                            document.add(localFile);
                        }else if(pos==1){
                            wx_document.add(localFile);
                        }else if(pos == 2){
                            qq_document.add(localFile);
                        }
                    }
                    if(type ==7){
                        String path = file.getPath();
                        long update = file.lastModified();
                        QMLocalFile localFile = new QMLocalFile(name, path, size, update, type);
                        if(pos==3){
                            wx_img.add(localFile);
                        }else if(pos==4){
                            qq_img.add(localFile);
                        }
                    }
                    if(type == 8){
                        String path = file.getPath();
                        long update = file.lastModified();
                        QMLocalFile localFile = new QMLocalFile(name, path, size, update, type);
                         if(pos==3){
                             wx_video.add(localFile);
                         }else if(pos==2){
                             qq_video.add(localFile);
                         }
                    }
                }
               }
            //开启线程遍历文件夹
                getFileFromDir(pos);
           }
      }

    //递归遍历某文件夹下的所有文件
    private void getTxt(String filePath, int pos){
        File root = new File(filePath);
        File[] files = root.listFiles();
        if (files == null){
            return;
        }
        for (File file : files){
            if (file.isDirectory()) {
                getTxt(file.getAbsolutePath(), pos);
            } else {
                String name = file.getName();
                long size = file.length();
                if (size == 0)
                    continue;
                int type = QMFileType.getType(file.getName());
                if (type == 9 || type == 10 || type == 11 || type == 12||type==5 || type ==6) {//归属‘其他’分类
                    String path = file.getPath();
                    long update = file.lastModified();
                    QMLocalFile localFile = new QMLocalFile(name, path, size, update, type);
                    if(pos==0){
                        other.add(localFile);
                    }else if(pos==1){
                        wx_document.add(localFile);
                    }else if(pos == 2){
                        qq_document.add(localFile);
                    }
                }
                if (type <= 4 && type > 0) {//归属‘文档分类’
                    String path = file.getPath();
                    long update = file.lastModified();
                    QMLocalFile localFile = new QMLocalFile(name, path, size, update, type);
                    if(pos==0){
                        document.add(localFile);
                    }else if(pos==1){
                        wx_document.add(localFile);
                    }else if(pos == 2){
                        qq_document.add(localFile);
                    }
                }else if(type ==7){
                    String path = file.getPath();
                    long update = file.lastModified();
                    QMLocalFile localFile = new QMLocalFile(name, path, size, update, type);
                    if(pos==3){
                        wx_img.add(localFile);
                    }else if(pos==4){
                        qq_img.add(localFile);
                    }
                }else if(type == 8){
                    String path = file.getPath();
                    long update = file.lastModified();
                    QMLocalFile localFile = new QMLocalFile(name, path, size, update, type);
                    if(pos==3){
                        wx_video.add(localFile);
                    }else if(pos==2){
                        qq_video.add(localFile);
                    }
                }
            }
        }
    }

    private void getFileFromDir(int pos){
        threadCount = listDir.size() / COUNT + 1;//计算开启线程数量
      //  threadCount2 = threadCount;
       for (int i = 0; i < threadCount; i++){
            threadPool.submit(new GetTxtByPath(pos,i));
           }
      //  new GetTxtByPath(pos);
    }

    private class GetTxtByPath extends Thread {

        private int pos;
        private int i;

        public GetTxtByPath( int pos,int i){
            this.i = i * COUNT;
            this.pos=pos;
        }

        public void run(){
            if(pos!=2){
//                if(listDir!=null){
//                    for (int i = 0; i < listDir.size(); i++){
//                         getTxt(listDir.get(i),pos);
//                    }
//                }
                for (int j = i; j < i + COUNT; j++){
                    if (j < listDir.size()){
                        getTxt(listDir.get(j),pos);
                    }
                }
            }

//            threadCount2--;
//            if (threadCount2 == 0){//当threadCount2为0时所有线程均结束，发起读完所有文件的消息
//            threadPool.shutdown();
//            boolean isFinish=false;
//            while (!isFinish){
//                if(threadPool.isTerminated()){
//                    isFinish=true;
         //   threadPool.shutdown();
          //  DebugLog.showLog(this,"线程执行完毕："+threadPool.isTerminated());
                    if(pos==0){
                        sortByTime(document);//按照时间排序
                      //  DebugLog.showLog(this,document.toString(),0);
                        sortByTime(other);
                    }else if(pos==1){
                        sortByTime(wx_document);
                      //  DebugLog.showLog(this,"wx:"+wx_document.toString());
                        if(mListener!=null){
                            mListener.OnFileData(1);
                        }
                    }else if(pos==2){
                        sortByTime(qq_document);
                        sortByTime(qq_video);
                        if(mListener!=null){
                            mListener.OnFileData(2);
                        }
                    }else if(pos==3){
                        sortByTime(wx_img);
                        sortByTime(wx_video);
                        if(mListener!=null){
                            mListener.OnFileData(3);
                        }
                    }else if(pos==4){
                        sortByTime(qq_img);
                        if(mListener!=null){
                            mListener.OnFileData(4);
                        }
                       // DebugLog.showLog(this,"qq:"+qq_img.toString());
                    }
                    Message message = handler.obtainMessage();
                    message.what = SD_OK;
                    //     message.arg1 = i;
                    message.arg2 =pos;
                    handler.sendMessage(message);
                }
//            }
//
//          //  }
       // }
    }

    //==================================文件共享中‘文档’、‘其他’文件的获取===============================
    //获取共享文件
    public ArrayList<LancherFile> getShareFile(){
        ArrayList<LancherFile> shareFile = null;
        try {
            shareFile = QMDbUtil.getIntance(App.getApp()).getAll();
            if (shareFile != null&&shareFile.size()!=0){
                for(int i = shareFile.size()-1; i >=0 ; i--){
                    if (shareFile.get(i).getFileDir() == 1 && !new File(shareFile.get(i).getPath()).exists()){
                        QMDbUtil.getIntance(App.getApp()).delete(shareFile.get(i).getId());
                        shareFile.remove(i);
                      }
                }
//                if (shareFile.size() == 0)
//                    return null;
                sortShareFile(shareFile);
            }
        } catch (DbException e) {
            e.printStackTrace();
        }
        return shareFile;
    }

    /**
     * 对本地共享文件排序
     *
     * @param shareFile
     */
    public void sortShareFile(ArrayList<LancherFile> shareFile){
        Collections.sort(shareFile, new Comparator<LancherFile>(){
            @Override
            public int compare(LancherFile lhs, LancherFile rhs){
                if (lhs.getFileDir() > rhs.getFileDir())
                    return 1;
                if (lhs.getFileDir() == rhs.getFileDir())
                    return 0;

                return -1;
            }

        });
        ArrayList<ArrayList<LancherFile>> mm = new ArrayList<ArrayList<LancherFile>>();
        int old_type = -2;
        ArrayList<LancherFile> kk = null;
        for (int i = 0; i < shareFile.size(); i++) {
            if (old_type != shareFile.get(i).getFileDir()) {
                kk = new ArrayList<LancherFile>();
                LancherFile pp = shareFile.get(i);
                kk.add(pp);
                mm.add(kk);
            } else {
                LancherFile pp = shareFile.get(i);
                kk.add(pp);
            }
            old_type = shareFile.get(i).getFileDir();
        }
        shareFile.clear();
        for (int i = 0; i < mm.size(); i++) {
            ArrayList<LancherFile> qq = mm.get(i);
            Collections.sort(qq, new Comparator<LancherFile>(){
                @Override
                public int compare(LancherFile lhs, LancherFile rhs) {
                    if (lhs.getUpdate() > rhs.getUpdate())
                        return -1;
                    if (lhs.getUpdate() == rhs.getUpdate())
                        return 0;

                    return 1;
                }
            });

            shareFile.addAll(qq);
        }
    }

    private void sortByTime(ArrayList<QMLocalFile> al) {
        Collections.sort(al,new Comparator<QMLocalFile>(){
            @Override
            public int compare(QMLocalFile lhs, QMLocalFile rhs) {
                if(lhs.getUpdate()>rhs.getUpdate())
                    return -1;
                if(lhs.getUpdate()==rhs.getUpdate())
                    return 0;

                return 1;
            }
        });
    }

    public interface FileDataListener{

        void OnFileData(int type);
    }
}

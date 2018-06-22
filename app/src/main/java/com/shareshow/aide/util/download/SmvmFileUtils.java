package com.shareshow.aide.util.download;

import android.os.Environment;

import com.liulishuo.filedownloader.BaseDownloadTask;
import com.liulishuo.filedownloader.FileDownloadListener;
import com.liulishuo.filedownloader.FileDownloader;
import com.shareshow.App;
import com.shareshow.aide.event.DownloadRefurbish;
import com.shareshow.aide.retrofit.entity.StudyMaterialsVisitingMaterials;
import com.shareshow.aide.util.CacheUserInfo;
import com.shareshow.db.GreenDaoManager;
import com.socks.library.KLog;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by xiongchengguang on 2018/3/27.
 */

public class SmvmFileUtils implements FileDownloadHelper.OnDownloadListener {

    private String SRC_PATH;

    public SmvmFileUtils(){
        SRC_PATH = Environment
                .getExternalStorageDirectory()
                + File.separator
                + App.getApp().getPackageName()
                + File.separator
                + CacheUserInfo.get().getUserPhone()
                + File.separator
                + "smvm";
    }

    public static SmvmFileUtils get() {
        return SmvmHolder.holder;
    }

    private static class SmvmHolder {
        private static SmvmFileUtils holder = new SmvmFileUtils();
    }

    public void download(List<StudyMaterialsVisitingMaterials> listSmvm){
        List<BaseDownloadTask> taskList = new ArrayList<>();
        for (StudyMaterialsVisitingMaterials smvm : listSmvm){
            KLog.d("文件名称:" + smvm.getSfFilename() + "   文件路径:" + smvm.getSfUrl());
            String url = smvm.getSfUrl();
            BaseDownloadTask task = FileDownloader.getImpl().create(url)
                    .setTag(smvm)
                    .setCallbackProgressTimes(0)
                    .setPath(SRC_PATH + File.separator + smvm.getSfFilename());
            taskList.add(task);
        }
        FileDownloadHelper.get().setOnDownloadListener(this);
        FileDownloadHelper.get().downloadSequentially(taskList);
    }

    @Override
    public void onDownloadComplete(BaseDownloadTask task) {
        StudyMaterialsVisitingMaterials smvm = (StudyMaterialsVisitingMaterials) task.getTag();
        File file = new File(task.getPath());
        smvm.setDate(file.lastModified());
        smvm.setLocalPath(file.getPath());
        if (GreenDaoManager.getDaoSession().getStudyMaterialsVisitingMaterialsDao().load(smvm.getSfId()) == null) {
            KLog.d("添加新数据");
            GreenDaoManager.getDaoSession().getStudyMaterialsVisitingMaterialsDao().insert(smvm);
            EventBus.getDefault().post(new DownloadRefurbish(DownloadRefurbish.UPDATE));
        }
    }

    @Override
    public void onDownloadTaskError(BaseDownloadTask task, Throwable e){
        StudyMaterialsVisitingMaterials smvm = (StudyMaterialsVisitingMaterials) task.getTag();
        KLog.d("下载失败:" + smvm.getSfFilename()+"  地址:"+smvm.getSfUrl());
    }

    @Override
    public void onDownloadProgress(BaseDownloadTask task, int soFarBytes, int totalBytes) {

    }

    @Override
    public void onDownloadTaskOver() {
        EventBus.getDefault().post(new DownloadRefurbish(DownloadRefurbish.OVER));
    }


    public static void deleteDatabaseAndFile(StudyMaterialsVisitingMaterials smvm) {
        GreenDaoManager.getDaoSession().getStudyMaterialsVisitingMaterialsDao().delete(smvm);
        File file = new File(smvm.getLocalPath());
        boolean del = file.delete();
        KLog.d(smvm.getSfFilename() + "删除:" + del);
        if (del) {
            EventBus.getDefault().post(new DownloadRefurbish(DownloadRefurbish.UPDATE));
        }
    }
}

package com.shareshow.aide.util.download;

import com.liulishuo.filedownloader.BaseDownloadTask;
import com.liulishuo.filedownloader.FileDownloadListener;
import com.liulishuo.filedownloader.FileDownloadQueueSet;
import com.liulishuo.filedownloader.FileDownloader;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by xiongchengguang on 2018/3/27.
 */

public class FileDownloadHelper {
    private static final String TAG = FileDownloadHelper.class.getSimpleName();
    private static FileDownloadHelper mHelper;
    private OnDownloadListener mListener;
    private FileDownloadListener mDownloadListener;
    private FileDownloadQueueSet mQueueSet;

    private List<BaseDownloadTask> mAllDownloadTasks;

    private FileDownloadHelper() {
        mAllDownloadTasks = Collections.synchronizedList(new LinkedList<BaseDownloadTask>());
    }

    public static FileDownloadHelper get(){
        if (mHelper == null) {
            synchronized (FileDownloadHelper.class) {
                if (mHelper == null) {
                    mHelper = new FileDownloadHelper();
                }
            }
        }
        return mHelper;
    }

    private void initDownloadListener(){
        mDownloadListener = new FileDownloadListener() {
            @Override
            protected void pending(BaseDownloadTask task, int soFarBytes, int totalBytes) {
                // 之所以加这句判断，是因为有些异步任务在pause以后，会持续回调pause回来，而有些任务在pause之前已经完成，
                // 但是通知消息还在线程池中还未回调回来，这里可以优化
                // 后面所有在回调中加这句都是这个原因
                if (task.getListener() != mDownloadListener) {
                    return;
                }
            }

            @Override
            protected void connected(BaseDownloadTask task, String etag, boolean isContinue,
                                     int soFarBytes, int totalBytes) {
                super.connected(task, etag, isContinue, soFarBytes, totalBytes);
                if (task.getListener() != mDownloadListener) {
                    return;
                }
            }

            @Override
            protected void progress(BaseDownloadTask task, int soFarBytes, int totalBytes) {
                if (task.getListener() != mDownloadListener) {
                    return;
                }

                if (mListener != null) {
                    mListener.onDownloadProgress(task, soFarBytes, totalBytes);
                }
            }

            @Override
            protected void blockComplete(BaseDownloadTask task) {
                if (task.getListener() != mDownloadListener) {
                    return;
                }
            }

            @Override
            protected void retry(BaseDownloadTask task, Throwable ex, int retryingTimes, int soFarBytes) {
                super.retry(task, ex, retryingTimes, soFarBytes);
                if (task.getListener() != mDownloadListener) {
                    return;
                }
            }

            @Override
            protected void completed(BaseDownloadTask task) {
                if (task.getListener() != mDownloadListener) {
                    return;
                }
                if (mListener != null) {
                    mListener.onDownloadComplete(task);
                }

            }

            @Override
            protected void paused(BaseDownloadTask task, int soFarBytes, int totalBytes) {
                if (task.getListener() != mDownloadListener) {
                    return;
                }
            }

            @Override
            protected void error(BaseDownloadTask task, Throwable e) {
                if (task.getListener() != mDownloadListener) {
                    return;
                }

                if (mListener != null) {
                    mListener.onDownloadTaskError(task, e);
                }
            }

            @Override
            protected void warn(BaseDownloadTask task) {
                if (task.getListener() != mDownloadListener) {
                    return;
                }
            }
        };
    }

    private void initQueueSet() {
        mQueueSet = new FileDownloadQueueSet(mDownloadListener);
        mQueueSet.disableCallbackProgressTimes();
        //重试10次
        mQueueSet.setAutoRetryTimes(10);
        mQueueSet.addTaskFinishListener(new BaseDownloadTask.FinishListener() {
            @Override
            public void over(BaseDownloadTask task) {
                mAllDownloadTasks.remove(task);
                if (mAllDownloadTasks.isEmpty()) {
                    if (mListener != null) {
                        mListener.onDownloadTaskOver();
                    }
                }
            }
        });
    }

    /**
     * 自定义一些设置的downloadQueueSet
     *
     * @param fileDownloadQueueSet
     */
    public void setFileDownloadQueueSet(FileDownloadQueueSet fileDownloadQueueSet) {
        mQueueSet = fileDownloadQueueSet;
    }

    public void downloadTogether(List<BaseDownloadTask> tasks){
        //暂停之前的下载任务
        if (mDownloadListener != null){
            FileDownloader.getImpl().pause(mDownloadListener);
        }
        initDownloadListener();
        initQueueSet();

        mAllDownloadTasks.addAll(tasks);
        mQueueSet.downloadTogether(mAllDownloadTasks);
        mQueueSet.start();
    }


    //暂停所有的下载任务
    public void pauseAllDownload() {
        FileDownloader.getImpl().pauseAll();
    }

    public void downloadSequentially(List<BaseDownloadTask> tasks) {
        if (mDownloadListener != null) {
            FileDownloader.getImpl().pause(mDownloadListener);
        }
        initDownloadListener();
        initQueueSet();

        mAllDownloadTasks.addAll(tasks);
        mQueueSet.downloadSequentially(mAllDownloadTasks);
        mQueueSet.start();
    }

    public void downloadSequentially(BaseDownloadTask task) {
        if (mDownloadListener != null) {
            FileDownloader.getImpl().pause(mDownloadListener);
        }
        initDownloadListener();
        initQueueSet();

        mAllDownloadTasks.add(0, task);
        mQueueSet.downloadSequentially(mAllDownloadTasks);
        mQueueSet.start();
    }

    public void setOnDownloadListener(OnDownloadListener listener) {
        mListener = listener;
    }

    public interface OnDownloadListener {
        //在下载线程
        void onDownloadComplete(BaseDownloadTask task);

        void onDownloadTaskError(BaseDownloadTask task, Throwable e);

        void onDownloadProgress(BaseDownloadTask task, int soFarBytes, int totalBytes);

        //所有下载任务完成
        void onDownloadTaskOver();
    }
}

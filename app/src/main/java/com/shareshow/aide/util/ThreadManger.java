package com.shareshow.aide.util;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


/**
 * Created by xiongchengguang on 2017/12/5.
 */
public class ThreadManger {
    private ExecutorService threadPools;

    private ThreadManger() {
        threadPools = Executors.newCachedThreadPool();
    }

    static class ThreadHolder {
        private static ThreadManger holder = new ThreadManger();
    }

    public static ThreadManger getThreadPool() {
        return ThreadHolder.holder;
    }

    public ThreadManger execute(Runnable args) {
        threadPools.execute(args);
        return this;
    }

}

package com.shareshow.aide.util.uuid;

/**
 * UUID缓存类
 * Created by xiongchengguang on 2016/11/4.
 */

public interface CacheUuid {
    public void put(String key);

    public String get();
}

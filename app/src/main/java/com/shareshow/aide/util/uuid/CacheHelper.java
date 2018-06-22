package com.shareshow.aide.util.uuid;


import com.shareshow.aide.util.uuid.cache.AndroidFileCache;
import com.shareshow.aide.util.uuid.cache.DCIMFileCache;
import com.shareshow.aide.util.uuid.cache.SharePrefCache;

/**
 * Created by xiongchengguang on 2016/11/4.
 */

public class CacheHelper implements CacheUuid {
    private AndroidFileCache android = new AndroidFileCache();
    private DCIMFileCache dcim = new DCIMFileCache();
    private SharePrefCache share = new SharePrefCache();

    public void put(String key) {
        android.put(key);
        dcim.put(key);
        share.put(key);
    }

    public String get() {
        String id = share.get();
        if (id == null) {
            id = android.get();
            if (id == null) {
                id = dcim.get();
            }
        }
        return id;
    }
}

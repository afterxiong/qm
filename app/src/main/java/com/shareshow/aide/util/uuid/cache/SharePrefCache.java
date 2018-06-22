package com.shareshow.aide.util.uuid.cache;


import android.content.Context;
import android.content.SharedPreferences;

import com.shareshow.App;


/**
 * Created by xiongchengguang on 2016/11/4.
 */

public class SharePrefCache {
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    private final String DEVICES_UUID = "devices_uuid";

    public SharePrefCache() {
        preferences = App.getApp().getSharedPreferences(App.getApp().getPackageName(), Context.MODE_PRIVATE);
    }

    public void put(String key) {
    	editor = preferences.edit();
        editor.putString(DEVICES_UUID, key);
        editor.commit();
    }

    public String get() {
        return preferences.getString(DEVICES_UUID, null);
    }
}

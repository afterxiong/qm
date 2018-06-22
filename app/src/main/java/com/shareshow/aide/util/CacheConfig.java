package com.shareshow.aide.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.shareshow.App;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by xiongchengguang on 2017/12/5.
 */

public class CacheConfig {

    private SharedPreferences preferences;

    private SharedPreferences.Editor editor;


    private CacheConfig() {
        preferences = App.getApp().getSharedPreferences(App.getApp().getPackageName(), Context.MODE_PRIVATE);
        editor = preferences.edit();
    }

    public static CacheConfig get() {
        return CacheHolder.config;
    }

    public void setNextBinding(boolean nextBinding) {

    }

    private static class CacheHolder {
        private static CacheConfig config = new CacheConfig();
    }

    //是否已经绑定设备
    private static final String DEVICES_BINGING = "devices_binging";

    public boolean isBinding() {
        return preferences.getBoolean(DEVICES_BINGING, false);
    }

    public void setBinding(boolean mark) {
        editor.putBoolean(DEVICES_BINGING, mark);
        editor.commit();
    }

    private final static String signin = "signin";


    //false  显示签到   true 显示签退  有效时长  当前24小时前
    public boolean isSignin() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String today = signin + dateFormat.format(System.currentTimeMillis());
        return preferences.getBoolean(today, true);
    }

    public void setSignin(boolean bool) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String today = signin + dateFormat.format(System.currentTimeMillis());
        editor.putBoolean(today, bool);
        editor.commit();
    }

    private final static String affer_upload = "affer_upload";

    public void saveAfferUploadKey(long id) {
        editor.putLong(affer_upload, id);
        editor.commit();
    }

    public long getAfferUploadKey() {
        return preferences.getLong(affer_upload, 0);
    }

    private static final String hintOrg = "hintOrg";

    public void saveHintOrg(boolean b) {
        editor.putBoolean(hintOrg, b);
        editor.commit();
    }

    public boolean getHitorg() {
        return preferences.getBoolean(hintOrg, true);
    }

    private static final String hintDevice = "hintDevice";

    public void saveHintDevice(boolean b) {
        editor.putBoolean(hintDevice, b);
        editor.commit();
    }

    public boolean getHintDevice() {
        return preferences.getBoolean(hintDevice, true);
    }


    private static final String cacheName = "cacheName";

    public void saveCahceName(String name) {
        editor.putString(cacheName, name);
        editor.commit();
    }

    public String getCacheName() {
        return preferences.getString(cacheName, "");
    }

    private static final String ad_download_complete = "ad_download_complete";

    public boolean getAdDodnloadComplete() {
        return preferences.getBoolean(ad_download_complete, false);
    }

    public void setAdDodnloadComplete() {
        editor.putBoolean(ad_download_complete, true);
        editor.commit();
    }


    private static final String visit_track_id = "visit_track_id";


    public void saveBindDeviceName(String phone, String userId) {
        editor.putString("BIND_" + phone, userId);
        editor.commit();
    }

    public String getBindDeviceName(String phone) {
        return preferences.getString("BIND_" + phone, "");
    }

    private static String MORNING_STATE = "morning_state";

    public boolean getMorningState() {
        String morningTrack = preferences.getString(MORNING_STATE, null);
        if (morningTrack == null || TextUtils.isEmpty(morningTrack)) {
            return false;
        }
        String[] tracks = morningTrack.split("&");
        if (tracks.length < 3) {
            return false;
        }
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        if (CacheUserInfo.get().getUserPhone().equals(tracks[1]) && dateFormat.format(new Date(System.currentTimeMillis())).equals(tracks[0])) {
            return tracks[2].equals("true");
        } else {
            return false;
        }
    }

    public void saveMorningState(String date, boolean isSave) {
        String morningTrack = date + "&" + CacheUserInfo.get().getUserPhone() + "&" + isSave;
        editor.putString(MORNING_STATE, morningTrack);
        editor.commit();
    }


    private static String AD_RECEIVE_FINISH = "ad_receive_finish";

    public void saveAdCommplete(boolean isFinish) {
        editor.putBoolean(AD_RECEIVE_FINISH, isFinish);
        editor.commit();
    }


    public boolean getAdCommplete() {
        return preferences.getBoolean(AD_RECEIVE_FINISH, false);
    }


    //存储屏保广告的更新时间
    public String getAdScreenUpdateTime(String phone) {
        if (phone == null) {
            phone = "";
        }
        return preferences.getString("ScreenUpdateTime" + phone, "");
    }

    public void setAdScreenUpdateTime(String phone, String updateTime) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("ScreenUpdateTime" + phone, updateTime);
        editor.commit();
    }


    //存储开机广告的更新时间
    public String getAdBootUpdateTime(String phone) {
        if (phone == null) {
            phone = "";
        }
        return preferences.getString("BootUpdateTime" + phone, "");
    }

    public void setAdBootUpdateTime(String phone, String updateTime) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("BootUpdateTime" + phone, updateTime);
        editor.commit();
    }

    //存储热门广告的更新时间
    public String getAdHotUpdateTime(String phone) {
        if (phone == null) {
            phone = "";
        }
        return preferences.getString("HotUpdateTime" + phone, "");
    }

    public void setAdHotUpdateTime(String phone, String updateTime) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("HotUpdateTime" + phone, updateTime);
        editor.commit();
    }

    //存储官网的更新时间
    public String getAdNetUpdateTime(String phone) {
        if (phone == null) {
            phone = "";
        }
        return preferences.getString("NetUpdateTime" + phone, "");
    }

    public void setAdNetUpdateTime(String phone, String updateTime) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("NetUpdateTime" + phone, updateTime);
        editor.commit();
    }


    //存储官网的更新时间
    public int getPermission(String permission) {
        return preferences.getInt(permission, -1);
    }

    public void setPermission(String permission, int state) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(permission, state);
        editor.commit();
    }

    private static final String last_user_phone = "last_user_phone";

    public String getLastUserPhone() {
        return preferences.getString(last_user_phone, "");
    }

    public void setLastUserPhone(String phone) {
        editor.putString(last_user_phone, phone);
        editor.commit();
    }


}

package com.shareshow.aide.util;

import android.content.Context;
import android.content.SharedPreferences;

import com.shareshow.App;
import com.shareshow.aide.retrofit.entity.VisitData;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by xiongchengguang on 2018/4/3.
 */

public class VisitCacheData {

    private SharedPreferences preferences;

    private SharedPreferences.Editor editor;

    private VisitCacheData() {
        preferences = App.getApp().getSharedPreferences("visit_cache_data", Context.MODE_PRIVATE);
        editor = preferences.edit();
    }

    public static VisitCacheData get() {
        return VisitCacheData.CacheHolder.config;
    }

    private static class CacheHolder {
        private static VisitCacheData config = new VisitCacheData();
    }

    private final String TAG_PHOTO = "photo";


    private String tag_vrId = "vrId";
    private String tag_vrUrId = "vrUrId";
    private String tag_vrPhone = "vrPhone";
    private String tag_vrTimestart = "vrTimestart";
    private String tag_vrTimeend = "vrTimeend";
    private String tag_vrPlanid = "vrPlanid";
    private String tag_vrFileplaytime = "vrFileplaytime";
    private String tag_vrGuestname = "vrGuestname";
    private String tag_vrAddresss = "vrAddresss";
    private String tag_vrContent = "vrContent";
    private String tag_vrPicurls = "vrPicurls";
    private String tag_vrGps = "vrGps";
    private String tag_vrFlag = "vrFlag";
    private String tag_vrDate = "vrDate";
    private String tag_vrDel = "vrDel";
    private String tag_visit_path = "visit_path";

    public String getVrId() {
        return preferences.getString(tag_vrId, "");
    }

    public void setVrId(String vrId) {
        editor.putString(tag_vrId, vrId);
        editor.commit();
    }

    public String getVrUrId() {
        return preferences.getString(tag_vrUrId, "");
    }

    public void setVrUrId(String vrUrId) {
        editor.putString(tag_vrUrId, vrUrId);
        editor.commit();
    }

    public String getVrPhone() {
        return preferences.getString(tag_vrPhone, "");
    }

    public void setVrPhone(String vrPhone) {
        editor.putString(tag_vrPhone, vrPhone);
        editor.commit();
    }

    public String getVrTimestart() {
        return preferences.getString(tag_vrTimestart, "");
    }

    public void setVrTimestart(String vrTimestart) {
        editor.putString(tag_vrTimestart, vrTimestart);
        editor.commit();
    }

    public String getVrTimeend() {
        return preferences.getString(tag_vrTimeend, "");
    }

    public void setVrTimeend(String vrTimeend) {
        editor.putString(tag_vrTimeend, vrTimeend);
        editor.commit();
    }

    public String getVrPlanid() {
        return preferences.getString(tag_vrPlanid, "");
    }

    public void setVrPlanid(String vrPlanid) {
        editor.putString(tag_vrPlanid, vrPlanid);
        editor.commit();
    }

    public int getVrFileplaytime() {
        return preferences.getInt(tag_vrFileplaytime, 0);
    }

    public void setVrFileplaytime(int vrFileplaytime) {
        editor.putInt(tag_vrFileplaytime, vrFileplaytime);
        editor.commit();
    }

    public String getVrGuestname() {
        return preferences.getString(tag_vrGuestname, "");
    }

    public void setVrGuestname(String vrGuestname) {
        editor.putString(tag_vrGuestname, vrGuestname);
        editor.commit();
    }

    public String getVrAddresss() {
        return preferences.getString(tag_vrAddresss, "");
    }

    public void setVrAddresss(String vrAddresss) {
        editor.putString(tag_vrAddresss, vrAddresss);
        editor.commit();
    }

    public String getVrContent() {
        return preferences.getString(tag_vrContent, "");
    }

    public void setVrContent(String vrContent) {
        editor.putString(tag_vrContent, vrContent);
        editor.commit();
    }


    public List<String> getVrPicurls() {
        Set<String> set = preferences.getStringSet(tag_vrPicurls, null);
        if (set == null) {
            return null;
        }
        List<String> list = new ArrayList<>(set);
        return list;
    }

    public void setVrPicurls(List<String> list) {
        if (list != null) {
            Set set = new HashSet(list);
            editor.putStringSet(tag_vrPicurls, set);
            editor.commit();
        }
    }

    public String getVrGps() {
        return preferences.getString(tag_vrGps, "");
    }

    public void setVrGps(String vrGps) {
        editor.putString(tag_vrGps, vrGps);
        editor.commit();
    }

    public int getVrFlag() {
        return preferences.getInt(tag_vrFlag, 0);
    }

    public void setVrFlag(int vrFlag) {
        editor.putInt(tag_vrFlag, vrFlag);
        editor.commit();
    }

    public String getVrDate() {
        return preferences.getString(tag_vrDate, "");
    }

    public void setVrDate(String vrDate) {
        editor.putString(tag_vrDate, vrDate);
        editor.commit();
    }

    public int getVrDel() {
        return preferences.getInt(tag_vrDel, 0);
    }

    public void setVrDel(int vrDel) {
        editor.putInt(tag_vrDel, vrDel);
        editor.commit();
    }

    public String getVisitAudioPath() {
        return preferences.getString(tag_visit_path, "");
    }

    public void setVisitAudioPath(String path) {
        editor.putString(tag_visit_path, path);
        editor.commit();
    }

    private static final String TAG_LAST_DATE = "last_date";


    public String getLastDate() {
        return preferences.getString(TAG_LAST_DATE, "2018-03-01");
    }

    public void saveLastDate(String date) {
        editor.putString(TAG_LAST_DATE, date);
        editor.commit();
    }

    public void setVisitData(VisitData data) {
        setVrId(data.getVrId());
        setVrUrId(data.getVrUrId());
        setVrTimestart(data.getVrTimestart());
        setVrTimeend(data.getVrTimeend());
        setVrGuestname(data.getVrGuestname());
        setVrAddresss(data.getVrAddresss());
        setVrGuestname(data.getVrGps());
        setVrContent(data.getVrContent());
        setVrDate(data.getVrDate());
        setVrPicurls(data.getVrPicurls());
        setVisitAudioPath(data.getAudioPath());
    }

    public VisitData getVisitData() {
        VisitData data = new VisitData();
        data.setVrId(getVrId());
        data.setVrPhone(CacheUserInfo.get().getUserPhone());
        data.setVrUrId(getVrUrId());
        data.setVrTimestart(getVrTimestart());
        data.setVrTimeend(getVrTimeend());
        data.setVrGuestname(getVrGuestname());
        data.setVrAddresss(getVrAddresss());
        data.setVrGps(getVrGps());
        data.setVrContent(getVrContent());
        data.setVrDate(getVrDate());
        data.setVrPicurls(getVrPicurls());
        data.setAudioPath(getVisitAudioPath());
        return data;
    }

    public void clear() {
        editor.clear();
        editor.commit();
        File file = new File(getVisitAudioPath());
        file.delete();
    }

    private static final String isVisitWay = "isVisitFinish";

    /**
     * 拜访是否进行中
     * true 进行中  false  结束
     *
     * @return
     */
    public boolean isVisitWay() {
        return preferences.getBoolean(isVisitWay, false);
    }

    public void setVisitWay(boolean way) {
        editor.putBoolean(isVisitWay, way);
        editor.commit();
    }
}

package com.shareshow.aide.util;

import android.content.Context;
import android.content.SharedPreferences;

import com.shareshow.App;
import com.shareshow.aide.retrofit.entity.UserInfo;
import com.shareshow.airpc.util.DebugLog;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;

/**
 * 存储用户相关信息
 * Created by xiongchengguang on 2018/1/30.
 */

public class CacheUserInfo {
    private SharedPreferences preferences;

    private SharedPreferences.Editor editor;

    private CacheUserInfo() {
        preferences = App.getApp().getSharedPreferences("user_info_cache", Context.MODE_PRIVATE);
        editor = preferences.edit();
    }

    public static CacheUserInfo get() {
        return CacheUserInfo.CacheHolder.config;
    }

    private static class CacheHolder {
        private static CacheUserInfo config = new CacheUserInfo();
    }

    private static String tag_returnCode = "returnCode";
    private static String tag_message = "message";
    private static String tag_datas = "datas";
    private static String tag_status = "status";
    private static String tag_userId = "userId";
    private static String tag_userName = "userName";
    private static String tag_userPhone = "userPhone";
    private static String tag_orgId = "orgId";
    private static String tag_orgName = "orgName";
    private static String tag_deptId = "deptId";
    private static String tag_deptName = "deptName";
    private static String tag_teamId = "teamId";
    private static String tag_teamName = "teamName";
    private static String tag_teamCreaterId = "teamCreaterId";
    private static String tag_rejectReason = "rejectReason";
    private static String tag_urResponsible = "urResponsible";
    private static String tag_studyRecord = "studyRecord";


    public int getReturnCode() {
        return preferences.getInt(tag_returnCode, 0);
    }

    public void setReturnCode(int returnCode) {
        editor.putInt(tag_returnCode, returnCode);
        editor.commit();
    }

    public String getMessage() {
        return preferences.getString(tag_message, "");
    }

    public void setMessage(String message) {
        if (message == null) {
            message = "";
        }
        editor.putString(tag_message, message);
        editor.commit();
    }

    public String getUserId() {
        return preferences.getString(tag_userId, "");
    }

    public void setUserId(String userId) {
        if (userId == null) {
            userId = "";
        }
        editor.putString(tag_userId, userId);
        editor.commit();
    }

    public String getUserName() {
        return preferences.getString(tag_userName, "");
    }

    public void setUserName(String userName) {
        if (userName == null) {
            userName = "";
        }
        editor.putString(tag_userName, userName);
        editor.commit();
    }

    public String getUserPhone() {
        return preferences.getString(tag_userPhone, "error");
    }

    public void setUserPhone(String userPhone) {
        if (userPhone == null) {
            userPhone = "";
        }
        CacheConfig.get().setLastUserPhone(userPhone);
        editor.putString(tag_userPhone, userPhone);
        editor.commit();
    }


    public String getOrgId() {
        return preferences.getString(tag_orgId, "");
    }

    public void setOrgId(String orgId) {
        if (orgId == null) {
            orgId = "";
        }
        editor.putString(tag_orgId, orgId);
        editor.commit();
    }

    public String getOrgName() {
        return preferences.getString(tag_orgName, "");
    }

    public void setOrgName(String orgName) {
        if (orgName == null) {
            orgName = "";
        }
        editor.putString(tag_orgName, orgName);
        editor.commit();
    }

    public String getDeptId() {
        return preferences.getString(tag_deptId, "");
    }

    public void setDeptId(String deptId) {
        if (deptId == null) {
            deptId = "";
        }
        editor.putString(tag_deptId, deptId);
        editor.commit();
    }

    public String getDeptName() {
        return preferences.getString(tag_deptName, "");
    }

    public void setDeptName(String deptName) {
        if (deptName == null) {
            deptName = "";
        }
        editor.putString(tag_deptName, deptName);
        editor.commit();
    }

    public String getTeamId() {
        return preferences.getString(tag_teamId, "");
    }

    public void setTeamId(String teamId) {
        if (teamId == null) {
            teamId = "";
        }
        editor.putString(tag_teamId, teamId);
        editor.commit();
    }

    public String getTeamName() {
        return preferences.getString(tag_teamName, "");
    }

    public void setTeamName(String teamName) {
        if (teamName == null) {
            teamName = "";
        }
        editor.putString(tag_teamName, teamName);
        editor.commit();
    }

    public String getTeamCreaterId() {
        return preferences.getString(tag_teamCreaterId, "");
    }

    public void setTeamCreaterId(String teamCreaterId) {
        if (teamCreaterId == null) {
            teamCreaterId = "";
        }
        editor.putString(tag_teamCreaterId, teamCreaterId);
        editor.commit();
    }


    public String getRejectReason() {
        return preferences.getString(tag_rejectReason, "");
    }

    public void setRejectReason(String rejectReason) {
        if (rejectReason == null) {
            rejectReason = "";
        }
        editor.putString(tag_rejectReason, rejectReason);
        editor.commit();
    }

    /**
     * @return 返回1表示部门负责人
     */
    public int getResponsible() {
        return preferences.getInt(tag_urResponsible, -1);
    }

    public void setResponsible(int responsible) {
        editor.putInt(tag_urResponsible, responsible);
        editor.commit();
    }

    public int getStudyRecord() {
        String studyRecord = preferences.getString(tag_studyRecord, null);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String nowdate = dateFormat.format(System.currentTimeMillis());
        if (studyRecord != null) {
            try {
                JSONObject jsonObject = new JSONObject(studyRecord);
                String date = jsonObject.getString("date");
                if (date == null || !date.equals(nowdate)) {
                    return 0;
                } else {
                    return jsonObject.getInt("total");
                }
            } catch (JSONException e) {
                return 0;
            }
        } else {
            return 0;
        }
    }

    //    tag_studyRecord
    public void addStudyRecord() {
        String studyRecord = preferences.getString(tag_studyRecord, null);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String nowdate = dateFormat.format(System.currentTimeMillis());
        if (studyRecord == null) {
            try {
                JSONObject newJsonObject = new JSONObject();
                newJsonObject.put("date", nowdate);
                newJsonObject.put("total", 1);
                DebugLog.showLog(this, "保存学习记录:" + newJsonObject.toString());
                editor.putString(tag_studyRecord, newJsonObject.toString());
                editor.commit();
            } catch (JSONException e) {
                return;
            }
        } else {
            try {
                JSONObject jsonObject = new JSONObject(studyRecord);
                String date = jsonObject.getString("date");
                if (date == null || !date.equals(nowdate)) {
                    JSONObject newJsonObject = new JSONObject();
                    newJsonObject.put("date", nowdate);
                    newJsonObject.put("total", 1);
                    DebugLog.showLog(this, "保存学习记录:" + newJsonObject.toString());
                    editor.putString(tag_studyRecord, newJsonObject.toString());
                    editor.commit();
                } else {
                    JSONObject newJsonObject = new JSONObject();
                    newJsonObject.put("date", nowdate);
                    newJsonObject.put("total", jsonObject.getInt("total") + 1);
                    editor.putString(tag_studyRecord, newJsonObject.toString());
                    editor.commit();
                }
            } catch (JSONException e) {
                return;
            }
        }
    }


    public void setUserInfo(UserInfo userInfo) {
        if (userInfo != null) {
            setReturnCode(userInfo.getReturnCode());
            setMessage(userInfo.getMessage());
            setUserId(userInfo.getData().getUserId());
            setUserName(userInfo.getData().getUserName());
            setUserPhone(userInfo.getData().getUserPhone());
            setOrgId(userInfo.getData().getOrgId());
            setOrgName(userInfo.getData().getOrgName());
            setDeptId(userInfo.getData().getDeptId());
            setDeptName(userInfo.getData().getDeptName());
            setTeamId(userInfo.getData().getTeamId());
            setTeamName(userInfo.getData().getTeamName());
            setTeamCreaterId(userInfo.getData().getTeamCreaterId());
            setRejectReason(userInfo.getData().getRejectReason());
            setResponsible(userInfo.getData().getUrResponsible());
        }
    }

    public UserInfo getUserInfo() {
        UserInfo userInfo = new UserInfo();
        userInfo.setReturnCode(getReturnCode());
        userInfo.setMessage(getMessage());
        UserInfo.Data data = new UserInfo.Data();
        data.setUserId(getUserId());
        data.setUserName(getUserName());
        data.setUserPhone(getUserPhone());
        data.setOrgId(getOrgId());
        data.setOrgName(getOrgName());
        data.setDeptId(getDeptId());
        data.setDeptName(getDeptName());
        data.setTeamId(getTeamId());
        data.setTeamName(getTeamName());
        data.setTeamCreaterId(getTeamCreaterId());
        data.setRejectReason(getRejectReason());
        data.setUrResponsible(getResponsible());
        userInfo.setData(data);
        return userInfo;
    }


    public boolean getUserTeanCreate() {
        String userId = getUserId();
        String teamCeateId = getTeamCreaterId();
        if (userId.isEmpty() || teamCeateId.isEmpty()) {
            return false;
        }
        return userId.equals(teamCeateId);
    }

    public void clear() {
        editor.clear();
        editor.commit();
    }

}

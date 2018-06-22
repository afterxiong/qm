package com.shareshow.aide.retrofit.entity;

import java.util.List;

/**
 * Created by xiongchengguang on 2018/1/26.
 */

public class Team {
    private int returnCode;
    private String message;
    private String data;
    private List<TeamMember> datas;
    private String status;

    public int getReturnCode() {
        return returnCode;
    }

    public void setReturnCode(int returnCode) {
        this.returnCode = returnCode;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public List<TeamMember> getDatas() {
        return datas;
    }

    public void setDatas(List<TeamMember> datas) {
        this.datas = datas;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public static class TeamMember {
        private String userId;
        private String name;
        private String phone;

        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getPhone() {
            return phone;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }
    }

}

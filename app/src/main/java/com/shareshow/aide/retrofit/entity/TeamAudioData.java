package com.shareshow.aide.retrofit.entity;

import java.util.List;

/**
 * Created by FENGYANG on 2018/1/25.
 */

public class TeamAudioData {

    /**
     * returnCode : 1
     * message : 查询成功
     * data : null
     * datas : [{"chmUserid":"46b9effb-0e46-458f-a908-52eee0cf070b","chmPhone":"13971187810","chmDay":"2018-03-05","chmTime":"2018-03-05 09:57:08","chmFilename":"冯洋","chmFilepath":"http://172.16.21.159:8080/Resource/data/CheckMorningVoice/2018-03-05/5e2f63d6-5ba2-434e-afed-2cf18d799777.aac","chmAudiotime":"00:00:28","chmDel":0}]
     * status : true
     */

    private int returnCode;
    private String message;
    private Object data;
    private boolean status;
    private List<DatasBean> datas;

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

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public List<DatasBean> getDatas() {
        return datas;
    }

    public void setDatas(List<DatasBean> datas) {
        this.datas = datas;
    }

    public static class DatasBean {
        /**
         * chmUserid : 46b9effb-0e46-458f-a908-52eee0cf070b
         * chmPhone : 13971187810
         * chmDay : 2018-03-05
         * chmTime : 2018-03-05 09:57:08
         * chmFilename : 冯洋
         * chmFilepath : http://172.16.21.159:8080/Resource/data/CheckMorningVoice/2018-03-05/5e2f63d6-5ba2-434e-afed-2cf18d799777.aac
         * chmAudiotime : 00:00:28
         * chmDel : 0
         */
        private String chmId;
        private String chmUserid;
        private String chmPhone;
        private String chmDay;
        private String chmTime;
        private String chmFilename;
        private String chmFilepath;
        private String chmAudiotime;
        private int chmDel;

        public String getChmUserid() {
            return chmUserid;
        }

        public void setChmUserid(String chmUserid) {
            this.chmUserid = chmUserid;
        }

        public String getChmPhone() {
            return chmPhone;
        }

        public void setChmPhone(String chmPhone) {
            this.chmPhone = chmPhone;
        }

        public String getChmDay() {
            return chmDay;
        }

        public void setChmDay(String chmDay) {
            this.chmDay = chmDay;
        }

        public String getChmTime() {
            return chmTime;
        }

        public void setChmTime(String chmTime) {
            this.chmTime = chmTime;
        }

        public String getChmFilename() {
            return chmFilename;
        }

        public void setChmFilename(String chmFilename) {
            this.chmFilename = chmFilename;
        }

        public String getChmFilepath() {
            return chmFilepath;
        }

        public void setChmFilepath(String chmFilepath) {
            this.chmFilepath = chmFilepath;
        }

        public String getChmAudiotime() {
            return chmAudiotime;
        }

        public void setChmAudiotime(String chmAudiotime) {
            this.chmAudiotime = chmAudiotime;
        }

        public int getChmDel() {
            return chmDel;
        }

        public void setChmDel(int chmDel) {
            this.chmDel = chmDel;
        }

        public String getChmId() {
            return chmId;
        }

        public void setChmId(String chmId) {
            this.chmId = chmId;
        }

        @Override
        public String toString() {
            return "DeptMemberInfo{" +
                    "chmId='" + chmId + '\'' +
                    ", chmUserid='" + chmUserid + '\'' +
                    ", chmPhone='" + chmPhone + '\'' +
                    ", chmDay='" + chmDay + '\'' +
                    ", chmTime='" + chmTime + '\'' +
                    ", chmFilename='" + chmFilename + '\'' +
                    ", chmFilepath='" + chmFilepath + '\'' +
                    ", chmAudiotime='" + chmAudiotime + '\'' +
                    ", chmDel=" + chmDel +
                    '}';
        }
    }
}

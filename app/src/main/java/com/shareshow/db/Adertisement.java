package com.shareshow.db;

import java.util.List;

/**
 * Created by TEST on 2017/12/19.
 */


public class Adertisement {


    /**
     * list : [{"adcAdid":"5c84b0b2-5eb9-45e3-ad6a-35f6e9ead3f6","adcAdschemeId":"b02cb0f6-9cac-4dff-898b-13064aeafcf1","adcOrgid":"6b74a02d-e719-43c1-a46e-5af74abd4dea","adcType":2,"adcUpdatetime":"2017-12-18 19:46:10.0","adcStarttime":"2017-12-06","adcEndtime":"2017-12-28","adcPicurl":null,"adcAdschemeIndex":1,"adcIndex":1,"adcHoturl":null,"adcVideourl":"http://172.16.21.30:8080/Resource/data/advertising/6b74a02d-e719-43c1-a46e-5af74abd4dea/2017-12-13/db4ce4fd-e05e-43a8-a0db-2fcb5742b75c.mp4","adcVideoflag":1,"adcDel":0}]
     * timeWait : 300
     * timeInterval : 6
     */

    private int timeWait;  //等待时间
    private int timeInterval;  //广告的间隔时间
    private Long updateTime;   //广告更新的时间
    private List<ListBean> list;  //广告的数据

    public int getTimeWait() {
        return timeWait;
    }

    public void setTimeWait(int timeWait) {
        this.timeWait = timeWait;
    }

    public int getTimeInterval(){
        return timeInterval;
    }

    public void setTimeInterval(int timeInterval) {
        this.timeInterval = timeInterval;
    }

    public List<ListBean> getList() {
        return list;
    }

    public void setList(List<ListBean> list) {
        this.list = list;
    }

    public Long getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Long updateTime) {
        this.updateTime = updateTime;
    }

    @Override
    public String toString() {
        return "Adertisement{" +
                "timeWait=" + timeWait +
                ", timeInterval=" + timeInterval +
                ", updateTime='" + updateTime + '\'' +
                ", list=" + list +
                '}';
    }

    public static class ListBean {
        /**
         * adcAdid : 5c84b0b2-5eb9-45e3-ad6a-35f6e9ead3f6
         * adcAdschemeId : b02cb0f6-9cac-4dff-898b-13064aeafcf1
         * adcOrgid : 6b74a02d-e719-43c1-a46e-5af74abd4dea
         * adcType : 2
         * adcUpdatetime : 2017-12-18 19:46:10.0
         * adcStarttime : 2017-12-06
         * adcEndtime : 2017-12-28
         * adcPicurl : null
         * adcAdschemeIndex : 1
         * adcIndex : 1
         * adcHoturl : null
         * adcVideourl : http://172.16.21.30:8080/Resource/data/advertising/6b74a02d-e719-43c1-a46e-5af74abd4dea/2017-12-13/db4ce4fd-e05e-43a8-a0db-2fcb5742b75c.mp4
         * adcVideoflag : 1
         * adcDel : 0
         */

        private String adcAdid;  //广告文件ID
        private String adcAdschemeId;  //广告方案ID
        private String adcOrgid;  //组织ID
        private int adcType;     //广告类型
        private String adcUpdatetime;
        private String adcStarttime;
        private String adcEndtime;
        private String adcPicurl;
        private int adcAdschemeIndex;
        private int adcIndex;
        private String adcAdsfId;

        public String getAdcAdsfId() {
            return adcAdsfId;
        }

        public void setAdcAdsfId(String adcAdsfId) {
            this.adcAdsfId = adcAdsfId;
        }

        private String adcHoturl;
        private String adcVideourl;
        private int adcVideoflag;
        private int adcDel;

        public String getAdcAdid() {
            return adcAdid;
        }

        public void setAdcAdid(String adcAdid) {
            this.adcAdid = adcAdid;
        }

        public String getAdcAdschemeId() {
            return adcAdschemeId;
        }

        public void setAdcAdschemeId(String adcAdschemeId) {
            this.adcAdschemeId = adcAdschemeId;
        }

        public String getAdcOrgid() {
            return adcOrgid;
        }

        public void setAdcOrgid(String adcOrgid) {
            this.adcOrgid = adcOrgid;
        }

        public int getAdcType() {
            return adcType;
        }

        public void setAdcType(int adcType) {
            this.adcType = adcType;
        }

        public String getAdcUpdatetime() {
            return adcUpdatetime;
        }

        public void setAdcUpdatetime(String adcUpdatetime) {
            this.adcUpdatetime = adcUpdatetime;
        }

        public String getAdcStarttime() {
            return adcStarttime;
        }

        public void setAdcStarttime(String adcStarttime) {
            this.adcStarttime = adcStarttime;
        }

        public String getAdcEndtime() {
            return adcEndtime;
        }

        public void setAdcEndtime(String adcEndtime) {
            this.adcEndtime = adcEndtime;
        }

        public String getAdcPicurl() {
            return adcPicurl;
        }

        public void setAdcPicurl(String adcPicurl) {
            this.adcPicurl = adcPicurl;
        }

        public int getAdcAdschemeIndex() {
            return adcAdschemeIndex;
        }

        public void setAdcAdschemeIndex(int adcAdschemeIndex) {
            this.adcAdschemeIndex = adcAdschemeIndex;
        }

        public int getAdcIndex() {
            return adcIndex;
        }

        public void setAdcIndex(int adcIndex) {
            this.adcIndex = adcIndex;
        }

        public String getAdcHoturl() {
            return adcHoturl;
        }

        public void setAdcHoturl(String adcHoturl) {
            this.adcHoturl = adcHoturl;
        }

        public String getAdcVideourl() {
            return adcVideourl;
        }

        public void setAdcVideourl(String adcVideourl) {
            this.adcVideourl = adcVideourl;
        }

        public int getAdcVideoflag() {
            return adcVideoflag;
        }

        public void setAdcVideoflag(int adcVideoflag) {
            this.adcVideoflag = adcVideoflag;
        }

        public int getAdcDel() {
            return adcDel;
        }

        public void setAdcDel(int adcDel) {
            this.adcDel = adcDel;
        }

        @Override
        public String toString() {
            return "ListBean{" +
                    "adcAdid='" + adcAdid + '\'' +
                    ", adcAdschemeId='" + adcAdschemeId + '\'' +
                    ", adcOrgid='" + adcOrgid + '\'' +
                    ", adcType=" + adcType +
                    ", adcUpdatetime='" + adcUpdatetime + '\'' +
                    ", adcStarttime='" + adcStarttime + '\'' +
                    ", adcEndtime='" + adcEndtime + '\'' +
                    ", adcPicurl='" + adcPicurl + '\'' +
                    ", adcAdschemeIndex=" + adcAdschemeIndex +
                    ", adcIndex=" + adcIndex +
                    ", adcAdsfId='" + adcAdsfId + '\'' +
                    ", adcHoturl='" + adcHoturl + '\'' +
                    ", adcVideourl='" + adcVideourl + '\'' +
                    ", adcVideoflag=" + adcVideoflag +
                    ", adcDel=" + adcDel +
                    '}';
        }
    }
}

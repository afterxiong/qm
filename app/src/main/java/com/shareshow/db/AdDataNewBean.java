package com.shareshow.db;

import java.util.List;

/**
 * Created by wulong on 2017/9/25 0025.
 */

public class AdDataNewBean {

    /**
     * list : [{"adcAdid":"15364ea4-bb87-4bff-930e-8a1b9ee5a018","adcAdschemeId":"dcf801de-4008-478b-8ea8-92c527cb129a","adcType":2,"adcUpdatetime":"2017-09-25 14:23:25.0","adcStarttime":"2017-09-25","adcEndtime":"2017-09-25","adcPicurl":null,"adcIndex":4,"adcHoturl":null,"adcVideourl":null,"adcVideoflag":0,"adcDel":0},{"adcAdid":"2d417050-aa89-46ac-8ee6-14fb71e4bf38","adcAdschemeId":"dcf801de-4008-478b-8ea8-92c527cb129a","adcType":2,"adcUpdatetime":"2017-09-25 14:23:25.0","adcStarttime":"2017-09-25","adcEndtime":"2017-09-25","adcPicurl":null,"adcIndex":8,"adcHoturl":null,"adcVideourl":null,"adcVideoflag":0,"adcDel":0},{"adcAdid":"2d417050-aa89-46ac-8ee6-14fb71e4bf38","adcAdschemeId":"dcf801de-4008-478b-8ea8-92c527cb129a","adcType":2,"adcUpdatetime":"2017-09-25 14:23:25.0","adcStarttime":"2017-09-25","adcEndtime":"2017-09-25","adcPicurl":null,"adcIndex":8,"adcHoturl":null,"adcVideourl":null,"adcVideoflag":0,"adcDel":0}]
     * timeWait : 300
     * timeInterval : 5
     */

    private int timeWait;
    private int timeInterval;
    private List<ListBean> list;

    public int getTimeWait() {
        return timeWait;
    }

    public int getTimeInterval() {
        return timeInterval;
    }

    public List<ListBean> getList() {
        return list;
    }

    public static class ListBean {
        /**
         * adcAdid : 15364ea4-bb87-4bff-930e-8a1b9ee5a018
         * adcAdschemeId : dcf801de-4008-478b-8ea8-92c527cb129a
         * adcType : 2
         * adcUpdatetime : 2017-09-25 14:23:25.0
         * adcStarttime : 2017-09-25
         * adcEndtime : 2017-09-25
         * adcPicurl : null
         * adcIndex : 4
         * adcHoturl : null
         * adcVideourl : null
         * adcVideoflag : 0
         * adcDel : 0
         */

        private String adcAdid;
        private String adcAdschemeId;
        private int adcType;
        private String adcUpdatetime;
        private String adcStarttime;
        private String adcEndtime;
        private String adcPicurl;
        private int adcIndex;
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
            return adcPicurl == null?"":adcPicurl;
        }

        public void setAdcPicurl(String adcPicurl) {
            this.adcPicurl = adcPicurl;
        }

        public int getAdcIndex() {
            return adcIndex;
        }

        public void setAdcIndex(int adcIndex) {
            this.adcIndex = adcIndex;
        }

        public String getAdcHoturl() {
            return adcHoturl == null?"":adcHoturl;
        }

        public void setAdcHoturl(String adcHoturl) {
            this.adcHoturl = adcHoturl;
        }

        public String getAdcVideourl() {
            return adcVideourl == null?"":adcVideourl;
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
    }
}

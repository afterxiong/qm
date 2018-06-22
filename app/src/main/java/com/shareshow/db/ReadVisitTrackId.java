package com.shareshow.db;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Property;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Created by xiongchengguang on 2018/2/1.
 */

@Entity
public class ReadVisitTrackId {
    @Property
    public String visitId;

    public String getVisitId() {
        return this.visitId;
    }

    public void setVisitId(String visitId) {
        this.visitId = visitId;
    }

    @Generated(hash = 400833786)
    public ReadVisitTrackId(String visitId) {
        this.visitId = visitId;
    }

    @Generated(hash = 1050682186)
    public ReadVisitTrackId() {
    }




}

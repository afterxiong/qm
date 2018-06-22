package com.shareshow.aide.retrofit.entity;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Property;
import org.greenrobot.greendao.annotation.Unique;

/**
 * Created by xiongchengguang on 2017/12/16.
 */

@Entity
public class OrgAndDept {
    @Unique
    private String giGroupid;

    @Property
    private String giParentgroupid;

    @Property
    private String giGroupname;
    @Property
    private String giLevel;
    @Property
    private String giType;

    @Generated(hash = 1294619205)
    public OrgAndDept(String giGroupid, String giParentgroupid, String giGroupname,
                      String giLevel, String giType) {
        this.giGroupid = giGroupid;
        this.giParentgroupid = giParentgroupid;
        this.giGroupname = giGroupname;
        this.giLevel = giLevel;
        this.giType = giType;
    }

    @Generated(hash = 1144176710)
    public OrgAndDept() {
    }

    public String getGiGroupid() {
        return giGroupid;
    }

    public void setGiGroupid(String giGroupid) {
        this.giGroupid = giGroupid;
    }

    public String getGiParentgroupid() {
        return giParentgroupid;
    }

    public void setGiParentgroupid(String giParentgroupid) {
        this.giParentgroupid = giParentgroupid;
    }

    public String getGiGroupname() {
        return giGroupname;
    }

    public void setGiGroupname(String giGroupname) {
        this.giGroupname = giGroupname;
    }

    public String getGiLevel() {
        return giLevel;
    }

    public void setGiLevel(String giLevel) {
        this.giLevel = giLevel;
    }

    public String getGiType() {
        return giType;
    }

    public void setGiType(String giType) {
        this.giType = giType;
    }

    
}

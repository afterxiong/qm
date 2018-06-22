package com.shareshow.db;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Property;

import java.io.Serializable;

/**
 * Created by TEST on 2017/12/27.
 */

@Entity
public class NotityBean implements Serializable{

    private static final long serialVersionUID = 1L;

    /**
     * nosId : e2ffa530-6077-49b3-af51-742f492e666c
     * nosUserid : 启目测试
     * nosOrgids : ["76de19c6-bac3-439f-9b1c-8aafc6a3c1ac"]
     * nosOrgnames : ["启目科技公司整体测试"]
     * nosTitle : null
     * nosContent : 拜访文件　1.jpg,2.jpg,12345643413132142324312342312.12.jpg　已经更新。
     * nosFilepath : null
     * nosFilename : ["1.jpg","2.jpg","12345643413132142324312342312.12.jpg"]
     * nosCreatetime : 1514540873000
     * nosType : null
     * nosSmstId : null
     * nosDel : 0
     */
    @Id
    private Long id;
    @Property
    private String nosId;
    @Property
    private String nosUserid;
    @Property
    private String nosOrgids;
    @Property
    private String nosOrgnames;
    @Property
    private String nosTitle;
    @Property
    private String nosContent;
    @Property
    private String nosFilepath;
    @Property
    private String nosFilename;
    @Property
    private long nosCreatetime;
    @Property
    private int nosType;
    @Property
    private String nosSmstId;
    @Property
    private int nosDel;
    @Property
    private boolean isOpen;

    @Generated(hash = 1676719809)
    public NotityBean(Long id, String nosId, String nosUserid, String nosOrgids,
            String nosOrgnames, String nosTitle, String nosContent,
            String nosFilepath, String nosFilename, long nosCreatetime,
            int nosType, String nosSmstId, int nosDel, boolean isOpen) {
        this.id = id;
        this.nosId = nosId;
        this.nosUserid = nosUserid;
        this.nosOrgids = nosOrgids;
        this.nosOrgnames = nosOrgnames;
        this.nosTitle = nosTitle;
        this.nosContent = nosContent;
        this.nosFilepath = nosFilepath;
        this.nosFilename = nosFilename;
        this.nosCreatetime = nosCreatetime;
        this.nosType = nosType;
        this.nosSmstId = nosSmstId;
        this.nosDel = nosDel;
        this.isOpen = isOpen;
    }

    @Generated(hash = 1010404554)
    public NotityBean() {
    }

    public String getNosId() {
        return nosId;
    }

    public void setNosId(String nosId) {
        this.nosId = nosId;
    }

    public String getNosUserid() {
        return nosUserid;
    }

    public void setNosUserid(String nosUserid) {
        this.nosUserid = nosUserid;
    }

    public String getNosOrgids() {
        return nosOrgids;
    }

    public void setNosOrgids(String nosOrgids) {
        this.nosOrgids = nosOrgids;
    }

    public String getNosOrgnames() {
        return nosOrgnames;
    }

    public void setNosOrgnames(String nosOrgnames) {
        this.nosOrgnames = nosOrgnames;
    }

    public String getNosTitle() {
        return nosTitle;
    }

    public void setNosTitle(String nosTitle) {
        this.nosTitle = nosTitle;
    }

    public String getNosContent() {
        return nosContent;
    }

    public void setNosContent(String nosContent) {
        this.nosContent = nosContent;
    }

    public String getNosFilepath() {
        return nosFilepath;
    }

    public void setNosFilepath(String nosFilepath) {
        this.nosFilepath = nosFilepath;
    }

    public String getNosFilename() {
        return nosFilename;
    }

    public void setNosFilename(String nosFilename) {
        this.nosFilename = nosFilename;
    }

    public long getNosCreatetime() {
        return nosCreatetime;
    }

    public void setNosCreatetime(long nosCreatetime) {
        this.nosCreatetime = nosCreatetime;
    }

    public int getNosType() {
        return nosType;
    }

    public void setNosType(int nosType) {
        this.nosType = nosType;
    }

    public String getNosSmstId() {
        return nosSmstId;
    }

    public void setNosSmstId(String nosSmstId) {
        this.nosSmstId = nosSmstId;
    }

    public int getNosDel() {
        return nosDel;
    }

    public void setNosDel(int nosDel) {
        this.nosDel = nosDel;
    }

    public boolean isOpen() {
        return isOpen;
    }

    public void setOpen(boolean open) {
        isOpen = open;
    }

    public boolean getIsOpen() {
        return this.isOpen;
    }

    public void setIsOpen(boolean isOpen) {
        this.isOpen = isOpen;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "NotityBean{" +
                "id=" + id +
                ", nosId='" + nosId + '\'' +
                ", nosUserid='" + nosUserid + '\'' +
                ", nosOrgids='" + nosOrgids + '\'' +
                ", nosOrgnames='" + nosOrgnames + '\'' +
                ", nosTitle='" + nosTitle + '\'' +
                ", nosContent='" + nosContent + '\'' +
                ", nosFilepath='" + nosFilepath + '\'' +
                ", nosFilename='" + nosFilename + '\'' +
                ", nosCreatetime=" + nosCreatetime +
                ", nosType=" + nosType +
                ", nosSmstId='" + nosSmstId + '\'' +
                ", nosDel=" + nosDel +
                ", isOpen=" + isOpen +
                '}';
    }
}

package com.shareshow.aide.retrofit.entity;

import android.os.Parcel;
import android.os.Parcelable;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

/**
 * 公告通知
 * Created by xiongchengguang on 2018/1/5.
 */

@Entity
public class AmplyNotify implements Parcelable {

    @Id
    private String nosId;
    private String nosUserid;
    private String nosOrgids;
    private String nosTitle;
    private String nosOrgnames;
    private String nosContent;
    private String nosFilepath;
    private String nosFilename;
    private String nosCreatetime;
    private String nosDel;
    private int nosTimelength;
    private String nosEndtime;

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

    public String getNosTitle() {
        return nosTitle;
    }

    public void setNosTitle(String nosTitle) {
        this.nosTitle = nosTitle;
    }

    public String getNosOrgnames() {
        return nosOrgnames;
    }

    public void setNosOrgnames(String nosOrgnames) {
        this.nosOrgnames = nosOrgnames;
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

    public String getNosCreatetime() {
        return nosCreatetime;
    }

    public void setNosCreatetime(String nosCreatetime) {
        this.nosCreatetime = nosCreatetime;
    }

    public String getNosDel() {
        return nosDel;
    }

    public void setNosDel(String nosDel) {
        this.nosDel = nosDel;
    }

    public int getNosTimelength() {
        return nosTimelength;
    }

    public void setNosTimelength(int nosTimelength) {
        this.nosTimelength = nosTimelength;
    }

    public String getNosEndtime() {
        return nosEndtime;
    }

    public void setNosEndtime(String nosEndtime) {
        this.nosEndtime = nosEndtime;
    }

    protected AmplyNotify(Parcel in) {
        nosId = in.readString();
        nosUserid = in.readString();
        nosOrgids = in.readString();
        nosTitle = in.readString();
        nosOrgnames = in.readString();
        nosContent = in.readString();
        nosFilepath = in.readString();
        nosFilename = in.readString();
        nosCreatetime = in.readString();
        nosDel = in.readString();
        nosTimelength = in.readInt();
        nosEndtime = in.readString();
    }

    @Generated(hash = 1573842163)
    public AmplyNotify(String nosId, String nosUserid, String nosOrgids,
            String nosTitle, String nosOrgnames, String nosContent,
            String nosFilepath, String nosFilename, String nosCreatetime,
            String nosDel, int nosTimelength, String nosEndtime) {
        this.nosId = nosId;
        this.nosUserid = nosUserid;
        this.nosOrgids = nosOrgids;
        this.nosTitle = nosTitle;
        this.nosOrgnames = nosOrgnames;
        this.nosContent = nosContent;
        this.nosFilepath = nosFilepath;
        this.nosFilename = nosFilename;
        this.nosCreatetime = nosCreatetime;
        this.nosDel = nosDel;
        this.nosTimelength = nosTimelength;
        this.nosEndtime = nosEndtime;
    }

    @Generated(hash = 2005756384)
    public AmplyNotify() {
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(nosId);
        dest.writeString(nosUserid);
        dest.writeString(nosOrgids);
        dest.writeString(nosTitle);
        dest.writeString(nosOrgnames);
        dest.writeString(nosContent);
        dest.writeString(nosFilepath);
        dest.writeString(nosFilename);
        dest.writeString(nosCreatetime);
        dest.writeString(nosDel);
        dest.writeInt(nosTimelength);
        dest.writeString(nosEndtime);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<AmplyNotify> CREATOR = new Creator<AmplyNotify>() {
        @Override
        public AmplyNotify createFromParcel(Parcel in) {
            return new AmplyNotify(in);
        }

        @Override
        public AmplyNotify[] newArray(int size) {
            return new AmplyNotify[size];
        }
    };
}



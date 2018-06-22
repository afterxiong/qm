package com.shareshow.aide.retrofit.entity;

import android.os.Parcel;
import android.os.Parcelable;

import org.greenrobot.greendao.annotation.Generated;

/**
 * 拜访资料和学习资料
 * Created by xiongchengguang on 2018/1/20.
 */

public class StudyAndVisit implements Parcelable {
    private String sfId;
    private String sfFilename;
    private String sfUrl;
    private String sfSpid;
    private String sfDel;
    private String sfType;
    private String localPath;

    public String getSfId() {
        return sfId;
    }

    public void setSfId(String sfId) {
        this.sfId = sfId;
    }

    public String getSfFilename() {
        return sfFilename;
    }

    public void setSfFilename(String sfFilename) {
        this.sfFilename = sfFilename;
    }

    public String getSfUrl() {
        return sfUrl;
    }

    public void setSfUrl(String sfUrl) {
        this.sfUrl = sfUrl;
    }

    public String getSfSpid() {
        return sfSpid;
    }

    public void setSfSpid(String sfSpid) {
        this.sfSpid = sfSpid;
    }

    public String getSfDel() {
        return sfDel;
    }

    public void setSfDel(String sfDel) {
        this.sfDel = sfDel;
    }

    public String getSfType() {
        return sfType;
    }

    public void setSfType(String sfType) {
        this.sfType = sfType;
    }

    public String getLocalPath() {
        return localPath;
    }

    public void setLocalPath(String localPath) {
        this.localPath = localPath;
    }

    protected StudyAndVisit(Parcel in) {
        sfId = in.readString();
        sfFilename = in.readString();
        sfUrl = in.readString();
        sfSpid = in.readString();
        sfDel = in.readString();
        sfType = in.readString();
        localPath = in.readString();
    }

    @Generated(hash = 1181218362)
    public StudyAndVisit(String sfId, String sfFilename, String sfUrl, String sfSpid,
            String sfDel, String sfType, String localPath) {
        this.sfId = sfId;
        this.sfFilename = sfFilename;
        this.sfUrl = sfUrl;
        this.sfSpid = sfSpid;
        this.sfDel = sfDel;
        this.sfType = sfType;
        this.localPath = localPath;
    }

    @Generated(hash = 1458103868)
    public StudyAndVisit() {
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(sfId);
        dest.writeString(sfFilename);
        dest.writeString(sfUrl);
        dest.writeString(sfSpid);
        dest.writeString(sfDel);
        dest.writeString(sfType);
        dest.writeString(localPath);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<StudyAndVisit> CREATOR = new Creator<StudyAndVisit>() {
        @Override
        public StudyAndVisit createFromParcel(Parcel in) {
            return new StudyAndVisit(in);
        }

        @Override
        public StudyAndVisit[] newArray(int size) {
            return new StudyAndVisit[size];
        }
    };
}

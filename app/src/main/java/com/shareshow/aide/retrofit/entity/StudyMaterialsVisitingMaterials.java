package com.shareshow.aide.retrofit.entity;

import android.os.Parcel;
import android.os.Parcelable;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Property;

/**
 * Created by xiongchengguang on 2018/3/27.
 */

@Entity
public class StudyMaterialsVisitingMaterials implements Parcelable {

    @Id
    private String sfId;
    @Property
    private String sfFilename;
    @Property
    private String sfUrl;
    @Property
    private String sfSpid;
    @Property
    private int sfDel;
    @Property
    private int sfType;
    @Property
    private String localPath;
    @Property
    private Long date;

    protected StudyMaterialsVisitingMaterials(Parcel in) {
        sfId = in.readString();
        sfFilename = in.readString();
        sfUrl = in.readString();
        sfSpid = in.readString();
        sfDel = in.readInt();
        sfType = in.readInt();
        localPath = in.readString();
        if (in.readByte() == 0) {
            date = null;
        } else {
            date = in.readLong();
        }
    }

    @Generated(hash = 312679838)
    public StudyMaterialsVisitingMaterials(String sfId, String sfFilename, String sfUrl, String sfSpid, int sfDel,
                                           int sfType, String localPath, Long date) {
        this.sfId = sfId;
        this.sfFilename = sfFilename;
        this.sfUrl = sfUrl;
        this.sfSpid = sfSpid;
        this.sfDel = sfDel;
        this.sfType = sfType;
        this.localPath = localPath;
        this.date = date;
    }

    @Generated(hash = 825749319)
    public StudyMaterialsVisitingMaterials() {
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(sfId);
        dest.writeString(sfFilename);
        dest.writeString(sfUrl);
        dest.writeString(sfSpid);
        dest.writeInt(sfDel);
        dest.writeInt(sfType);
        dest.writeString(localPath);
        if (date == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeLong(date);
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<StudyMaterialsVisitingMaterials> CREATOR = new Creator<StudyMaterialsVisitingMaterials>() {
        @Override
        public StudyMaterialsVisitingMaterials createFromParcel(Parcel in) {
            return new StudyMaterialsVisitingMaterials(in);
        }

        @Override
        public StudyMaterialsVisitingMaterials[] newArray(int size) {
            return new StudyMaterialsVisitingMaterials[size];
        }
    };

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

    public int getSfDel() {
        return sfDel;
    }

    public void setSfDel(int sfDel) {
        this.sfDel = sfDel;
    }

    public int getSfType() {
        return sfType;
    }

    public void setSfType(int sfType) {
        this.sfType = sfType;
    }

    public String getLocalPath() {
        return localPath;
    }

    public void setLocalPath(String localPath) {
        this.localPath = localPath;
    }

    public Long getDate() {
        return date;
    }

    public void setDate(Long date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return "StudyMaterialsVisitingMaterials{" +
                "sfId='" + sfId + '\'' +
                ", sfFilename='" + sfFilename + '\'' +
                ", sfUrl='" + sfUrl + '\'' +
                ", sfSpid='" + sfSpid + '\'' +
                ", sfDel=" + sfDel +
                ", sfType=" + sfType +
                ", localPath='" + localPath + '\'' +
                ", date=" + date +
                '}';
    }

    @Override
    public int hashCode() {
        return sfId.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this){
            return true;
        }
        if (!(obj instanceof StudyMaterialsVisitingMaterials)) {
            return false;
        }
        StudyMaterialsVisitingMaterials smvm = (StudyMaterialsVisitingMaterials) obj;
        return smvm.getSfId().equals(sfId);

    }
}

package com.shareshow.aide.retrofit.entity;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.shareshow.aide.util.StringConverter;

import org.greenrobot.greendao.annotation.Convert;
import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;

import java.util.List;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Created by xiongchengguang on 2018/4/3.
 */

@Entity
public class VisitData implements Parcelable, Comparable<VisitData> {
    @Id
    private String vrId;
    private String vrUrId;
    private String vrPhone;
    private String vrTimestart;
    private String vrTimeend;
    private String vrPlanid;
    private int vrFileplaytime;
    private String vrGuestname;
    private String vrAddresss;
    private String vrContent;
    private String vrGps;
    private int vrFlag;
    private String vrDate;
    private int vrDel;
    @Convert(columnType = String.class, converter = StringConverter.class)
    private List<String> vrPicurls;
    private String audioPath;

    public VisitData() {
    }

    public String getVrId() {
        return vrId;
    }

    public void setVrId(String vrId) {
        this.vrId = vrId;
    }

    public String getVrUrId() {
        return vrUrId;
    }

    public void setVrUrId(String vrUrId) {
        this.vrUrId = vrUrId;
    }

    public String getVrPhone() {
        return vrPhone;
    }

    public void setVrPhone(String vrPhone) {
        this.vrPhone = vrPhone;
    }

    public String getVrTimestart() {
        return vrTimestart;
    }

    public void setVrTimestart(String vrTimestart) {
        this.vrTimestart = vrTimestart;
    }

    public String getVrTimeend() {
        return vrTimeend;
    }

    public void setVrTimeend(String vrTimeend) {
        this.vrTimeend = vrTimeend;
    }

    public String getVrPlanid() {
        return vrPlanid;
    }

    public void setVrPlanid(String vrPlanid) {
        this.vrPlanid = vrPlanid;
    }

    public int getVrFileplaytime() {
        return vrFileplaytime;
    }

    public void setVrFileplaytime(int vrFileplaytime) {
        this.vrFileplaytime = vrFileplaytime;
    }

    public String getVrGuestname() {
        return vrGuestname;
    }

    public void setVrGuestname(String vrGuestname) {
        this.vrGuestname = vrGuestname;
    }

    public String getVrAddresss() {
        return vrAddresss;
    }

    public void setVrAddresss(String vrAddresss) {
        this.vrAddresss = vrAddresss;
    }

    public String getVrContent() {
        return vrContent;
    }

    public void setVrContent(String vrContent) {
        this.vrContent = vrContent;
    }

    public String getVrGps() {
        return vrGps;
    }

    public void setVrGps(String vrGps) {
        this.vrGps = vrGps;
    }

    public int getVrFlag() {
        return vrFlag;
    }

    public void setVrFlag(int vrFlag) {
        this.vrFlag = vrFlag;
    }

    public String getVrDate() {
        return vrDate;
    }

    public void setVrDate(String vrDate) {
        this.vrDate = vrDate;
    }

    public int getVrDel() {
        return vrDel;
    }

    public void setVrDel(int vrDel) {
        this.vrDel = vrDel;
    }

    public List<String> getVrPicurls() {
        return vrPicurls;
    }

    public void setVrPicurls(List<String> vrPicurls) {
        this.vrPicurls = vrPicurls;
    }

    public String getAudioPath() {
        return audioPath;
    }

    public void setAudioPath(String audioPath) {
        this.audioPath = audioPath;
    }

    protected VisitData(Parcel in) {
        vrId = in.readString();
        vrUrId = in.readString();
        vrPhone = in.readString();
        vrTimestart = in.readString();
        vrTimeend = in.readString();
        vrPlanid = in.readString();
        vrFileplaytime = in.readInt();
        vrGuestname = in.readString();
        vrAddresss = in.readString();
        vrContent = in.readString();
        vrGps = in.readString();
        vrFlag = in.readInt();
        vrDate = in.readString();
        vrDel = in.readInt();
        vrPicurls = in.createStringArrayList();
        audioPath = in.readString();
    }

    @Generated(hash = 1699719400)
    public VisitData(String vrId, String vrUrId, String vrPhone,
            String vrTimestart, String vrTimeend, String vrPlanid,
            int vrFileplaytime, String vrGuestname, String vrAddresss,
            String vrContent, String vrGps, int vrFlag, String vrDate, int vrDel,
            List<String> vrPicurls, String audioPath) {
        this.vrId = vrId;
        this.vrUrId = vrUrId;
        this.vrPhone = vrPhone;
        this.vrTimestart = vrTimestart;
        this.vrTimeend = vrTimeend;
        this.vrPlanid = vrPlanid;
        this.vrFileplaytime = vrFileplaytime;
        this.vrGuestname = vrGuestname;
        this.vrAddresss = vrAddresss;
        this.vrContent = vrContent;
        this.vrGps = vrGps;
        this.vrFlag = vrFlag;
        this.vrDate = vrDate;
        this.vrDel = vrDel;
        this.vrPicurls = vrPicurls;
        this.audioPath = audioPath;
    }

    public static final Creator<VisitData> CREATOR = new Creator<VisitData>() {
        @Override
        public VisitData createFromParcel(Parcel in) {
            return new VisitData(in);
        }

        @Override
        public VisitData[] newArray(int size) {
            return new VisitData[size];
        }
    };

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        VisitData data = (VisitData) obj;
        return data.getVrId().equals(getVrId());
    }

    @Override
    public int compareTo(@NonNull VisitData o) {
        Integer t1 = Integer.parseInt(getVrId());
        Integer t2 = Integer.parseInt(o.getVrId());
        return t2 - t1;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(vrId);
        dest.writeString(vrUrId);
        dest.writeString(vrPhone);
        dest.writeString(vrTimestart);
        dest.writeString(vrTimeend);
        dest.writeString(vrPlanid);
        dest.writeInt(vrFileplaytime);
        dest.writeString(vrGuestname);
        dest.writeString(vrAddresss);
        dest.writeString(vrContent);
        dest.writeString(vrGps);
        dest.writeInt(vrFlag);
        dest.writeString(vrDate);
        dest.writeInt(vrDel);
        dest.writeStringList(vrPicurls);
        dest.writeString(audioPath);
    }
}

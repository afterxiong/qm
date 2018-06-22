package com.shareshow.aide.retrofit.entity;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by xiongchengguang on 2018/1/25.
 */

public class MyQrCode implements Parcelable{

    private int dt;
    private String ds;
    private int type;
    private String dn;
    private String phone;
    private String orgId;
    private String deptId;
    private String teamId;
    private String orgName;
    private String deptName;
    private String teamName;
    private String qrCodeUserId;

    public MyQrCode() {
    }

    public int getDt() {
        return dt;
    }

    public void setDt(int dt) {
        this.dt = dt;
    }

    public String getDs() {
        return ds;
    }

    public void setDs(String ds) {
        this.ds = ds;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getDn() {
        return dn;
    }

    public void setDn(String dn) {
        this.dn = dn;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getOrgId() {
        return orgId;
    }

    public void setOrgId(String orgId) {
        this.orgId = orgId;
    }

    public String getDeptId() {
        return deptId;
    }

    public void setDeptId(String deptId) {
        this.deptId = deptId;
    }

    public String getTeamId() {
        return teamId;
    }

    public void setTeamId(String teamId) {
        this.teamId = teamId;
    }

    public String getOrgName() {
        return orgName;
    }

    public void setOrgName(String orgName) {
        this.orgName = orgName;
    }

    public String getDeptName() {
        return deptName;
    }

    public void setDeptName(String deptName) {
        this.deptName = deptName;
    }

    public String getTeamName() {
        return teamName;
    }

    public void setTeamName(String teamName) {
        this.teamName = teamName;
    }

    public String getQrCodeUserId() {
        return qrCodeUserId;
    }

    public void setQrCodeUserId(String qrCodeUserId) {
        this.qrCodeUserId = qrCodeUserId;
    }

    protected MyQrCode(Parcel in) {
        dt = in.readInt();
        ds = in.readString();
        type = in.readInt();
        dn = in.readString();
        phone = in.readString();
        orgId = in.readString();
        deptId = in.readString();
        teamId = in.readString();
        orgName = in.readString();
        deptName = in.readString();
        teamName = in.readString();
        qrCodeUserId = in.readString();
    }

    public static final Creator<MyQrCode> CREATOR = new Creator<MyQrCode>() {
        @Override
        public MyQrCode createFromParcel(Parcel in) {
            return new MyQrCode(in);
        }

        @Override
        public MyQrCode[] newArray(int size) {
            return new MyQrCode[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(dt);
        dest.writeString(ds);
        dest.writeInt(type);
        dest.writeString(dn);
        dest.writeString(phone);
        dest.writeString(orgId);
        dest.writeString(deptId);
        dest.writeString(teamId);
        dest.writeString(orgName);
        dest.writeString(deptName);
        dest.writeString(teamName);
        dest.writeString(qrCodeUserId);
    }
}

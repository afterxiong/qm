package com.shareshow.aide.retrofit.entity;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by xiongchengguang on 2018/1/16.
 */

public class Datadoc implements Parcelable {
    private String Id;
    private String Filename;
    private String Url;
    private String Spid;
    private String Del;
    private String folder;

    public Datadoc(String id, String filename, String url, String spid, String del, String folder) {
        Id = id;
        Filename = filename;
        Url = url;
        Spid = spid;
        Del = del;
        this.folder = folder;
    }

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    public String getFilename() {
        return Filename;
    }

    public void setFilename(String filename) {
        Filename = filename;
    }

    public String getUrl() {
        return Url;
    }

    public void setUrl(String url) {
        Url = url;
    }

    public String getSpid() {
        return Spid;
    }

    public void setSpid(String spid) {
        Spid = spid;
    }

    public String getDel() {
        return Del;
    }

    public void setDel(String del) {
        Del = del;
    }

    public String getFolder() {
        return folder;
    }

    public void setFolder(String folder) {
        this.folder = folder;
    }

    protected Datadoc(Parcel in) {
        Id = in.readString();
        Filename = in.readString();
        Url = in.readString();
        Spid = in.readString();
        Del = in.readString();
        folder = in.readString();
    }

    public static final Creator<Datadoc> CREATOR = new Creator<Datadoc>() {
        @Override
        public Datadoc createFromParcel(Parcel in) {
            return new Datadoc(in);
        }

        @Override
        public Datadoc[] newArray(int size) {
            return new Datadoc[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(Id);
        dest.writeString(Filename);
        dest.writeString(Url);
        dest.writeString(Spid);
        dest.writeString(Del);
        dest.writeString(folder);
    }
}

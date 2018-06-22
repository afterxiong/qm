package com.shareshow.airpc.share;

/**
 * Created by Administrator on 2017/7/6 0006.
 * 分享的好友
 */

public class Friend {

    private String diviceName;

    private int postion;

    private boolean isSelect;

    private String address;

    private int diviceType;

    public int getDiviceType() {
        return diviceType;
    }

    public void setDiviceType(int diviceType) {
        this.diviceType = diviceType;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getDiviceName() {
        return diviceName;
    }

    public void setDiviceName(String diviceName) {
        this.diviceName = diviceName;
    }

    public int getPostion() {
        return postion;
    }

    public void setPostion(int postion) {
        this.postion = postion;
    }

    public boolean isSelect() {
        return isSelect;
    }

    public void setSelect(boolean select) {
        isSelect = select;
    }

    @Override
    public String toString() {
        return "Friend{" +
                "diviceName='" + diviceName + '\'' +
                ", postion=" + postion +
                ", isSelect=" + isSelect +
                ", address='" + address + '\'' +
                '}';
    }
}

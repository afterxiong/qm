package com.shareshow.aide.retrofit.entity;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by xiongchengguang on 2017/12/19.
 */

public class UserInfo implements Parcelable{
    private int returnCode;
    private String message;
    private Data data;
    private String datas;
    private boolean status;

    public UserInfo() {
    }

    protected UserInfo(Parcel in) {
        returnCode = in.readInt();
        message = in.readString();
        datas = in.readString();
        status = in.readByte() != 0;
    }

    public static final Creator<UserInfo> CREATOR = new Creator<UserInfo>() {
        @Override
        public UserInfo createFromParcel(Parcel in) {
            return new UserInfo(in);
        }

        @Override
        public UserInfo[] newArray(int size) {
            return new UserInfo[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(returnCode);
        dest.writeString(message);
        dest.writeString(datas);
        dest.writeByte((byte) (status ? 1 : 0));
    }

    public int getReturnCode() {
        return returnCode;
    }

    public void setReturnCode(int returnCode) {
        this.returnCode = returnCode;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public String getDatas() {
        return datas;
    }

    public void setDatas(String datas) {
        this.datas = datas;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }



    public static class Data implements Parcelable {
        private String userId;
        private String userName;
        private String userPhone;
        private String userPhoneDevNo;
        private String orgId;
        private String orgName;
        private String deptId;
        private String deptName;
        private String teamId;
        private String teamName;
        private String teamCreaterId;
        private String rejectReason;
        private int urResponsible;

        public Data() {

        }

        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }

        public String getUserName() {
            return userName;
        }

        public void setUserName(String userName) {
            this.userName = userName;
        }

        public String getUserPhone() {
            return userPhone;
        }

        public void setUserPhone(String userPhone) {
            this.userPhone = userPhone;
        }

        public String getUserPhoneDevNo() {
            return userPhoneDevNo;
        }

        public void setUserPhoneDevNo(String userPhoneDevNo) {
            this.userPhoneDevNo = userPhoneDevNo;
        }

        public String getOrgId() {
            return orgId;
        }

        public void setOrgId(String orgId) {
            this.orgId = orgId;
        }

        public String getOrgName() {
            return orgName;
        }

        public void setOrgName(String orgName) {
            this.orgName = orgName;
        }

        public String getDeptId() {
            return deptId;
        }

        public void setDeptId(String deptId) {
            this.deptId = deptId;
        }

        public String getDeptName() {
            return deptName;
        }

        public void setDeptName(String deptName) {
            this.deptName = deptName;
        }

        public String getTeamId() {
            return teamId;
        }

        public void setTeamId(String teamId) {
            this.teamId = teamId;
        }

        public String getTeamName() {
            return teamName;
        }

        public void setTeamName(String teamName) {
            this.teamName = teamName;
        }

        public String getTeamCreaterId() {
            return teamCreaterId;
        }

        public void setTeamCreaterId(String teamCreaterId) {
            this.teamCreaterId = teamCreaterId;
        }

        public String getRejectReason() {
            return rejectReason;
        }

        public void setRejectReason(String rejectReason) {
            this.rejectReason = rejectReason;
        }

        public int getUrResponsible() {
            return urResponsible;
        }

        public void setUrResponsible(int urResponsible) {
            this.urResponsible = urResponsible;
        }

        protected Data(Parcel in) {
            userId = in.readString();
            userName = in.readString();
            userPhone = in.readString();
            userPhoneDevNo = in.readString();
            orgId = in.readString();
            orgName = in.readString();
            deptId = in.readString();
            deptName = in.readString();
            teamId = in.readString();
            teamName = in.readString();
            teamCreaterId = in.readString();
            rejectReason = in.readString();
            urResponsible = in.readInt();
        }

        public static final Creator<Data> CREATOR = new Creator<Data>() {
            @Override
            public Data createFromParcel(Parcel in) {
                return new Data(in);
            }

            @Override
            public Data[] newArray(int size) {
                return new Data[size];
            }
        };

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(userId);
            dest.writeString(userName);
            dest.writeString(userPhone);
            dest.writeString(userPhoneDevNo);
            dest.writeString(orgId);
            dest.writeString(orgName);
            dest.writeString(deptId);
            dest.writeString(deptName);
            dest.writeString(teamId);
            dest.writeString(teamName);
            dest.writeString(teamCreaterId);
            dest.writeString(rejectReason);
            dest.writeInt(urResponsible);
        }
    }
}

package com.shareshow.aide.retrofit.entity;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

/**
 * Created by xiongchengguang on 2018/1/31.
 */

public class VisitRecord implements Parcelable {

    /**
     * returnCode : 1
     * message : 查询成功
     * data : null
     * datas : [{"vrId":"10957372-5f3c-4104-9854-e8ba17744878","vrUrId":"1d13187c-fe38-42cb-abfe-dd73e37fddf4","vrPhone":"13971518451","vrTimestart":"2018-01-31 12:02:46","vrTimeend":"2018-01-31 12:03:02","vrPlanid":"熊承光","vrFileplaytime":0,"vrGuestname":"鸡脆骨","vrAddresss":"玉树临风精品酒店","vrContent":"擦擦擦擦擦擦888556","vrPicurl":"[\"http://www.shareshow.com.cn/Resource/data/VisitRecord/2018-01-31/1c0d43d3-a017-4118-929a-6c22ede77c8d.jpg\"]","vrGps":"114.403086<#>30.477828","vrFlag":0,"vrDate":"2018-01-31","vrDel":0},{"vrId":"bfcb3075-5c79-4d28-bdd8-29d49b0b46eb","vrUrId":"1d13187c-fe38-42cb-abfe-dd73e37fddf4","vrPhone":"13971518451","vrTimestart":"2018-01-31 11:27:31","vrTimeend":"2018-01-31 11:52:51","vrPlanid":"熊承光","vrFileplaytime":0,"vrGuestname":"刘强东","vrAddresss":"玉树临风精品酒店","vrContent":"12258965566586998","vrPicurl":"[\"http://www.shareshow.com.cn/Resource/data/VisitRecord/2018-01-31/36af253c-cb90-4ca1-8d05-e33a383c4ba7.png\",\"http://www.shareshow.com.cn/Resource/data/VisitRecord/2018-01-31/ecc9baf5-2d52-4322-acab-181716cce9dd.png\",\"http://www.shareshow.com.cn/Resource/data/VisitRecord/2018-01-31/ba6744e5-df5a-45f2-8533-86230755f808.png\"]","vrGps":"114.403091<#>30.477822","vrFlag":0,"vrDate":"2018-01-31","vrDel":0},{"vrId":"176ccf63-97c9-4b4b-928d-7c055b1b0050","vrUrId":"1d13187c-fe38-42cb-abfe-dd73e37fddf4","vrPhone":"13971518451","vrTimestart":"2018-01-31 11:25:39","vrTimeend":"2018-01-31 11:26:39","vrPlanid":"熊承光","vrFileplaytime":0,"vrGuestname":"马化腾","vrAddresss":"玉树临风精品酒店","vrContent":"12236998555888","vrPicurl":"[\"http://www.shareshow.com.cn/Resource/data/VisitRecord/2018-01-31/2fddc658-08ed-4475-826a-2859b22230ed.png\"]","vrGps":"114.403091<#>30.477822","vrFlag":0,"vrDate":"2018-01-31","vrDel":0}]
     * status : true
     */

    private int returnCode;
    private String message;
    private Object data;
    private boolean status;
    private List<VisitData> datas;

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

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public List<VisitData> getDatas() {
        return datas;
    }

    public void setDatas(List<VisitData> datas) {
        this.datas = datas;
    }

    protected VisitRecord(Parcel in) {
        returnCode = in.readInt();
        message = in.readString();
        status = in.readByte() != 0;
        datas = in.createTypedArrayList(VisitData.CREATOR);
    }

    public static final Creator<VisitRecord> CREATOR = new Creator<VisitRecord>() {
        @Override
        public VisitRecord createFromParcel(Parcel in) {
            return new VisitRecord(in);
        }

        @Override
        public VisitRecord[] newArray(int size) {
            return new VisitRecord[size];
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
        dest.writeByte((byte) (status ? 1 : 0));
        dest.writeTypedList(datas);
    }
}

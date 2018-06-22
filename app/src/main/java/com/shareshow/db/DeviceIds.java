package com.shareshow.db;

import java.util.List;

/**
 * Created by TEST on 2017/12/28.
 */

public class DeviceIds {

    /**
     * returnCode : 1
     * message : 查询成功
     * data : null
     * datas : ["48dd00e2a0da6526"]
     * status : true
     */

    private int returnCode;
    private String message;
    private String data;
    private boolean status;
    private List<String> datas;

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

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public List<String> getDatas() {
        return datas;
    }

    public void setDatas(List<String> datas) {
        this.datas = datas;
    }

    @Override
    public String toString() {
        return "DeviceIds{" +
                "returnCode=" + returnCode +
                ", message='" + message + '\'' +
                ", data=" + data +
                ", status=" + status +
                ", datas=" + datas +
                '}';
    }
}

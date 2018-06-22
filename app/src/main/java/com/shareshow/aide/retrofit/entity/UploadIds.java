package com.shareshow.aide.retrofit.entity;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by xiongchengguang on 2018/4/4.
 */

public class UploadIds {

    private int returnCode;
    private String message;
    private Object data;
    private boolean status;
    private List<String> datas;

    public static List<UploadIds> arrayUploadIdsFromData(String str) {

        Type listType = new TypeToken<ArrayList<UploadIds>>() {
        }.getType();

        return new Gson().fromJson(str, listType);
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

    public List<String> getDatas() {
        return datas;
    }

    public void setDatas(List<String> datas) {
        this.datas = datas;
    }
}

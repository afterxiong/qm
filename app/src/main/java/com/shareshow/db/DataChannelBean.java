package com.shareshow.db;

/**
 * Created by wulong on 2017/12/11 0011.
 */

public class DataChannelBean {

    //{command:201,content:'',del:0,type:1}

    private String command;
    private String content;
    private int del;
    private int type; //1,广告数据；2,设备使用数据 3,文件记录；

    public DataChannelBean(String command, String content, int del, int type){
        this.command = command;
        this.content = content;
        this.del = del;
        this.type = type;
    }

    public String getCommand() {
        return command;
    }

    public String getContent() {
        return content;
    }

    public int getDel() {
        return del;
    }

    public int getType() {
        return type;
    }


    @Override
    public String toString() {
        return "DataChannelBean{" +
                "command='" + command + '\'' +
                ", content='" + content + '\'' +
                ", del=" + del +
                ", type=" + type +
                '}';
    }
}

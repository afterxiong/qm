package com.shareshow.db;

import java.io.Serializable;

/**
 * Created by TEST on 2017/12/12.
 */

public class SearchBean implements Serializable{


    private static final long serialVersionUID = 1L;

    //{command: 101,ids: '423sd34sdfw',post:34242,content:'',del:0}
    public static final int CMD_SEACH = 101;//搜索
    public static final String CMD_DATA_ADD = "cmd START AD";//广告统计
    public static final String CMD_DATA_USER = "cmd START USE";//广告统计
    public static final String CMD_DATA_RECORD = "cmd START RECORD";//广告统计
    public static final String CMD_DATA_UP_ADRECORD = "cmd START COUNT";//详细广告数据的统计
    public static final String CMD_AD = "AD&";//广告统计
    public static final String CMD_USER = "USE&";//广告统计
    public static final String CMD_RECORD= "RECORD&";//广告统计
    public static final String CMD_DATA_FILE = "FILE&";//广告统计
    public static final String CMD_TIME = "cmd TIME";//对时间
    public static final String CMD_FILE = "cmd FILE";//广告文件操作
    public static final String CMD_AD_RECORD ="COUNT&";


    public static String DEV_AREA_USEINFO = "dev_area_useinfo";
    public static String DEV_APP_USEINFO = "dev_app_useinfo";
    public static String DEV_FILE_WORKINFO = "work";


    private int command;
    private String ids;
    private int port;
    private String ip;

    public SearchBean(int command, String ids, int port, String ip) {
        this.command = command;
        this.ids = ids;
        this.port = port;
        this.ip = ip;
    }

    public String getIds() {
        return ids;
    }

    public void setIds(String ids) {
        this.ids = ids;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }
}

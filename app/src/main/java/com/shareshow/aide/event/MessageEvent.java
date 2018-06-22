package com.shareshow.aide.event;

/**
 * Created by FENGYANG on 2018/1/19.
 */

public class MessageEvent {

    public static final int SEND_FILE_COMPILETE = 1;

    public static final int SEND_FILE_TO_BOX = 2;
    //创建团队成功
    public static final int UPDATE_TEAM = 3;
    //创建团队成功
    public static final int ADD_TEAN = 4;
    //显示团队二维码
    public static final int SHOW_TEAM_QR_CODE = 5;
    //退出删除
    public static final int DELETE_TEAM = 6;
    public static final int EVENT_DISMISS_TEAM = 7;
    public static final int EVENT_EXIT_TEAM = 8;

    public static final int EVENT_DELETE_TEAM_FRIEND=9;
    //删除团队成员

    public static final int EVENT_CHANGE_BIND_NAME=10;
    //更改绑定设备名称

    public static final int EVENT_PERMISSION_SETTING =11;
    //去设置权限

    public static final int EVENT_UPDATE_MORNINGDATA =12;
    //获取签到数据



    private boolean isTimeOut;
    private int cmd;

    public MessageEvent(int cmd, boolean isTimeOut) {
        this.cmd = cmd;
        this.isTimeOut = isTimeOut;
    }


    public boolean isTimeOut() {
        return isTimeOut;
    }

    public int getCmd() {
        return cmd;
    }

    private int sign;


    public int getSign() {
        return sign;
    }

    private String string;

    public String getString() {
        return string;
    }

    public MessageEvent(int sign) {
        this.sign = sign;
    }

    public MessageEvent(int sign, String string) {
        this.sign = sign;
        this.string = string;
    }
}

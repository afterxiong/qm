package com.shareshow.aide.nettyfile.Utils;

/**
 * Created by TEST on 2017/11/9.
 * 服务器的接口
 */

public class HttpContent {

    public static final String USER_RIGSTER = "http://localhost:8080/NetShare/register.form";
    //用户注册

    public static final String RECORD_POST = "http://localhost:8080/NetShare/uploadCheckMorningVoice.form";
    //录音文件上传

    public static final String AGAIN_RIGSTER = "http://localhost:8080/NetShare/rebind.form";
    //重新注册

    public static final String GET_USER_INFORMATION = "http://localhost:8080/NetShare/getOrgAndDept.form";
    //获取用户的公司和部门

    public static final String DOWNLOAD_START_AD ="http://172.16.21.30:8080/NetShare/getAdCurrentForDevice.form";
    //?phoneNumber=13333333333&adType=1";
    //开机广告

    public static final String DOWNLOAD_MIDDLE_AD ="http://172.16.21.30:8080/NetShare/getAdCurrentForDevice.form";
    //?phoneNumber=13333333333&adType=2
    //屏保


}

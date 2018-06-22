package com.shareshow.aide.util;

import android.os.Environment;

import com.shareshow.App;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;


/**
 * Created by xiongchengguang on 2017/12/20.
 */

public class FileBuild {

    public static final String ENCODING = "UTF-8";//常量，代表编码格式。
    public static String RootPath = "";

    private static void checkPath(String child){
        RootPath = Environment.getExternalStorageDirectory() + File.separator + App.getApp().getPackageName();
        File file = new File(RootPath + File.separator + child);
        if (!file.exists()){
            file.mkdirs();
        }
    }

    public static String getPhoneByInfo(String phone,String name) {
        try {
            FileInputStream in = new FileInputStream(RootPath + File.separator + phone);
            StringBuffer sb = new StringBuffer();
            int length = in.available();
            byte[] buffer = new byte[length];
            while ((in.read(buffer)) != -1) {
                sb.append(new String(buffer, ENCODING));
            }
            return sb.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public static void savePhoneByUserInfo(String phone, String name, String content) {
        try {
            checkPath(phone);
            CacheUserInfo.get().setUserPhone(phone);
            FileOutputStream out = new FileOutputStream(RootPath + File.separator + phone + File.separator + name);
            byte[] buffer = content.getBytes(ENCODING);
            out.write(buffer);
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}

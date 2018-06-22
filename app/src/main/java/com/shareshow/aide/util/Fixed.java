package com.shareshow.aide.util;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.telephony.TelephonyManager;

import com.shareshow.App;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;

/**
 * Created by xiongchengguang on 2017/12/5.
 */

public class Fixed {
    public static final String CLIENT_REFURBISH = "refurbish";
    public static final int MAX_COUNT_TIME = 60;
    public static final String AUDIO_PATH = Environment.getExternalStorageDirectory() + File.separator + "AudioRecoder";
    //进入扫描标记符号
    public static final String SCAN_TYPE = "scan_type";
    //加入公司
    @Deprecated
    public static final int TAG_ADD_ORG = 101;
    //加入部门
    @Deprecated
    public static final int TAG_ADD_DEPT = 102;
    //加入部门
    public static final int TAG_ADD_ORG_DEPT = 105;
    //加入团队
    public static final int TAG_ADD_TEAM = 103;
    //刷新个人信息
    public static final int UPDATE_USER_INFO = 104;
    //扫描绑定
    public static final int TAG_BINDING = 2;
    //扫描结果
    public static final String SCAN_RESULT = "scan_result";
    //选择联系人
    public static final int PICK_CONTACT_REQUEST = 201;
    //返回二维码内容
    public static final String QR_CONTENT = "qr_content";

    public static final int TAG_SCAN = 666;
    public static final String TAG_IDS_VALUE = "tag_ids_value";
    public static final String TAG_NAME_VALUE = "tag_name_value";
    public static final String TAG_PHONE_VALUE = "tag_phone_value";
    public static final String USER_LOGIN = "user_login";
    public static final String Official_Website = "http://www.shareshow.com.cn/download/index.html?";
    public static final String USER_ID = "user_id";
    public static final String USER_NAME = "user_name";
    public static final String USER_PHONE = "user_phone";
    public static final int MAX_DAY = 90;

    public static final int REQUEST_CODE_CAPTURE_PERM = 0X010;
    public static final int code1 = 223;

    public static String getUUID() {
        return SampleNetworkInterface.getMacAddress() + getDeviceId();
    }


    public static String getRootPath() {
        return Environment.getExternalStorageDirectory() + File.separator + App.getApp().getPackageName();
    }

    public static String getAdPath() {
        return getBoxPath() + File.separator + "ad";
    }

    public static String getBoxPath() {

        return getRootPath() + File.separator + CacheUserInfo.get().getUserPhone() + File.separator + "box";
    }

    public static String getAdZipPath() {
        return getRootPath() + File.separator + CacheUserInfo.get().getUserPhone() + File.separator + "ad_zip";
    }

    public static String getMoningFile() {
        return getRootPath() + File.separator + CacheUserInfo.get().getUserPhone() + File.separator + "MorningAudio";
    }

    public static String getRemouteMoningFile() {
        return getRootPath() + File.separator + CacheUserInfo.get().getUserPhone() + File.separator + "RemouteMorningAudio";
    }

    private static String getDeviceId() {
        final Context context = App.getApp();
        final TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(context.TELEPHONY_SERVICE);
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
            return telephonyManager.getDeviceId();
        }
        return "00000";
    }

    /**
     * 客户拜访文件临时路径
     *
     * @return
     */
    public static String getVisitAudioPath() {
        return Fixed.getRootPath() + File.separator + CacheUserInfo.get().getUserPhone() + File.separator + "VisitAudio" + File.separator + "visit_audio_cache.aac";
    }

    public static String getVisitAudioCache() {
        return Fixed.getRootPath() + File.separator + CacheUserInfo.get().getUserPhone() + File.separator + "CacheVisitDwonload";
    }

    public static String inputStreanByString(InputStream in) {
        StringBuffer sb = new StringBuffer();
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            String line = "";
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sb.toString();
    }

    private static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    public static String getToDay() {
        return dateFormat.format(System.currentTimeMillis());
    }


}

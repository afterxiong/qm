package com.shareshow.airpc.util;

import android.os.Environment;

import com.shareshow.aide.retrofit.RetrofitProvider;
import com.shareshow.aide.util.Fixed;

import java.io.File;

/**
 * Created by TEST on 2017/12/21.
 * 广告的文件目录
 */

public class FileContent {

    public static final String XT_CONFIG= Fixed.getBoxPath();
    public static final String JSONFILE_AD= Fixed.getAdPath();
    public static final String JSONFILE_AD_SCREENSAVER=JSONFILE_AD+File.separator+"screensaver";
    public static final String JSONFILE_AD_HOT=JSONFILE_AD+File.separator+"hot";
    public static final String JSONFILE_AD_BOOT=JSONFILE_AD+File.separator+"boot";
    public static final String JSONFILE_AD_OFFICIAL_DIR=JSONFILE_AD+File.separator+"official";
    public static final String JSONFILE_AD_SCREENSAVER_CACHE=JSONFILE_AD_SCREENSAVER+File.separator+"cache";
    public static final String JSONFILE_AD_HOT_CACHE=JSONFILE_AD_HOT+File.separator+"cache";
    public static final String ZIP_PATH = Environment.getExternalStorageDirectory()+File.separator+"renyiping/ad_zip";

    public static final String  DEV_USEINFO = RetrofitProvider.ENDPOINT+"devUseInfo.form";
    public static final String  APP_USEINFO =RetrofitProvider.ENDPOINT+"devAppUse.form";
    public static final String  UP_VISITE_FILEINFO=RetrofitProvider.ENDPOINT+"visitFilePlayRecord.form";
    public static final String  UP_STUDY_FILEINFO=RetrofitProvider.ENDPOINT+"studyFilePlayRecord.form";
    public static final String  UP_AD_STATE=RetrofitProvider.ENDPOINT+"setAdReceiverByDevice.form";
    public static final String  UP_AD_USER_RECORD = RetrofitProvider.ENDPOINT+"devAdPlayRecord.form";

}

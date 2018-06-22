package com.shareshow.aide.util;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.v4.content.FileProvider;

import java.io.File;

/**
 * Created by xiongchengguang on 2017/10/12 0012.
 */

public class OpenFileUtil {

    private static final String[][] MIME_MapTable = {
            //{后缀名，    MIME类型}
            {".txt", "text/plain"},
            {".xls", "application/vnd.ms-excel"},
            {".mp3", "audio/x-mpeg"},
            {".mp4", "video/mp4"},
            {".png", "image/png"},
            {".jpg", "image/jpeg"},
            {".jpeg", "image/jpeg"},
            {".ppt", "application/vnd.ms-powerpoint"},
            {".pdf", "application/pdf"},
            {".pqf", "application/x-cprplayer"},
            {".pps", "application/vnd.ms-powerpoint"},
            {".htm", "text/html"},
            {".html", "text/html"},
            {".wav", "audio/x-wav"},
            {".wma", "audio/x-ms-wma"},
            {".wmv", "audio/x-ms-wmv"},
            {".wps", "application/vnd.ms-works"},
            {".xml", "text/plain"},
            {".doc", "application/msword"},
            {".docx", "application/msword"},
            {".xlsx", "application/vnd.ms-excel"},
            {".3gp", "video/3gpp"},
            {".apk", "application/vnd.android.package-archive"},
            {".pptx", "application/vnd.ms-powerpoint"},
            {".prop", "text/plain"},
            {".rar", "application/x-rar-compressed"},
            {".asf", "video/x-ms-asf"},
            {".avi", "video/x-msvideo"},
            {".bin", "application/octet-stream"},
            {".bmp", "image/bmp"},
            {".c", "text/plain"},
            {".class", "application/octet-stream"},
            {".conf", "text/plain"},
            {".cpp", "text/plain"},
            {".file_icon_xls", "application/vnd.ms-excel"},
            {".exe", "application/octet-stream"},
            {".gif", "image/gif"},
            {".gtar", "application/x-gtar"},
            {".gz", "application/x-gzip"},
            {".h", "text/plain"},
            {".jar", "application/java-archive"},
            {".java", "text/plain"},
            {".js", "application/x-javascript"},
            {".log", "text/plain"},
            {".m3u", "audio/x-mpegurl"},
            {".m4a", "audio/mp4a-latm"},
            {".m4b", "audio/mp4a-latm"},
            {".m4p", "audio/mp4a-latm"},
            {".m4u", "video/vnd.mpegurl"},
            {".m4v", "video/x-m4v"},
            {".mov", "video/quicktime"},
            {".mp2", "audio/x-mpeg"},
            {".mpc", "application/vnd.mpohun.certificate"},
            {".mpe", "video/mpeg"},
            {".mpeg", "video/mpeg"},
            {".mpg", "video/mpeg"},
            {".mpg4", "video/mp4"},
            {".mpga", "audio/mpeg"},
            {".msg", "application/vnd.ms-outlook"},
            {".ogg", "audio/ogg"},
            {".file_icon_pdf", "application/file_icon_pdf"},
            {".file_icon_ppt", "application/vnd.ms-powerpoint"},
            {".rc", "text/plain"},
            {".rmvb", "audio/x-pn-realaudio"},
            {".rtf", "application/rtf"},
            {".sh", "text/plain"},
            {".tar", "application/x-tar"},
            {".tgz", "application/x-compressed"},
            {".file_icon_txt", "text/plain"},
            {".z", "application/x-compress"},
            {".file_icon_zip", "application/file_icon_zip"},
            {"", "*/*"}
    };

    public static String getMIMEType(File file){
        String type = "*/*";
        String fName = file.getName();
        int dotIndex = fName.lastIndexOf(".");
        if(dotIndex < 0){
            return type;
        }

        String fileType = fName.substring(dotIndex, fName.length()).toLowerCase();
        if (fileType == null || "".equals(fileType)){
            return type;
        }

        for (int i = 0; i < MIME_MapTable.length; i++){
            if (fileType.equals(MIME_MapTable[i][0])){
                type = MIME_MapTable[i][1];
                break;
            }
        }
        return type;
    }

    public static Intent openFile(Context context, String filePath){
        Intent intent = new Intent();
        File param = new File(filePath);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);//设置标记
        intent.setAction(Intent.ACTION_VIEW);//动作，查看
        Uri uri;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            uri = FileProvider.getUriForFile(context, context.getPackageName() + ".fileProvider", param);
        } else {
            uri = Uri.fromFile(param);
        }
        intent.setDataAndType(uri, getMIMEType(param));//设置类型
        return intent;
    }
}

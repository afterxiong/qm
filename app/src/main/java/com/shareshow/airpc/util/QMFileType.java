package com.shareshow.airpc.util;

import android.annotation.SuppressLint;
import android.os.Environment;

import java.text.SimpleDateFormat;

@SuppressLint("SimpleDateFormat")
public class QMFileType{
	
	public static final String SDCARD = Environment
			.getExternalStorageDirectory().getPath();

	public static final String ANYSCREEN = SDCARD+"/renyiping";

	public static final String WEIXIN ="/tencent";

	public static final String WEIXIN_FILE ="/tencent/MicroMsg/Download";

	public static final String WEIXIN_IMG ="/tencent/MicroMsg/WeiXin";

	public static final String QQ_FILE ="/tencent/QQfile_recv";

	public static final String QQ_IMG ="/tencent/QQ_images";
	
	public static final String LOCALPATH = ANYSCREEN + "/DownLoads";

	public static String CACHE = ANYSCREEN + "/cache";
	
	public static final String SIMPLE_FORMAT_SHOW_TIME = "yyyy-MM-dd HH:mm";

	// 时间格式转换
	public static CharSequence makeSimpleDateStringFromLong(long inTimeInMillis) {
		return new SimpleDateFormat(SIMPLE_FORMAT_SHOW_TIME)
				.format(inTimeInMillis);
	}
	
//判断文件的类型
	public static int getType(String name) {
		int type = 0;
		int index = name.lastIndexOf(".");
		if (index < 0)
			return type;
		String end = name.substring(index + 1, name.length()).toLowerCase();
		if (end == "")
			return type;
		switch (end) {
		case "ppt":
		case "pptx":
			type = 1;
			break;
		case "doc":
		case "docx":
			type = 2;
			break;
		case "file_icon_xls":
		case "xlsx":
			type = 3;
			break;
		case "pdf":
			type = 4;
			break;
		case "file_icon_txt":
		case "xml":
		case "log":
			type = 5;
			break;
		case "htm":
		case "html":
			type = 6;
			break;
		case "jpg":
		case "png":
		case "gif":
		case "jpeg":
		case "bmp":
			type = 7;
			break;
		case "mp4":
		case "m4v":
	//	case "dat":
		case "avi":
		case "mkv":
		case "rmvb":
		case "wmv":
		case "flv":
		case "mov":
		case "3gp":
		case "3gpp":
			type = 8;
			break;
		case "mp3":
			type = 9;
			break;
		case "apk":
			type = 10;
			break;
		case "zip":
		case "rar":
			type = 11;
			break;
		case "tar":
			type = 0;
			break;
		}
		return type;
	}

	
	public static String getTime(long tt){
		long mm=tt/1000;
		if(mm<60){
			if(mm<10)
				return "00:00:0"+tt/1000;
			else
				return "00:00:"+tt/1000;
		}else if(mm/60<60){
			if(mm/60<10&&mm%60<10)
				return "00:0"+mm/60+":0"+mm%60;
			else if(mm/60<10&&mm%60>=10)
				return "00:0"+mm/60+":"+mm%60;
			else if(mm/60>=10&&mm%60<10)
				return "00:"+mm/60+":0"+mm%60;
			else if(mm/60>=10&&mm%60>=10)
				return "00:"+mm/60+":"+mm%60;
		}else{
			if(mm/60/60<10){
				if(mm/60/60<10&&mm/60%60<10)
					return "0"+mm/60/60+":0"+mm/60/60+":0"+mm/60%60;
				else if(mm/60/60<10&&mm/60%60>=10)
					return "0"+mm/60/60+":0"+mm/60/60+":"+mm/60%60;
				else if(mm/60/60>=10&&mm/60%60<10)
					return "0"+mm/60/60+":"+mm/60/60+":0"+mm/60%60;
				else if(mm/60/60>=10&&mm/60%60>=10)
					return "0"+mm/60/60+":"+mm/60/60+":"+mm/60%60;
			}else{
				if(mm/60/60<10&&mm/60%60<10)
					return ""+mm/60/60+":0"+mm/60/60+":0"+mm/60%60;
				else if(mm/60/60<10&&mm/60%60>=10)
					return ""+mm/60/60+":0"+mm/60/60+":"+mm/60%60;
				else if(mm/60/60>=10&&mm/60%60<10)
					return ""+mm/60/60+":"+mm/60/60+":0"+mm/60%60;
				else if(mm/60/60>=10&&mm/60%60>=10)
					return ""+mm/60/60+":"+mm/60/60+":"+mm/60%60;
			}
		}
		return null;
	}
}

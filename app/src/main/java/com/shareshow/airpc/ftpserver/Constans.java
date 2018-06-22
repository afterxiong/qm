package com.shareshow.airpc.ftpserver;

import android.os.Environment;

import java.io.File;

public class Constans{

	public static String USERNAME = "xtxk";
	public static String PASSWORD = "xtxk";
	public static int PORT = 2121;
	public static int VERSION = 1;
	public static int CLIENT_VERSION = 1;
	//public static String CHROOTDIR = FilePath.SHAREPATH;
	public static String CHROOTDIR = Environment.getExternalStorageDirectory()+ File.separator;
	public static boolean ACCEPTWIFI = true;
	public static boolean ACCEPTNET = false;
	public static boolean AWAKE = false;
	
	
	public static String CHROOTDIR2 ="/storage"+ File.separator/*+"任易屏"+File.separator+"本地分享"*/;
	//   /storage/extSdCard
}

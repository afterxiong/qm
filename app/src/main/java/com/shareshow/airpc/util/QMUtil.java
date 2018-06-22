package com.shareshow.airpc.util;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.provider.Settings.Secure;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.shareshow.aide.R;
import com.shareshow.airpc.model.QMLocalFile;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class QMUtil {

	private static  QMUtil instance;
	
	private QMDocumentFile qmDocumentFile;
	public QMDocumentFile getQmDocumentFile() {
		return qmDocumentFile;
	}

	public void setQmDocumentFile(QMDocumentFile qmDocumentFile) {
		this.qmDocumentFile = qmDocumentFile;
	}
	//相册UI跳转查看图片 数量过大无法用Intent传值
	private ArrayList<QMLocalFile> photos = new ArrayList<QMLocalFile>();
	
	public ArrayList<QMLocalFile> getPhotos() {
		return photos;
	}
	
	private QMUtil() {
		super();
		qmDocumentFile=new QMDocumentFile();
	}

	public static QMUtil getInstance(){
		if(instance==null)
			instance=new QMUtil();
		return instance;
	}
	//个性吐司==========================Toast===========================
	private Toast result;
	
	private void initToast(Context context){
		if(result==null){
			result=new Toast(context);
			LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View layout = inflater.inflate(R.layout.show_toast, null);
			result.setView(layout);
		}
		result.setDuration(500);
		result.show();
	}
	
	public  void showToast(Context context, int id){
		initToast(context);
		((TextView)result.getView().findViewById(R.id.TextViewInfo)).setText(context.getResources().getString(id));
	}
	
	public  void showToast2(Context context, String versionHigher){
		initToast(context);
		((TextView)result.getView().findViewById(R.id.TextViewInfo)).setText(versionHigher);
	}
	/**
	 * 进度弹框
	 * //===============================progressDialog==================================
	 * */
	
	private Dialog dialog;
	
	public   void  closeDialog(){
		if(dialog!=null)
			dialog.dismiss();
	}
	
	public  void showProgressDialog(Activity context, int text){
		if(dialog!=null)
			dialog.dismiss();
		View vv = View.inflate(context, R.layout.show_load, null);
		((TextView)vv.findViewById(R.id.text)).setText(context.getResources().getString(text));
		dialog = new Dialog(context, R.style.alert_dialog);
		dialog.setContentView(vv);
		dialog.setCanceledOnTouchOutside(false);
		dialog.setCancelable(false);
		dialog.show();
	}

	//===============================progressDialog==================================
	
	//============================================================================

	//获取年份
	public String getTimeYear(){
		SimpleDateFormat format = new SimpleDateFormat("yyyy");
		String year=format.format(new Date());
		return year;
	}
	
	//只允许输入字母、数字、汉子
	public String stringFilter(String str){
		String regEx="[^a-zA-Z0-9\u4E00-\u9FA5]";
		Pattern p= Pattern.compile(regEx);
		Matcher m=p.matcher(str);
		return m.replaceAll("").trim();
	}
	
	/**
	 * 判断某个界面是否在前台
	 */
	public boolean isForeground(Activity context, String className){
		ActivityManager am = (ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningTaskInfo> infos = am.getRunningTasks(1);
		if(infos!=null&&infos.size()>0){
			ComponentName cpn=infos.get(0).topActivity;
			if(className.equals(cpn.getClassName()))
				return true;
		}
		return false;
	}
	
	//分辨率转换
	public int dp2px(Context context, int dp){
		return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
				context.getResources().getDisplayMetrics());
	}
	
	//获取手机唯一标识
//	public String getUUID(Context context){
//		TelephonyManager TelephonyMgr = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
//		String m_szImei = TelephonyMgr.getDeviceId();
//		if(m_szImei!=null)
//			return m_szImei;
//		String m_szAndroidID = Secure.getString(context.getContentResolver(), Secure.ANDROID_ID);
//		return m_szAndroidID;
//	}

	public String getMobileType(Context context){
		if(isPad(context))
			return "androidpad";
		return "android";
	}
	
	public boolean isValid(Context context){
		Activity a=(Activity) context;
		if(a.isDestroyed()||a.isFinishing())
			return false;
		else
			return true;
	}
	
	/**
	 * 判断手机还是平板
	 * 
	 * @return
	 */
	public  boolean isPad(Context context) {
		WindowManager wm = (WindowManager)context.getSystemService(Context.WINDOW_SERVICE);
		Display dis = wm.getDefaultDisplay();
		DisplayMetrics dm = new DisplayMetrics();
		dis.getMetrics(dm);
		double x = Math.pow(dm.widthPixels / dm.xdpi, 2);
		double y = Math.pow(dm.heightPixels / dm.ydpi, 2);
		double z = Math.sqrt(x + y);
		if (z >= 6.0)
			return true;
		else
			return false;
	}
	/**
	 * 获取手机内存大小
	 * @return
	 */
	public long getSDCardMemorry(){
		long size2=0;
		String state = Environment.getExternalStorageState();
    	if(Environment.MEDIA_MOUNTED.equals(state)) {
    		File sdcardDir = Environment.getExternalStorageDirectory();
    		StatFs stat = new StatFs(sdcardDir.getPath());
    		long blockSize;
			long availableBlocks;
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.BASE)
    		{
    		blockSize = stat.getBlockSize();
    		availableBlocks = stat.getAvailableBlocks();
    		}
    		else
    		{
    		blockSize = stat.getBlockSize();
    		availableBlocks = stat.getAvailableBlocks();
    		}
			size2= blockSize*availableBlocks;
    		return size2;
    	} 	
		return size2;
	}
}

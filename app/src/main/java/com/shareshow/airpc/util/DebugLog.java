package com.shareshow.airpc.util;

import android.os.Environment;
import android.util.Log;

import com.tencent.bugly.crashreport.BuglyLog;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Calendar;
import java.util.Date;

public class DebugLog
{

  public static boolean IS_SHOW = true;

  private static final int LEVEL = 1;

  private static final String SHOW_LOG = "1";

  private static final boolean IS_WRITE_FILE = false;

  public static final String TAG = "AirPc";

  private static final String WRITE_LOG_PATH = Environment.getExternalStorageDirectory().getPath() + "/renyiping/log";


  private static String assemblingMsg(Object paramObject, String paramString)
  {
      if(IS_WRITE_FILE){
          return writeLog(paramObject.getClass().getName() + "---------->" + paramString);
      }else{
          return paramObject.getClass().getName() + "---------->" + paramString;
      }
  }

  private static String assemblingMsg(String paramString1, String paramString2)
  {


      if(IS_WRITE_FILE){
          return writeLog(paramString1 + "---------->" + paramString2);
      }else{
          return paramString1 + "---------->" + paramString2;
      }

  }

  public static void init(){
    try {
    	long oneDay = 1000*60*60*24;
    	long newTime = System.currentTimeMillis() / oneDay;
    	long lastTime;
		File file = new File(WRITE_LOG_PATH );
		if (file.exists()){
				for (int i = 0; i < file.listFiles().length; i++){
					lastTime = file.listFiles()[i].lastModified() / oneDay ;
					if ((newTime - lastTime) > 3 || newTime < lastTime) {
						file.listFiles()[i].delete();
					}
				}
		}

    } 
    catch (NumberFormatException localNumberFormatException){
    	
    }
  }

  public static void showLog(Object paramObject, String paramString)
  {
     showLog(paramObject, paramString,3);
  }

    public static void showLog2(Object paramObject, String paramString)
    {
        if (IS_SHOW)
            Log.i("hzk2", assemblingMsg(paramObject, paramString));
        BuglyLog.i("hzk2",assemblingMsg(paramObject, paramString));
    }

  public static void showLog(String paramString1, String paramString2)
  {
	  showLog(paramString1, paramString2,3);
  }
  
  public static void showLog(Object paramObject, String paramString, int level) {
	  if (IS_SHOW && level >= LEVEL)
	      Log.i(TAG, assemblingMsg(paramObject, paramString));
          BuglyLog.i(TAG,assemblingMsg(paramObject, paramString));
  }


  public static void showLog(String paramString1, String paramString2, int level)
  {

	  if (IS_SHOW && level >= LEVEL)
      Log.i(TAG, assemblingMsg(paramString1, paramString2));
  }

  
//+calendar.get(Calendar.YEAR)+"-"+ calendar.get(Calendar.MONTH) +"-" +calendar.get(Calendar.DAY_OF_MONTH) + ".file_icon_txt"
  private static String writeLog(String msg) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new Date(System.currentTimeMillis()));
		try {
		File file = new File(WRITE_LOG_PATH );
		if (!file.exists()) {
				file.mkdirs();
		}
		file =  new File(WRITE_LOG_PATH+ File.separator+calendar.get(Calendar.YEAR)+"-"+ (calendar.get(Calendar.MONTH) + 1) +"-" +calendar.get(Calendar.DAY_OF_MONTH) + ".file_icon_txt" );
		if (!file.exists()) {
			file.createNewFile();
	    }
		RandomAccessFile accessFile = new RandomAccessFile(file, "rw");
		accessFile.seek(accessFile.length());
		accessFile.write("\r\n".getBytes());
//		accessFile.writeBytes(calendar.get(Calendar.HOUR_OF_DAY)+":"+calendar.get(Calendar.MINUTE)+":"+calendar.get(Calendar.SECOND)+":"+calendar.get(Calendar.MILLISECOND)+"    "+msg);
		accessFile.writeUTF(calendar.get(Calendar.HOUR_OF_DAY)+":"+calendar.get(Calendar.MINUTE)+":"+calendar.get(Calendar.SECOND)+":"+calendar.get(Calendar.MILLISECOND)+"    "+msg);
		accessFile.close();
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
      return msg;
  }

}
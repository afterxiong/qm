package com.shareshow.aide.util;


import com.shareshow.aide.retrofit.entity.TeamMorningData;
import com.shareshow.aide.retrofit.entity.UserInfo;
import com.shareshow.airpc.util.DebugLog;
import com.socks.library.KLog;
import com.shareshow.airpc.Collocation;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.FileChannel;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class CopyFile{

    public static String TAG = "CopyFile";
    private static FileInputStream fosfrom;
    private static FileOutputStream fosto;
    private static BufferedInputStream bis;
    private static BufferedOutputStream bos;

    private static SimpleDateFormat timeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");


    /**
     * 复制整个文件夹内容
     */
    public static void copyFolder(final String oldPath, final String newPath,final boolean isHot) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                File oldFile_dir = new File(oldPath);
                if (!oldFile_dir.exists()){
                    return;
                }
                File newFile_dir = new File(newPath);
                if (!newFile_dir.exists()){
                    newFile_dir.mkdirs();
                }
                File[] files = oldFile_dir.listFiles();
                try {
                    for (File file: files){
                        File newfile = new File(newPath, file.getName());
                        file.renameTo(newfile);
                    }
                    if(isHot){
                        if (adListener != null){
                            adListener.adHotlistener();
                        }
                    }else {
                        if (adListener != null){
                            adListener.adScreenlistener();
                        }
                    }
                } catch (Exception e) {
                    if (isHot){
                        Collocation.getCollocation().setAdHotFlag("");
                    }else{
                        Collocation.getCollocation().setAdScreenFlag("");
                    }
                    e.printStackTrace();
                }
            }
        }).start();
    }
    /**
     * 复制整个文件夹内容
     */
    public static void copyFile(final String oldFile, final String newFile) {
                try {
                    File oldFiles = new File(oldFile);
                    if (!oldFiles.exists()) {
                        return;
                    }
                    File newFile_dir = new File(newFile);
                    if (!newFile_dir.exists()) {
                        newFile_dir.createNewFile();
                    }
                    fosfrom = new FileInputStream(oldFiles);
                    fosto = new FileOutputStream(newFile_dir);
                    bis = new BufferedInputStream(fosfrom);
                    bos = new BufferedOutputStream(fosto);
                    byte[] buffer = new byte[1024 * 1024];
                    int len;
                    while ((len = bis.read(buffer)) != -1) {
                        bos.write(buffer, 0, len);
                    }
                    bos.flush();
                    bis.close();
                    bos.close();
                    fosfrom.close();
                    fosto.close();
                } catch (Exception e) {
                    KLog.d(TAG,e.getMessage());
                    e.printStackTrace();
                }
    }

    //文件拷贝
    //要复制的目录下的所有非子目录(文件夹)文件拷贝
    public static boolean CopySdcardFile(String fromFile, String toFile) {
        try {
            InputStream fosfrom = new FileInputStream(fromFile);
            OutputStream fosto = new FileOutputStream(toFile);
            byte bt[] = new byte[1024];
            int c;
            while ((c = fosfrom.read(bt)) > 0) {
                fosto.write(bt, 0, c);
            }
            fosfrom.close();
            fosto.close();
            return true;

        } catch (Exception ex) {
            return false;
        }
    }


    public static boolean  fileChannelCopy(File s, File t) {
        boolean isSuccess =false;
        FileInputStream fi = null;
        FileOutputStream fo = null;
        FileChannel in = null;
        FileChannel out = null;
        try {
            fi = new FileInputStream(s);
            fo = new FileOutputStream(t);
            in = fi.getChannel();//得到对应的文件通道
            out = fo.getChannel();//得到对应的文件通道
            in.transferTo(0, in.size(), out);//连接两个通道，并且从in通道读取，然后写入out通道
            isSuccess=true;
        } catch (IOException e) {
            e.printStackTrace();
            isSuccess = false;
        } finally {
            try {
                fi.close();
                in.close();
                fo.close();
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return isSuccess;

    }


    public static void copyFile(final File oldFile, final String newFile) {
        try {
            File newFile_dir = new File(newFile);
            if (!newFile_dir.exists()) {
                newFile_dir.createNewFile();
            }
            fosfrom = new FileInputStream(oldFile);
            fosto = new FileOutputStream(newFile_dir);
            bis = new BufferedInputStream(fosfrom);
            bos = new BufferedOutputStream(fosto);
            byte[] buffer = new byte[1024 * 1024];
            int len;
            while ((len = bis.read(buffer)) != -1) {
                bos.write(buffer, 0, len);
            }
            bos.flush();
            bis.close();
            bos.close();
            fosfrom.close();
            fosto.close();
        } catch (Exception e) {
            KLog.d(TAG,e.getMessage());
            e.printStackTrace();
        }
    }
    public interface AdCopyListener{
        void adHotlistener();
        void adScreenlistener();
    }

    /**
     * 获取前n天日期、后n天日期
     *
     * @param distanceDay 前几天 如获取前7天日期则传-7即可；如果后7天则传7
     * @return
     */
    public static String getOldDate(int distanceDay){
        SimpleDateFormat dft = new SimpleDateFormat("yyyy-MM-dd");
        Date beginDate = new Date();
        Calendar date = Calendar.getInstance();
        date.setTime(beginDate);
        date.set(Calendar.DATE, date.get(Calendar.DATE) + distanceDay);
        Date endDate = null;
        try {
            endDate = dft.parse(dft.format(date.getTime()));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return dft.format(endDate);
    }


    /**
     * 获取前n天日期、后n天日期
     *
     * @param distanceDay 前几天 如获取前7天日期则传-7即可；如果后7天则传7
     * @return
     */
    public static Calendar getOldCalendar(int distanceDay){
        Date beginDate = new Date();
        Calendar date = Calendar.getInstance();
        date.setTime(beginDate);
        date.set(Calendar.DATE, date.get(Calendar.DATE) + distanceDay);
        return date;
    }



    /***
     *  获取指定日后 后 dayAddNum 天的 日期
     *  @param day  日期，格式为String："2013-9-3";
     *  @param dayAddNum 增加天数 格式为int;
     *  @return
     */
    public static String getDateStr(String day, long dayAddNum){
       SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        Date nowDate = null;
        try {
            nowDate = df.parse(day);
        } catch (ParseException e){
            e.printStackTrace();
        }
        Date newDate2 = new Date(nowDate.getTime() + dayAddNum * 24 * 60 * 60 * 1000);
        Date today = new Date();
        if(newDate2.getTime()>today.getTime()){
            String todayTime = df.format(today);
            return todayTime;
        }else{
            String dateOk = df.format(newDate2);
            return dateOk;
        }
    }

    //是否间隔90天
    public static boolean isPassNinety(Date startTime, Date endTime){
       SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
       long intervalTime = endTime.getTime()-startTime.getTime();
       int intervalday = (int) (intervalTime/(24 * 60 * 60 * 1000));
       if(intervalday>90){
           return true;
       }else{
           return false;
       }
    }


    public static long getTime(String data){
        try {
            return timeFormat.parse(data).getTime();
        } catch (ParseException e) {
            e.printStackTrace();
            return 0;
        }
    }


    public static AdCopyListener adListener;
    public static void setAdCopyListener(AdCopyListener lis){
        adListener = lis;
    }
}

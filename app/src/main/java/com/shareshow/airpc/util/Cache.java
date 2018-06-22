package com.shareshow.airpc.util;

import android.text.TextUtils;

import java.io.File;
import java.io.FileFilter;
import java.math.BigDecimal;

public class Cache {

    /**
     * 获取文件夹大小
     * @param file File
     * @return long
     */
    public static long getFolderSize(File file){

        long size = 0;
        try {
            File[] fileList = file.listFiles();
            for (int i = 0; i < fileList.length; i++)
            {
                if (fileList[i].isDirectory())
                {
                    size = size + getFolderSize(fileList[i]);

                }else{
                    size = size + fileList[i].length();

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return size;
    }

    /**
     * 删除文件夹内容
     * @param filePath
     * @return
     */
    public static void deleteFolderFile(String filePath) {
        if (!TextUtils.isEmpty(filePath)) {
            try {
                File file = new File(filePath);
                if (file.isDirectory()) {
                    File files[] = file.listFiles();
                    for (int i = 0; i < files.length; i++) {
                        files[i].delete();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    /**
     * 删除文件夹内容
     * @param fileDir
     * @return
     */
    public static boolean deleteFolderFile(File fileDir) {
        try {
            if (fileDir.isDirectory()) {
                File files[] = fileDir.listFiles();
                for (int i = 0; i < files.length; i++) {
                    boolean success = deleteFolderFile(files[i]);
                    if (!success){
                        return false;
                    }
                }
            }
            return fileDir.delete();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    /**
     * 删除文件夹指定内容
     * @param filePath
     * @param fileFilter   删除文件filter
     * @return
     */
    public static void deleteFolderFileNameFilter(String filePath, final String fileFilter) {
        if (!TextUtils.isEmpty(filePath)) {
            try {
                File file = new File(filePath);
                if (file.exists()&&file.isDirectory()) {
                    File files[] = file.listFiles(new FileFilter() {
                        @Override
                        public boolean accept(File pathname) {
                            String s = pathname.getName().toLowerCase();
                            return s.contains(fileFilter.toLowerCase());
                        }
                    });
                    for (File delfile: files) {
                        delfile.delete();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    /**
     * 删除文件夹指定内容
     * @param filePath
     * @param fileFilter   删除文件filter
     * @return
     */
    public static void deleteFolderFileFilter(String filePath, final String fileFilter) {
        if (!TextUtils.isEmpty(filePath)) {
            try {
                File file = new File(filePath);
                if (file.isDirectory()) {
                    File files[] = file.listFiles(new FileFilter() {
                        @Override
                        public boolean accept(File pathname) {
                            String s = pathname.getName().toLowerCase();
                            return s.endsWith(fileFilter);
                        }
                    });
                    for (File delfile: files) {
                        delfile.delete();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    /**
     * 删除文件夹内容   并过滤指定文件
     * @param filePath
     * @param savaFile   过滤指定文件
     * @return
     */
    public static void deleteFolderFile(String filePath,String savaFile) {
        if (!TextUtils.isEmpty(filePath)) {
            try {
                File file = new File(filePath);
                if (file.isDirectory()) {
                    File files[] = file.listFiles();
                    for (int i = 0; i < files.length; i++) {
                        if (!files[i].getName().equals(savaFile)) {
                            files[i].delete();
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    /**
     * 格式化文件大小
     * @param size
     * @return
     */
    public static String getFormatSize(double size) {
        double kiloByte = size/1024;
        if(kiloByte < 1) {
            return size + "Byte(s)";
        }

        double megaByte = kiloByte/1024;
        if(megaByte < 1) {
            BigDecimal result1 = new BigDecimal(Double.toString(kiloByte));
            return result1.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + "KB";
        }

        double gigaByte = megaByte/1024;
        if(gigaByte < 1) {
            BigDecimal result2  = new BigDecimal(Double.toString(megaByte));
            return result2.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + "MB";
        }

        double teraBytes = gigaByte/1024;
        if(teraBytes < 1) {
            BigDecimal result3 = new BigDecimal(Double.toString(gigaByte));
            return result3.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + "GB";
        }
        BigDecimal result4 = new BigDecimal(teraBytes);
        return result4.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + "TB";
    }
}

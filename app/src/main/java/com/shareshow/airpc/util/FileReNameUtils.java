package com.shareshow.airpc.util;


import java.io.File;

public class FileReNameUtils {


	/**
	 * @return	文件下载存在重命名
	 */
	private static int i = 1;
	private static String newPath;

	public  static File fileDownReName(String path,String oldPath){
		File file = new File(path);
		if (file.exists()) {
			if (!path.equals(oldPath)) {
				if (oldPath.lastIndexOf(".")>=0) {
					String subPath = oldPath.substring(0, oldPath.lastIndexOf("."));
					newPath = subPath+"("+i+")"+oldPath.substring(oldPath.lastIndexOf("."), oldPath.length());
				}else {
					newPath = oldPath+"("+i+")";
				}
			}else {
				if (oldPath.lastIndexOf(".")>=0) {
					String subPath = path.substring(0, path.lastIndexOf("."));
					newPath = subPath+"("+i+")"+path.substring(path.lastIndexOf("."), path.length());
				}else {
					newPath = path+"("+i+")";
				}
			}
			i++;
			file =fileDownReName(newPath,oldPath);
			i =1;
		}
		return file;
	}


	public static boolean delAllFile(String path){
		boolean flag = false;
		File file = new File(path);
		if (!file.exists()) {
			return flag;
		}
		if (!file.isDirectory()) {
			return flag;
		}
		String[] tempList = file.list();
		File temp = null;
		for (int i = 0; i < tempList.length; i++) {
			if (path.endsWith(File.separator)) {
				temp = new File(path + tempList[i]);
			} else {
				temp = new File(path + File.separator + tempList[i]);
			}
			if (temp.isFile()) {
				temp.delete();
			}
			if (temp.isDirectory()) {
				delAllFile(path + "/" + tempList[i]);//先删除文件夹里面的文件
				flag = true;
			}
		}
		return flag;
	}

}

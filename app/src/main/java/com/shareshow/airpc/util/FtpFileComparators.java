package com.shareshow.airpc.util;

import com.shareshow.airpc.ftpclient.FTPFile;
import com.shareshow.airpc.model.LancherFile;
import com.shareshow.airpc.model.QMRemoteFTPFile;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Locale;


public class FtpFileComparators implements Comparator<FTPFile> {

	private static Boolean[] orderByFtp ={false,false,false,false};
	private String order;
	private static FtpFileComparators comparators =null;

	public static FtpFileComparators getIntance(){
		    if(comparators==null){
		    	synchronized (FtpFileComparators.class) {
		    		comparators = new FtpFileComparators();
				}
			}

			return comparators;
	}

	private FtpFileComparators(){
		super();
	}

	public void setOrder(String order){
		this.order=order;
	}

	public boolean getOrderBy(String order) {
		boolean result =false;
		switch (order){
			case "name":
				result =  orderByFtp[0];
				break;
			case "date":
				result = orderByFtp[1];
				break;
			case "type":
				result = orderByFtp[2];
				break;
			case "size":
				result = orderByFtp[3];
				break;
		}
		return result;
	}

	public void setOrderBy(String order,boolean flag) {
		switch (order){
			case "name":
				orderByFtp[0] =flag;
				break;
			case "date":
				orderByFtp[1] =flag;
				break;
			case "type":
				orderByFtp[2] =flag;
				break;
			case "size":
				orderByFtp[3] =flag;
				break;
		}
	}

	public void cleanOrderBy(){
		orderByFtp[0] =false;
		orderByFtp[1] =true;
		orderByFtp[2] =false;
		orderByFtp[3] =false;
	}

	@Override
	public int compare(FTPFile file1, FTPFile file2) {
		if (file1 == null || file2 == null){
			return -1;
		}
		Collator instance = Collator.getInstance(Locale.CHINA);
		switch (order) {
			case "name":
				if (file1.getType() == FTPFile.TYPE_DIRECTORY && file2.getType() == FTPFile.TYPE_DIRECTORY) {
					return instance.compare(file1.getName().toLowerCase(), file2.getName().toLowerCase());
				} else {
					if (file1.getType() == FTPFile.TYPE_DIRECTORY && file2.getType() == FTPFile.TYPE_FILE) {
						return -1;
					} else if (file1.getType() == FTPFile.TYPE_FILE && file2.getType() == FTPFile.TYPE_DIRECTORY) {
						return 1;
					} else {
						return instance.compare(file1.getName().toLowerCase(), file2.getName().toLowerCase());
					}
				}
			case "date":
				if (file1.getType() == FTPFile.TYPE_DIRECTORY && file2.getType() == FTPFile.TYPE_DIRECTORY) {
					return compare(file1.getModifiedDate().getTime(), file2.getModifiedDate().getTime());
				} else {
					if (file1.getType() == FTPFile.TYPE_DIRECTORY && file2.getType() == FTPFile.TYPE_FILE) {
						return 1;
					}
					if (file1.getType() == FTPFile.TYPE_FILE && file2.getType() == FTPFile.TYPE_DIRECTORY) {
						return -1;
					}
					if (file1.getModifiedDate().getTime() == file2.getModifiedDate().getTime()) {//时间相同,按照名称排序
						return instance.compare(file1.getName().toLowerCase(), file2.getName().toLowerCase());
					} else {
						return compare(file1.getModifiedDate().getTime(), file2.getModifiedDate().getTime());
					}
				}
			case "type":
				if (file1.getType() == FTPFile.TYPE_DIRECTORY && file2.getType() == FTPFile.TYPE_DIRECTORY) {
					return instance.compare(file1.getName().toLowerCase(), file2.getName().toLowerCase());
				} else {
					if (file1.getType() == FTPFile.TYPE_DIRECTORY && file2.getType() == FTPFile.TYPE_FILE) {
						return -1;
					} else if (file1.getType() == FTPFile.TYPE_FILE && file2.getType() == FTPFile.TYPE_DIRECTORY) {
						return 1;
					} else {
						if (file1.getName().lastIndexOf(".") < 0) {
							return 1;
						}
						if (file1.getName().lastIndexOf(".") >= 0) {
							String file1Type = file1.getName().substring(file1.getName().lastIndexOf(".") + 1);
							String file2Type = file2.getName().substring(file2.getName().lastIndexOf(".") + 1);
							if (file1Type.equals(file2Type)) {//类型相同,比较时间
								if (file1.getModifiedDate().getTime() == file2.getModifiedDate().getTime()) {//时间相同,按照名称排序
									return instance.compare(file1.getName().toLowerCase(), file2.getName().toLowerCase());
								} else {
									return compare(file1.getModifiedDate().getTime(), file2.getModifiedDate().getTime());
								}
							}
							return compare(file1Type, file2Type);
						}
					}
				}
				return -1;
			case "size":
				if (file1.getType() == FTPFile.TYPE_DIRECTORY && file2.getType() == FTPFile.TYPE_DIRECTORY) {
					return instance.compare(file1.getName().toLowerCase(), file2.getName().toLowerCase());
				} else {
					if (file1.getType() == FTPFile.TYPE_DIRECTORY && file2.getType() == FTPFile.TYPE_FILE) {
						return -1;
					}
					if (file1.getType() == FTPFile.TYPE_FILE && file2.getType() == FTPFile.TYPE_DIRECTORY) {
						return 1;
					}
					if (file1.getSize() == file2.getSize()) {//文件长度相同,比较时间
						if (file1.getModifiedDate().getTime() == file2.getModifiedDate().getTime()) {//时间相同,按照名称排序
							return instance.compare(file1.getName().toLowerCase(), file2.getName().toLowerCase());
						} else {
							return compare(file1.getModifiedDate().getTime(), file2.getModifiedDate().getTime());
						}
					} else {
						return compare(file1.getSize(), file2.getSize());
					}
				}
			default:
				System.err.println("未找到合适的比较器");
				return 1;

		}

	}


	private int compare(String s1, String s2) {
		int len1 = s1.length();
		int len2 = s2.length();
		int n = Math.min(len1, len2);
		char v1[] = s1.toCharArray();
		char v2[] = s2.toCharArray();
		int pos = 0;

		while (n-- != 0) {
			char c1 = v1[pos];
			char c2 = v2[pos];
			if (c1 != c2) {
				return c1 - c2;
			}
			pos++;
		}
		return len1 - len2; 	}


	private int compare(long o1, long o2) {
		return (o1 < o2 ? -1 : (o1 == o2 ? 0 : 1));

	}


	public void sortShareFile(ArrayList<QMRemoteFTPFile> shareFile){
		Collections.sort(shareFile, new Comparator<FTPFile>(){
			@Override
			public int compare(FTPFile lhs, FTPFile rhs){
				if (lhs.getDir() > rhs.getDir())
				return 1;
				if (lhs.getDir() == rhs.getDir())
					return 0;

				return -1;
			}

		});
		ArrayList<ArrayList<QMRemoteFTPFile>> mm = new ArrayList<ArrayList<QMRemoteFTPFile>>();
		int old_type = -2;
		ArrayList<QMRemoteFTPFile> kk = null;
		for (int i = 0; i < shareFile.size(); i++) {
			if (old_type != shareFile.get(i).getDir()) {
				kk = new ArrayList<QMRemoteFTPFile>();
				QMRemoteFTPFile pp = shareFile.get(i);
				kk.add(pp);
				mm.add(kk);
			} else {
				QMRemoteFTPFile pp = shareFile.get(i);
				kk.add(pp);
			}
			old_type = shareFile.get(i).getDir();
		}
		shareFile.clear();
		for (int i = 0; i < mm.size(); i++) {
			ArrayList<QMRemoteFTPFile> qq = mm.get(i);
			Collections.sort(qq, new Comparator<QMRemoteFTPFile>(){
				@Override
				public int compare(QMRemoteFTPFile lhs, QMRemoteFTPFile rhs) {
					if (lhs.getModifiedDate().getTime()> rhs.getModifiedDate().getTime())
						return -1;
					if (lhs.getModifiedDate().getTime()== rhs.getModifiedDate().getTime())
						return 0;

					return 1;
				}
			});

			shareFile.addAll(qq);
		}
	}

}  

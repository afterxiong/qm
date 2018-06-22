package com.shareshow.airpc.util;

import java.io.File;
import java.text.Collator;
import java.util.Comparator;
import java.util.Locale;

public class FileComparators implements Comparator<File> {

	private static Boolean[] orderBy ={false,false,false,false};
	private String order;
	public FileComparators(String order) {
		super();
		this.order = order;
	}

	public boolean getOrderBy(String order) {
		boolean result =false;
		switch (order){
			case "name":
				result =  orderBy[0];
				break;
			case "date":
				result = orderBy[1];
				break;
			case "type":
				result = orderBy[2];
				break;
			case "size":
				result = orderBy[3];
				break;
		}
		return result;
	}

	public void setOrderBy(String order, boolean flag) {
		switch (order){
			case "name":
				orderBy[0] =flag;
				break;
			case "date":
				orderBy[1] =flag;
				break;
			case "type":
				orderBy[2] =flag;
				break;
			case "size":
				orderBy[3] =flag;
				break;
		}
	}

	public static void cleanOrderBy(){
		orderBy[0] =false;
		orderBy[1] =true;
		orderBy[2] =false;
		orderBy[3] =false;
	}

	@Override
	public int compare(File file1, File file2) {
		if (order.equals("name")) {
			Collator instance = Collator.getInstance(Locale.CHINA);
			if (file1.isDirectory()&&file2.isDirectory()) {
				return instance.compare(file1.getName().toLowerCase(), file2.getName().toLowerCase());
			}else {
				if (file1.isDirectory()&&file2.isFile()) {
					return -1;
				}else if(file1.isFile()&&file2.isDirectory()){
					return 1;
				}else {
					return instance.compare(file1.getName().toLowerCase(), file2.getName().toLowerCase());
				}
			}
		}else if(order.equals("date")) {
			if (file1.isDirectory()&&file2.isDirectory()) {
				return compare(file1.lastModified(), file2.lastModified());
			}else {
				if (file1.isDirectory()&&file2.isFile()) {
					return 1;
				}
				if(file1.isFile()&&file2.isDirectory()){
					return -1;
				}
				if (file1.lastModified() == file2.lastModified()) {//时间相同,按照名称排序
					Collator instance = Collator.getInstance(Locale.CHINA);
					return instance.compare(file1.getName().toLowerCase(), file2.getName().toLowerCase());
				}
				else{
					return compare(file1.lastModified(), file2.lastModified());
				}
			}
		}else if(order.equals("type")) {
			if (file1.isDirectory()&&file2.isDirectory()) {
				Collator instance = Collator.getInstance(Locale.CHINA);
				return instance.compare(file1.getName().toLowerCase(), file2.getName().toLowerCase());
			}else {
				if (file1.isDirectory()&&file2.isFile()) {
					return -1;
				}else if(file1.isFile()&&file2.isDirectory()){
					return 1;
				}else {
					if (file1.getName().lastIndexOf(".")<0) {
						return 1;
					}
					if (file1.getName().lastIndexOf(".")>=0) {
						String file1Type = file1.getName().substring(file1.getName().lastIndexOf(".") + 1).toLowerCase();
						String file2Type = file2.getName().substring(file2.getName().lastIndexOf(".") + 1).toLowerCase();
						if (file1Type.equals(file2Type)){//类型相同,比较时间
							if (file1.lastModified() == file2.lastModified()) {//时间相同,按照名称排序
								Collator instance = Collator.getInstance(Locale.CHINA);
								return instance.compare(file1.getName().toLowerCase(), file2.getName().toLowerCase());
							}else {
								return compare(file1.lastModified(), file2.lastModified());
							}
						}
						else {
							return compare(file1Type, file2Type);
						}
					}
				}
			}
			return -1;
		}else if(order.equals("size")) {
			if (file1.isDirectory()&&file2.isDirectory()) {
				Collator instance = Collator.getInstance(Locale.CHINA);
				return instance.compare(file1.getName().toLowerCase(), file2.getName().toLowerCase());
			}else {
				if (file1.isDirectory()&&file2.isFile()) {
					return -1;
				}
				if(file1.isFile()&&file2.isDirectory()){
					return 1;
				}
				if (file1.length() == file2.length()){//文件长度相同,比较时间
					if (file1.lastModified() == file2.lastModified()) {//时间相同,按照名称排序
						Collator instance = Collator.getInstance(Locale.CHINA);
						return instance.compare(file1.getName().toLowerCase(), file2.getName().toLowerCase());
					}else {
						return compare(file1.lastModified(), file2.lastModified());
					}
				}
				else{
					return compare(file1.length(), file2.length());
				}
			}
		}else {
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


	public int compare(Long o1, Long o2) {
		long val1 = o1.longValue();
		long val2 = o2.longValue();
		return (val1 < val2 ? -1 : (val1 == val2 ? 0 : 1));

	}

}  

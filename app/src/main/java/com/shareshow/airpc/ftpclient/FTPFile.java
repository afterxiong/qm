package com.shareshow.airpc.ftpclient;

import java.io.Serializable;
import java.util.Date;

public class FTPFile implements Serializable {
	public static final int TYPE_FILE = 0;
	public static final int TYPE_DIRECTORY = 1;
	public static final int TYPE_LINK = 2;
	
	private String name = null;//文件名称

	private String link = null;//对于Android手机，文件的下载路径

	private Date modifiedDate = null;//文件的修改时间

	private long size = -1L;//文件的大小
	
	private int type;//文件的类型

	private int permit;//文件是否允许下载
 
	private int dir = 1;//是否是文件夹 1文件  0文件夹

	public int getDir() {
		return dir;
	}

	public void setDir(int dir) {
		this.dir = dir;
	}

	public int getPermit() {
		return permit;
	}

	public void setPermit(int permit) {
		this.permit = permit;
	}

	public Date getModifiedDate() {
		return this.modifiedDate;
	}

	public void setModifiedDate(Date modifiedDate) {
		this.modifiedDate = modifiedDate;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getType() {
		return this.type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public long getSize() {
		return this.size;
	}

	public void setSize(long size) {
		this.size = size;
	}

	public String getLink() {
		return this.link;
	}

	public void setLink(String link) {
		this.link = link;
	}

}

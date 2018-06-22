package com.shareshow.airpc.model;


import com.shareshow.airpc.ftpclient.FTPFile;


public class QMRemoteFTPFile extends FTPFile {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private boolean isChoose;//选择文件下载时是否选中
	private boolean isLoading;//是否正在下载
	private String localName;//下载在本地时的文件名称

	public boolean isLoading() {
		return isLoading;
	}

	public void setLoading(boolean isLoading) {
		this.isLoading = isLoading;
	}

	public String getLocalName() {
		return localName;
	}

	public void setLocalName(String localName) {
		this.localName = localName;
	}

	public boolean isChoose() {
		return isChoose;
	}

	public void setChoose(boolean isChoose) {
		this.isChoose = isChoose;
	}
}

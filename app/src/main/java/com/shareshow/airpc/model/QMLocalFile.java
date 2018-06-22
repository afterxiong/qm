package com.shareshow.airpc.model;



public class QMLocalFile extends LancherFile {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String sortLetters;  //显示数据拼音的首字母
	private boolean isChoose;//选择文件
	private boolean isUping;//是否正在上传
	private String remoteName;//远程文件名称

	@Override
	public String toString() {
		return "name:"+getName()+"path:"+getPath()+"size:"+getSize()+"update_:"+getUpdate()+"type:"+getType();
	}

	public String getRemoteName() {
		return remoteName;
	}
	public void setRemoteName(String remoteName) {
		this.remoteName = remoteName;
	}
	public boolean isUping() {
		return isUping;
	}
	public void setUping(boolean isUping) {
		this.isUping = isUping;
	}
	
	public boolean isChoose() {
		return isChoose;
	}
	public void setChoose(boolean isChoose) {
		this.isChoose = isChoose;
	}
	
	public String getSortLetters() {
		return sortLetters;
	}
	public void setSortLetters(String sortLetters) {
		this.sortLetters = sortLetters;
	}
	
	public QMLocalFile() {
		super();
	}

	public QMLocalFile(String path, String name, int type) {
		super(path,name,type);
	}
	public QMLocalFile(String name, String path, long size, long update_, int type) {
		super(name,path,size,update_,type);
	}
}

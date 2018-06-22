package com.shareshow.db;

/**
 * 
 * <P>
 * 客户拜访文件播放记录
 * </P>
 * 
 * @author 周欣(13667212859)
 * @date 2017年12月4日 下午4:21:11
 */
public class VisitFilePlayRecord {

	private static final long serialVersionUID = 1L;

	private String vfrDevid;// 设备id

	private String vfrFileid;// 文件id

	private String vfrTimestart;// 文件播放开始时间

	private String vfrTimeend;// 文件播放结束时间

	public String getVfrDevid(){
		return vfrDevid;
	}

	public void setVfrDevid(String vfrDevid){
		this.vfrDevid = vfrDevid == null ? null : vfrDevid.trim();
	}

	public String getVfrFileid() {
		return vfrFileid;
	}

	public void setVfrFileid(String vfrFileid) {
		this.vfrFileid = vfrFileid == null ? null : vfrFileid.trim();
	}

	public String getVfrTimestart() {
		return vfrTimestart;
	}

	public void setVfrTimestart(String vfrTimestart) {
		this.vfrTimestart = vfrTimestart;
	}

	public String getVfrTimeend() {
		return vfrTimeend;
	}

	public void setVfrTimeend(String vfrTimeend) {
		this.vfrTimeend = vfrTimeend;
	}
}
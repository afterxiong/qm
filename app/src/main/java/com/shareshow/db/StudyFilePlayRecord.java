package com.shareshow.db;

import java.io.Serializable;

/**
 * 
 * <P>
 * 学习情况记录
 * </P>
 * 
 * @author 周欣(13667212859)
 * @date 2017年12月8日 上午9:41:50
 */
public class StudyFilePlayRecord implements Serializable {
	private static final long serialVersionUID = 1L;

	private String sfrUrId;// 注册用户id

	private String sfrFileid;// 文件id

	private String sfrSpid;// 学习计划id

	private String sfrTimestart;// 开始学习时间

	private String sfrTimeend;// 结束学习时间

	private String sfrDate;// 播放日期

	public String getSfrUrId() {
		return sfrUrId;
	}

	public void setSfrUrId(String sfrUrId) {
		this.sfrUrId = sfrUrId == null ? null : sfrUrId.trim();
	}

	public String getSfrFileid() {
		return sfrFileid;
	}

	public void setSfrFileid(String sfrFileid) {
		this.sfrFileid = sfrFileid == null ? null : sfrFileid.trim();
	}

	public String getSfrSpid() {
		return sfrSpid;
	}

	public void setSfrSpid(String sfrSpid) {
		this.sfrSpid = sfrSpid == null ? null : sfrSpid.trim();
	}

	public String getSfrTimestart() {
		return sfrTimestart;
	}

	public void setSfrTimestart(String sfrTimestart) {
		this.sfrTimestart = sfrTimestart;
	}

	public String getSfrTimeend() {
		return sfrTimeend;
	}

	public void setSfrTimeend(String sfrTimeend) {
		this.sfrTimeend = sfrTimeend;
	}

	public String getSfrDate() {
		return sfrDate;
	}

	public void setSfrDate(String sfrDate) {
		this.sfrDate = sfrDate;
	}
}
package com.shareshow.db;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Property;

/**
 * 
 * <P>
 * 设备地域使用时长
 * </P>
 * 
 * @author 周欣(13667212859)
 * @date 2017年10月10日 下午1:57:07
 */
@Entity
public class FileCordBean {

	@Id(autoincrement = true)
	private Long id;

    @Property
	private String vfrDevid;// 设备id

    @Property
	private int vfr_File_Type;// 文件类型： 2,客户拜访  3,学习资料

    @Property
	private String vfrFileid;// 应用id

	@Property
	private String fileVid; //

    @Property
	private String vfr_File_Name;// 应用名称

    @Property
	private long vfrTimestart;// 打开时间

    @Property
	private long vfrTimeend;// 关闭时间

				public long getVfrTimeend() {
					return this.vfrTimeend;
				}

				public void setVfrTimeend(long vfrTimeend) {
					this.vfrTimeend = vfrTimeend;
				}

				public long getVfrTimestart() {
					return this.vfrTimestart;
				}

				public void setVfrTimestart(long vfrTimestart) {
					this.vfrTimestart = vfrTimestart;
				}

				public String getVfr_File_Name() {
					return this.vfr_File_Name;
				}

				public void setVfr_File_Name(String vfr_File_Name) {
					this.vfr_File_Name = vfr_File_Name;
				}

				public String getFileVid() {
					return this.fileVid;
				}

				public void setFileVid(String fileVid) {
					this.fileVid = fileVid;
				}

				public String getVfrFileid() {
					return this.vfrFileid;
				}

				public void setVfrFileid(String vfrFileid) {
					this.vfrFileid = vfrFileid;
				}

				public int getVfr_File_Type() {
					return this.vfr_File_Type;
				}

				public void setVfr_File_Type(int vfr_File_Type) {
					this.vfr_File_Type = vfr_File_Type;
				}

				public String getVfrDevid() {
					return this.vfrDevid;
				}

				public void setVfrDevid(String vfrDevid) {
					this.vfrDevid = vfrDevid;
				}

				public Long getId() {
					return this.id;
				}

				public void setId(Long id) {
					this.id = id;
				}

				@Generated(hash = 313443096)
				public FileCordBean(Long id, String vfrDevid, int vfr_File_Type,
						String vfrFileid, String fileVid, String vfr_File_Name, long vfrTimestart,
						long vfrTimeend) {
					this.id = id;
					this.vfrDevid = vfrDevid;
					this.vfr_File_Type = vfr_File_Type;
					this.vfrFileid = vfrFileid;
					this.fileVid = fileVid;
					this.vfr_File_Name = vfr_File_Name;
					this.vfrTimestart = vfrTimestart;
					this.vfrTimeend = vfrTimeend;
				}

				@Generated(hash = 956497512)
				public FileCordBean() {
				}


	@Override
	public String toString() {
		return "FileCordBean{" +
				"id=" + id +
				", vfrDevid='" + vfrDevid + '\'' +
				", vfr_File_Type=" + vfr_File_Type +
				", vfrFileid='" + vfrFileid + '\'' +
				", fileVid='" + fileVid + '\'' +
				", vfr_File_Name='" + vfr_File_Name + '\'' +
				", vfrTimestart=" + vfrTimestart +
				", vfrTimeend=" + vfrTimeend +
				'}';
	}
}
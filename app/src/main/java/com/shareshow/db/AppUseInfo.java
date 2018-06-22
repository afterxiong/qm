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
public class AppUseInfo {

	@Id(autoincrement = true)
	private Long id;

	@Property
	private String dauDbiId;// 设备id

    @Property
	private String dauApp;// 应用名称

    @Property
	private long dauOpentime;// 打开时间

    @Property
	private long dauClosetime;// 关闭时间

				public long getDauClosetime(){
					return this.dauClosetime;
				}

				public void setDauClosetime(long dauClosetime){
					this.dauClosetime = dauClosetime;
				}

				public long getDauOpentime() {
					return this.dauOpentime;
				}

				public void setDauOpentime(long dauOpentime){
					this.dauOpentime = dauOpentime;
				}

				public String getDauApp() {
					return this.dauApp;
				}

				public void setDauApp(String dauApp){
					this.dauApp = dauApp;
				}

				public String getDauDbiId() {
					return this.dauDbiId;
				}

				public void setDauDbiId(String dauDbiId) {
					this.dauDbiId = dauDbiId;
				}

				@Generated(hash = 223010466)
				public AppUseInfo(Long id, String dauDbiId, String dauApp, long dauOpentime,
						long dauClosetime) {
					this.id = id;
					this.dauDbiId = dauDbiId;
					this.dauApp = dauApp;
					this.dauOpentime = dauOpentime;
					this.dauClosetime = dauClosetime;
				}

				@Generated(hash = 937709977)
				public AppUseInfo() {
				}

	@Override
	public String toString() {
		return "AppUseInfo{" +
				"dauDbiId='" + dauDbiId + '\'' +
				", dauApp='" + dauApp + '\'' +
				", dauOpentime=" + dauOpentime +
				", dauClosetime=" + dauClosetime +
				'}';
	}

	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}


}
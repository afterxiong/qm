package com.shareshow.db;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Keep;
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
public class DevAreaUseInfo{

    @Id(autoincrement = true)
    private Long id;

    @Property
	private String dauDbiId;// 设备id

    @Property
	private String dauGroup;// 设备组织

    @Property
	private String dauProvince;// 省

    @Property
	private String dauCity;// 市

    @Property
	private Integer dauTime;// 时长（分钟）

    @Property
	private String dauLastusertime;// 最后使用时间

    @Property
	private Integer dauDel;

    @Property
	private Integer dauBootCount;//开机次数

    @Property
	private Integer dauBootAdShowCount;//开机广告显示次数

    @Property
	private Integer dauSaverAdShowCount;//屏保显示次数

    @Property
	private Integer dauSaverAdClickCount;//屏保打开次数

    @Property
	private Integer dauHotAdShowCount;//热门显示次数

    @Property
	private Integer dauHotAdClickCount;//热门打开次数

    @Property
	private Integer dauOfficialAdClickCount;//官网打开次数

    @Property
    private String dauDate;//当前日期


    public String getDauDate() {
        return this.dauDate;
    }

    public void setDauDate(String dauDate) {
        this.dauDate = dauDate;
    }

    public Integer getDauOfficialAdClickCount() {
        return this.dauOfficialAdClickCount;
    }

    public void setDauOfficialAdClickCount(Integer dauOfficialAdClickCount) {
        this.dauOfficialAdClickCount = dauOfficialAdClickCount;
    }

    public Integer getDauHotAdClickCount() {
        return this.dauHotAdClickCount;
    }

    public void setDauHotAdClickCount(Integer dauHotAdClickCount) {
        this.dauHotAdClickCount = dauHotAdClickCount;
    }

    public Integer getDauHotAdShowCount() {
        return this.dauHotAdShowCount;
    }

    public void setDauHotAdShowCount(Integer dauHotAdShowCount) {
        this.dauHotAdShowCount = dauHotAdShowCount;
    }

    public Integer getDauSaverAdClickCount() {
        return this.dauSaverAdClickCount;
    }

    public void setDauSaverAdClickCount(Integer dauSaverAdClickCount) {
        this.dauSaverAdClickCount = dauSaverAdClickCount;
    }

    public Integer getDauSaverAdShowCount() {
        return this.dauSaverAdShowCount;
    }

    public void setDauSaverAdShowCount(Integer dauSaverAdShowCount) {
        this.dauSaverAdShowCount = dauSaverAdShowCount;
    }

    public Integer getDauBootAdShowCount() {
        return this.dauBootAdShowCount;
    }

    public void setDauBootAdShowCount(Integer dauBootAdShowCount) {
        this.dauBootAdShowCount = dauBootAdShowCount;
    }

    public Integer getDauBootCount() {
        return this.dauBootCount;
    }

    public void setDauBootCount(Integer dauBootCount) {
        this.dauBootCount = dauBootCount;
    }

    public Integer getDauDel() {
        return this.dauDel;
    }

    public void setDauDel(Integer dauDel) {
        this.dauDel = dauDel;
    }

    public String getDauLastusertime() {
        return this.dauLastusertime;
    }

    public void setDauLastusertime(String dauLastusertime) {
        this.dauLastusertime = dauLastusertime;
    }

    public Integer getDauTime() {
        return this.dauTime;
    }

    public void setDauTime(Integer dauTime) {
        this.dauTime = dauTime;
    }

    public String getDauCity() {
        return this.dauCity;
    }

    public void setDauCity(String dauCity) {
        this.dauCity = dauCity;
    }

    public String getDauProvince() {
        return this.dauProvince;
    }

    public void setDauProvince(String dauProvince) {
        this.dauProvince = dauProvince;
    }

    public String getDauGroup() {
        return this.dauGroup;
    }

    public void setDauGroup(String dauGroup) {
        this.dauGroup = dauGroup;
    }

    public String getDauDbiId() {
        return this.dauDbiId;
    }

    public void setDauDbiId(String dauDbiId) {
        this.dauDbiId = dauDbiId;
    }

    @Keep@Generated(hash = 1869734504)
    public DevAreaUseInfo( String dauDbiId, String dauGroup,
            String dauProvince, String dauCity, Integer dauTime,
            String dauLastusertime, Integer dauDel, Integer dauBootCount,
            Integer dauBootAdShowCount, Integer dauSaverAdShowCount,
            Integer dauSaverAdClickCount, Integer dauHotAdShowCount,
            Integer dauHotAdClickCount, Integer dauOfficialAdClickCount,
            String dauDate) {
        this.dauDbiId = dauDbiId;
        this.dauGroup = dauGroup;
        this.dauProvince = dauProvince;
        this.dauCity = dauCity;
        this.dauTime = dauTime;
        this.dauLastusertime = dauLastusertime;
        this.dauDel = dauDel;
        this.dauBootCount = dauBootCount;
        this.dauBootAdShowCount = dauBootAdShowCount;
        this.dauSaverAdShowCount = dauSaverAdShowCount;
        this.dauSaverAdClickCount = dauSaverAdClickCount;
        this.dauHotAdShowCount = dauHotAdShowCount;
        this.dauHotAdClickCount = dauHotAdClickCount;
        this.dauOfficialAdClickCount = dauOfficialAdClickCount;
        this.dauDate = dauDate;
    }

    @Generated(hash = 2127997095)
    public DevAreaUseInfo() {
    }

    @Generated(hash = 726668418)
    public DevAreaUseInfo(Long id, String dauDbiId, String dauGroup,
            String dauProvince, String dauCity, Integer dauTime,
            String dauLastusertime, Integer dauDel, Integer dauBootCount,
            Integer dauBootAdShowCount, Integer dauSaverAdShowCount,
            Integer dauSaverAdClickCount, Integer dauHotAdShowCount,
            Integer dauHotAdClickCount, Integer dauOfficialAdClickCount,
            String dauDate) {
        this.id = id;
        this.dauDbiId = dauDbiId;
        this.dauGroup = dauGroup;
        this.dauProvince = dauProvince;
        this.dauCity = dauCity;
        this.dauTime = dauTime;
        this.dauLastusertime = dauLastusertime;
        this.dauDel = dauDel;
        this.dauBootCount = dauBootCount;
        this.dauBootAdShowCount = dauBootAdShowCount;
        this.dauSaverAdShowCount = dauSaverAdShowCount;
        this.dauSaverAdClickCount = dauSaverAdClickCount;
        this.dauHotAdShowCount = dauHotAdShowCount;
        this.dauHotAdClickCount = dauHotAdClickCount;
        this.dauOfficialAdClickCount = dauOfficialAdClickCount;
        this.dauDate = dauDate;
    }

    @Override
    public String toString() {
        return "DevAreaUseInfo{" +
                "dauDbiId='" + dauDbiId + '\'' +
                ", dauGroup='" + dauGroup + '\'' +
                ", dauProvince='" + dauProvince + '\'' +
                ", dauCity='" + dauCity + '\'' +
                ", dauTime=" + dauTime +
                ", dauLastusertime='" + dauLastusertime + '\'' +
                ", dauDel=" + dauDel +
                ", dauBootCount=" + dauBootCount +
                ", dauBootAdShowCount=" + dauBootAdShowCount +
                ", dauSaverAdShowCount=" + dauSaverAdShowCount +
                ", dauSaverAdClickCount=" + dauSaverAdClickCount +
                ", dauHotAdShowCount=" + dauHotAdShowCount +
                ", dauHotAdClickCount=" + dauHotAdClickCount +
                ", dauOfficialAdClickCount=" + dauOfficialAdClickCount +
                ", dauDate='" + dauDate + '\'' +
                '}';
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

}
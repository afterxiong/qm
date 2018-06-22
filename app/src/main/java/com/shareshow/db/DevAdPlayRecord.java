package com.shareshow.db;
import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Property;
import org.greenrobot.greendao.annotation.Generated;


@Entity
public class DevAdPlayRecord {

    @Id(autoincrement = true)
    private Long id;

    @Property
    private String darDevid;//设备id

    @Property
    private String darDate;//广告播放日期（格式：2017-01-08）

    @Property
    private String darFileid;//文件id

    @Property
    private Integer darPlaynum;//播放次数

    @Property
    private Integer darClicknum;//点击次数

    public Integer getDarClicknum() {
        return this.darClicknum;
    }

    public void setDarClicknum(Integer darClicknum) {
        this.darClicknum = darClicknum;
    }

    public Integer getDarPlaynum() {
        return this.darPlaynum;
    }

    public void setDarPlaynum(Integer darPlaynum) {
        this.darPlaynum = darPlaynum;
    }

    public String getDarFileid() {
        return this.darFileid;
    }

    public void setDarFileid(String darFileid) {
        this.darFileid = darFileid;
    }

    public String getDarDate() {
        return this.darDate;
    }

    public void setDarDate(String darDate) {
        this.darDate = darDate;
    }

    public String getDarDevid() {
        return this.darDevid;
    }

    public void setDarDevid(String darDevid) {
        this.darDevid = darDevid;
    }


    public DevAdPlayRecord(String darDevid, String darDate, String darFileid, Integer darPlaynum, Integer darClicknum) {
        this.darDevid = darDevid;
        this.darDate = darDate;
        this.darFileid = darFileid;
        this.darPlaynum = darPlaynum;
        this.darClicknum = darClicknum;
    }

    @Generated(hash = 1895526027)
    public DevAdPlayRecord(Long id, String darDevid, String darDate,
            String darFileid, Integer darPlaynum, Integer darClicknum) {
        this.id = id;
        this.darDevid = darDevid;
        this.darDate = darDate;
        this.darFileid = darFileid;
        this.darPlaynum = darPlaynum;
        this.darClicknum = darClicknum;
    }

    @Generated(hash = 1727903317)
    public DevAdPlayRecord() {
    }

    @Override
    public String toString() {
        return "DevAdPlayRecord{" +
                "darDevid='" + darDevid + '\'' +
                ", darDate='" + darDate + '\'' +
                ", darFileid='" + darFileid + '\'' +
                ", darPlaynum=" + darPlaynum +
                ", darClicknum=" + darClicknum +
                '}';
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
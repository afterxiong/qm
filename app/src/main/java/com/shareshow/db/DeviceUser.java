package com.shareshow.db;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Property;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Created by TEST on 2018/1/8.
 */

@Entity
public class DeviceUser {

    @Id(autoincrement = true)
    private Long id;

    @Property
    private String ids;

    public String getIds() {
        return this.ids;
    }

    public void setIds(String ids) {
        this.ids = ids;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Generated(hash = 1171047406)
    public DeviceUser(Long id, String ids) {
        this.id = id;
        this.ids = ids;
    }

    @Generated(hash = 846503461)
    public DeviceUser() {
    }

    @Override
    public String toString() {
        return "DeviceUser{" +
                "id=" + id +
                ", ids='" + ids + '\'' +
                '}';
    }
}

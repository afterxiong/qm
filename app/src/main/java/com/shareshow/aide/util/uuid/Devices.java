package com.shareshow.aide.util.uuid;

/**
 * Created by xiongchengguang on 2016/11/4.
 */

public class Devices {
    private CacheUuid cacheUuid;
    private static Devices devices;

    //依赖注入框架
    public Devices(CacheUuid cacheUuid) {
        this.cacheUuid = cacheUuid;
    }

    public static Devices getInstace(CacheUuid cacheUuid) {
        if (devices == null) {
            synchronized (Devices.class) {
                if (devices == null) {
                    devices = new Devices(cacheUuid);
                }
            }
        }
        return devices;
    }

    public String getKey() {
        String id = cacheUuid.get();
        if (id == null) {
            id = createUUID();
        }
        cacheUuid.put(id);
        return id;
    }

    //生成一个128位的唯一标识符
    private String createUUID() {
        return java.util.UUID.randomUUID().toString();
    }

}

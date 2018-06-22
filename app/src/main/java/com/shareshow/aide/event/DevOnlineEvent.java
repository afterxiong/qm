package com.shareshow.aide.event;

import com.shareshow.airpc.model.RootPoint;

/**
 * Created by Administrator on 2018/3/16 0016.
 */

public class DevOnlineEvent {
    public int onlineState = 0;//0 离线，1 在线 2投屏成功 3结束投屏 4更新绑定设备实体对象
    public DevOnlineEvent(int onlineState){
        this.onlineState = onlineState;
    }
}

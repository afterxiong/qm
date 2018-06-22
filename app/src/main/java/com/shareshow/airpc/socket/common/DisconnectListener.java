package com.shareshow.airpc.socket.common;

import com.shareshow.airpc.model.RootPoint;

/**
 * Created by TEST on 2017/9/22.
 * 断流的
 */

public interface DisconnectListener {

    void disconnectStream(RootPoint rootPoint);

}

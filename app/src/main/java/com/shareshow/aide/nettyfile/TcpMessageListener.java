package com.shareshow.aide.nettyfile;


import com.shareshow.db.FileChannelBean;

/**
 * Created by TEST on 2017/12/14.
 */

public interface TcpMessageListener {

      void success(FileChannelBean bean);

      void fail(int state);

     // void complete(boolean isTimeOut);
}

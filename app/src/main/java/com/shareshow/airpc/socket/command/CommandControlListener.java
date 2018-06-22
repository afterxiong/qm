package com.shareshow.airpc.socket.command;

/**
 * Created by TEST on 2017/12/18.
 */

public interface CommandControlListener {

      void control(int cmd, int count);

      void scroll(int cmd, float moveX, float moveY);

}

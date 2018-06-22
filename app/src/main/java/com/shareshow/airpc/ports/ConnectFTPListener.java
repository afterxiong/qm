package com.shareshow.airpc.ports;


/**
 * 适配器listview中图片点击事件的监听，现无作用了
 * @author tanwei
 *
 */
public interface ConnectFTPListener{
	public void success();
	public void fail(int state);
}

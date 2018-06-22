package com.shareshow.airpc.socket.command;

import com.shareshow.airpc.model.RootPoint;

/**
 *
 */
public interface CommandListenerMobile {
	/**
	 * 搜索
	 */
	public void searchMobile(RootPoint rootPoint);
	
	/**
	 * 投屏
	 */
	public void screenMobile(RootPoint rootPoint);

	/**
	 * 断开投屏
	 */
	public void screenInterruptMobile(RootPoint rootPoint);
	/**
	 * 心跳
	 * @param mRootPoint
	 */
	public void heartBeatMobile(RootPoint mRootPoint);


	/**
	 * @param mRootPoint
	 * 切换视频源，请求重新分享
	 */
	public void swichMobileScreen(RootPoint mRootPoint);


	/**
	 * 请求I帧
	 */
	public void requestScreenFrame();

}

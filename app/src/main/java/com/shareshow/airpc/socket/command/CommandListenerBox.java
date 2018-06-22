package com.shareshow.airpc.socket.command;

import com.shareshow.airpc.model.RootPoint;

/**
 * @author xiongchengguang
 * @ClassName CommandListener
 * @Descripton 命令的监听
 * @date 2015年11月16日上午10:44:10
 */

public interface CommandListenerBox {
	
	
	/**
	 * 连接     -----Box端的程序并非Lancher端
	 */
	public void connectBox(RootPoint rootPoint);

	/**
	 * 播放    ----- 断流再次点播成功后解码端发送过来的的2002消息
	 *            或    Box端主动投屏过来手机需要有个tip显示表示屏幕滑动过了，否则投屏不上
	 */
	public void playBox(RootPoint rootPoint);

	/**
	 * 停止    -----盒子端手动退出
	 */
	public void exitBox(RootPoint rootPoint);
	
	/**
	 * 心跳    -----与Box端保持投屏心跳的链接
	 */
	public void heartBeatBox(RootPoint rootPoint);
	
    /**
     * 返回200  投屏成功
     */
	public void screenSuccessBox(RootPoint rootPoint);


	public void screenCoverBox(RootPoint rootPoint);


}

package com.shareshow.airpc.socket.command;

import com.shareshow.airpc.model.RootPoint;

/**
 * @author xiongchengguang
 * @ClassName CommandListener
 * @Descripton 命令的监听
 * @date 2015年11月16日上午10:44:10
 */

public interface CommandListenerLancher{

	/**
	 * 搜索
	 */
	public void searchLancher(RootPoint rootPoint);
	
	/**
	 * 连接
	 */
	public void connectLancher(RootPoint rootPoint);

	/**
	 * 控制
	 */
	public void controlLancher(RootPoint rootPoint);
	
	/**
	 * 屏幕投屏开启
	 */
	public void screenOpenLancher(RootPoint rootPoint);
	
	/**
	 * 密码修改
	 */
	public void passwdAlterLancher(RootPoint rootPoint);
	
	/**
	 * 检测远程控制是否正在被操作  -心跳
	 */
	public void controlHeartBeatLancher(RootPoint rootPoint);
	/**
	 * 投屏PC端后，PC端回复的消息
	 * */
	public void touPingPc(RootPoint rp);
	/**
	 * 接到pc退出投屏的指令
	 * */
	public void stopPc(RootPoint rp);

	/**
	 * 接到pc投屏的指令
	 * */
	public void pcTouPing(RootPoint rp);

	/*
	  *覆盖点播
	 */
	void pcCoverShare(RootPoint rp);
}

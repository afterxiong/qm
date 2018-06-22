package com.shareshow.airpc.ports;

import android.view.MotionEvent;

/**
 * 分享UI中，滑动事件的监听
 * @author tanwei
 *
 */
public interface MoveListener {

	public void down(MotionEvent event);
	public void up(MotionEvent event);
	public void move(MotionEvent event);
	public void showHead(int i);

}

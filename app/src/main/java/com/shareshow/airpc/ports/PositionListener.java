package com.shareshow.airpc.ports;

/**
 * 弹出提示框，"是"、“否”选着事件的监听
 * @author tanwei
 *
 */

public interface PositionListener {

	public void method(int position);

	public void cancel();
}

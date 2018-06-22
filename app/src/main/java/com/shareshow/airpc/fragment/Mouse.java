package com.shareshow.airpc.fragment;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.shareshow.aide.R;
import com.shareshow.airpc.QMCommander;

import com.shareshow.airpc.model.RootPoint;
import com.shareshow.airpc.socket.command.CommandExecutorLancher;

public class Mouse extends Fragment implements OnClickListener {

	private View vv;
	private TextView shubiao_left,shubiao_right,gunlun_up,gunlun_down;
	private FrameLayout shubiao_move;
	private int height;// 屏幕高度
	private int width;// 屏幕宽度
	
	private RootPoint rootPoint;
	
	public Mouse() {
	}
	
	@SuppressLint("ValidFragment")
	public Mouse(RootPoint rootPoint) {
		this.rootPoint=rootPoint;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
		if(vv==null)
		vv=inflater.inflate(R.layout.shubiao, container, false);
		return vv;
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		initView();
		initEvent();
		initDisplayMetrics();
	}

	private void initEvent() {
		shubiao_left.setOnClickListener(this);
		shubiao_right.setOnClickListener(this);
		gunlun_up.setOnClickListener(this);
		gunlun_down.setOnClickListener(this);
		shubiao_move.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View arg0, MotionEvent arg1) {
				touchEvent(arg1);
				return true;
			}
		});
	}

	/**
	 * 滑动事件
	 * @param event
	 */
	protected void touchEvent(MotionEvent event) {
		switch (event.getAction()) {
		case MotionEvent.ACTION_MOVE:
			String xy="";
			xy="x='"+event.getX()/width+"' y='"+event.getY()/height+"'";
			CommandExecutorLancher.getOnlyExecutor().controlMessageXY(QMCommander.SHUBIAO.TYPE_SHUBIAO_MOVE,
					rootPoint.getAddress(), xy);
			break;
		}
	}

	private void initView() {
		shubiao_left=(TextView) vv.findViewById(R.id.shubiao_left);
		shubiao_right=(TextView) vv.findViewById(R.id.shubiao_right);
		gunlun_up=(TextView) vv.findViewById(R.id.gunlun_up);
		gunlun_down=(TextView) vv.findViewById(R.id.gunlun_down);
		shubiao_move=(FrameLayout) vv.findViewById(R.id.shubiao_move);
	}
	
	private void initDisplayMetrics() {
		DisplayMetrics metrics = new DisplayMetrics();
		getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);
		// 屏幕的分辨率
		width = metrics.widthPixels;
		height = metrics.heightPixels;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.shubiao_left://左键
			control(QMCommander.SHUBIAO.TYPE_SHUBIAO_LEFT);
			break;
		case R.id.shubiao_right://右键
			control(QMCommander.SHUBIAO.TYPE_SHUBIAO_RIGHT);
			break;
		case R.id.gunlun_up://滚轮上
			control(QMCommander.SHUBIAO.TYPE_SHUBIAO_UP);
			break;
		case R.id.gunlun_down://滚轮下
			control(QMCommander.SHUBIAO.TYPE_SHUBIAO_DOWN);
			break;
		}
	}
	
	public void control(int rec){
		CommandExecutorLancher.getOnlyExecutor().controlMessage(rec,
				rootPoint.getAddress());
	}
}

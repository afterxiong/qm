package com.shareshow.airpc.util;

import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.WindowManager.LayoutParams;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.shareshow.aide.R;


public class QMPopupWindow extends PopupWindow {

	public QMPopupWindow() {
	}
	
	public QMPopupWindow(View vv , int id) {
		initPopupWindow(vv, id);
	}

	public QMPopupWindow(View vv) {
		this(vv, R.style.timepopwindow_anim_style);
	}

	public QMPopupWindow(View vv, final TextView popup_bg) {
		setOnDismissListener(new OnDismissListener(){

			@Override
			public void onDismiss() {
				popup_bg.setVisibility(View.GONE);
			}
		});
		initPopupWindow(vv, R.style.timepopwindow_anim_style);
	}

	private void initPopupWindow(View vv , int id){
		setWidth(LayoutParams.WRAP_CONTENT);
		setHeight(LayoutParams.WRAP_CONTENT);
		setBackgroundDrawable(new ColorDrawable(0x00000000));
		setOutsideTouchable(true);
		setFocusable(true);
		setAnimationStyle(id);
		setContentView(vv);
	}
}

package com.shareshow.airpc.util;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.shareshow.aide.R;
import com.shareshow.airpc.ports.PositionListener;

public class QMDialog extends Dialog {

	public QMDialog(Context context) {
		super(context);
	}
	
	public QMDialog(Context context, int theme) {
		super(context, theme);
	}
	
	public QMDialog(Context context, View vv) {
		this(context, R.style.alert_dialog);
		showDialog(vv,false);
	}
	
	public QMDialog(Context context, View vv, boolean flag) {
		this(context, R.style.alert_dialog);
		showDialog(vv,flag);
	}

	public QMDialog(Context context, int titleId, int okId, final PositionListener mCPosition){
		this(context, titleId, okId, 2, mCPosition);//2表示正常情况
	}
	
	public QMDialog(Context context, int titleId, int okId, int type, final PositionListener mCPosition){
		this(context, R.style.alert_dialog);
		View vv = View.inflate(context, R.layout.app_tip, null);
		((TextView)vv.findViewById(R.id.title)).setText(context.getResources().getString(titleId));
		Button comfirm=(Button) vv.findViewById(R.id.comfirm);
		comfirm.setText(context.getResources().getString(okId));
		(vv.findViewById(R.id.cancel)).setOnClickListener(new  View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				dismiss();
				mCPosition.cancel();

			}
		});
		comfirm.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				dismiss();
				mCPosition.method(0);
			}
		});
		showDialog(vv,false);
	}
	
	private void showDialog(View vv, boolean flag){
		setContentView(vv);
		setCancelable(flag);
		setCanceledOnTouchOutside(flag);
		show();
	}
	
}

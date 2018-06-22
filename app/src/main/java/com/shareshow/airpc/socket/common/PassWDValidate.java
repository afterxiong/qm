package com.shareshow.airpc.socket.common;

import android.app.Activity;
import android.os.Handler;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.shareshow.aide.R;
import com.shareshow.airpc.ports.InputEditListener;
import com.shareshow.airpc.ports.PassWDCallBack;
import com.shareshow.airpc.util.QMDialog;
import com.shareshow.airpc.widget.PasswordInputView;

/**
 * 输入密码验证弹出框
 * @author tanwei
 *
 */
public class PassWDValidate {

	private QMDialog myDialog;
	private Activity context;
	
	public PassWDValidate(){
	}
	
	public PassWDValidate(Activity context){
		this();
		this.context=context;
	}
	
	public void showPassWDInput(final PassWDCallBack callBack){
		View vv = View.inflate(context, R.layout.input_pw, null);
		final PasswordInputView texts = (PasswordInputView) vv
				.findViewById(R.id.input);// 自定义控件，网上找的
		// 给输入密码设置输入数字的监听事件，4位数全部输入完就调用该方法
		texts.setEditChaged(new InputEditListener() {

			@Override
			public void textChange(View v, CharSequence text) {
				hideSoftInputView();
				if(callBack!=null)
					callBack.callBack(text+"");
			}
		});
		myDialog=new QMDialog(context, vv,true);
		new Handler().postDelayed(new Runnable() {

			@Override
			public void run() {
				showSoftInputView(texts);
			}
		}, 300);
	}
	
	// 显示软键盘
	private void showSoftInputView(EditText text) {
		((InputMethodManager) context.getSystemService(context.INPUT_METHOD_SERVICE))
				.showSoftInput(text, 0);
	}
	// 隐藏软键盘
	private void hideSoftInputView() {
		if(myDialog!=null)
			myDialog.dismiss();
		InputMethodManager manager = ((InputMethodManager) context
				.getSystemService(Activity.INPUT_METHOD_SERVICE));
		if (context.getWindow().getAttributes().softInputMode != WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN) {
			if (context.getCurrentFocus() != null)
				manager.hideSoftInputFromWindow(context.getCurrentFocus()
						.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
		}
	}
}

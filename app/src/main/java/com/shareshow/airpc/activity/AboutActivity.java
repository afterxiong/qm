package com.shareshow.airpc.activity;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;


import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import com.shareshow.aide.R;
import com.shareshow.airpc.widget.BaseActivity;


/**
 * 关于界面
 */

//@ContentView(R.layout.x_activity_about)
public class AboutActivity extends BaseActivity {

	@BindView(R.id.version)
	public TextView versionText;

	@BindView(R.id.toolbar)
	public Toolbar toolbar;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
		setContentView(R.layout.x_activity_about);
		ButterKnife.bind(this);
		initView();
		initToolbar();
		initVersionCode();

	}
/**
 * 初始化点击事件
 * */
	private void initView(){
		TextView tv = (TextView) findViewById(R.id.tv);
		TextView tv2 = (TextView) findViewById(R.id.tv2);
		String text=getResources().getString(R.string.about_net);
		//String text2=getResources().getString(R.string.about_qq);
		SpannableString builder = new SpannableString(text);
		builder.setSpan(new ForegroundColorSpan(Color.BLUE), 3, text.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		tv.setText(builder);
		//SpannableString builder2 = new SpannableString(text2);
//		builder2.setSpan(new ForegroundColorSpan(Color.BLUE), 3, text2.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//		builder2.setSpan(new ForegroundColorSpan(Color.BLACK), text2.length()-8, text2.length()-7, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//		tv2.setText(builder2);
	}

	private void initToolbar() {
//		setSupportActionBar(toolbar);
//		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//		getSupportActionBar().setDisplayShowTitleEnabled(false);
//		toolbar.setNavigationOnClickListener(new View.OnClickListener() {
//			@Override
//			public void onClick(View v) {
//
//			}
//		});
	}
	public void back(View view){
		onBackPressed();
	}

	/**
	 * 获取版本号
	 */
	private void initVersionCode() {
		try {
			String code = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
			versionText.setText(getResources().getString(R.string.app_name) + code);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@OnClick(R.id.tv)
	public void toNet(View v) {
		Uri uri= Uri.parse("http://www.shareshow.com.cn");
		Intent intent = new Intent(Intent.ACTION_VIEW, uri);
		startActivity(intent);
	}

//	@OnClick(R.id.tv2)
//	public void makeCall(View v) {
//		Uri uri= Uri.parse("tel:02787179095");
//		Intent intent=new Intent(Intent.ACTION_CALL,uri);
//		startActivity(intent);
//	}


//	@OnClick(R.id.help_layout)
//	public void help(View view) {
//		Intent intent = new Intent(this, HelpActivity.class);
//		startActivity(intent);
//	}

//	@OnClick(R.id.propose_layout)
//	public void propose(View view) {
//		Intent intent = new Intent(this, ProposeActivity.class);
//		startActivity(intent);
//	}

}

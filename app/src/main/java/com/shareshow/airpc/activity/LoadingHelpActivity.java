package com.shareshow.airpc.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.shareshow.aide.R;


public class LoadingHelpActivity extends Activity {

@Override
protected void onCreate(Bundle savedInstanceState) {
	// TODO Auto-generated method stub
	super.onCreate(savedInstanceState);
	setContentView(R.layout.activit_loading_help);
	TextView tv = (TextView) findViewById(R.id.tv1);
	String text=getResources().getString(R.string.downloadNet);
    SpannableString builder = new SpannableString(text);
    builder.setSpan(new ForegroundColorSpan(Color.BLUE), 9, text.length()-6, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
    tv.setText(builder);
    tv.setOnClickListener(new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			Uri uri= Uri.parse("http://www.shareshow.com.cn");
			Intent intent = new Intent(Intent.ACTION_VIEW, uri);
			startActivity(intent);
		}
	});
}


}

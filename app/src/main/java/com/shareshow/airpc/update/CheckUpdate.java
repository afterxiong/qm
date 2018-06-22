package com.shareshow.airpc.update;

import android.app.Activity;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.shareshow.aide.R;
import com.socks.library.KLog;
import com.shareshow.App;
import com.shareshow.airpc.util.QMDialog;


import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class CheckUpdate {

	private static final String url = "http://www.shareshow.com.cn/download/update/index.html";
	private Activity context;
	private QMDialog dialog;
	public CheckUpdate(Activity context) {
		super();
		this.context=context;
	}

	public void startCheckUpdate() {
		OkHttpClient okHttpClient = new OkHttpClient();
		Request request = new Request.Builder().url(url).build();
		Call call = okHttpClient.newCall(request);
		call.enqueue(new Callback() {

			public void onResponse(Call call, Response response) throws IOException {
				String temp = response.body().string();
				KLog.json(temp);
				try {
					JSONObject object = new JSONObject(temp);
					JSONObject box = object.getJSONObject("androidmobile");
					UpdateInfo info = new UpdateInfo();
					info.setVersion(Integer.valueOf(box.get("m_versinio").toString()));
					info.setReborn(box.get("m_reborn").toString());
					info.setUrl(box.get("m_url").toString());
					Message message=handler.obtainMessage();
					message.obj=info;
					handler.sendMessage(message);
					
				} catch (Exception e){
					e.printStackTrace();

				}

			}

			public void onFailure(Call call, IOException e) {

			}
		});
	}

	
	private Handler handler=new Handler(){

		@Override
		public void handleMessage(Message msg){
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			checkUpdate((UpdateInfo)msg.obj);
		}
		
	};
	
	
	private void checkUpdate(final UpdateInfo info){
		try {
			int QMApplicationVersion = context.getPackageManager().getPackageInfo(App.getApp().getPackageName(), 0).versionCode;
			if (QMApplicationVersion < info.getVersion()) {
				startDownLoad(info);
			} else {
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void startDownLoad(final UpdateInfo info){
		View vv= View.inflate(context, R.layout.version_update_dialog, null);
		((TextView) vv.findViewById(R.id.text)).setText(info.getReborn());
		((Button) vv.findViewById(R.id.comfirm)).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v){
				
				if(dialog!=null)
					dialog.dismiss();
				new DownloadTask(context).execute(info.getUrl());
				
			}
		});


		//隐藏了取消按钮实现了版本强制更新
		((Button) vv.findViewById(R.id.cancel)).setOnClickListener(new OnClickListener(){

				@Override
				public void onClick(View v) {
					if(dialog!=null)
						dialog.dismiss();
				}
			});
			
			dialog=new QMDialog(context, vv);
	}

}

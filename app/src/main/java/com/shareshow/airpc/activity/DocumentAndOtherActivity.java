package com.shareshow.airpc.activity;

import android.os.Bundle;
import android.view.View;

import com.shareshow.aide.R;
import com.umeng.analytics.MobclickAgent;
import com.shareshow.airpc.adapter.DocumentOtherAdapter;
import com.shareshow.airpc.model.QMLocalFile;
import com.shareshow.airpc.model.RootPoint;
import com.shareshow.airpc.util.QMUtil;
import com.shareshow.airpc.util.SortUtils;


/**
 * 本地‘文档’和‘其他’类
 * @author tanwei
 *
 */
public class DocumentAndOtherActivity extends BaseLocalFileActivity {

	private DocumentOtherAdapter<QMLocalFile> adapter;
	
	private boolean isFinish;
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		initData();
	}


	public void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
	}

	public void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
	}

	private void initData(){
		switch (type){
		case 1:
			al.addAll(QMUtil.getInstance().getQmDocumentFile().getDocument());
			break;
		case 4:
			al.addAll(QMUtil.getInstance().getQmDocumentFile().getOther());
			break;
		}

		adapter = new DocumentOtherAdapter<QMLocalFile>(this, al);
		listView.setAdapter(adapter);
		//SortUtils.sort(this,0, adapter, al);
		runOnUiThread(new Runnable(){
			@Override
			public void run() {
				adapter.notifyDataSetChanged();
			}
		});
		showWithoutFile();
	}

	@Override
	public void upAdapter(int flag){
		switch (flag) {
		case 1:
			adapter.notifyDataSetChanged();
			break;
		case 2:
			adapter.isLongclick(isLongclickState);
			break;
		}
	}


	@Override
	public void sortFile(int i){
		SortUtils.sort(this,i,adapter,al);
	}


	private void showWithoutFile(){
		if(al.size()==0){
			without_localfile.setText(getResources().getString(R.string.without_document));
			without_localfile.setVisibility(View.VISIBLE);
		}else{
			listView.setVisibility(View.VISIBLE);
			without_localfile.setVisibility(View.GONE);
		}
	}

	@Override
	public void touPingPc(RootPoint rp) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void stopPc(RootPoint rp) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void pcTouPing(RootPoint rp) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void pcCoverShare(RootPoint rp) {

	}

//	@Override
//	protected void onDestroy() {
//		super.onDestroy();
//        if(!isFinish){
//			isFinish=true;
//       }
//	}
}

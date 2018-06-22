package com.shareshow.airpc.activity;

import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.provider.MediaStore.Images.ImageColumns;
import android.provider.MediaStore.Images.Media;
import android.view.View;

import com.shareshow.aide.R;
import com.umeng.analytics.MobclickAgent;
import com.shareshow.airpc.adapter.AlbumFileAdapter;
import com.shareshow.airpc.adapter.QMBaseAdapter;
import com.shareshow.airpc.adapter.VideoFileAdapter;
import com.shareshow.airpc.model.QMLocalFile;
import com.shareshow.airpc.model.RootPoint;
import com.shareshow.airpc.util.QMUtil;
import com.shareshow.airpc.util.SortUtils;


import java.io.File;
import java.util.Collections;
import java.util.Comparator;

/**
 * 读取本地相册或视频
 * @author tanwei
 *
 */
public class AlbumAndVideoFileActivity extends BaseLocalFileActivity{

	private QMBaseAdapter<QMLocalFile> adapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		initData();
		getPictureVideo();
	}
	

	public void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
	}

	public void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
	}

	/**
	 * type   =2相册   =3视频
	 * 根据类型添加适配器
	 */
	private void initData(){
		switch (type){
		case 2:
			adapter = new AlbumFileAdapter<QMLocalFile>(this, al);
			break;
		case 3:
			adapter = new VideoFileAdapter<QMLocalFile>(this, al);
			break;
		}
		listView.setAdapter(adapter);
//		wheel.setVisibility(View.GONE);
	}
	
	/**
	 * 线程加载本地图片和视频
	 */
	private void getPictureVideo(){
		//出现加载动画
		QMUtil.getInstance().showProgressDialog(this, R.string.getPicture_Video);
		final Handler handler=new Handler(){
			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				//关闭加载动画
				QMUtil.getInstance().closeDialog();
				showWithoutFile();
				if(adapter!=null){
					adapter.notifyDataSetChanged();
				}
			}
		};

		//开启线程加载图片或者视频
		new Thread(){
			public void run(){
				try{
					switch (type){
					case 2:
						getPicture(getIntent().getStringExtra("name"));
						break;
					case 3:
						getVideo();
						break;
					}	
					sortByTime();
					handler.sendEmptyMessage(0);
				 }catch (Exception e){
					e.printStackTrace();
				 }
			};
		}.start();
	}

	/**
	 * 获取某相册下所有图片
	 */
	private void getPicture(String name) {
		Cursor cursor = getContentResolver().query(Media.EXTERNAL_CONTENT_URI, new String[] { ImageColumns.BUCKET_DISPLAY_NAME,
	            ImageColumns.DATA, ImageColumns.DATE_ADDED, ImageColumns.DISPLAY_NAME, ImageColumns.SIZE }, "bucket_display_name = ?",
	            new String[] { name }, ImageColumns.DATE_ADDED);
		 String old_displayName="";
		if (null != cursor&&cursor.getCount() > 0) {
			while(cursor.moveToNext()){
				long size=cursor.getLong(cursor.getColumnIndex(ImageColumns.SIZE));
	            
	        	String displayName = cursor.getString(cursor.getColumnIndex(ImageColumns.DISPLAY_NAME));
	        	String path =cursor.getString(cursor.getColumnIndex(ImageColumns.DATA));
	        	if(!new File(path).exists()||new File(path).length()==0)
					continue;
	        	if(old_displayName.equals(path))
	        		continue;
				String update = cursor.getString(cursor.getColumnIndex(ImageColumns.DATE_ADDED));
				
				QMLocalFile localFile = new QMLocalFile(displayName,path,size, Long.parseLong(update)*1000, 7);
				al.add(localFile);
				old_displayName=path;	
			}
		}
	    cursor.close();
	}
	/**
	 * 获取所有视频
	 */
	public void getVideo(){
		Cursor cursor = getContentResolver().query(
				MediaStore.Video.Media.EXTERNAL_CONTENT_URI, null, null, null,
				null);
		if (cursor != null) {
			while (cursor.moveToNext()) {
				String displayName = cursor
						.getString(cursor
								.getColumnIndexOrThrow(MediaStore.Video.Media.DISPLAY_NAME));
				String path = cursor.getString(cursor
						.getColumnIndexOrThrow(MediaStore.Video.Media.DATA));
				if(!new File(path).exists()||new File(path).length()==0)
					continue;
				String update = cursor
						.getString(cursor
								.getColumnIndexOrThrow(MediaStore.Video.Media.DATE_ADDED));
				long size = cursor.getLong(cursor
						.getColumnIndexOrThrow(MediaStore.Video.Media.SIZE));
				QMLocalFile localFile = new QMLocalFile(displayName,path, 
						size, Long.parseLong(update)*1000, 8);
				al.add(localFile);
			}
			cursor.close();
		}
	}
	/**
	 * 按照时间排序
	 */
	private void sortByTime(){
		Collections.sort(al,new Comparator<QMLocalFile>(){

			@Override
			public int compare(QMLocalFile lhs, QMLocalFile rhs) {
				if(lhs.getUpdate()>rhs.getUpdate())
					return -1;
				if(lhs.getUpdate()==rhs.getUpdate())
					return 0;
				
					return 1;
			}
		});
	}


	@Override
	public void upAdapter(int flag) {
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
	public void sortFile(int i) {
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

}
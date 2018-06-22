package com.shareshow.airpc.activity;

import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore.Images.ImageColumns;
import android.provider.MediaStore.Images.Media;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import com.shareshow.aide.R;
import com.umeng.analytics.MobclickAgent;
import com.shareshow.airpc.adapter.AlbumNameAdapter;
import com.shareshow.airpc.character.CharacterParser;
import com.shareshow.airpc.character.PinyinComparator;
import com.shareshow.airpc.model.QMLocalFile;
import com.shareshow.airpc.model.RootPoint;
import com.shareshow.airpc.util.QMUtil;
import com.shareshow.airpc.util.SortUtils;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

/**
 * 读取相册名称列表
 * @author tanwei
 *
 */
public class AlbumNameActivity extends BaseLocalFileActivity implements OnItemClickListener, OnItemLongClickListener, OnClickListener {

	private AlbumNameAdapter<QMLocalFile> adapter;
	/**
	 * 汉字转换成拼音的类
	 */
	private CharacterParser characterParser;
	/**
	 * 根据拼音来排列ListView里面的数据类
	 */
	private PinyinComparator pinyinComparator;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initData();
	}

	public static int album;
	private void initData() {
		sort.setVisibility(View.GONE);
		albumName=0;//指明是相册名列表
		//实例化汉字转拼音类
		characterParser = CharacterParser.getInstance();				
		pinyinComparator = new PinyinComparator();
		adapter=new AlbumNameAdapter<QMLocalFile>(this, al);
		listView.setAdapter(adapter);
	//	wheel.setVisibility(View.GONE);
		getAlbumName();
	}
	

	public void onResume() {
		super.onResume();
		album=1;
		MobclickAgent.onResume(this);
	}

	@Override
	protected void onStop() {
		super.onStop();
		album=0;
	}

	public void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
	}
	/**
	 * 获取相册名称列表
	 */
	private void getAlbumName(){
		QMUtil.getInstance().showProgressDialog(this, R.string.getAlbumName);
		final Handler handler=new Handler(){
			@Override
			public void handleMessage(Message msg){
				super.handleMessage(msg);
				QMUtil.getInstance().closeDialog();
				showWithoutFile();
				adapter.notifyDataSetChanged();
			}
		};
		new Thread(){
			public void run() {
				try {
					sortByCharacter();
					handler.sendEmptyMessage(0);
				} catch (Exception e) {
					e.printStackTrace();
				} 
			};
		}.start();
	}

	/**
	 * 按照相册名称字母排序
	 */
	private void sortByCharacter(){
		ArrayList<QMLocalFile> albums=getAlbums();

		for(int i=0; i<albums.size(); i++){
			QMLocalFile sortModel =albums.get(i);
			//汉字转换成拼音
			String pinyin = characterParser.getSelling(sortModel.getName());
			String sortString = pinyin.substring(0, 1).toUpperCase();
			// 正则表达式，判断首字母是否是英文字母
			if(sortString.matches("[A-Z]")){
				sortModel.setSortLetters(sortString.toUpperCase());
			}else{
				sortModel.setSortLetters("#");
			}
			al.add(sortModel);
		}
		// 根据a-z进行排序源数据
		Collections.sort(al, pinyinComparator);
	}
	
	/** 获取所有相册列表 
	 * @return */
	private ArrayList<QMLocalFile> getAlbums() {
		ArrayList<QMLocalFile> kk=new ArrayList<QMLocalFile>();
		HashMap<String, Integer> map=new HashMap<String, Integer>();
	    Cursor cursor =  getContentResolver().query(Media.EXTERNAL_CONTENT_URI, new String[] { ImageColumns.DATA,
	            ImageColumns.BUCKET_DISPLAY_NAME, ImageColumns.SIZE , ImageColumns.DATE_ADDED}, null, null, null);
	    if (cursor == null || !cursor.moveToNext())
	        return kk;
	    cursor.moveToLast();
	    do {
	        String name = cursor.getString(cursor.getColumnIndex(ImageColumns.BUCKET_DISPLAY_NAME));

	        String path =cursor.getString(cursor.getColumnIndex(ImageColumns.DATA));
	        String update = cursor.getString(cursor.getColumnIndex(ImageColumns.DATE_ADDED));
	        if(!new File(path).exists())
	        	continue;
	        if(map.containsKey(name)){
	        	for (int i = 0; i <kk.size(); i++) {
					if(kk.get(i).getName().equals(name)){
						kk.get(i).setType(kk.get(i).getType()+1);
						kk.get(i).setSize(kk.get(i).getType());
						kk.get(i).setUpdate(Long.parseLong(update)*1000);
						break;
					}
				}
	        }else{
	        	map.put(name, 1);
	        	QMLocalFile localFile = new QMLocalFile(name,path, 1);
	        	localFile.setSize(1);
	        	localFile.setUpdate(Long.parseLong(update)*1000);
	        	kk.add(localFile);
	        }
	       
	    } while (cursor.moveToPrevious());
	    cursor.close();
	    return kk;
	}
	
	private void showWithoutFile(){
		show_upLoad.setVisibility(View.GONE);
		if(al.size()==0){
			without_localfile.setText(getResources().getString(R.string.without_document));
			without_localfile.setVisibility(View.VISIBLE);
		}else{
			listView.setVisibility(View.VISIBLE);
			without_localfile.setVisibility(View.GONE);
		}
	}
	
	@Override
	public void passwdVertifyCallBack(RootPoint rp) {
		
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

package com.shareshow.airpc.activity;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;

import com.shareshow.aide.R;
import com.umeng.analytics.MobclickAgent;
import com.shareshow.airpc.adapter.DocumentOtherAdapter;
import com.shareshow.airpc.menu.SwipeMenu;
import com.shareshow.airpc.menu.SwipeMenuCreator;
import com.shareshow.airpc.menu.SwipeMenuItem;
import com.shareshow.airpc.menu.SwipeMenuListView.OnMenuItemClickListener;
import com.shareshow.airpc.model.QMLocalFile;
import com.shareshow.airpc.model.RootPoint;
import com.shareshow.airpc.util.QMFileType;
import com.shareshow.airpc.util.QMUtil;
import com.shareshow.airpc.util.SortUtils;

import java.io.File;
import java.util.Collections;
import java.util.Comparator;

/**
 * 本地‘下载’的类
 * @author tanwei
 *
 */
public class DownLoadFileActivity extends BaseLocalFileActivity {

	private DocumentOtherAdapter<QMLocalFile> adapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
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
		getDownLoadFile();
		initSwipeMenu();
		showWithoutFile();
	}
	
	/**
	 * 初始化右滑列表
	 */
	private void initSwipeMenu(){
		adapter = new DocumentOtherAdapter<QMLocalFile>(this, al);
		swipeMenu_ListView.setAdapter(adapter);
		SwipeMenuCreator creator = new SwipeMenuCreator(){

			@Override
			public void create(SwipeMenu menu) {
				SwipeMenuItem item2 = new SwipeMenuItem(DownLoadFileActivity.this);
				item2.setBackground(new ColorDrawable(Color.argb(0xff,0xe4, 0x4d,
						0x3f)));
				item2.setWidth(QMUtil.getInstance().dp2px(DownLoadFileActivity.this,90));
				item2.setIcon(null);
				item2.setTitle(getResources().getString(R.string.delete));
				item2.setTitleSize(15);
				item2.setTitleColor(Color.WHITE);
				menu.addMenuItem(item2);
			}
		};
		swipeMenu_ListView.setMenuCreator(creator);
		swipeMenu_ListView.setOnMenuItemClickListener(new OnMenuItemClickListener() {
			@Override
			public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
				swipeMenu_ListView.smoothCloseMenu();
				switch (index) {
				case 0:
					new File(al.get(position).getPath()).delete();
					al.remove(position);
					adapter.notifyDataSetChanged();
					showWithoutFile();
					break;
				}
				return true;
			}
		});
	}

	/**
	 * 加载从任盒或手机中下载的文件
	 */
	private void getDownLoadFile() {
		File root=new File(QMFileType.LOCALPATH);
		File[] files=root.listFiles();
		if(root==null||files==null){
			return;
		}
		for (File file:files) {
			if(file.isDirectory()){
				continue;
			}else{
				String name = file.getName();
				long size=file.length();
				if(size==0)
					continue;
				int type=QMFileType.getType(file.getName());
				String path = file.getPath();
				long update=file.lastModified();
				QMLocalFile localFile=new QMLocalFile(name,path,  size, update, type);
				al.add(localFile);
			}
		}
		sortByTime();
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


	public void notifyChangeData(){
		if(adapter!=null){
			adapter.notifyDataSetChanged();
		//	wheel.setVisibility(View.GONE);
		}
	}

	@Override
	public void sortFile(int i) {
		SortUtils.sort(this,i,adapter,al);
	}

	/**
	 * 按照时间排序
	 */
	private void sortByTime() {
		Collections.sort(al,new Comparator<QMLocalFile>() {

			@Override
			public int compare(QMLocalFile lhs, QMLocalFile rhs) {
				if(lhs.getUpdate()>rhs.getUpdate())
					return -1;
				if(lhs.getUpdate()==rhs.getUpdate())
					return  compare1(lhs.getUpdate(), rhs.getUpdate());;
				
					return 1;
			}
		});
		//将数据进行倒过来
	//	Collections.reverse(al);
	}
	public int compare1(Long o1, Long o2) {
		long val1 = o1.longValue();
		long val2 = o2.longValue();
		return (val1 < val2 ? -1 : (val1 == val2 ? 0 : 1));

	}
	private void showWithoutFile(){
		if(al.size()==0){
			without_localfile.setText(getResources().getString(R.string.without_download_file));
			without_localfile.setVisibility(View.VISIBLE);
		}else{
			swipeMenu_ListView.setVisibility(View.VISIBLE);
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

package com.shareshow.airpc.activity;

import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.shareshow.aide.R;
import com.shareshow.airpc.util.DebugLog;


import java.util.ArrayList;

/**
 * 滑动图片的父类
 * 抽象类
 * @author tanwei
 *
 */
public abstract class PhotoScanActivity extends ConnectFTPActivity implements OnClickListener, OnPageChangeListener {
	
	private ViewPager viewpager;
	private LinearLayout show_head;
	private LinearLayout back;
	public TextView title_head;
	private LinearLayout buttom;
	private LinearLayout show_share;
	private ImageView share;
	public ImageView upOrdownLoad;
	
	private ArrayList<View> view =null;
	public int position;
	
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_photoscan);
		position=getIntent().getIntExtra("position", 0);
		initView();
		initEvent();
	}
	
	private void initView() {
		viewpager=(ViewPager) findViewById(R.id.viewpager);
		show_head=(LinearLayout) findViewById(R.id.show_head);
		back=(LinearLayout) findViewById(R.id.back);
		buttom=(LinearLayout) findViewById(R.id.buttom);
		show_share=(LinearLayout) findViewById(R.id.show_share);
		title_head=(TextView) findViewById(R.id.title_head);
		share=(ImageView) findViewById(R.id.share);
		upOrdownLoad=(ImageView) findViewById(R.id.upOrdownLoad);
	}
	
	private void initEvent() {
		back.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		share.setOnClickListener(this);
		upOrdownLoad.setOnClickListener(this);
	}

	/**
	 * 装配数据
	 * @param size
	 */
	public void loadData(int size,boolean flag,int layoutId) {
		if(flag){
			show_share.setVisibility(View.GONE);
			upOrdownLoad.setImageResource(R.drawable.down_load_bg);
		}
		view=new ArrayList<View>();
		for (int i = 0; i <size; i++) {
			View vv= View.inflate(this,layoutId, null);
			view.add(vv);
		}
		viewpager.setAdapter(new GuideAdapter());
		viewpager.addOnPageChangeListener(this);
		viewpager.setCurrentItem(position);
	}

	/**
	 * 点击显示头部和底部视图
	 */
	public void onPointClick(){
		if(show_head.getVisibility()== View.VISIBLE){
			show_head.setVisibility(View.GONE);
			buttom.setVisibility(View.GONE);
		}else{
			show_head.setVisibility(View.VISIBLE);
			if(getIntent().getIntExtra("scan", 0)==0)
				buttom.setVisibility(View.VISIBLE);
		}
	}

	/**定义适配器*/
	private class GuideAdapter extends PagerAdapter {
	
		@Override
		public int getCount() {
			return view.size();
		}
		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0==arg1;
		}
		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			container.removeView((View)object);
		}
		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			DebugLog.showLog(this,"instantiateItem");
			View vv=view.get(position);
			itemView(vv, position);
			container.addView(vv);
			return vv;
		}
	}

	public abstract void itemView(View vv, int position);

	@Override
	public void onPageScrollStateChanged(int arg0) {
	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {
	}
	
	@Override
	public void onPageSelected(int postion) {
		this.position=postion;
	}
}

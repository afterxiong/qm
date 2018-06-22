package com.shareshow.airpc.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.shareshow.aide.R;
import com.shareshow.aide.activity.MainActivity;
import com.shareshow.airpc.widget.BaseActivity;
import com.shareshow.airpc.Collocation;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

//@ContentView(R.layout.x_activity_guide)
public class GuideActivity extends BaseActivity {

	@BindView(R.id.guideViewPager)
    ViewPager guideViewPager;


	private ArrayList<View> viewArrayList = new ArrayList<>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.x_activity_guide);
		ButterKnife.bind(this);
		initDataView();
		initViewPager();
	}

	private void initDataView() {
		View view1 = View.inflate(this, R.layout.guide_item, null);
		View view2 = View.inflate(this, R.layout.guide_item, null);
		View view3 = View.inflate(this, R.layout.guide_item, null);
		viewArrayList.add(view1);
		viewArrayList.add(view2);
		viewArrayList.add(view3);
	}

	private void initViewPager() {
		//设置Adapter
		guideViewPager.setAdapter(new GuideAdapter());
		//添加Indicator
		guideViewPager.addOnPageChangeListener(new GuidePagerListener());
	}

	/**
	 * 定义适配器
	 */
	class GuideAdapter extends PagerAdapter {

		@Override
		public int getCount() {
			return viewArrayList.size();
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == arg1;
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			container.removeView((View) object);
		}

		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			View view = viewArrayList.get(position);
			ImageView img = (ImageView) view.findViewById(R.id.img);
			Button btn = (Button) view.findViewById(R.id.next);
			switch (position) {
				case 0:
					img.setImageResource(R.mipmap.guide_one);
					btn.setVisibility(View.GONE);
					break;
				case 1:
					img.setImageResource(R.mipmap.guide_two);
					btn.setVisibility(View.GONE);
					break;
				default:
					img.setImageResource(R.mipmap.guide_three);
					btn.setVisibility(View.VISIBLE);
					break;
			}

			btn.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					skipToMainActivity();
				}
			});
			container.addView(view);
			return view;
		}
	}

	private void skipToMainActivity() {
		Collocation.getCollocation().saveLogo();
		Intent intent = new Intent(this, MainActivity.class);
		startActivity(intent);
		finish();
	}

	private class GuidePagerListener implements OnPageChangeListener {
		/**
		 * 页面滚动时执行
		 */
		@Override
		public void onPageScrolled(int position, float offset, int arg2) {

		}

		@Override
		public void onPageScrollStateChanged(int arg0) {

		}

		@Override
		public void onPageSelected(int postion) {
		}
	}
}

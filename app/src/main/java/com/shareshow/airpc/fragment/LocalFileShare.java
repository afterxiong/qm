package com.shareshow.airpc.fragment;

import android.app.Fragment;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.shareshow.aide.R;
import com.shareshow.airpc.activity.AlbumAndVideoFileActivity;
import com.shareshow.airpc.activity.AlbumNameActivity;
import com.shareshow.airpc.activity.DocumentAndOtherActivity;
import com.shareshow.airpc.activity.DownLoadFileActivity;
import com.shareshow.airpc.activity.LocalPhotoScanActivity;
import com.shareshow.airpc.adapter.LocalFileAdapter;
import com.shareshow.airpc.menu.SwipeMenu;
import com.shareshow.airpc.menu.SwipeMenuCreator;
import com.shareshow.airpc.menu.SwipeMenuItem;
import com.shareshow.airpc.menu.SwipeMenuListView;
import com.shareshow.airpc.model.LancherFile;
import com.shareshow.airpc.model.QMLocalFile;
import com.shareshow.airpc.util.OpenAppUtils;
import com.shareshow.airpc.util.QMDbUtil;
import com.shareshow.airpc.util.QMUtil;
import com.shareshow.airpc.widget.ProgressWheel;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnItemClick;
import butterknife.Unbinder;

public class LocalFileShare extends Fragment {

	@BindView(R.id.document)
    LinearLayout document;
	@BindView(R.id.album)
    LinearLayout album;
	@BindView(R.id.video)
    LinearLayout video;
	@BindView(R.id.other)
    LinearLayout other;
	@BindView(R.id.download)
    LinearLayout download;
	@BindView(R.id.list)
	 SwipeMenuListView list;
	@BindView(R.id.without_share)
    TextView without_share;
	@BindView(R.id.wheel)
    ProgressWheel wheel;

	public ArrayList<LancherFile> al = new ArrayList<LancherFile>();

	public LocalFileAdapter<LancherFile> adapter;

	public Handler handler =new Handler();
	public Unbinder unbinder;

	public LocalFileShare(){

	}


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View vv = inflater.inflate(R.layout.fragment_fileshare, container, false);
		unbinder = ButterKnife.bind(this,vv);
		//x.view().inject(this, vv);
		return vv;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState){
		super.onActivityCreated(savedInstanceState);
		initSwipeMenu();
		initData();
	}

	private Handler mHandler =new Handler(){
		@Override
		public void dispatchMessage(Message msg) {
			super.dispatchMessage(msg);
			switch (msg.what){
				case 0x0001:
					Bundle bundle= msg.getData();
					ArrayList<LancherFile> shareFile= (ArrayList<LancherFile>) bundle.getSerializable("sharefile");
					al.clear();
					if (shareFile != null)
						al.addAll(shareFile);
					if (al.size() == 0)
						without_share.setVisibility(View.VISIBLE);
					else
						without_share.setVisibility(View.GONE);
					adapter.notifyDataSetChanged();
					wheel.setVisibility(View.GONE);
					break;
			}

		}
	};


	public void initData(){
		if (adapter == null)
			return;
		new Thread(new Runnable() {
			@Override
			public void run(){
				ArrayList<LancherFile> shareFiles = QMUtil.getInstance().getQmDocumentFile().getShareFile();
				Message msg= new Message();
				Bundle bundle=new Bundle();
				bundle.putSerializable("sharefile",shareFiles);
                msg.setData(bundle);
				msg.what=0x0001;
				mHandler.sendMessage(msg);
			}
		}).start();
	}

	public void initSwipeMenu(){
		adapter = new LocalFileAdapter<LancherFile>(getActivity(), al);
		list.setAdapter(adapter);
//		swipeRefresh.setOnRefreshListener(this);
//		swipeRefresh.setColorSchemeResources(android.R.color.holo_red_light, android.R.color.holo_green_light, android.R.color.holo_blue_bright, android.R.color.holo_orange_light);
		SwipeMenuCreator creator = new SwipeMenuCreator(){
			@Override
			public void create(SwipeMenu menu) {
				switch (menu.getViewType()) {
					case 0:
					case 1:
						createMenu(menu);
						break;
				}
			}

			public void createMenu(SwipeMenu menu) {
				SwipeMenuItem item1 = new SwipeMenuItem(getActivity());
				item1.setBackground(new ColorDrawable(Color.argb(0xaa, 0xdd, 0xdd,
						0xdd)));
				item1.setWidth(QMUtil.getInstance().dp2px(getActivity(), 90));
				item1.setIcon(null);
				if (menu.getViewType() == 0)
					item1.setTitle(getResources().getString(R.string.downlaod_forbidden));
				else
					item1.setTitle(getResources().getString(R.string.downlaod_allow));
				item1.setTitleSize(15);
				item1.setTitleColor(Color.WHITE);
				menu.addMenuItem(item1);

				SwipeMenuItem item2 = new SwipeMenuItem(getActivity());
				item2.setBackground(new ColorDrawable(Color.argb(0xff, 0xe4, 0x4d,
						0x3f)));
				item2.setWidth(QMUtil.getInstance().dp2px(getActivity(), 90));
				item2.setIcon(null);
				item2.setTitle(getResources().getString(R.string.cancel_share));
				item2.setTitleSize(15);
				item2.setTitleColor(Color.WHITE);
				menu.addMenuItem(item2);
			}

		};
		// set creator
		list.setMenuCreator(creator);

		// step 2. listener item click OnClick
		list.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
			@Override
			public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
				list.smoothCloseMenu();
				switch (index) {
					case 0:
						QMDbUtil.getIntance(getActivity()).update(al.get(position));
						break;
					case 1:
						QMDbUtil.getIntance(getActivity()).delete(al.get(position).getId());

						al.remove(position);
						if (al.size() == 0)
							without_share.setVisibility(View.VISIBLE);
						else
							without_share.setVisibility(View.GONE);
						break;
				}
				adapter.notifyDataSetChanged();
				return true;
			}
		});
	}


	@OnClick(R.id.document)
	public void document(View v){
		//判断文档数据有没有读取完毕
//		if (!QMUtil.getInstance().getQmDocumentFile().isFileDealFinished()) {
//			QMUtil.getInstance().showToast(getActivity(), R.string.getfiles_unfinished);
//			return;
//		}
		skipActivity(DocumentAndOtherActivity.class, 1);
	}

	@OnClick(R.id.album)
	public void album(View v) {
		skipActivity(AlbumNameActivity.class, 2);
	}

	@OnClick(R.id.video)
	public void video(View v) {
		skipActivity(AlbumAndVideoFileActivity.class, 3);
	}

	@OnClick(R.id.other)
	public void other(View v) {
//		if (!QMUtil.getInstance().getQmDocumentFile().isFileDealFinished()) {
//			QMUtil.getInstance().showToast(getActivity(), R.string.getfiles_unfinished);
//			return;
//		}
		skipActivity(DocumentAndOtherActivity.class, 4);
	}

	@OnClick(R.id.download)
	public void download(View v) {
//		if (!QMUtil.getInstance().getQmDocumentFile().isFileDealFinished()){
//			QMUtil.getInstance().showToast(getActivity(), R.string.getfiles_unfinished);
//			return;
//		}
		skipActivity(DownLoadFileActivity.class, 5);
	}

	public void skipActivity(Class className, int type) {
		Intent intent = new Intent(getActivity(), className);
		intent.putExtra("type", type);

		if (type == 2)
			intent.putExtra("name", getResources().getString(R.string.album));
		startActivity(intent);
	}

	//列表的点击事件
	//@OnClick(value = R.id.list,type = AdapterView.OnItemClickListener.class)
	@OnItemClick(R.id.list)
	public void onItemClick(AdapterView<?> parent, View view, int position,
                            long id) {
		if (al.get(position).getFileDir() == 1){
			if (al.get(position).getType() == 7) {
				QMUtil.getInstance().getPhotos().clear();
				int position_ = 0;
				for (int i = 0; i < al.size(); i++) {
					if (al.get(i).getType() == 7) {
						QMLocalFile mMyLocalFile = new QMLocalFile();
						mMyLocalFile.setName(al.get(i).getName());
						mMyLocalFile.setPath(al.get(i).getPath());
						mMyLocalFile.setType(al.get(i).getType());
						QMUtil.getInstance().getPhotos().add(mMyLocalFile);
						if (al.get(i).getName().equals(al.get(position).getName()))
							position_ = QMUtil.getInstance().getPhotos().size() - 1;
					}
				}
				Intent intent = new Intent(getActivity(), LocalPhotoScanActivity.class);
				intent.putExtra("position", position_);
				intent.putExtra("scan", 1);
				startActivity(intent);
			} else
				new OpenAppUtils(getActivity()).openFiles(al.get(position).getPath());
		} else {
			Intent intent = new Intent(getActivity(), AlbumAndVideoFileActivity.class);
			intent.putExtra("type", 2);
			intent.putExtra("name", al.get(position).getName());
			intent.putExtra("scan", 1);
			startActivity(intent);
		}
	}

//	@Override
//	public void onRefresh(){
//		initData();
//		handler.postDelayed(new Runnable() {
//			@Override
//			public void run(){
//				swipeRefresh.setRefreshing(false);
//			}
//		},2000);
//	}

	@Override
	public void onResume() {
		super.onResume();
		initData();
	}


	@Override
	public void onHiddenChanged(boolean hidden) {
		super.onHiddenChanged(hidden);

		if(!hidden){
			initData();
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		if(unbinder!=null){
			unbinder.unbind();
		}

	}
}

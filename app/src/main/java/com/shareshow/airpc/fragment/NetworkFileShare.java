package com.shareshow.airpc.fragment;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.shareshow.App;
import com.shareshow.aide.R;
import com.shareshow.airpc.activity.BoxPhotoScanActivity;
import com.shareshow.airpc.activity.FileShareActivity;
import com.shareshow.airpc.adapter.AnyScreenAdapter;
import com.shareshow.airpc.ftpclient.FTPFile;
import com.shareshow.airpc.model.QMRemoteFTPFile;
import com.shareshow.airpc.model.RootPoint;
import com.shareshow.airpc.ports.DownLoadListener;
import com.shareshow.airpc.socket.common.DownLoadFile;
import com.shareshow.airpc.socket.common.FTPUtil;
import com.shareshow.airpc.socket.common.QMDevice;
import com.shareshow.airpc.util.DebugLog;
import com.shareshow.airpc.util.OpenAppUtils;
import com.shareshow.airpc.util.QMFileType;
import com.shareshow.airpc.util.QMUtil;


import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnItemClick;
import butterknife.OnItemLongClick;

public  class NetworkFileShare extends Fragment implements OnRefreshListener, AbsListView.OnScrollListener {

	@BindView(R.id.lastDirectory)
    LinearLayout lastDirectory;
	@BindView(R.id.swipeRefresh)
    SwipeRefreshLayout swipeRefresh;
	@BindView(R.id.listView)
    ListView listView;
	@BindView(R.id.buttom)
    LinearLayout buttom;
	@BindView(R.id.download)
    ImageView download;
	@BindView(R.id.selectAll)
    ImageView selectAll;
	@BindView(R.id.rollBack)
    TextView rollBack;
	@BindView(R.id.error_show)
    TextView error_show;
	@BindView(R.id.showProgress)
    LinearLayout showProgress;


	public AnyScreenAdapter<QMRemoteFTPFile> adapter;
	public ArrayList<QMRemoteFTPFile> al = new ArrayList<QMRemoteFTPFile>();
	
	private Context mContext;

	public String isDirectory =null;
	public Object mLock = new Object();// lock
	public static final int GETFILE_OK = 1002;// 获取文件成功
	public static final int GETFILE_ERROR = 1003;// 获取文件失败
	public static final int GETFILE_DIRECTORY_ERROR = 1004;// 获取文件夹中的数据失败

	public boolean isLongclick = false;
	public boolean chooseAll = false;
	public RootPoint beforePoint;//之前选中的设备，切换其他设备该设备要先断开

	public Handler handler = new Handler(){
		@Override
		public void handleMessage(Message msg){
			super.handleMessage(msg);

			swipeRefresh.setRefreshing(false);
			QMUtil.getInstance().closeDialog();

			switch (msg.what) {
				case GETFILE_OK:
					getFileSuccess();
					break;
				case GETFILE_ERROR:
					getFileFail();
					break;
				case GETFILE_DIRECTORY_ERROR:
					QMUtil.getInstance().showToast2(getActivity(),getResources().getString(R.string.getDiretoryError));
					break;
			}
		}
	};

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
		View vv=inflater.inflate(R.layout.fragment_anyscreen, container, false);
		//x.view().inject(this,vv);
		ButterKnife.bind(this,vv);
		return vv;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState){
		super.onActivityCreated(savedInstanceState);
		initData();
	}

	public void initData(){
		adapter = new AnyScreenAdapter<QMRemoteFTPFile>(getActivity(), al,isDirectory);
		listView.setAdapter(adapter);
		swipeRefresh.setOnRefreshListener(this);
		swipeRefresh.setColorSchemeResources(android.R.color.holo_red_light,
				android.R.color.holo_green_light,
				android.R.color.holo_blue_bright,
				android.R.color.holo_orange_light);
		listView.setOnScrollListener(this);
		showView(0);
		onRefresh();
	}

	public void setBeforePoint(RootPoint beforePoint){
		this.beforePoint = beforePoint;
		FTPUtil.beforPoint=beforePoint;
	}

	public boolean isLongclick(){
		return isLongclick;
	}

	/**
	 * 断开之前设备的链接
	 */
	public void interrupterConnect(){
		FTPUtil.getInstance(beforePoint).unConnectException();
		if(beforePoint.getdType()==-1)
			beforePoint.setLcount(-1);
		beforePoint=null;
	}

	public NetworkFileShare(){

	}
	/**
	 * 成功获取到文件
	 */
	public void getFileSuccess(){
		if (al.size() == 0){
			showView(1);
		} else {
			showView(2);
		}
		adapter.notifyDataSetChanged();
		if (isDirectory!=null){//某文件夹下需要展示头部目录列表
			showHeadView();
		}
	}
	/**
	 * 获取文件失败
	 */
	public void getFileFail(){
		showView(3);
		if (isDirectory!=null){//某文件夹下需要展示头部目录列表
			showHeadView();
		}
	}

	/**
	 * 显示头部视图
	 */

	public void  showHeadView(){
		lastDirectory.removeAllViews();
		TextView uu = (TextView)(View.inflate(getActivity(), R.layout.anysreen_lastdirectory,null));
		uu.setText(QMDevice.getInstance().getSelectDevice().getName()+"\t> ");
		uu.setTextColor(getResources().getColor(R.color.black));
		uu.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v){
				if(isLongclick){//长按状态现取消
					cancelSelect();
					return ;
				}
				onRefreshData();
			}
		});
		lastDirectory.addView(uu);
		final String[] str = isDirectory.split("/");
		for (int i = 0; i < str.length; i++){
			final TextView vv = (TextView) View.inflate(getActivity(), R.layout.anysreen_lastdirectory, null);
			vv.setOnClickListener(new OnClickListener(){

				@Override
				public void onClick(View v){
					if(isLongclick){//长按状态现取消
						cancelSelect();
						return ;
					}
					isDirectory ="";
					for (int j = 0; j < (Integer) vv.getTag() + 1; j++) {
						isDirectory += str[j] + "/";
					}
					isDirectory = isDirectory.substring(0, isDirectory.length() - 1);
					showHeadView();//重新显示头部视图
					al.clear();
					adapter.notifyDataSetChanged();
					showView(5);
					onRefresh();
				}
			});
			if (i == str.length - 1){
				vv.setTextColor(getResources().getColor(R.color.xtaaaaaa));
				vv.setEnabled(false);
				vv.setText(str[i]+"");
			}else{
				vv.setTextColor(getResources().getColor(R.color.black));
				vv.setText(str[i]+"\t> ");
			}

			vv.setTag(i);
			lastDirectory.addView(vv);
		}
	}

	/**
	 * 视图间的显示
	 * @param i
	 */
	public void showView(int i){
		switch (i) {
			case 0://加载进度条
				showProgress.setVisibility(View.VISIBLE);
				error_show.setVisibility(View.GONE);
				lastDirectory.setVisibility(View.GONE);
				break;
			case 1://暂无文件
				showProgress.setVisibility(View.GONE);
				error_show.setVisibility(View.VISIBLE);
				error_show.setText(getResources().getString(R.string.without_fileshare));
				if(isDirectory==null)//根目录下时lastDirectory不显示
					lastDirectory.setVisibility(View.GONE);
				else
					lastDirectory.setVisibility(View.VISIBLE);
				break;
			case 2://有文件共享
				showProgress.setVisibility(View.GONE);
				error_show.setVisibility(View.GONE);
				if(isDirectory==null)//根目录下时lastDirectory不显示
					lastDirectory.setVisibility(View.GONE);
				else
					lastDirectory.setVisibility(View.VISIBLE);
				break;
			case 3://获取文件失败
				showProgress.setVisibility(View.GONE);
				error_show.setVisibility(View.VISIBLE);
				error_show.setText(App.getApp().getResources().getString(R.string.please_pull));
				if(isDirectory==null)//根目录下时lastDirectory不显示
					lastDirectory.setVisibility(View.GONE);
				else
					lastDirectory.setVisibility(View.VISIBLE);
				break;
			case 4://下拉刷新
				error_show.setVisibility(View.GONE);
				break;
			case 5://头部点击事件
				showProgress.setVisibility(View.VISIBLE);
				error_show.setVisibility(View.GONE);
				break;
		}
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {

	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

	    if (isLongclick) {
			rollBack.setVisibility(View.GONE);
		} else {
			if (firstVisibleItem >3) {
				rollBack.setVisibility(View.VISIBLE);
			} else {
				rollBack.setVisibility(View.GONE);
			}
		}
	}



//	@OnClick(value = R.id.listView,type = NestedScrollView.OnScrollChangeListener.class)
//	public void onScroll(AbsListView view, int firstVisibleItem,
//						  int visibleItemCount, int totalItemCount) {
//		if (isLongclick) {
//			rollBack.setVisibility(View.GONE);
//		} else {
//			if (firstVisibleItem >3) {
//				rollBack.setVisibility(View.VISIBLE);
//			} else {
//				rollBack.setVisibility(View.GONE);
//			}
//		}
//	}
	/**
	 * 获取远程文件
	 * @author tanwei
	 *
	 */
	public class RemoteFile implements Runnable {

		public String name=null;

		public RemoteFile(String name) {
			super();
			this.name = name;
		}

		public void run() {
			RootPoint rp= QMDevice.getInstance().getSelectDevice();
			try {
				if(beforePoint!=null)//断开之前的链接
					interrupterConnect();
				if(!FTPUtil.getInstance(rp).isConnect()){
					//没有链接FTP则要重新链接
					FTPUtil.getInstance(rp).loinMethod();
				}
				FTPFile[] ftpFiles= FTPUtil.getInstance(rp).requestFTPList(getActivity(),rp, name);
				if(ftpFiles!=null)
					synchronized (mLock){
						analysisFTPFile(ftpFiles,rp);
					}
				isDirectory=name;
				handler.sendEmptyMessage(GETFILE_OK);
			} catch (Exception e){
				e.printStackTrace();
				FTPUtil.getInstance(rp).interruptConnect(true);//重新链接请求
				if(name==null)
					handler.sendEmptyMessage(GETFILE_ERROR);
				else
					handler.sendEmptyMessage(GETFILE_DIRECTORY_ERROR);
			}
		}
	}
	/**
	 * 解析返回的FTP数据
	 */
	public void analysisFTPFile(FTPFile[] ftpFiles,RootPoint rp){
		java.util.List<FTPFile> list = Arrays.asList(ftpFiles);
		al.clear();
		for (int i = 0; i < list.size(); i++) {
			FTPFile mMyFTPFile = list.get(i);
			QMRemoteFTPFile file = new QMRemoteFTPFile();
			file.setName(mMyFTPFile.getName());
			if (rp.getdType() == 2
					&& mMyFTPFile.getDir() == 0
					&& (mMyFTPFile.getName().equals(".") || mMyFTPFile
					.getName().equals("..")))
				continue;
			file.setSize(mMyFTPFile.getSize());
			file.setModifiedDate(mMyFTPFile.getModifiedDate());
			if (rp.getdType() != -1) {
				if (rp.getdType() == 0) {
					file.setLink(mMyFTPFile.getLink());
				}
				file.setPermit(mMyFTPFile.getPermit());
				file.setDir(mMyFTPFile.getDir());
			}
			int type = QMFileType.getType(mMyFTPFile.getName());
			file.setType(type);
			al.add(file);
		}
		//排序
		Collections.sort(al,new Comparator<FTPFile>() {
			@Override
			public int compare(FTPFile file1, FTPFile file2) {

				if(file1.getModifiedDate().after(file2.getModifiedDate())){
					return -1;
				}else {
					return 1;

				}
			}
		});

	}




	//没有获取到文件点击视图重新获取
	@OnClick(R.id.error_show)
	public void errorShow(View v) {
		onRefresh();
	}

	//下载文件
	@OnClick(R.id.download)
	public void download(View v) {
		if (candownload())
			new DownLoadFile(getActivity(), al, QMDevice.getInstance().getSelectDevice(), isDirectory,
					new DownLoadListener() {

						@Override
						public void success() {
							cancelSelect();
						}

						@Override
						public void fail() {
							QMUtil.getInstance().showToast2(getActivity(),getResources().getString(R.string.downLoadError));
						}

					});
	}

	//全选
	@OnClick(R.id.selectAll)
	public void selectAll(View v) {
		if (chooseAll) {
			for (int i = 0; i < al.size(); i++) {
				al.get(i).setChoose(false);
			}
			chooseAll = false;
			selectAll.setBackgroundResource(R.drawable.choose_all_bg);
		} else {
			for (int i = 0; i < al.size(); i++) {
				if (al.get(i).getPermit() == 1||al.get(i).getDir() == 0)
					continue;
				al.get(i).setChoose(true);
			}
			chooseAll = true;
			selectAll.setBackgroundResource(R.drawable.undo_choose);
		}
		adapter.notifyDataSetChanged();
	}

	//列表数据过多回到顶部
	@OnClick(R.id.rollBack)
	public void rollBack(View v) {
		listView.setSelection(0);
	}

	/**
	 * 判断下载的条件
	 * @return
	 */
	public boolean candownload() {
		long size = 0;
		for (int i = 0; i < al.size(); i++) {
			if (al.get(i).isChoose()) {
				size = size + al.get(i).getSize();
				break;
			}
		}
		if (size == 0) {
			QMUtil.getInstance().showToast(App.getApp(), R.string.unselectedDownLoadFile);
			return false;
		}
		if (QMUtil.getInstance().getSDCardMemorry()<size) {
			QMUtil.getInstance().showToast(App.getApp(), R.string.memory_out);
			return false;
		}
		return true;
	}

	//列表的点击事件
	@OnItemClick(value = R.id.listView )
	public void onItemClick(AdapterView<?> parent, View view, int position,
                            long id){
		if (isLongclick) {
			if (al.get(position).getDir() == 0) {
				QMUtil.getInstance().showToast(App.getApp(), R.string.dir_unload);
				return;
			}
			if (al.get(position).isChoose()) {
				chooseAll = false;
				selectAll.setBackgroundResource(R.drawable.choose_all_bg);
				al.get(position).setChoose(false);
			} else {
				if (al.get(position).getPermit() == 1){
					QMUtil.getInstance().showToast(App.getApp(),
							R.string.without_permittion);
					return;
				}
				al.get(position).setChoose(true);
			}
			int kk = 0;
			for (int i = 0; i < al.size(); i++) {
				if (al.get(i).getPermit() == 1)
					continue;
				if (!al.get(i).isChoose()) {
					kk = 1;
					break;
				}
			}
			if (kk == 0) {
				chooseAll = true;
				selectAll.setBackgroundResource(R.drawable.undo_choose);
			}
			adapter.notifyDataSetChanged();
		}else{
			int type = al.get(position).getType();
			if (al.get(position).getDir() == 0){
				if (QMDevice.getInstance().getSelectDevice().getdType() == 2)
					QMUtil.getInstance().showProgressDialog(getActivity(), R.string.getDir2);
				else
					QMUtil.getInstance().showProgressDialog(getActivity(), R.string.getDir);
				if (isDirectory==null)
					getRemoteFile(QMDevice.getInstance().getSelectDevice(),al.get(position).getName());
				else
					getRemoteFile(QMDevice.getInstance().getSelectDevice(),isDirectory + "/" + al.get(position).getName());
			} else if (type == 7){
				int position_ = 0;
				ArrayList<QMRemoteFTPFile> all = new ArrayList<QMRemoteFTPFile>();
				for (int i = 0; i < al.size(); i++){
					if (al.get(i).getType() == 7){
						all.add(al.get(i));
						if (al.get(i).getName()
								.equals(al.get(position).getName()))
							position_ = all.size() - 1;
					}
				}
				Intent intent = new Intent(getActivity(), BoxPhotoScanActivity.class);
				intent.putExtra("path", all);
				intent.putExtra("isDirectory", isDirectory);
				intent.putExtra("position", position_);
				startActivity(intent);
			} else if (fileExists(al.get(position))){
				new DownLoadFile(getActivity(), al.get(position), QMDevice.getInstance().getSelectDevice(), isDirectory,false,
						new DownLoadListener() {

							@Override
							public void success() {

							}

							@Override
							public void fail() {
							}
						});
			}

		}
	}
	/**
	 * p判断某文件是否存在 ，存在就直接打开，不存在就加载
	 * @param mMyFTPFile
	 * @return
	 */
	public boolean fileExists(QMRemoteFTPFile mMyFTPFile){
		File fileDir = new File(QMFileType.CACHE);
		if (!fileDir.exists())
			fileDir.mkdirs();
		File file = new File(QMFileType.CACHE + "/" + mMyFTPFile.getName());
		if (file.exists()) {
			new OpenAppUtils(getActivity()).openFiles(QMFileType.CACHE + "/"
					+ mMyFTPFile.getName());
			return false;
		}else{
			File fileDir2 = new File(QMFileType.LOCALPATH);
			if (!fileDir2.exists())
				fileDir2.mkdirs();
			File file2 = new File(QMFileType.LOCALPATH + "/"
					+ mMyFTPFile.getName());
			if (file2.exists()) {
				new OpenAppUtils(getActivity()).openFiles(QMFileType.LOCALPATH
						+ "/" + mMyFTPFile.getName());
				return false;
			}
		}
		return true;
	}


	//列表的长按事件
	@OnItemLongClick(value = R.id.listView)
	public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
		if (isLongclick)
			return false;
		isLongclick = true;
		adapter.isLongclick(isLongclick);
		Animation animation = AnimationUtils.loadAnimation(getActivity(),
				R.anim.photo_dialog_in_anim);
		animation.setDuration(500);
		animation.setFillAfter(true);
		buttom.startAnimation(animation);
		animation.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {
			}

			@Override
			public void onAnimationRepeat(Animation animation) {
			}

			@Override
			public void onAnimationEnd(Animation animation) {
				buttom.setVisibility(View.VISIBLE);
				download.setVisibility(View.VISIBLE);
				selectAll.setVisibility(View.VISIBLE);

			}
		});
		((FileShareActivity) getActivity()).hiddenCancel(0);
		return false;
	}

	/**
	 * 取消选择
	 */
	public void cancelSelect() {
		for (int i = 0; i < al.size(); i++) {
			al.get(i).setChoose(false);
		}
		isLongclick = false;
		adapter.isLongclick(isLongclick);
		Animation animation2 = AnimationUtils.loadAnimation(getActivity(),
				R.anim.photo_dialog_out_anim);
		animation2.setDuration(800);
		animation2.setFillAfter(true);
		buttom.startAnimation(animation2);
		buttom.setVisibility(View.GONE);
		download.setVisibility(View.GONE);
		selectAll.setVisibility(View.GONE);
		chooseAll = false;
		selectAll.setBackgroundResource(R.drawable.choose_all_bg);
		((FileShareActivity) getActivity()).hiddenCancel(8);
	}

	/**
	 * 开启获取远程文件的线程
	 * @param rp
	 * @param name    文件夹的名称  根目录下为null
	 */
	public void getRemoteFile(RootPoint rp,String name){

		if(rp!=null){
			FTPUtil.getInstance(rp).getThreadPool().execute(new RemoteFile(name));
		}else{
			DebugLog.showLog(this,"FTP连接失败");
		}

	}




	@Override
	public void onRefresh(){
		showView(4);
		RootPoint rp= QMDevice.getInstance().getSelectDevice();
		getRemoteFile(rp,isDirectory);
		if (chooseAll){
		for (int i = 0; i < al.size(); i++) {
			if (al.get(i).getPermit() == 1||al.get(i).getDir() == 0)
				continue;
			al.get(i).setChoose(true);
		}
		chooseAll = false;
		selectAll.setBackgroundResource(R.drawable.choose_all_bg);
		adapter.notifyDataSetChanged();
		}
	}

	/**
	 *
	 * @param
	 */
	public void onRefreshData(){
		showView(0);
		isDirectory =null;
		al.clear();
		adapter.notifyDataSetChanged();
		onRefresh();
	}

}

package com.shareshow.airpc.activity;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;
import com.shareshow.aide.R;
import com.umeng.analytics.MobclickAgent;
import com.shareshow.airpc.QMCommander;
import com.shareshow.airpc.adapter.SelectedDeviceListAdapter;
import com.shareshow.airpc.fragment.BaseWQFragment;
import com.shareshow.airpc.fragment.LocalFileShare;
import com.shareshow.airpc.fragment.NetworkFileShare;
import com.shareshow.airpc.fragment.QQFragment;
import com.shareshow.airpc.fragment.WxFragment;
import com.shareshow.airpc.model.RootPoint;
import com.shareshow.airpc.socket.command.CommandExecutorLancher;
import com.shareshow.airpc.socket.common.FTPUtil;
import com.shareshow.airpc.socket.common.QMDevice;
import com.shareshow.airpc.util.QMPopupWindow;
import com.shareshow.airpc.util.QMUtil;


import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 文件共享UI
 *
 * @author tanwei
 */

public class FileShareActivity extends ConnectLancherActivity implements PopupWindow.OnDismissListener,ListView.OnItemClickListener,BaseWQFragment.callBackActivity {
	@BindView(R.id.popup)
    Button cancel;// 在AnyScreen fragment中list列表选项长按事件显示的‘取消’按钮
	@BindView(R.id.local_file)
    TextView local_file;// 布局顶部的控件
	@BindView(R.id.remote_device)
    TextView remote_device;// 布局顶部的控件
	@BindView(R.id.show_arrow)
    LinearLayout show_arrow;// 右边下三角图标
	@BindView(R.id.popup_bg)
    TextView popup_bg;//PopupWindow的背景颜色
	@BindView(R.id.arrow_popup)
    TextView arrow_popup;// 点击右上角弹出视图的触发控件

	public QMPopupWindow right_popupWindow;// 弹出右上角的菜单
//	@BindView(R.id.listView)
//	ListView listView;// 右上角弹出视图布局中控件
	public BaseAdapter adapter;//选择设备的弹出框listview的适配器
	@BindView(R.id.text)
    TextView text;//顶部中间的文字
//	@BindView(R.id.popup)
//	 Button popup;

	//@BindView(R.id.qq_file)
	 RelativeLayout qq_file;
	 RelativeLayout wx_file;
	//@BindView(R.id.choose_img)
	 ImageView local_img;
	//@BindView(R.id.choose_img_qq)
	 ImageView qq_img;
	//@BindView(R.id.choose_img_wx)
	 ImageView wx_img;
	 @BindView(R.id.show_local)
     LinearLayout show_local;
	 @BindView(R.id.arrow_local)
     TextView arrow_local;
	//@BindView(R.id.line1)
	 LinearLayout line1;
	//@BindView(R.id.line2)
	 LinearLayout line2;
	public int index;
	public int local_pos;

	/**
	 * 用于对Fragment进行管理
	 */
	public FragmentManager fragmentManager;
	public LocalFileShare fileShare;//本地文件 fragment
	public NetworkFileShare anyScreen;//远程设备 fragment

	public int currentPosition;// 当前左右按钮选中位置
	public Toolbar toolbar;
	public PopupWindow left_popupWindow;
	public WxFragment wxFile;
	public Fragment fragment;
	public QQFragment qqFile;
	/**
	 * ATTENTION: This was auto-generated to implement the App Indexing API.
	 * See https://g.co/AppIndexing/AndroidStudio for more information.
	 */
	public GoogleApiClient client;
	private ListView listView;
	private RelativeLayout localShareFile;
	private boolean isShowRemoteFile;

	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_fileshare);
		//	x.view().inject(this);
		ButterKnife.bind(this);
		initPopupWindow();
		initLeftPopuWindow();
		initData();
		if (savedInstanceState != null){
			fileShare = (LocalFileShare) fragmentManager.findFragmentByTag("fileShare");
			anyScreen = (NetworkFileShare) fragmentManager.findFragmentByTag("anyScreen");
			qqFile = (QQFragment) fragmentManager.findFragmentByTag("qqFile");
			wxFile = (WxFragment) fragmentManager.findFragmentByTag("wxFile");
			Log.i("test", "index:" + index);
//			 Log.i("test","fileShare:"+fileShare.isHidden()+"anyScreen:"+
//			 anyScreen.isHidden()+"qqFile:"+qqFile.isHidden()+"wxFile:"+wxFile.isHidden());
			index = savedInstanceState.getInt("index");
		}
		setTabSelection(index);
//		toolbar= (Toolbar) findViewById(R.id.toolbar);
//		setSupportActionBar(toolbar);
//		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//		getSupportActionBar().setDisplayShowTitleEnabled(false);
//		toolbar.setNavigationOnClickListener(new View.OnClickListener() {
//			@Override
//			public void OnClick(View v) {
//				onBackPressed();
//			}
//		});
		// ATTENTION: This was auto-generated to implement the App Indexing API.
		// See https://g.co/AppIndexing/AndroidStudio for more information.
		client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
	}

	public void initLeftPopuWindow(){
		View vv = View.inflate(this, R.layout.local_list_dialog, null);
		//x.view().inject(this, vv);
		left_popupWindow = new PopupWindow(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
		left_popupWindow.setBackgroundDrawable(new ColorDrawable(0x00000000));
		left_popupWindow.setOutsideTouchable(true);
		left_popupWindow.setFocusable(true);
		left_popupWindow.setAnimationStyle(R.style.timepopwindow_anim_style);
		//left_popupWindow.setAnimationStyle(id);
		left_popupWindow.setContentView(vv);
		//ButterKnife.bind(vv);
		localShareFile = (RelativeLayout) vv.findViewById(R.id.local_share_file);
		wx_file = (RelativeLayout) vv.findViewById(R.id.weixin_file);
		qq_file = (RelativeLayout) vv.findViewById(R.id.qq_file);
		line1 = (LinearLayout) vv.findViewById(R.id.line1);
		line2 = (LinearLayout) vv.findViewById(R.id.line2);
		local_img = (ImageView) vv.findViewById(R.id.choose_img);
		qq_img = (ImageView) vv.findViewById(R.id.choose_img_qq);
		wx_img = (ImageView) vv.findViewById(R.id.choose_img_wx);
//		left_popupWindow = new QMPopupWindow(vv,popup_bg);
		left_popupWindow.setOnDismissListener(this);
		if (!isWeixinAvilible(this) && isQQClientAvailable(this)) {
			wx_file.setVisibility(View.GONE);
			line1.setVisibility(View.GONE);
		} else if (!isQQClientAvailable(this) && isWeixinAvilible(this)){
			qq_file.setVisibility(View.GONE);
			line2.setVisibility(View.GONE);
		} else if (!isQQClientAvailable(this) && !isWeixinAvilible(this)){
			wx_file.setVisibility(View.GONE);
			line1.setVisibility(View.GONE);
			qq_file.setVisibility(View.GONE);
			line2.setVisibility(View.GONE);
		}
	}


	public void back(View view){
		onBackPressed();
	}

	// 初始化右侧菜单弹出框
	public void initPopupWindow(){
		View vv = View.inflate(this, R.layout.device_list_popup, null);
	//	x.view().inject(this, vv);
		//ButterKnife.bind(vv);
	    listView= (ListView) vv.findViewById(R.id.listView);
		listView.setOnItemClickListener(this);
		right_popupWindow = new QMPopupWindow(vv, popup_bg);
	}


	public void initData(){
		cancel.setVisibility(View.INVISIBLE);
		text.setText(getResources().getString(R.string.file_share_2));
		RootPoint rp = QMDevice.getInstance().getSelectDevice();
		if (rp != null) {//选中了设备进入文件共享UI
			remote_device.setText(rp.getName());
			if (QMDevice.getInstance().getAl().size() > 1)//主页搜索的设备个数大于1时显示下拉图标
				show_arrow.setVisibility(View.VISIBLE);
		}
		adapter = new SelectedDeviceListAdapter<RootPoint>(this, QMDevice.getInstance().getAl());
		listView.setAdapter(adapter);
		fragmentManager = getFragmentManager();
	}

	@Override
	protected void onResume(){
		super.onResume();
		MobclickAgent.onResume(this);
		if (fileShare != null)//在‘文本’、‘图片’里面共享文件后回来要更新
			fileShare.initData();
		RootPoint rp = QMDevice.getInstance().getSelectDevice();
		if (rp != null) {//在‘文本’、‘图片’里面如果进行了选中设备上传了的话返回也得更新
			remote_device.setText(rp.getName());
			if (QMDevice.getInstance().getAl().size() > 1)
				show_arrow.setVisibility(View.VISIBLE);
		}
	}


	public void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
	}

//	//返回
//	@OnClick(R.id.back)
//	public void back(View v) {
//		if (anyScreen != null && anyScreen.isLongclick()) {
//			cancel.setVisibility(View.GONE);
//			anyScreen.cancelSelect();
//		} else {
//			finish();
//		}
//	}

	//anyScreen取消选择
	@OnClick(R.id.popup)
	public void cancel(View v){
		if (anyScreen != null) {
			anyScreen.cancelSelect();
			cancel.setVisibility(View.GONE);
		}

		if (wxFile != null) {
			wxFile.cancelSelect();
			cancel.setVisibility(View.GONE);
		}

		if (qqFile != null) {
			qqFile.cancelSelect();
			cancel.setVisibility(View.GONE);
		}
	}

	//选中本地文件
	@OnClick(R.id.local_file)
	public void localFile(View v){
		if (local_file.getBackground() == null){
			setTabSelection(local_pos);
		} else {
			showLeftPopuwindow();
		}
	}


	//选中远程设备
	@OnClick(R.id.remote_device)
	public void remoteDevice(View v) {
		setTabSelection(1);
	}

	public void setTabSelection(int i) {
		if (i == 1) {
			//选中的是anyScreen
			if (QMDevice.getInstance().getSelectDevice() == null) {
				//从主页没有选择任盒进入UI时
				if (QMDevice.getInstance().getAl().size() == 0) {//没有任盒tip下
					QMUtil.getInstance().showToast(this, R.string.unabledDivice);
				} else {
					showPopupWindow();//有的话弹出PopupWindow
				}
				return;
			} else {
				if (anyScreen != null) {
					if (anyScreen.isLongclick()) {//anyScreen处于长长按状态时取消
						cancel.setVisibility(View.GONE);
						anyScreen.cancelSelect();
						return;
					} else if (currentPosition == 1 && QMDevice.getInstance().getAl().size() > 1) {
						showPopupWindow();
						return;
					}
				}
			}
		} else {//选中的是fileShare
			if (anyScreen != null && anyScreen.isLongclick()) {//anyScreen处于长长按状态时取消
				cancel.setVisibility(View.GONE);
				anyScreen.cancelSelect();
				return;
			}
			if (wxFile != null && wxFile.isLongclick()) {
				cancel.setVisibility(View.GONE);
				wxFile.cancelSelect();
			}

		}
		showFragment(i);
	}

	/**
	 * 切换fragment
	 *
	 * @param i
	 */
	public void showFragment(int i){
		index = i;
		// 更改底部控件状态
		colorSelection(i);
		// 开启一个Fragment事务
		FragmentTransaction transaction = fragmentManager.beginTransaction();
		// 先隐藏掉所有的Fragment，以防止有多个Fragment显示在界面上的情况
		hideFragments(transaction);
		switch (i) {
			case 0:
				if (fileShare == null) {
					// 如果LocalFileFragment为空，则创建一个并添加到界面上
					fileShare = new LocalFileShare();
					transaction.add(R.id.frame, fileShare, "fileShare");
				} else {
					// 如果LocalFileFragment不为空，则直接将它显示出来
					transaction.show(fileShare);
				}
				fragment = fileShare;
				break;

			case 1:
				if (anyScreen == null) {
					// 如果MyRP为空，则创建一个并添加到界面上
					anyScreen=new NetworkFileShare();
					transaction.add(R.id.frame, anyScreen, "anyScreen");
				} else {
					// 如果MyRP不为空，则直接将它显示出来
					transaction.show(anyScreen);

				}
				fragment = anyScreen;
				break;

			case 2:
				if (qqFile == null) {
					// 如果LocalFileFragment为空，则创建一个并添加到界面上
					qqFile = new QQFragment();
					transaction.add(R.id.frame, qqFile, "qqFile");
				} else {
					// 如果LocalFileFragment不为空，则直接将它显示出来
					transaction.show(qqFile);
				}
				fragment = qqFile;


				break;


			case 3:
				if (wxFile == null) {
					// 如果LocalFileFragment为空，则创建一个并添加到界面上
					wxFile = new WxFragment();
					transaction.add(R.id.frame, wxFile, "wxFile");
				} else {
					// 如果LocalFileFragment不为空，则直接将它显示出来
					transaction.show(wxFile);
				}
				fragment = wxFile;
				break;
		}
		transaction.commit();
		currentPosition = i;
	}

	/**
	 * 改变底部颜色和中间的文字
	 *
	 * @param i
	 */
	@SuppressLint("NewApi")
	public void colorSelection(int i) {
		local_file.setBackground(null);
		remote_device.setBackground(null);
		switch (i) {
			case 0:
			case 2:
			case 3:
				local_file.setBackgroundResource(R.drawable.file_left_bg);
				break;
			case 1:
				remote_device.setBackgroundResource(R.drawable.file_right_bg);
				break;
		}

	}

	/**
	 * 隐藏显示的fragment
	 *
	 * @param transaction
	 */
	public void hideFragments(FragmentTransaction transaction) {
		if (fileShare != null)
			transaction.hide(fileShare);
		if (anyScreen != null)
			transaction.hide(anyScreen);
		if (wxFile != null) {
			transaction.hide(wxFile);
		}
		if (qqFile != null) {
			transaction.hide(qqFile);
		}
	}

	@Override
	public void onBackPressed() {
		if (anyScreen != null && anyScreen.isLongclick()){
			cancel.setVisibility(View.GONE);
			anyScreen.cancelSelect();
		}else if (wxFile != null && wxFile.isLongclick()){
			cancel.setVisibility(View.GONE);
			wxFile.cancelSelect();
		}else if (qqFile != null && qqFile.isLongclick()){
			cancel.setVisibility(View.GONE);
			qqFile.cancelSelect();
		}else{
			finish();
			super.onBackPressed();
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		RootPoint rp = QMDevice.getInstance().getSelectDevice();
		if (rp != null)
			FTPUtil.getInstance(rp).interruptConnectByThread(false);
	}

	/**
	 * 显示PopupWindow
	 */
	public void showPopupWindow(){
		adapter.notifyDataSetChanged();
		popup_bg.setVisibility(View.VISIBLE);
		right_popupWindow.showAsDropDown(arrow_popup);
	}

	public void showLeftPopuwindow(){
		popup_bg.setVisibility(View.VISIBLE);
		int[] location = new int[2];
		arrow_local.getLocationOnScreen(location);
		left_popupWindow.showAtLocation(arrow_local, Gravity.TOP | Gravity.RIGHT, location[0], location[1]);
		//left_popupWindow.showAtLocation(arrow_local, Gravity.RIGHT | Gravity.BOTTOM,10,10);
	}


	//列表的点击事件
	//@OnClick(value = R.id.listView, type = AdapterView.OnItemClickListener.class)
	//@OnItemClick(R.id.listView)
	public void onItemClick(AdapterView<?> parent, View view, final int position, long id){
		final RootPoint rp = QMDevice.getInstance().getAl().get(position);
		RootPoint beforePoint = QMDevice.getInstance().getSelectDevice();
		   isShowRemoteFile = true;
		if (QMDevice.getInstance().getSelectDevice() == null){
			if (rp.getdType() != -1){//非任盒不用密码验证
				alterDeivice(beforePoint, rp, false, true);
				return;
			}
			if (rp.getEnablepwd().equals("true")) {
				passwdVertify(rp);
			} else {//没有密码要发送有链接任盒的消息
				alterDeivice(beforePoint, rp, true, true);
			}
		}else{
			 //这是切换设备，要把之前的设备FTP链接断开
			if (rp.getAddress().equals(beforePoint.getAddress())){
				QMUtil.getInstance().showToast2(this, getResources().getString(R.string.sharing) + rp.getName());
				if(right_popupWindow!=null){
					right_popupWindow.dismiss();
				}
				return;
			}

			if (rp.getdType() != -1){
				alterDeivice(beforePoint, rp, false, false);
				return;
			}

			if(rp.getEnablepwd().equals("true")){
				passwdVertify(rp);
			}else{
				alterDeivice(beforePoint, rp, true, false);
			}
		}
	}

	public void hiddenCancel(int a){
		//这里报空指针
		cancel.setVisibility(a);
	}

	/**
	 * 切换设备查看文件共享
	 *
	 * @param
	 * @param flag 是否需要发送链接请求
	 */

	public void alterDeivice(RootPoint beforeRP, RootPoint newRP, boolean flag, boolean isFirst){
		newRP.setPlay(true);
		remote_device.setText(newRP.getName());
		if (QMDevice.getInstance().getAl().size() > 1){
			//主页搜索的设备个数大于1时显示下拉图标
			show_arrow.setVisibility(View.VISIBLE);
	    }
		adapter.notifyDataSetChanged();
		if (right_popupWindow != null)
			right_popupWindow.dismiss();
		if (flag){
			sendEmptyPasswdConnect(newRP);
		}
		if(isShowRemoteFile){
			showFragment(1);
			if(!isFirst){
			//	QMDevice.getInstance().removeAllPlay();
				beforeRP.setPlay(false);
				anyScreen.setBeforePoint(beforeRP);
				anyScreen.onRefreshData();
			  }
			isShowRemoteFile=false;
		}else{
			if (fragment.equals(wxFile)){
				if(!isFirst){
					beforeRP.setPlay(false);
				}
				FTPUtil.beforPoint = beforeRP;
				wxFile.sendFileOk(newRP, flag);
				return;
			}
			if(fragment.equals(qqFile)){
				if (!isFirst) {
					beforeRP.setPlay(false);
				}
				FTPUtil.beforPoint = beforeRP;
				qqFile.sendFileOk(newRP, flag);
				return;
			}
		}
	}


	@Override
	public void passwdVertifyCallBack(RootPoint rp) {
		RootPoint point = QMDevice.getInstance().getSelectDevice();
		if (point == null)
			alterDeivice(null, rp, false, true);//进行了密码验证不用再发送链接请求
		else {
			alterDeivice(point, rp, false, false);
		}
	}

	//收到投屏成功后返回的指令
	@Override
	public void touPingPc(RootPoint rp) {
		// TODO Auto-generated method stub

	}

	@Override
	public void stopPc(RootPoint rp){
		// TODO Auto-generated method stub

	}

	@Override
	public void pcTouPing(RootPoint rp){

		CommandExecutorLancher.getOnlyExecutor().connectPcMessage(QMCommander.TYPE_SCREEN_REFUSED , rp.getAddress());
	}

	@Override
	public void pcCoverShare(RootPoint rp) {

	}


	//选中本地文件
	//@OnClick(R.id.local_share_file)
	public void shareFile(View v) {
		local_img.setVisibility(View.VISIBLE);
		qq_img.setVisibility(View.GONE);
		wx_img.setVisibility(View.GONE);
		local_file.setText("本地文件");
		left_popupWindow.dismiss();
		setTabSelection(0);
		local_pos = 0;
	}

	//@OnClick(R.id.qq_file)
	public void qqFile(View v) {
		local_img.setVisibility(View.GONE);
		qq_img.setVisibility(View.VISIBLE);
		wx_img.setVisibility(View.GONE);
		local_file.setText("QQ文件");
		left_popupWindow.dismiss();
		setTabSelection(2);
		local_pos = 2;
	}

	//@OnClick(R.id.weixin_file)
	public void wxFile(View v) {
		local_img.setVisibility(View.GONE);
		qq_img.setVisibility(View.GONE);
		wx_img.setVisibility(View.VISIBLE);
		local_file.setText("微信文件");
		left_popupWindow.dismiss();
		setTabSelection(3);
		local_pos = 3;

	}

	@Override
	public void onDismiss() {
		popup_bg.setVisibility(View.GONE);
	}

	public static boolean isWeixinAvilible(Context context) {
		final PackageManager packageManager = context.getPackageManager();// 获取packagemanager
		List<PackageInfo> pinfo = packageManager.getInstalledPackages(0);// 获取所有已安装程序的包信息
		if (pinfo != null) {
			for (int i = 0; i < pinfo.size(); i++) {
				String pn = pinfo.get(i).packageName;
				if (pn.equals("com.tencent.mm")) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * 判断qq是否可用
	 *
	 * @param context
	 * @return
	 */
	public static boolean isQQClientAvailable(Context context) {
		final PackageManager packageManager = context.getPackageManager();
		List<PackageInfo> pinfo = packageManager.getInstalledPackages(0);
		if (pinfo != null) {
			for (int i = 0; i < pinfo.size(); i++) {
				String pn = pinfo.get(i).packageName;
				if (pn.equals("com.tencent.mobileqq")) {
					return true;
				} else if (pn.equals("com.tencent.minihd.qq")) {
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
		outState.putInt("index", index);
		super.onSaveInstanceState(outState, outPersistentState);
	}

	/**
	 * ATTENTION: This was auto-generated to implement the App Indexing API.
	 * See https://g.co/AppIndexing/AndroidStudio for more information.
	 */
	public Action getIndexApiAction(){
		Thing object = new Thing.Builder()
				.setName("FileShare Page") // TODO: Define a title for the content shown.
				// TODO: Make sure this auto-generated URL is correct.
				.setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
				.build();
		return new Action.Builder(Action.TYPE_VIEW)
				.setObject(object)
				.setActionStatus(Action.STATUS_TYPE_COMPLETED)
				.build();
	}

	@Override
	public void onStart(){
		super.onStart();

		// ATTENTION: This was auto-generated to implement the App Indexing API.
		// See https://g.co/AppIndexing/AndroidStudio for more information.
		client.connect();
		AppIndex.AppIndexApi.start(client, getIndexApiAction());
	}

	@Override
	public void onStop(){
		super.onStop();

		// ATTENTION: This was auto-generated to implement the App Indexing API.
		// See https://g.co/AppIndexing/AndroidStudio for more information.
		AppIndex.AppIndexApi.end(client, getIndexApiAction());
		client.disconnect();
	}


	public void setRemoteDevice(RootPoint rp){
		if(rp!=null){
			remote_device.setText(rp.getName());
			if(QMDevice.getInstance().getSize()>1){
				show_arrow.setVisibility(View.VISIBLE);
			}
		}
	}

	@Override
	public void onDeviceName(RootPoint rp) {

		setRemoteDevice(rp);
	}

}

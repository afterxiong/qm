package com.shareshow.airpc.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.shareshow.aide.R;
import com.shareshow.airpc.Collocation;
import com.shareshow.airpc.menu.SwipeMenuListView;
import com.shareshow.airpc.model.QMLocalFile;
import com.shareshow.airpc.ports.ConnectFTPListener;
import com.shareshow.airpc.ports.ImgOnClick;
import com.shareshow.airpc.socket.common.FTPUtil;
import com.shareshow.airpc.socket.common.QMDevice;
import com.shareshow.airpc.socket.common.QMShareDao;
import com.shareshow.airpc.socket.common.UploadFile;
import com.shareshow.airpc.util.DebugLog;
import com.shareshow.airpc.util.NetworkUtils;
import com.shareshow.airpc.util.OpenAppUtils;
import com.shareshow.airpc.util.QMDbUtil;
import com.shareshow.airpc.util.QMPopupWindow;
import com.shareshow.airpc.util.QMUtil;


import java.util.ArrayList;

/**
 * 本地文件父类 主要处理长按列表显示底部控件  分享 上传 全选的功能
 * @author tanwei
 *
 */

public abstract class BaseLocalFileActivity extends ConnectFTPActivity implements OnClickListener, OnItemClickListener, OnItemLongClickListener {
	
	//Xutil2.5 无法继承注解
	private LinearLayout back;
	private TextView head_title;
	protected Button cancel;
	public ListView listView;
	public SwipeMenuListView swipeMenu_ListView;
	public TextView without_localfile;
	private LinearLayout buttom;
	private ImageView share;
	public LinearLayout show_upLoad;
	private ImageView up_load;
	private ImageView selectAll;
	
	
	public int type = 0;//1文档  2图片 3视频 4其他 5下载的类型
	//父类，公用al集合，代表的是加载的
	public ArrayList<QMLocalFile> al = new ArrayList<QMLocalFile>();
	
	public  boolean isLongclickState = false;//是否处于长按状态
	private boolean chooseAll = false;//是否全选文件
	public int albumName=1;//0相册文件名列表  1非相册文件名列表(默认)
	protected Button sort;
    private LinearLayout ll_sort;



    @Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_base_localfile);
		initView();
		initEvent();
        initPopupWindow();
		intTitle();
	}

	private QMPopupWindow right_popupWindow;// 弹出右上角的菜单
	private ImageView iv_date;
	private ImageView iv_type;
	private ImageView iv_name;
	private void initPopupWindow() {
		View vv = View.inflate(this, R.layout.activity_sort_popup, null);
        ll_sort = (LinearLayout) vv.findViewById(R.id.ll_sort);
		RelativeLayout date = (RelativeLayout) vv.findViewById(R.id.date);
		iv_date = (ImageView) vv.findViewById(R.id.iv_date);
		iv_date.setImageDrawable(getResources().getDrawable(R.mipmap.sortfile));
		date.setOnClickListener(this);
		RelativeLayout type = (RelativeLayout) vv.findViewById(R.id.type);
		iv_type = (ImageView) vv.findViewById(R.id.iv_type);
		type.setOnClickListener(this);
		RelativeLayout name = (RelativeLayout) vv.findViewById(R.id.name);
		iv_name = (ImageView) vv.findViewById(R.id.iv_name);
		name.setOnClickListener(this);
		right_popupWindow = new QMPopupWindow(vv);
	}
	
	private void initView() {

		back=(LinearLayout) findViewById(R.id.back);
		head_title=(TextView) findViewById(R.id.head_title);
		sort = (Button) findViewById(R.id.sort);
		cancel=(Button) findViewById(R.id.cancel);
		listView=(ListView) findViewById(R.id.listView);
		swipeMenu_ListView=(SwipeMenuListView) findViewById(R.id.swipeMenu_ListView);
		without_localfile=(TextView) findViewById(R.id.without_localfile);
		buttom=(LinearLayout) findViewById(R.id.buttom);
		share=(ImageView) findViewById(R.id.share);
		up_load=(ImageView) findViewById(R.id.up_load);
		show_upLoad=(LinearLayout) findViewById(R.id.show_upLoad);
		selectAll=(ImageView) findViewById(R.id.selectAll);
	}

	private void initEvent() {
		sort.setOnClickListener(this);
		back.setOnClickListener(this);
		cancel.setOnClickListener(this);
		share.setOnClickListener(this);
		up_load.setOnClickListener(this);
		selectAll.setOnClickListener(this);
		listView.setOnItemClickListener(this);
		swipeMenu_ListView.setOnItemClickListener(this);
		listView.setOnItemLongClickListener(this);
		swipeMenu_ListView.setOnItemLongClickListener(this);
	}

	/**
	 * 刷新适配器
	 * @param flag  1表是只刷新适配器 2表示要更改状态
	 */
	public abstract void upAdapter(int flag);
    int time=0;
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		    case R.id.back:
			back();
			break;
		    case R.id.cancel:
			cancel();	
			break;
		    case R.id.share:
			shareMethod();
			break;
		    case R.id.up_load:
			upLoad();	
			break;
		    case R.id.selectAll:
			selectAll();
			break;
			case R.id.sort:
				sortDialog();
				break;
			case R.id.date:
				sortFile(0);
				selectType(0);
				break;
			case R.id.type:
				sortFile(2);
				selectType(2);
				break;
			case R.id.name:
				sortFile(3);
				selectType(3);
				break;
		}

		
	}
	public abstract void sortFile(int i);
	/**
	 * 调整后面的勾选状态
	 * */
	private void selectType(int i) {
		iv_date.setImageDrawable(null);
		iv_name.setImageDrawable(null);
		iv_type.setImageDrawable(null);
		if (i==0) {
			iv_date.setImageDrawable(getResources().getDrawable(R.mipmap.sortfile));
		}
		else if (i==2) {
			iv_type.setImageDrawable(getResources().getDrawable(R.mipmap.sortfile));
		}else {
			iv_name.setImageDrawable(getResources().getDrawable(R.mipmap.sortfile));
		}

		right_popupWindow.dismiss();
	}


	String[] str=new String[]{"时间","大小","类型","名称"};
	public  void sortDialog() {
		right_popupWindow.showAsDropDown(sort);
	}
	//退出程序
	private void back() {
		if (!isLongclickState) {
			finish();
		} else{
			cancel();
		}
	}
	
	@Override
	public void onBackPressed(){
		if (!isLongclickState){
			finish();
			super.onBackPressed();
		} else{
			cancel();
		}
	}
	
	//取消选中
	private void cancel(){
		cancel.setVisibility(View.GONE);
		if (AlbumNameActivity.album==0){
			sort.setVisibility(View.VISIBLE);
		}else {
			sort.setVisibility(View.GONE);
		}
		for (int i = 0; i < al.size(); i++) {
			  al.get(i).setChoose(false); 
		}
		isLongclickState=false;
		upAdapter(2);
		Animation animation= AnimationUtils.loadAnimation(this, R.anim.photo_dialog_out_anim);
		animation.setDuration(800);
		animation.setFillAfter(false); 
		buttom.startAnimation(animation);
		buttom.setVisibility(View.GONE);
		chooseAll=false;
		selectAll.setBackgroundResource(R.drawable.choose_all_bg);
	}
	
	//分享--保存在本地数据库
	private void shareMethod(){
		new QMShareDao(this, al,albumName, new ImgOnClick(){
			@Override
			public void imgClick(int position){
				if(position==0)
					cancel();
			}
		});
	}
	
	//上传文件
	private void upLoad(){
		if(canUpLoad()) {
			startUpLoad();
		}
	}
	/**
	 * 判断是否符合上传文件的条件
	 * @return
	 */
	private boolean canUpLoad(){
		int kk=0;
		for (int i = 0; i < al.size(); i++) {
			if(al.get(i).isChoose()){
				kk++;
				break;
			}
		}
		if(kk==0){
			QMUtil.getInstance().showToast(this, R.string.unselectedUpLoadFile);
			return false;
		}
		if(QMDevice.getInstance().getSelectDevice()==null){
			//主页没有选中设备进入文件共享页面来的
			if(QMDevice.getInstance().getAl().size()==0)
				QMUtil.getInstance().showToast(this, R.string.unabledDivice);
			else
				showDevice();//展示可用上传的设备
			return false;
		}

		return true;
	}
	
	/**
	 * 上传文件...
	 * @param
	 */
	private void startUpLoad(){
		new UploadFile(this, al, QMDevice.getInstance().getSelectDevice(), new ConnectFTPListener() {
			
			@Override
			public void success(){
				new Thread(new Runnable(){
					@Override
					public void run(){
						try {
							FTPUtil.getInstance(QMDevice.getInstance().getSelectDevice()).getFTPClient().sendCustomCommand("FINISH&&DEVICENAME="+ Collocation.getCollocation().getDeviceName()+"&&ADDRESS="+NetworkUtils.getNetworkIP());
							DebugLog.showLog(this,"FINISH&&DEVICENAME="+Collocation.getCollocation().getDeviceName()+"&&ADDRESS="+ NetworkUtils.getNetworkIP());
						}catch (Exception e){

						}
					}
				}).start();

				cancel();
			}
			
			@Override
			public void fail(int state){

			}
		});
	}
	
	//全选
	private void selectAll() {
		if(chooseAll){
			for (int i = 0; i < al.size(); i++) {
				al.get(i).setChoose(false);
			}
			chooseAll=false;
			selectAll.setBackgroundResource(R.drawable.choose_all_bg);
		}
		else{
			for (int i = 0; i < al.size(); i++) {
				al.get(i).setChoose(true);
			}
			chooseAll=true;
			selectAll.setBackgroundResource(R.drawable.undo_choose);
		}
		upAdapter(1);
	}

	//初始化标题和弹出框
	private void intTitle(){
		type = getIntent().getIntExtra("type", 0);
		if (type==2||type==3||type==5){
            ll_sort.setVisibility(View.GONE);
        }else {
            ll_sort.setVisibility(View.VISIBLE);
        }
		switch (type) {
		case 4:
			head_title.setText(getResources().getString(R.string.other));
			break;
		case 1:
			head_title.setText(getResources().getString(R.string.document));
			break;
		case 2:
			head_title.setText(getIntent().getStringExtra("name")+"");
			break;
		case 3:
			head_title.setText(getResources().getString(R.string.video));
			break;
		case 5:
			head_title.setText(getResources().getString(R.string.download));
			break;
		}
	}
	
	
	//列表的点击事件
	public void onItemClick(AdapterView<?> parent, View view, int position, long id){
		if (isLongclickState) {
			if (al.get(position).isChoose()){
				al.get(position).setChoose(false);
				chooseAll=false;
				selectAll.setBackgroundResource(R.drawable.choose_all_bg);
			}
			else{
				if(albumName==0&& QMDbUtil.getIntance(this).hashName(al.get(position).getName())){
					QMUtil.getInstance().showToast(this, R.string.alphe_exist);
					return ;
				}
				al.get(position).setChoose(true);
			}
			upAdapter(1);
			int kk=0;
			for (int i = 0; i <al.size(); i++) {
				if(!al.get(i).isChoose()){
					kk=1;
					break;
				}
			}
			if(kk==0){
				chooseAll=true;
				selectAll.setBackgroundResource(R.drawable.undo_choose);
			}
		}else{
			if(albumName==0){
				Intent intent=new Intent(this,AlbumAndVideoFileActivity.class);
				intent.putExtra("type", 2);
				intent.putExtra("name", al.get(position).getName());
				startActivity(intent);
			}else if(type==2){
				Intent intent=new Intent(this,LocalPhotoScanActivity.class);
				QMUtil.getInstance().getPhotos().clear();
				QMUtil.getInstance().getPhotos().addAll(al);
				intent.putExtra("position",position);
				intent.putExtra("scan",getIntent().getIntExtra("scan", 0));
				startActivity(intent);
				return ;
			}else{
				new OpenAppUtils(this).openFiles(al.get(position).getPath());
			}
		}
	}

	//长按事件
	public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id){
		if(getIntent().getIntExtra("scan", 0)!=0||isLongclickState)//从显示分享相册处查看图片时不需要显示底部视图
			return false;
		isLongclickState = true;
		upAdapter(2);
		Animation animation = AnimationUtils.loadAnimation(this,
				R.anim.photo_dialog_in_anim);
		animation.setDuration(800);
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
				
			}
		});
		cancel.setVisibility(View.VISIBLE);
		sort.setVisibility(View.GONE);
		return false;
	}

	@Override
	public void connectFTPSuccess() {
		startUpLoad();
	}



}

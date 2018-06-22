package com.shareshow.airpc.activity;


import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

import com.shareshow.aide.R;
import com.umeng.analytics.MobclickAgent;
import com.shareshow.airpc.Collocation;
import com.shareshow.airpc.imagebat.LocalBitmapManager;
import com.shareshow.airpc.model.RootPoint;
import com.shareshow.airpc.ports.ConnectFTPListener;
import com.shareshow.airpc.ports.ImgOnClick;
import com.shareshow.airpc.socket.common.FTPUtil;
import com.shareshow.airpc.socket.common.QMDevice;
import com.shareshow.airpc.socket.common.QMShareDao;
import com.shareshow.airpc.socket.common.UploadFile;
import com.shareshow.airpc.util.DebugLog;
import com.shareshow.airpc.util.NetworkUtils;
import com.shareshow.airpc.util.QMUtil;
import com.shareshow.airpc.widget.TouchImageView;

/**
 * 本地相册浏览
 * @author tanwei
 *
 */
public class LocalPhotoScanActivity extends PhotoScanActivity{
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initData();
	}

	public void onResume(){
		super.onResume();
		MobclickAgent.onResume(this);
	}

	public void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
	}

	private void initData() {
		title_head.setText(""+ QMUtil.getInstance().getPhotos().get(position).getName());
		loadData(QMUtil.getInstance().getPhotos().size(),false, R.layout.local_photo_scan);
	}

//分享
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.share:
			new QMShareDao(this, QMUtil.getInstance().getPhotos().get(position), new ImgOnClick() {
				
				@Override
				public void imgClick(int position) {
					if(position==0)
						onPointClick();
				}
			});
			break;
//上传或下载
		case R.id.upOrdownLoad:
			if(QMDevice.getInstance().getSelectDevice()==null){
				if(QMDevice.getInstance().getAl().size()==0)
					QMUtil.getInstance().showToast(this, R.string.unabledDivice);
				else
					showDevice();//展示可用上传的设备
				return ;
			}
			startUpLoad() ;
		}
	}
	
	/**
	 * 上传文件...
	 * @param
	 */
	private void startUpLoad() {
		new UploadFile(this, QMUtil.getInstance().getPhotos().get(position), QMDevice.getInstance().getSelectDevice(), new ConnectFTPListener() {
			
			@Override
			public void success() {
				new Thread(new Runnable(){
					@Override
					public void run(){
						try {
							FTPUtil.getInstance(QMDevice.getInstance().getSelectDevice()).getFTPClient().sendCustomCommand("FINISH&&DEVICENAME="+ Collocation.getCollocation().getDeviceName()+"&&ADDRESS="+ NetworkUtils.getNetworkIP());
							DebugLog.showLog(this,"FINISH&&DEVICENAME="+Collocation.getCollocation().getDeviceName()+"&&ADDRESS="+ NetworkUtils.getNetworkIP());
						}catch (Exception e){

						}
					}
				}).start();

				onPointClick();
			}
			
			@Override
			public void fail(int state) {
			}
		});
	}
	

	@Override
	public void itemView(View vv, int position) {
		TouchImageView  photoview=(TouchImageView) vv.findViewById(R.id.photoview);
		
		photoview.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				onPointClick();
			}
		});
		photoview.setTag(QMUtil.getInstance().getPhotos().get(position).getPath());
		new LocalBitmapManager(LocalPhotoScanActivity.this).loadBitmap(QMUtil.getInstance().getPhotos().get(position).getPath(), photoview);
		
	}

	@Override
	public void onPageSelected(int postion) {
		super.onPageSelected(postion);
		title_head.setText(""+QMUtil.getInstance().getPhotos().get(postion).getName());
	}

	@Override
	public void connectFTPSuccess() {
		startUpLoad();
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

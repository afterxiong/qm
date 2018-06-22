package com.shareshow.airpc.activity;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

import com.shareshow.aide.R;
import com.umeng.analytics.MobclickAgent;
import com.shareshow.airpc.imagebat.BoxImage;
import com.shareshow.airpc.imagebat.LocalBitmapManager;
import com.shareshow.airpc.model.QMRemoteFTPFile;
import com.shareshow.airpc.model.RootPoint;
import com.shareshow.airpc.ports.DownLoadListener;
import com.shareshow.airpc.socket.common.DownLoadFile;
import com.shareshow.airpc.socket.common.QMDevice;
import com.shareshow.airpc.util.DebugLog;
import com.shareshow.airpc.util.QMFileType;
import com.shareshow.airpc.util.QMUtil;
import com.shareshow.airpc.widget.ProgressWheel;

import java.io.File;
import java.util.ArrayList;

/**
 * 浏览远程设备图片
 * @author tanwei
 *
 */
public class BoxPhotoScanActivity extends PhotoScanActivity  {

	private ArrayList<QMRemoteFTPFile> al;
	private String dir="";
	BoxImage boximage;
	@SuppressWarnings("unchecked")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		al = (ArrayList<QMRemoteFTPFile>) getIntent().getSerializableExtra("path");
		String isDirectory=getIntent().getStringExtra("isDirectory");
		DebugLog.showLog(this,"isDirectory:"+isDirectory);
		boximage=new BoxImage(getApplicationContext());
		if(isDirectory!=null)
			dir=isDirectory+"/";
		loadData(al.size(),true, R.layout.box_photo_scan);
	}
	

	public void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
	}

	public void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()){
		case R.id.upOrdownLoad:
			final QMRemoteFTPFile mMyFTPFile = al.get(position);
			if(mMyFTPFile.getPermit()==1){
				QMUtil.getInstance().showToast(this, R.string.without_permittion);
				return ;
			}
			String isDirectory=null;
			if(!dir.equals(""))
				isDirectory=dir.substring(0, dir.length()-1);
			new DownLoadFile(this, mMyFTPFile, QMDevice.getInstance().getSelectDevice(),isDirectory,true, new DownLoadListener() {

				@Override
				public void success() {
				}

				@Override
				public void fail() {
				}
			});
			break;

		}

	}

	@Override
	public void passwdVertifyCallBack(RootPoint rp) {
		
	}

	@Override
	public void onPageSelected(int postion){
		super.onPageSelected(postion);
		title_head.setText("" + al.get(postion).getName());
	}

	@Override
	public void itemView(View vv, int position){
		 ImageView photoview = (ImageView) vv.findViewById(R.id.photoview);
		 ProgressWheel progress_wheel = (ProgressWheel) vv.findViewById(R.id.progress_wheel);
		 String cachePath = QMFileType.CACHE + "/"+ al.get(position).getName();
		 photoview.setOnClickListener(new OnClickListener(){
			
			@Override
			public void onClick(View v){
				onPointClick();
			}

		});
		if (new File(cachePath).exists()){
			DebugLog.showLog(this,"存在");
			photoview.setTag(cachePath);
			new LocalBitmapManager(BoxPhotoScanActivity.this).loadBitmap(cachePath,
					photoview);
		}else{
			progress_wheel.setVisibility(0);
			String remoteFileName = dir+al.get(position).getName();
			if (QMDevice.getInstance().getSelectDevice().getdType()==0) {
				remoteFileName = al.get(position).getLink();
			}else if(QMDevice.getInstance().getSelectDevice().getdType()==2){
				remoteFileName=getResources().getString(R.string.shareDirectory)+remoteFileName;
			}
			boximage.initImage(photoview, progress_wheel,al.get(position),dir);
		}
	} 
 


	@Override
	public void connectFTPSuccess() {
		
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

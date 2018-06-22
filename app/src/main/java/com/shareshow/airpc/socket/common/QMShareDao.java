package com.shareshow.airpc.socket.common;

import android.app.Activity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.shareshow.aide.R;
import com.shareshow.airpc.model.LancherFile;
import com.shareshow.airpc.model.QMLocalFile;
import com.shareshow.airpc.ports.ImgOnClick;
import com.shareshow.airpc.util.QMDbUtil;
import com.shareshow.airpc.util.QMDialog;
import com.shareshow.airpc.util.QMUtil;

import java.io.File;
import java.util.ArrayList;

public class QMShareDao {

	private Activity context;
	private ArrayList<QMLocalFile> al;
	private QMLocalFile mMyLocalFile;
	private ImgOnClick mImgOnClick;
	private int isFolder=1;//0文件夹  1文件
	
	public QMShareDao(Activity context, ArrayList<QMLocalFile> al, int isFolder, ImgOnClick mImgOnClick) {
		super();
		this.context = context;
		this.al = al;
		this.mImgOnClick = mImgOnClick;
		this.isFolder=isFolder;
		initDownLoadSetting();
	}
	
	public QMShareDao(Activity context, QMLocalFile mMyLocalFile, ImgOnClick mImgOnClick) {
		super();
		this.context = context;
		this.mMyLocalFile = mMyLocalFile;
		this.mImgOnClick = mImgOnClick;
		this.mMyLocalFile.setChoose(true);
		al=new ArrayList<QMLocalFile>();
		al.add(mMyLocalFile);
		initDownLoadSetting();
	}

	private void initDownLoadSetting() {
		int kk=0;
		for (int i = 0; i < al.size(); i++) {
			if(al.get(i).isChoose()){
				kk=1;
				break;
			}
		}
		if(kk==1){
			showShareDialog();
		}
		else{
			QMUtil.getInstance().showToast(context, R.string.unselectedSharedFile);
			if(mImgOnClick!=null)
				mImgOnClick.imgClick(1);			
		}
		
	}
	
	//显示分享后是否允许被下载的对话框
	private void showShareDialog() {
		View vv = View.inflate(context, R.layout.app_tip, null);
		final QMDialog dialog=new QMDialog(context,vv);
		((TextView)vv.findViewById(R.id.title)).setText(context.getResources().getString(R.string.file_load));
		Button comfirm=(Button) vv.findViewById(R.id.comfirm);
		comfirm.setText(context.getResources().getString(R.string.yes));
		(vv.findViewById(R.id.cancel)).setOnClickListener(new  View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				dialog.dismiss();
				share(1);
			}
		});
		comfirm.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				dialog.dismiss();
				share(0);
			}
		});
	}
	/**
	 * @param permit 是否允许被下载
	 */
	private void share(int permit) {
		try {
			QMDbUtil dbUtil=QMDbUtil.getIntance(context);
			for (int i = 0; i < al.size(); i++) {
				if(al.get(i).isChoose()){
					if(isFolder==0&&dbUtil.hashName(al.get(i).getName()))
						continue;
					if(isFolder==1&&dbUtil.hasPath(al.get(i).getPath(),permit)){
						continue;
					}
					LancherFile mLancherFile=new LancherFile();
					mLancherFile.setName(al.get(i).getName());
					if(isFolder==0)
						mLancherFile.setPath(new File(al.get(i).getPath()).getParent());
					else
						mLancherFile.setPath(al.get(i).getPath());
					mLancherFile.setSize(al.get(i).getSize());
					mLancherFile.setUpdate(al.get(i).getUpdate());
					mLancherFile.setType(al.get(i).getType());
					mLancherFile.setFileDir(isFolder);
					mLancherFile.setPermit(permit);
					dbUtil.insert(mLancherFile);
				}
			}
			QMUtil.getInstance().showToast(context, R.string.share_success);
			if(mImgOnClick!=null)
				mImgOnClick.imgClick(0);
		} catch (Exception e) {
			QMUtil.getInstance().showToast(context, R.string.share_fail);
			if(mImgOnClick!=null)
				mImgOnClick.imgClick(1);
		}
	}
	
}

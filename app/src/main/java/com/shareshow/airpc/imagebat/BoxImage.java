package com.shareshow.airpc.imagebat;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ImageView;

import com.shareshow.aide.R;
import com.shareshow.airpc.ftpclient.FTPDataTransferListener;
import com.shareshow.airpc.model.QMRemoteFTPFile;
import com.shareshow.airpc.socket.common.FTPUtil;
import com.shareshow.airpc.socket.common.QMDevice;
import com.shareshow.airpc.util.DebugLog;
import com.shareshow.airpc.util.QMFileType;
import com.shareshow.airpc.widget.ProgressWheel;

import java.io.File;

/**
 * 通过地址来加载图片进行显示
 * 
 * */
public class BoxImage {
	
	Context context;
	
	public BoxImage(Context context) {
		this.context = context;
	}
	
//加载图片
	public void initImage(ImageView photoview , ProgressWheel progress_wheel, QMRemoteFTPFile qmRemoteFTPFile, String dir) {
		String cachePath = QMFileType.CACHE + "/"+ qmRemoteFTPFile.getName();
		String remoteFileName = dir+qmRemoteFTPFile.getName();
		if (QMDevice.getInstance().getSelectDevice().getdType()==0){
			remoteFileName = qmRemoteFTPFile.getLink();
		}else if(QMDevice.getInstance().getSelectDevice().getdType()==2){
			remoteFileName=context.getResources().getString(R.string.shareDirectory)+remoteFileName;
		}
		loadBoxImg(photoview, progress_wheel, cachePath, remoteFileName);
}
	private void loadBoxImg(final ImageView photoview , final ProgressWheel progress_wheel, final String cachePath, final String remoteFileName) {
		
		final Handler handler = new Handler() {

			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				if (progress_wheel!=null) 
					progress_wheel.setVisibility(View.GONE);
				if (msg.obj != null&&photoview.getTag().equals(cachePath)) {
                	photoview.setImageBitmap((Bitmap) msg.obj);
                	photoview.invalidate();
                }
			}

		};
		new Thread() {
			public void run() {
				try {
					DebugLog.showLog(this,"加载图片..path:"+remoteFileName);
					FTPUtil.getInstance(QMDevice.getInstance().getSelectDevice())
							.getFTPClient()
							.download(remoteFileName, new File(cachePath),
									new FTPDataTransferListener() {

										@Override
										public void transferred(
												int paramInt) {
										}

										@Override
										public void started() {
											DebugLog.showLog(this,"started！");
										}

										@Override
										public void failed() {
											DebugLog.showLog(this,"failed！");
										}

										@Override
										public void completed() {
											photoview.setTag(cachePath);
											//从路径中下载出bitmap
											Bitmap bitmap=new LocalBitmapManager(context).downloadBitmap(cachePath);
											Message message=handler.obtainMessage();
											message.obj=bitmap;
											message.what=0;
											handler.sendMessage(message);
										}

										@Override
										public void aborted(){
											DebugLog.showLog(this,"aborted！");
										}
									});
				} catch (Exception e) {
					e.printStackTrace();
				}
			};
		}.start();
	}


}

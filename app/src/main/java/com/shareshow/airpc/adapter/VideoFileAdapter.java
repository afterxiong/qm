package com.shareshow.airpc.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.widget.ImageView;
import android.widget.TextView;

import com.shareshow.airpc.imagebat.VideoThumbLoader;
import com.shareshow.airpc.model.QMLocalFile;
import com.shareshow.airpc.util.QMFileType;


import java.util.ArrayList;

@SuppressLint("NewApi")
public class VideoFileAdapter<T> extends PhotoVideoAdapter<T> {

	private VideoThumbLoader mVideoThumbLoader;

	public VideoFileAdapter(Context context, ArrayList<T> al) {
		super(context, al);
		// 初始化缩略图载入方法
		mVideoThumbLoader = new VideoThumbLoader();
	}

	@Override
	public void loadView(ImageView img, TextView time, TextView size,
                         QMLocalFile mFTPFile) {
		time.setText(QMFileType.makeSimpleDateStringFromLong(mFTPFile.getUpdate()));
		size.setText(getSize(mFTPFile.getSize()));
		mVideoThumbLoader.showThumbByAsynctack(mFTPFile.getPath(),img);
	}


}

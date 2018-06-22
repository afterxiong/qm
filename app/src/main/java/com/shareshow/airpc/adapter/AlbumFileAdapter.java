package com.shareshow.airpc.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.widget.ImageView;
import android.widget.TextView;

import com.shareshow.airpc.imagebat.LocalBitmapManager;
import com.shareshow.airpc.model.QMLocalFile;
import com.shareshow.airpc.util.QMFileType;


import java.util.ArrayList;

@SuppressLint("NewApi")
public class AlbumFileAdapter<T> extends PhotoVideoAdapter <T>{

	private LocalBitmapManager bitmapManager;
	
	public AlbumFileAdapter(Context context, ArrayList<T> al) {
		super(context, al);
		bitmapManager=new LocalBitmapManager(context);
	}

	@Override
	public void loadView(ImageView img, TextView time, TextView size,
                         QMLocalFile mFTPFile) {
		time.setText(QMFileType.makeSimpleDateStringFromLong(mFTPFile.getUpdate())+"");
		size.setText(getSize(mFTPFile.getSize()));
		bitmapManager.loadBitmap(mFTPFile.getPath(), img);
	}
}

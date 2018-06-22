package com.shareshow.airpc.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.shareshow.airpc.imagebat.LocalBitmapManager;
import com.shareshow.airpc.model.QMLocalFile;

import java.util.ArrayList;

@SuppressLint("NewApi")
public class AlbumNameAdapter<T> extends PhotoVideoAdapter<T> {
	
	private LocalBitmapManager bitmapManager;
	
	public AlbumNameAdapter(Context context, ArrayList<T> al) {
		super(context, al);
		bitmapManager=new LocalBitmapManager(context);
	}

	@Override
	public void loadView(ImageView img, TextView time, TextView size,
                         QMLocalFile mFTPFile) {
		time.setText(""+mFTPFile.getType()+"张");
		size.setVisibility(View.GONE);
		//添加缩略图
		bitmapManager.loadBitmap(mFTPFile.getPath(),img);
	}

}

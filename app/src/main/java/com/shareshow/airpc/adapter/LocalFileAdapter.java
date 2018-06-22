package com.shareshow.airpc.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.shareshow.aide.R;
import com.shareshow.airpc.imagebat.LocalBitmapManager;
import com.shareshow.airpc.imagebat.VideoThumbLoader;
import com.shareshow.airpc.model.LancherFile;
import com.shareshow.airpc.util.QMFileType;

import java.util.ArrayList;


/**
 * 本地共享的文件
 * */
public class LocalFileAdapter<T> extends QMBaseAdapter<T> {
	private LocalBitmapManager bitmapManager;
	private VideoThumbLoader mVideoThumbLoader;
	public LocalFileAdapter(Context context, ArrayList<T> al) {
		super(context, al);
		bitmapManager=new LocalBitmapManager(context);
		mVideoThumbLoader = new VideoThumbLoader();
	}

	public View getView(final int position, View vv, ViewGroup parent) {
		final LancherFile mFTPFile=(LancherFile) getItem(position);
		if(vv==null)
			vv=getLayout(R.layout.localfile_list_item);
		ImageView img=ViewHolder.get(vv, R.id.img);
		TextView name=ViewHolder.get(vv, R.id.name);
		TextView time=ViewHolder.get(vv, R.id.time);
		TextView size=ViewHolder.get(vv, R.id.size);
		name.setText(mFTPFile.getName());
		if(mFTPFile.getFileDir()==1){
			time.setText(QMFileType.makeSimpleDateStringFromLong(mFTPFile.getUpdate()));
			size.setText(getSize(mFTPFile.getSize()));
			switch (mFTPFile.getType()) {
			case 7:
				img.setTag(mFTPFile.getPath());
				bitmapManager.loadBitmap(mFTPFile.getPath(),img);
				break;
			case 8:
				img.setTag(mFTPFile.getPath());
				mVideoThumbLoader.showThumbByAsynctack(mFTPFile.getPath(),img);
			break;
			default:
				img.setImageResource(getType(mFTPFile.getType()));
				break;
			}
			
			//img.setImageResource(getType(mFTPFile.getType()));
		}else{
			img.setImageResource(R.mipmap.wenjianjia);
			name.setText(mFTPFile.getName());
			time.setText(QMFileType.makeSimpleDateStringFromLong(mFTPFile.getUpdate()));
			size.setText("");
		}

		return vv;
	}
	

	@Override
	public int getItemViewType(int position) {
		return ((LancherFile) getItem(position)).getPermit();
	}


	@Override
	public int getViewTypeCount() {
		return 2;
	}
	
	

}

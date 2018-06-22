package com.shareshow.airpc.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.shareshow.aide.R;
import com.shareshow.airpc.model.QMLocalFile;

import java.util.ArrayList;

@SuppressLint("NewApi")
public abstract class PhotoVideoAdapter<T> extends QMBaseAdapter<T> {

	
	private boolean isLongclick;
	
	public PhotoVideoAdapter(Context context, ArrayList<T> al) {
		super(context, al);
	}


	public View getView(int position, View vv, ViewGroup parent) {
		final QMLocalFile mFTPFile =(QMLocalFile) getItem(position);
		if(vv==null)
			vv=getLayout(R.layout.photo_video_list_item);
		
		RelativeLayout relative=ViewHolder.get(vv, R.id.relative);
		ImageView img=ViewHolder.get(vv, R.id.img);
		TextView name=ViewHolder.get(vv, R.id.name);
		TextView time=ViewHolder.get(vv, R.id.time);
		TextView size=ViewHolder.get(vv, R.id.size);
		ImageView chebox=ViewHolder.get(vv, R.id.chebox);

		name.setText(mFTPFile.getName()+"");
		
		if (isLongclick) {
			chebox.setVisibility(View.VISIBLE);
			if (mFTPFile.isChoose()) {
				chebox.setImageResource(R.mipmap.chebox2);
				relative.setBackgroundResource(R.mipmap.choose_bg);
			} else {
				chebox.setImageResource(R.mipmap.chebox1);
				relative.setBackground(null);
			}
		} else{
			chebox.setVisibility(View.GONE);
			relative.setBackground(null);
		}
//		img.setImageResource(R.mipmap.base_article_bigimage);
		img.setTag(mFTPFile.getPath());
		loadView(img,time,size,mFTPFile);
		return vv;
	}


	public void isLongclick(boolean isLongclick) {
		this.isLongclick = isLongclick;
		notifyDataSetChanged();
	}
	
	public abstract void loadView(ImageView img, TextView time, TextView size, QMLocalFile mFTPFile);
}

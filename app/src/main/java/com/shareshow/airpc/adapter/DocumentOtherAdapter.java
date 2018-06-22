package com.shareshow.airpc.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.shareshow.aide.R;
import com.shareshow.airpc.imagebat.LocalBitmapManager;
import com.shareshow.airpc.model.QMLocalFile;
import com.shareshow.airpc.util.QMFileType;

import java.util.ArrayList;

@SuppressLint("NewApi")
public class DocumentOtherAdapter<T> extends QMBaseAdapter<T> {
	
	private boolean isLongclick;
	private LocalBitmapManager bitmapManager;
	public DocumentOtherAdapter(Context context, ArrayList<T> al) {
		super(context, al);
		bitmapManager=new LocalBitmapManager(context);
	}
	
	
	public View getView(int position, View vv, ViewGroup parent) {
		if(vv==null)
			vv=getLayout(R.layout.popup_list_item);
		RelativeLayout relative=ViewHolder.get(vv, R.id.relative);
		ImageView img=ViewHolder.get(vv, R.id.img);
		TextView name=ViewHolder.get(vv, R.id.name);
		TextView time=ViewHolder.get(vv, R.id.time);
		TextView size=ViewHolder.get(vv, R.id.size);
		ImageView chebox=ViewHolder.get(vv, R.id.chebox);

		final QMLocalFile mFTPFile=(QMLocalFile) getItem(position);
		if(mFTPFile.getFileDir()==1){//文件
			name.setText(mFTPFile.getName());
			time.setText(QMFileType.makeSimpleDateStringFromLong(mFTPFile.getUpdate()));
			size.setText(getSize(mFTPFile.getSize()));
			if(mFTPFile.getFileDir()==1){
				time.setText(QMFileType.makeSimpleDateStringFromLong(mFTPFile.getUpdate()));
				size.setText(getSize(mFTPFile.getSize()));
				switch (mFTPFile.getType()) {
				case 7:
					img.setTag(mFTPFile.getPath());
					bitmapManager.loadBitmap(mFTPFile.getPath(),img);
					break;
//				case 8:
//					img.setTag(mFTPFile.getPath());
//					mVideoThumbLoader.showThumbByAsynctack(mFTPFile.getPath(),img);
//				break;
				default:
					//添加图片的位置
					img.setImageResource(getType(mFTPFile.getType()));
					break;
				}
				}
//			img.setImageResource(getType(mFTPFile.getType()));
		}else{
			img.setImageResource(R.mipmap.wenjianjia);
			name.setText(mFTPFile.getName());
			time.setText(QMFileType.makeSimpleDateStringFromLong(mFTPFile.getUpdate()));
			size.setText("");
		}
		
		if(isLongclick){
			chebox.setVisibility(View.VISIBLE);
			if(mFTPFile.isChoose()){
				chebox.setImageResource(R.mipmap.chebox2);
				relative.setBackgroundResource(R.mipmap.choose_bg);
			}
			else{
				relative.setBackground(null);
				chebox.setImageResource(R.mipmap.chebox1);
			}
		}
		 else{
			chebox.setVisibility(View.GONE);
			relative.setBackground(null);
		}
	
		return vv;
	}
	

	public void isLongclick(boolean isLongclick) {
		this.isLongclick=isLongclick;
		notifyDataSetChanged();
	}

}

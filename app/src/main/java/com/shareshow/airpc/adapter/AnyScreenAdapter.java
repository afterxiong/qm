package com.shareshow.airpc.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.shareshow.aide.R;
import com.shareshow.airpc.imagebat.BoxImage;
import com.shareshow.airpc.model.QMRemoteFTPFile;
import com.shareshow.airpc.util.QMFileType;

import java.util.ArrayList;

@SuppressLint("NewApi")
public class AnyScreenAdapter<T> extends QMBaseAdapter<T>{
	
	private boolean isLongclick;
	private BoxImage boxImage;
	private String dir="";
	
	public AnyScreenAdapter(Context context, ArrayList<T> al , String isDirectory) {
		super(context, al);
		boxImage = new BoxImage(context);
		if(isDirectory!=null)
			dir=dir+"/";
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
		//集合中携带的对象
		final QMRemoteFTPFile mFTPFile=(QMRemoteFTPFile) getItem(position);
		if(mFTPFile.getDir()==1){//文件
			name.setText(mFTPFile.getName());
			time.setText(QMFileType.makeSimpleDateStringFromLong(mFTPFile.getModifiedDate().getTime()));
			size.setText(getSize(mFTPFile.getSize()));
			img.setImageResource(getType(mFTPFile.getType()));
//			switch (mFTPFile.getType()) {
//			case 7:
//				boxImage.initImage(img, null,mFTPFile,dir);
//				break;
//			default:
//				img.setImageResource(getType(mFTPFile.getType()));
//				break;
//			}
		}else{
			img.setImageResource(R.mipmap.wenjianjia);
			name.setText(mFTPFile.getName());
			time.setText(QMFileType.makeSimpleDateStringFromLong(mFTPFile.getModifiedDate().getTime()));
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

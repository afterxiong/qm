package com.shareshow.airpc.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.shareshow.aide.R;
import com.shareshow.airpc.socket.common.QMDevice;

import java.util.ArrayList;

public class SelectedDeviceListAdapter<T> extends QMBaseAdapter<T> {


	public SelectedDeviceListAdapter(Context context, ArrayList<T> al) {
		super(context, al);
	}

	@Override
	public View getView(int position, View vv, ViewGroup parent) {
		if(vv==null)
			vv=getLayout(R.layout.load_popup_item);
		ImageView icon_img= ViewHolder.get(vv, R.id.icon_img);
		ImageView choose_img= ViewHolder.get(vv, R.id.choose_img);
		TextView name= ViewHolder.get(vv, R.id.name);

		name.setText(QMDevice.getInstance().getAl().get(position).getName()+"");
		switch (QMDevice.getInstance().getAl().get(position).getdType()) {
		case -1:
			icon_img.setImageResource(R.mipmap.list_img);
			break;
		case 0:
		case 1:
			 icon_img.setImageResource(R.mipmap.mobile);
			break;
		case 2:
			icon_img.setImageResource(R.mipmap.pc_icon);
			break;
		}
		if (QMDevice.getInstance().getAl().get(position).isPlay()) {
			choose_img.setVisibility(View.VISIBLE);
		} else {
			choose_img.setVisibility(View.GONE);
		}
		return vv;
	}

}

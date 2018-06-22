package com.shareshow.airpc.adapter;

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.shareshow.aide.R;
import com.shareshow.airpc.model.RootPoint;
import com.shareshow.airpc.ports.BoxOnClickListener;
import com.shareshow.airpc.socket.common.QMDevice;

import java.util.ArrayList;

public class ScreenShareAdapter extends BaseAdapter {
	
	private Context context;
	private ArrayList<RootPoint> list=new ArrayList<RootPoint>();
	private BoxOnClickListener boxOnClick;

	public ScreenShareAdapter(Context context, BoxOnClickListener boxOnClick) {
		super();
		this.context = context;
		this.boxOnClick=boxOnClick;
		reLoadData();
	}
	
	public int getCount() {
		return this.list.size();
	}

	public Object getItem(int position) {
		return null;
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(int position, View vv, ViewGroup parent) {
		final RootPoint rootPoint=list.get(position);
		if(vv==null)
			vv= View.inflate(context, R.layout.load_popup_item, null);
		TextView name=ViewHolder.get(vv, R.id.name);
		ImageView choose_img=ViewHolder.get(vv, R.id.choose_img);
		ImageView icon_img=ViewHolder.get(vv, R.id.icon_img);
		RelativeLayout root=ViewHolder.get(vv, R.id.root);
		if(rootPointA!=null&&rootPoint.getAddress().equals(rootPointA.getAddress())){
			choose_img.setVisibility(View.VISIBLE);
			name.setTextColor(context.getResources().getColor(R.color.xtadff2f));
		}else{
			choose_img.setVisibility(View.GONE);
			name.setTextColor(context.getResources().getColor(R.color.white));
		}
		root.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(boxOnClick!=null)
					boxOnClick.onClick(rootPoint);
			}
		});
		switch (rootPoint.getdType()) {
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
		//icon_img.setImageResource(R.drawable.list_img);
		name.setText(rootPoint.getName());
		return vv;
	}

	private RootPoint rootPointA = null;
	
	
	public void reData(RootPoint rootPoint){
		this.rootPointA=rootPoint;
		 reLoadData();
		notifyDataSetChanged();
	}

	/**
	 * 重新装载数据 ，仅显示任盒的数据
	 */
	public void reLoadData(){
		this.list.clear();
		for (int i = 0; i < QMDevice.getInstance().getSize(); i++) {
			RootPoint point= QMDevice.getInstance().getRootPoint(i);
//			if(point.getdType()==-1)
				this.list.add(point);
		}
	}
}

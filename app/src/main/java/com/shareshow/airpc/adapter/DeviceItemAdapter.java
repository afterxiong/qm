package com.shareshow.airpc.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.jauker.widget.BadgeView;
import com.shareshow.aide.R;
import com.shareshow.airpc.model.QMLocalFile;
import com.shareshow.airpc.model.RootPoint;
import java.util.ArrayList;

/**
 * Created by TEST on 2017/12/16.
 */

public class DeviceItemAdapter<T> extends QMBaseAdapter {

    private DeviceItemAdapter.MainOnClickListener listener;
    private Context mContext;
    public DeviceItemAdapter(Context context, ArrayList<T> al, DeviceItemAdapter.MainOnClickListener listener) {
        super(context, al);
        this.mContext=context;
        this.listener=listener;
    }

    @SuppressLint("ResourceAsColor")
    public View getView(final int position, View convertView, ViewGroup parent) {
        final RootPoint rootPoint= (RootPoint) getItem(position);
        DeviceItemAdapter.Holder holder=null;
        if(convertView==null){
            convertView = LayoutInflater.from(mContext).inflate(R.layout.device_list_item,null);
            holder = new DeviceItemAdapter.Holder();
            holder.name= (TextView) convertView.findViewById(R.id.name);
            holder.lockItem= (RelativeLayout) convertView.findViewById(R.id.lock_item);
            holder.lock= (ImageView) convertView.findViewById(R.id.is_lock);
            holder.check= (ImageView) convertView.findViewById(R.id.check);
            holder.progress= (ProgressBar) convertView.findViewById(R.id.progress);
            holder.badgeView= (BadgeView) convertView.findViewById(R.id.bageView);
            holder.itemLine2= (LinearLayout) convertView.findViewById(R.id.item_line2);
          //  holder.itemLine1= (LinearLayout) convertView.findViewById(R.id.item_line1);
           // holder.shareScreen= (LinearLayout) convertView.findViewById(R.id.share_screen);
            holder.lookShareFile= (LinearLayout) convertView.findViewById(R.id.look_share_file);
            convertView.setTag(holder);
        }else{
            holder = (DeviceItemAdapter.Holder) convertView.getTag();
        }

        switch (rootPoint.getdType()){
            case -1:
                holder.lock.setImageResource(R.mipmap.list_img);
                break;
            case 0:
            case 1:
                holder. lock.setImageResource(R.mipmap.mobile);
                break;
            case 2:
                holder.lock.setImageResource(R.mipmap.pc_icon);
                break;
        }


        holder.lookShareFile.setVisibility(View.GONE);
//        holder.shareScreen.setVisibility(View.GONE);
//        holder.itemLine1.setVisibility(View.GONE);
        holder.itemLine2.setVisibility(View.GONE);


        if(rootPoint.isfileRecieve()&&rootPoint.getFiles()!=null){
            //Log.i("test","postion:"+position+"files:"+((RootPoint) getItem(position)).getFiles().size());
            //	Log.i("test","Adapter-------point:"+rootPoint.getName());
            holder.badgeView.setTargetView(holder.lockItem);
            //	Log.i("test","sie:"+rootPoint.getFiles().size());
            holder.badgeView.setBadgeCount(rootPoint.getFiles().size());
            holder.badgeView.setBackground(12,mContext.getResources().getColor(R.color.red));
            holder.badgeView.setBadgeGravity(Gravity.TOP| Gravity.RIGHT);
            holder. badgeView.setBadgeMargin(0,0,5,5);

            if(rootPoint.getFiles().size()==0){
                rootPoint.setIsfileRecieve(false);
                holder.lockItem.setOnClickListener(null);
            }else{
                holder.lockItem.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View v){
                        if(listener!=null){
                            listener.mainOnclick(rootPoint.getName(),rootPoint.getFiles());
                        }
                    }
                });
            }

        }else{
            holder.badgeView.setVisibility(View.GONE);
        }


        holder.lookShareFile.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                if(listener!=null){
                    listener.OnLookShareFile(rootPoint);
                }
            }
        });

//        holder.shareScreen.setOnClickListener(new View.OnClickListener(){
//            @Override
//            public void onClick(View v) {
//                if(listener!=null){
//                    listener.OnScrenShare(rootPoint);
//                }
//            }
//        });

        holder.name.setText(rootPoint.getName());
        if (rootPoint.isPlay()){
            holder.check.setVisibility(View.VISIBLE);
            holder.check.setImageResource(R.mipmap.screen_more_box_click);
            holder.itemLine2.setVisibility(View.VISIBLE);
              //  holder.itemLine1.setVisibility(View.VISIBLE);
              //  holder.shareScreen.setVisibility(View.VISIBLE);
                holder.lookShareFile.setVisibility(View.VISIBLE);

        }else{
            holder.check.setVisibility(View.VISIBLE);
            holder.check.setImageResource(R.mipmap.screen_more_box);
            holder.itemLine2.setVisibility(View.GONE);
//                holder.itemLine1.setVisibility(View.GONE);
//                holder.shareScreen.setVisibility(View.GONE);
            holder.lookShareFile.setVisibility(View.GONE);

        }

        if (rootPoint.isProgress()) {
            holder.check.setVisibility(View.GONE);
            holder.progress.setVisibility(View.VISIBLE);
        }else{
            holder.progress.setVisibility(View.GONE);
        }
        return convertView;
    }


    public interface  MainOnClickListener{
        void  mainOnclick(String name, ArrayList<QMLocalFile> files);
        void  OnScrenShare(RootPoint rootPoint);
        void  OnLookShareFile(RootPoint rootPoint);
        void  OnClickSelete(RootPoint rootPoint, int position);
    }


    public class Holder {
        TextView name;
        RelativeLayout lockItem;
        ImageView lock;
        ImageView check;
        ProgressBar progress;
        BadgeView badgeView;
        LinearLayout itemLine2;
//        LinearLayout itemLine1;
//        LinearLayout shareScreen;
        LinearLayout lookShareFile;

    }
}

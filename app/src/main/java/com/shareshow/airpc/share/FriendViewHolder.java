package com.shareshow.airpc.share;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.shareshow.aide.R;


/**
 * Created by Administrator on 2017/7/5 0005.
 */

public class FriendViewHolder extends BaseViewHolder  {

    public TextView friendName;
    private Context mContext;
    private LinearLayout linearLayout;
    private final ImageView item_img;

    public FriendViewHolder(View itemView, Context mContext) {
        super(itemView);
        friendName= (TextView) itemView.findViewById(R.id.friendName);
        linearLayout= (LinearLayout) itemView.findViewById(R.id.item);
        item_img = (ImageView) itemView.findViewById(R.id.item_img);
        this.mContext=mContext;

    }

    public void bindView(final Friend friend, final FriendAdapter.ItemClickListener itemClickListener){
        friendName.setText(friend.getDiviceName());
        Log.i("test","bindView");
       if(friend.getDiviceType()==-1){
           item_img.setImageDrawable(mContext.getResources().getDrawable(R.mipmap.list_img));
       }else if(friend.getDiviceType()==2){
           item_img.setImageDrawable(mContext.getResources().getDrawable(R.mipmap.pc_icon));
       }else{
           item_img.setImageDrawable(mContext.getResources().getDrawable(R.mipmap.mobile));
       }
        if(friend.isSelect()){
            linearLayout.setBackground(mContext.getResources().getDrawable(R.mipmap.friend_select));
        }else{
            linearLayout.setBackground(mContext.getResources().getDrawable(R.mipmap.friend_none));
        }
        linearLayout.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){

                if(!friend.isSelect()){
                    linearLayout.setBackground(mContext.getResources().getDrawable(R.mipmap.friend_select));
                    friend.setSelect(true);
                }else{
                    linearLayout.setBackground(mContext.getResources().getDrawable(R.mipmap.friend_none));
                    friend.setSelect(false);
                }
            }
        });
    }


}

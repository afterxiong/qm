package com.shareshow.airpc.share;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;


import com.shareshow.aide.R;

import java.util.List;

/**
 * Created by Administrator on 2017/7/5 0005.
 */

public class FriendAdapter  extends RecyclerView.Adapter<BaseViewHolder> {

    private Context mContext;

    private List<Friend> friends;

    private FriendViewHolder friendHodler;

    public FriendAdapter(Context context , List<Friend> friends){
        this.friends = friends;
        this.mContext=context;
    }

    public void setFriends(List<Friend> friends){
        this.friends=friends;
    }
    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        return new FriendViewHolder(LayoutInflater.from(mContext).inflate(R.layout.frienditem_l,null),mContext);
    }

    @Override
    public void onBindViewHolder(BaseViewHolder holder, int position) {

        friendHodler=(FriendViewHolder)holder;

        if(friends!=null&&friends.size()!=0){
            friendHodler.bindView(friends.get(position),listener);
        }


    }

    @Override
    public int getItemCount() {
        return friends==null?0:friends.size();
    }

    private ItemClickListener  listener=new ItemClickListener(){

        @Override
        public void onItemClick(int position) {


        }
    };

    public interface  ItemClickListener{

        void onItemClick(int position);
    }

}

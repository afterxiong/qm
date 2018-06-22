package com.shareshow.airpc.share;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.shareshow.aide.R;
import com.shareshow.airpc.model.QMLocalFile;

import java.util.ArrayList;


/**
 * Created by Administrator on 2017/7/5 0005.
 */

public class FileRecieveAdapter extends RecyclerView.Adapter<BaseViewHolder>  {

    private String friendName;
    private ArrayList<QMLocalFile> files;

    private Context mContext;
    private OnClickListener mItemClickListener;

    public FileRecieveAdapter(String friendName, Context context, ArrayList<QMLocalFile> files, OnClickListener listener){
        this.files=files;
        this.mContext=context;
        this.mItemClickListener=listener;
        this.friendName=friendName;

    }

    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view= LayoutInflater.from(mContext).inflate(R.layout.filerecieveitem,null);

        FileRecieveViewHolder holder= new FileRecieveViewHolder(view,mContext,mItemClickListener);

        return holder;
    }

    @Override
    public void onBindViewHolder(BaseViewHolder holder, int position){

        FileRecieveViewHolder fileViewHolder = (FileRecieveViewHolder) holder;

        if(files!=null){
            fileViewHolder.bindView(files,position,friendName);
        }
    }

    @Override
    public int getItemCount(){

        return files==null?0:files.size();
    }


    public interface  OnClickListener{

        void onItemClick(ArrayList<QMLocalFile> files, int position);
    }

}

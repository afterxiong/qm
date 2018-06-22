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

public class FileAdapter extends RecyclerView.Adapter<BaseViewHolder> {

    private ArrayList<QMLocalFile> files;
    private Context mContext;
    private int with;
    public FileAdapter(Context context , ArrayList<QMLocalFile> files, int with){
        this.files=files;
        this.mContext=context;
        this.with=with;
    }

    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType){

        View view= LayoutInflater.from(mContext).inflate(R.layout.fileitem_l,null);
//        LinearLayout.LayoutParams params= new LinearLayout.LayoutParams(1/2*with,ViewGroup.LayoutParams.WRAP_CONTENT);
//        view.setLayoutParams(params);
        FileViewHolder holder= new FileViewHolder(view,mContext);

        return holder;
    }

    @Override
    public void onBindViewHolder(BaseViewHolder holder, int position) {

        FileViewHolder fileViewHolder = (FileViewHolder) holder;

        if(files!=null){
            if(getItemCount()==1){
                fileViewHolder.bindView(files.get(position),listener,position,true);
            }else{
                fileViewHolder.bindView(files.get(position),listener,position,false);
            }

        }
    }

    @Override
    public int getItemCount() {

        return files==null?0:files.size();
    }


    private OnClickListener listener=new OnClickListener(){

        @Override
        public void onItemClick(int position) {
           if(files!=null){
               files.remove(position);
               notifyDataSetChanged();
           }
        }
    };

    public interface  OnClickListener{

        void onItemClick(int position);
    }

}

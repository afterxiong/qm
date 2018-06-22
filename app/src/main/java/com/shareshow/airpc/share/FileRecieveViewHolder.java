package com.shareshow.airpc.share;

import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.shareshow.aide.R;
import com.shareshow.airpc.model.QMLocalFile;

import java.util.ArrayList;

/**
 * Created by Administrator on 2017/7/5 0005.
 */

public class FileRecieveViewHolder extends BaseViewHolder {

    private final FileRecieveAdapter.OnClickListener listener;

    private Context mContext;

    public TextView fileName;
    private LinearLayout linearLayout;
    private final View view;

    //   public ImageView fileType;

  //  private ImageView itemClose;

//    private int[]  types = new int[] { R.mipmap.other, R.mipmap.ppt, R.mipmap.doc,
//            R.mipmap.file_icon_xls, R.mipmap.pdf, R.mipmap.file_icon_txt,
//            R.mipmap.html, R.mipmap.img_type2, R.mipmap.img_type3,
//            R.mipmap.mp3, R.mipmap.apk, R.mipmap.zip, R.mipmap.rar };

    public FileRecieveViewHolder(View itemView, Context mContext, FileRecieveAdapter.OnClickListener listener) {
        super(itemView);
        fileName= (TextView) itemView.findViewById(R.id.filename);
        linearLayout = (LinearLayout) itemView.findViewById(R.id.item);
        view = itemView.findViewById(R.id.view);
        this.listener=listener;
        this.mContext=mContext;


    }

     public void bindView(final ArrayList<QMLocalFile> files, final int position, String friendName){
         fileName.setText("收到"+friendName+"发来的:"+files.get(position).getName());
         if(position==(files.size()-1)){
             view.setVisibility(View.GONE);
         }
         linearLayout.setOnClickListener(new View.OnClickListener(){
             public void onClick(View v){
                 if(listener!=null){
                     listener.onItemClick(files,position);
                 }
             }
         });
        // fileType.setImageDrawable(mContext.getResources().getDrawable(file.getFileType()));
     //   fileType.setImageDrawable(mContext.getResources().getDrawable(types[file.getType()]));

     }

}

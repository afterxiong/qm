package com.shareshow.airpc.share;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

import com.shareshow.aide.R;


/**
 * Created by Administrator on 2017/7/10 0010.
 */

public class FileDialog extends AlertDialog {

     private ViewGroup d;

     private Context mContext;

    public FileDialog(Context context, int themeResId) {
        super(context, themeResId);
        initView(context);
    }

    public FileDialog(Context context) {
        super(context);
        initView(context);
    }

    private void initView(Context context) {
         d = (ViewGroup) View.inflate(context, R.layout.file_download_list, null);
         this.mContext=context;

      }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(d);
    }

   public ViewGroup getView(){

       return d;
   }


}
package com.shareshow.airpc.adapter;

import android.content.Context;
import android.text.format.Formatter;
import android.view.View;
import android.widget.BaseAdapter;


import com.shareshow.aide.R;

import java.util.ArrayList;

public abstract class QMBaseAdapter<T> extends BaseAdapter {

    private ArrayList<T> al = null;
    private Context context;
    private int[] types;

    public QMBaseAdapter(Context context, ArrayList<T> al){
        super();
        this.al = al;
        this.context = context;
        types = new int[]{R.mipmap.file_icon_unknown, R.mipmap.file_icon_ppt, R.mipmap.file_icon_doc,
                R.mipmap.file_icon_xls, R.mipmap.file_icon_pdf, R.mipmap.file_icon_txt,
                R.mipmap.file_icon_html, R.mipmap.file_icon_photo, R.mipmap.file_icon_video,
                R.mipmap.file_icon_mp3, R.mipmap.apk,R.mipmap.file_icon_zip};

//        types = new int[] { R.mipmap.other, R.mipmap.ppt, R.mipmap.doc,
//                R.mipmap.xls, R.mipmap.pdf, R.mipmap.txt,
//                R.mipmap.html, R.mipmap.img_type2, R.mipmap.img_type3,
//                R.mipmap.mp3, R.mipmap.apk, R.mipmap.zip, R.mipmap.rar };

    }

    @Override
    public int getCount() {
        return al.size();
    }

    @Override
    public Object getItem(int position) {
        return al.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    public View getLayout(int layout) {
        return View.inflate(context, layout, null);
    }

    public int getType(int position) {
        return types[position];
    }

    public void isLongclick(boolean isLongclick) {
    }

    public String getSize(long size) {
        return Formatter.formatFileSize(context, size) + "";
    }
}

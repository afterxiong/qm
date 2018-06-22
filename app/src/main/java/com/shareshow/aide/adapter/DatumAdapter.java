package com.shareshow.aide.adapter;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.shareshow.aide.R;
import com.shareshow.aide.dialog.DevConrtolWindow;
import com.shareshow.aide.dialog.FileConrtolWindow;
import com.shareshow.aide.dialog.ShareFileDialog;
import com.shareshow.aide.impl.OnItemClickListener;
import com.shareshow.aide.retrofit.entity.StudyMaterialsVisitingMaterials;
import com.shareshow.aide.util.OpenFileUtil;
import com.shareshow.airpc.util.DebugLog;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

/**
 * 学习资料列表
 * Created by xiongchengguang on 2017/12/12.
 */

public class DatumAdapter extends RecyclerView.Adapter<DatumAdapter.DatumHolder> implements View.OnClickListener,View.OnLongClickListener {

    private Activity activity;
    private OnItemClickListener listener;
    private List<StudyMaterialsVisitingMaterials> listSmvms;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    public static boolean isClickItem = false;
    private FileConrtolWindow fileConrtolWindow;
    private OnScrollListener onScrollListener;

    public DatumAdapter(Activity activity, List<StudyMaterialsVisitingMaterials> datadocList) {
        this.activity = activity;
        this.listSmvms = datadocList;
        fileConrtolWindow  = new FileConrtolWindow(activity, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = (int) v.getTag();
                StudyMaterialsVisitingMaterials smvm = listSmvms.get(position);
                ShareFileDialog shareFileDialog = new ShareFileDialog();
                Bundle args = new Bundle();
                args.putString(ShareFileDialog.FILE_PATH, smvm.getLocalPath());
                shareFileDialog.setArguments(args);
                shareFileDialog.setCancelable(false);
                shareFileDialog.show(activity.getFragmentManager(), "shareDialog");
            }
        });
        onScrollListener = new OnScrollListener();
    }

    public OnScrollListener getOnScrollListener() {
        return onScrollListener;
    }

    @Override
    public DatumHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(activity).inflate(R.layout.item_datum, parent, false);
        view.setOnClickListener(this);
        view.setOnLongClickListener(this);
        DatumHolder holder = new DatumHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(DatumHolder holder, int position) {
        holder.itemView.setTag(position);
        StudyMaterialsVisitingMaterials smvm = listSmvms.get(position);
        holder.name.setText(smvm.getSfFilename());
        holder.date.setText(dateFormat.format(smvm.getDate()));
        setFileIcon(holder, smvm.getSfFilename());
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    @Override
    public int getItemCount() {
        return listSmvms == null ? 0 : listSmvms.size();
    }


    @Override
    public void onClick(View v) {
        try {
            isClickItem = true;
            int position = (int) v.getTag();
            StudyMaterialsVisitingMaterials smvm = listSmvms.get(position);
            String oltPath = smvm.getLocalPath();
            activity.startActivity(OpenFileUtil.openFile(activity, oltPath));
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(activity, "无法打开文件", Toast.LENGTH_SHORT).show();
            isClickItem = false;
        }
    }

    @Override
    public boolean onLongClick(View v) {
        fileConrtolWindow.showAsDropDown(v, v.getWidth()/2,-(v.getHeight()/2));
        return true;
    }

    class DatumHolder extends RecyclerView.ViewHolder {

        private ImageView icon;
        private TextView name;
        private TextView date;
        private ImageView datumDelete;
        private ProgressBar progress;
        private View shareBut;

        public DatumHolder(View itemView) {
            super(itemView);
            icon = itemView.findViewById(R.id.file_icon);
            name = itemView.findViewById(R.id.file_name);
            date = itemView.findViewById(R.id.file_date);
//            datumInfo = itemView.findViewById(R.id.datum_info);
//            datumDelete = itemView.findViewById(R.id.datum_delete);
//            shareBut = itemView.findViewById(R.id.share_but);
//            shareBut.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    int position = (int) v.getTag();
//                    StudyMaterialsVisitingMaterials smvm = listSmvms.get(position);
//                    ShareFileDialog shareFileDialog = new ShareFileDialog();
//                    Bundle args = new Bundle();
//                    args.putString(ShareFileDialog.FILE_PATH, smvm.getLocalPath());
//                    shareFileDialog.setArguments(args);
//                    shareFileDialog.setCancelable(false);
//                    shareFileDialog.show(activity.getFragmentManager(), "shareDialog");
//                }
//            });
        }
    }


    public void setFileIcon(DatumHolder holder, String name) {
        String prefix = name.substring(name.lastIndexOf(".") + 1, name.length()).toLowerCase(Locale.getDefault());
        Drawable drawable = activity.getResources().getDrawable(R.mipmap.file_icon_unknown);
        if (prefix.equalsIgnoreCase("ppt") || prefix.equalsIgnoreCase("pptx")) {
            drawable = activity.getResources().getDrawable(R.mipmap.file_icon_ppt);
        } else if (prefix.equalsIgnoreCase("doc") || prefix.equalsIgnoreCase("docx")) {
            drawable = activity.getResources().getDrawable(R.mipmap.file_icon_doc);
        } else if (prefix.equalsIgnoreCase("xls") || prefix.equalsIgnoreCase("xlsx")) {
            drawable = activity.getResources().getDrawable(R.mipmap.file_icon_xls);
        } else if (prefix.equalsIgnoreCase("png") || prefix.equalsIgnoreCase("jpg")) {
            drawable = activity.getResources().getDrawable(R.mipmap.file_icon_photo);
        } else if (prefix.equalsIgnoreCase("mp4")) {
            drawable = activity.getResources().getDrawable(R.mipmap.file_icon_video);
        } else if (prefix.equalsIgnoreCase("mp3")) {
            drawable = activity.getResources().getDrawable(R.mipmap.file_icon_mp3);
        } else if (prefix.equalsIgnoreCase("pdf")){
            drawable = activity.getResources().getDrawable(R.mipmap.file_icon_pdf);
        } else if (prefix.equalsIgnoreCase("zip") || prefix.equalsIgnoreCase("rar")) {
            drawable = activity.getResources().getDrawable(R.mipmap.file_icon_zip);
        } else if (prefix.equalsIgnoreCase("txt")) {
            drawable = activity.getResources().getDrawable(R.mipmap.file_icon_txt);
        } else if(prefix.equalsIgnoreCase("apk")||prefix.equalsIgnoreCase("apk")){
            drawable = activity.getResources().getDrawable(R.mipmap.apk);
        }
//        drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
//        holder.datumInfo.setCompoundDrawables(drawable, null, null, null);
        holder.icon.setImageDrawable(drawable);
    }

    class OnScrollListener extends RecyclerView.OnScrollListener {

        public void onScrollStateChanged(RecyclerView recyclerView, int newState){
//            DebugLog.showLog2(this,"onScrollStateChanged");
        }

        public void onScrolled(RecyclerView recyclerView, int dx, int dy){
            if (fileConrtolWindow.isShowing()) {
                fileConrtolWindow.dismiss();
            }
        }
    }

}

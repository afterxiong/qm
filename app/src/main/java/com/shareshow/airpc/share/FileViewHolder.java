package com.shareshow.airpc.share;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.shareshow.aide.R;
import com.shareshow.airpc.model.QMLocalFile;

/**
 * Created by Administrator on 2017/7/5 0005.
 */

public class FileViewHolder extends BaseViewHolder {

    private Context mContext;

    public TextView fileName;

    public ImageView fileType;

    private ImageView itemClose;

    private int[] types = new int[]{R.mipmap.file_icon_unknown, R.mipmap.file_icon_ppt, R.mipmap.file_icon_doc,
            R.mipmap.file_icon_xls, R.mipmap.file_icon_pdf, R.mipmap.file_icon_txt,
            R.mipmap.file_icon_html, R.mipmap.file_icon_photo, R.mipmap.file_icon_video,
            R.mipmap.file_icon_mp3, R.mipmap.file_icon_zip};
    private final LinearLayout item;

    public FileViewHolder(View itemView, Context mContext) {

        super(itemView);
        fileName = (TextView) itemView.findViewById(R.id.filename);
        item = (LinearLayout) itemView.findViewById(R.id.item);
        fileType = (ImageView) itemView.findViewById(R.id.imagetype);
        itemClose = (ImageView) itemView.findViewById(R.id.item_close);
        this.mContext = mContext;

    }

    public void bindView(QMLocalFile file, final FileAdapter.OnClickListener listener, final int position, boolean isLast) {

        fileName.setText(file.getName());
        // fileType.setImageDrawable(mContext.getResources().getDrawable(file.getFileType()));
        fileType.setImageDrawable(mContext.getResources().getDrawable(types[file.getType()]));
        if (isLast) {
            itemClose.setOnClickListener(null);
            itemClose.setVisibility(View.GONE);
        } else {
            itemClose.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (listener != null) {
                        listener.onItemClick(position);
                    }
                }
            });
        }

    }
}

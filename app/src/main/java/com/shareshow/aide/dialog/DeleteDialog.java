package com.shareshow.aide.dialog;

import android.app.FragmentManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.shareshow.aide.R;
import com.shareshow.airpc.widget.BaseDialog;

/**
 * Created by xiongchengguang on 2017/11/13.
 */

public class DeleteDialog extends BaseDialog implements View.OnClickListener {

    public TextView title;
    public TextView content;
    public TextView confirm;
    public TextView cancel;

    private onDeleteListener listener;
    private static String contentValue;

    public static DeleteDialog get(String contentValue,FragmentManager fragmentManager) {
        DeleteDialog.contentValue=contentValue;
        DeleteDialog dialog = new DeleteDialog();
        return dialog;
    }


    @Override
    public View layout(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_audio_delete, container, false);
    }

    @Override
    public void initView(View view) {
        title = view.findViewById(R.id.del_title);
        content = view.findViewById(R.id.del_content);
        confirm = view.findViewById(R.id.del_confirm);
        cancel = view.findViewById(R.id.del_cancel);
        confirm.setOnClickListener(this);
        cancel.setOnClickListener(this);
        content.setText(contentValue);
    }



    public void setOnDeleteListener(onDeleteListener listener) {
        this.listener = listener;
    }

    @Override
    public void onClick(View v) {
        if (listener == null) {
            return;
        }
        if (v.getId() == R.id.del_cancel) {
            listener.cancel();
        } else if (v.getId() == R.id.del_confirm) {
            listener.confirm();
        }
    }

    public interface onDeleteListener {

        public void confirm();

        public void cancel();
    }

}

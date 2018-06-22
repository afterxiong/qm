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
 * 检测拜访进行中
 * Created by xiongchengguang on 2017/11/13.
 */

public class VisitWayDialog extends BaseDialog implements View.OnClickListener {

    public TextView title;
    public TextView content;
    public TextView confirm;
    public TextView cancel;

    private OnClickListener listener;

    public static VisitWayDialog get(FragmentManager fragmentManager) {
        VisitWayDialog dialog = new VisitWayDialog();
        return dialog;
    }


    @Override
    public View layout(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_visit_way, container, false);
    }

    @Override
    public void initView(View view) {
        title = view.findViewById(R.id.del_title);
        content = view.findViewById(R.id.del_content);
        confirm = view.findViewById(R.id.del_confirm);
        cancel = view.findViewById(R.id.del_cancel);
        confirm.setOnClickListener(this);
        cancel.setOnClickListener(this);
        title.setText("温馨提示");
        confirm.setText("前往");
        cancel.setText("放弃");
        content.setText("检测到拜访进行中，前往结束");
    }


    public void setOnClickListener(OnClickListener listener) {
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

    public interface OnClickListener {

        public void confirm();

        public void cancel();
    }

}

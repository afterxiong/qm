package com.shareshow.aide.dialog;

import android.app.FragmentManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.shareshow.aide.R;
import com.shareshow.aide.retrofit.entity.MyQrCode;
import com.shareshow.aide.util.CacheUserInfo;
import com.shareshow.aide.util.Fixed;
import com.shareshow.airpc.widget.BaseDialog;

/**
 * Created by xiongchengguang on 2017/11/13.
 */

public class UpdateTeamDialog extends BaseDialog implements View.OnClickListener {

    public TextView title;
    public TextView content;
    public TextView confirm;
    public TextView cancel;

    private onDeleteListener listener;
    private MyQrCode code;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        code = getArguments().getParcelable(Fixed.QR_CONTENT);

    }

    public static UpdateTeamDialog get(FragmentManager fragmentManager) {
        UpdateTeamDialog dialog = new UpdateTeamDialog();
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
        title.setText("更换团队");
        String teanName = CacheUserInfo.get().getTeamName();
        String newTeanName = code.getTeamName();
        content.setText("是否退出" + teanName + "团队,并加入" + newTeanName + "团队");
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

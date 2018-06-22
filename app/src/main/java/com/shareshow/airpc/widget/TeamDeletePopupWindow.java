package com.shareshow.airpc.widget;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.shareshow.aide.R;
import com.shareshow.aide.event.MessageEvent;

import org.greenrobot.eventbus.EventBus;


/**
 * 删除团队成员
 */
public class TeamDeletePopupWindow extends BasePopupWindow {


    private String id;
    private Context context;
    private TextView delete;

    public TeamDeletePopupWindow(Context context, String id) {
        super(context);
        this.context = context;
        this.id = id;
    }


    @Override
    public View inflater(LayoutInflater inflater) {
        return inflater.inflate(R.layout.team_delete_popuwindow, null);
    }

    /**
     * @param view
     */
    @Override
    public void initView(View view) {
        delete = (TextView) view.findViewById(R.id.delete);
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EventBus.getDefault().post(new MessageEvent(MessageEvent.EVENT_DELETE_TEAM_FRIEND, id));
                dismiss();
            }
        });
    }


}

package com.shareshow.aide.dialog;

import android.app.FragmentManager;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.shareshow.aide.R;
import com.shareshow.aide.event.MessageEvent;
import com.shareshow.aide.util.CacheUserInfo;
import com.shareshow.airpc.widget.BasePopupWindow;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by xiongchengguang on 2018/1/25.
 */

public class ShowTeamWindow extends BasePopupWindow implements View.OnClickListener {

    private Context context;
    private TextView showTeamQrCode;
    private TextView deleteTeam;
    private FragmentManager fragmentManager;

    public ShowTeamWindow(FragmentManager fragmentManager, Context context) {
        super(context);
        this.fragmentManager = fragmentManager;
        this.context = context;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.show_team_qr_code) {
            EventBus.getDefault().post(new MessageEvent(MessageEvent.SHOW_TEAM_QR_CODE));
            dismiss();
        } else if (v.getId() == R.id.delete_team) {
            dismiss();
            int tag = (int) deleteTeam.getTag();
            if (tag == MessageEvent.EVENT_DISMISS_TEAM) {
                //解散团队
                EventBus.getDefault().post(new MessageEvent(MessageEvent.EVENT_DISMISS_TEAM));
            } else if (tag == MessageEvent.EVENT_EXIT_TEAM) {
                //退出团队
                EventBus.getDefault().post(new MessageEvent(MessageEvent.EVENT_EXIT_TEAM));
            }
        }
    }

    @Override
    public View inflater(LayoutInflater inflater) {
        return inflater.inflate(R.layout.window_activity_team_info_show, null);
    }

    @Override
    public void initView(View view) {
        showTeamQrCode = view.findViewById(R.id.show_team_qr_code);
        deleteTeam = view.findViewById(R.id.delete_team);
        String userId = CacheUserInfo.get().getUserId();
        String teamCreateId = CacheUserInfo.get().getTeamCreaterId();
        if (userId.equals(teamCreateId)) {
            deleteTeam.setTag(MessageEvent.EVENT_DISMISS_TEAM);
            deleteTeam.setText("解散团队");
        } else {
            deleteTeam.setTag(MessageEvent.EVENT_EXIT_TEAM);
            deleteTeam.setText("退出团队");
        }
        showTeamQrCode.setOnClickListener(this);
        deleteTeam.setOnClickListener(this);

    }
}

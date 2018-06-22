package com.shareshow.aide.dialog;

import android.app.FragmentManager;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.shareshow.App;
import com.shareshow.aide.R;
import com.shareshow.aide.event.MessageEvent;
import com.shareshow.aide.util.CacheUserInfo;
import com.shareshow.airpc.widget.BasePopupWindow;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by xiongchengguang on 2018/1/25.
 */

public class CreateTeamWindow extends BasePopupWindow implements View.OnClickListener {

    private Context context;
    private TextView createTeam;
    private TextView addTeam;
    private FragmentManager fragmentManager;

    public CreateTeamWindow(FragmentManager fragmentManager, Context context) {
        super(context);
        this.fragmentManager = fragmentManager;
        this.context = context;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.add_create) {
            EventBus.getDefault().post(new MessageEvent(MessageEvent.ADD_TEAN));
            dismiss();
        } else if (v.getId() == R.id.create_team) {
            dismiss();
            String teamId = CacheUserInfo.get().getTeamId();
            if (teamId.isEmpty()) {
                CreateTeamDialog.newInstance().show(fragmentManager, "");
            } else {
                Toast.makeText(App.getApp(), "你已经有团队了", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public View inflater(LayoutInflater inflater) {
        return inflater.inflate(R.layout.window_activity_mine_team, null);
    }

    @Override
    public void initView(View view) {
        createTeam = view.findViewById(R.id.create_team);
        addTeam = view.findViewById(R.id.add_create);
        createTeam.setOnClickListener(this);
        addTeam.setOnClickListener(this);

    }
}

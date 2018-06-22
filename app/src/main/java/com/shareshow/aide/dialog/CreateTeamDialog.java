package com.shareshow.aide.dialog;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.shareshow.App;
import com.shareshow.aide.R;
import com.shareshow.aide.event.MessageEvent;
import com.shareshow.aide.retrofit.entity.UserInfo;
import com.shareshow.aide.util.CacheUserInfo;

import org.greenrobot.eventbus.EventBus;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by xiongchengguang on 2018/1/25.
 */

public class CreateTeamDialog extends BaseDialog implements View.OnClickListener {
    private ImageView closeIcon;
    private EditText teamName;
    private Button create;

    private static CreateTeamDialog dialog;

    public static CreateTeamDialog newInstance() {
        if (dialog == null) {
            dialog = new CreateTeamDialog();
            dialog.setCancelable(false);
        }
        return dialog;
    }

    @Override
    public View layout(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_create_team, container, false);
        initView(view);
        return view;
    }

    private void initView(View view) {
        closeIcon = view.findViewById(R.id.close);
        teamName = view.findViewById(R.id.new_team_name);
        create = view.findViewById(R.id.create);
        create.setOnClickListener(this);
        closeIcon.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.create) {
            create();
        } else if (v.getId() == R.id.close) {
            getDialog().dismiss();
        }
    }

    public void create() {
        String name = teamName.getText().toString();
        if (name != null && name.isEmpty()) {
            Toast.makeText(App.getApp(), "请输入团队名称", Toast.LENGTH_SHORT).show();
            return;
        }
        if (name.length() > 5) {
            Toast.makeText(App.getApp(), "团队名称最多5个字符", Toast.LENGTH_SHORT).show();
            return;
        }
        String userId = CacheUserInfo.get().getUserId();
        api.createTeam(userId, name)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.newThread())
                .subscribe(new Consumer<UserInfo>() {
                    @Override
                    public void accept(UserInfo userInfo) throws Exception {
                        if (userInfo.getReturnCode() == 1) {
                            Toast.makeText(App.getApp(), "创建团队成功", Toast.LENGTH_SHORT).show();
                            getDialog().dismiss();
                            teamName.setText("");
                            EventBus.getDefault().post(new MessageEvent(MessageEvent.UPDATE_TEAM));
                        } else {
                            Toast.makeText(App.getApp(), "创建团队失败", Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        throwable.printStackTrace();
                        Toast.makeText(App.getApp(), "创建团队失败", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}

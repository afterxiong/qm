package com.shareshow.aide.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.shareshow.App;
import com.shareshow.aide.R;
import com.shareshow.aide.activity.DeptMemberActivity;
import com.shareshow.aide.activity.TeamMenberActivity;
import com.shareshow.aide.activity.UserSettingActivity;
import com.shareshow.aide.dialog.BindingDeviceDialog;
import com.shareshow.aide.dialog.CreateTeamDialog;
import com.shareshow.aide.dialog.QRCodeDialog;
import com.shareshow.aide.dialog.UpdateTeamDialog;
import com.shareshow.aide.event.MessageEvent;
import com.shareshow.aide.mvp.presenter.PersonalPresenter;
import com.shareshow.aide.mvp.view.PersonalView;
import com.shareshow.aide.retrofit.entity.MyQrCode;
import com.shareshow.aide.retrofit.entity.UserInfo;
import com.shareshow.aide.util.CacheConfig;
import com.shareshow.aide.util.CacheUserInfo;
import com.shareshow.aide.util.Fixed;
import com.shareshow.airpc.activity.AboutActivity;
import com.shareshow.airpc.activity.UserHelpActivity;
import com.socks.library.KLog;
import com.xcg.ScanActivity1;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.w3c.dom.Text;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.app.Activity.RESULT_OK;

/**
 * 个人中心
 * Created by xiongchengguang on 2018/3/29.
 */

public class PersonalFragment extends BaseFragment<PersonalView, PersonalPresenter> implements PersonalView {

    //拥有组织和部门或团队
    @BindView(R.id.possess_org_dept_layout)
    public LinearLayout possess_org_dept_layout;
    @BindView(R.id.possess_name)
    public TextView possess_name;
    @BindView(R.id.possess_org_name)
    public TextView possess_org_name;
    @BindView(R.id.possess_cut)
    public TextView possess_cut;
    @BindView(R.id.possess_dept_name)
    public TextView possess_dept_name;
    @BindView(R.id.possess_qr_code)
    public ImageView possess_qr_code;
    @BindView(R.id.possess_check)
    public TextView possess_check;
    @BindView(R.id.possess_team_name)
    public TextView possess_team_name;
    @BindView(R.id.possess_team_qr_code)
    public ImageView possess_team_qr_code;
    //没有组织和部门及团队
    @BindView(R.id.not_have_layout)
    public LinearLayout not_have_layout;
    @BindView(R.id.have_name)
    public TextView have_name;
    @BindView(R.id.have_create_team)
    public TextView have_create_team;
    @BindView(R.id.have_add_team)
    public TextView have_add_team;
    //设置
    @BindView(R.id.aciotn_binding_devices)
    public RelativeLayout aciotn_binding_devices;
    @BindView(R.id.action_helper_propose)
    public RelativeLayout action_helper_propose;
    @BindView(R.id.action_about)
    public RelativeLayout action_about;
    @BindView(R.id.action_new_message)
    public RelativeLayout action_new_message;
    @BindView(R.id.action_setting)
    public RelativeLayout action_setting;
    @BindView(R.id.bind_device_name)
    public TextView bind_device_name;


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        ButterKnife.bind(this, view);
    }


    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser){
            KLog.d("可见刷新数据");
            onViewRefurbishUserInfo();
            presenter.getUserInfo();
        }
    }


    public void onResume() {
        super.onResume();
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
    }


    @Override
    public void onPause() {
        super.onPause();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public PersonalPresenter createPresenter() {
        return new PersonalPresenter();
    }

    @Override
    public int getLayoutResource() {
        return R.layout.fragment_personal;
    }

    @Override
    public void onViewRefurbishUserInfo() {
        KLog.d("数据刷新");
        bind_device_name.setText(CacheConfig.get().getBindDeviceName(CacheUserInfo.get().getUserPhone()));
        String name = CacheUserInfo.get().getUserName();
        if (name != null) {
            possess_name.setText(name);
            have_name.setText(name);
        }
        UserInfo userInfo = CacheUserInfo.get().getUserInfo();
        if (CacheUserInfo.get().getReturnCode() == 1) {
            possess_check.setVisibility(View.GONE);
            possess_qr_code.setVisibility(View.VISIBLE);
            possess_org_name.setEnabled(true);
        } else if (CacheUserInfo.get().getReturnCode() == 2) {
            possess_check.setVisibility(View.VISIBLE);
            possess_qr_code.setVisibility(View.GONE);
            possess_org_name.setEnabled(false);
        }
        String orgId = CacheUserInfo.get().getOrgId();
        String deptId = CacheUserInfo.get().getDeptId();
        String teamId = CacheUserInfo.get().getTeamId();

        //有团队 有部门 有组织
        if (!orgId.isEmpty() && !deptId.isEmpty() && !teamId.isEmpty()) {
            not_have_layout.setVisibility(View.GONE);
            possess_org_dept_layout.setVisibility(View.VISIBLE);
            String orgName = userInfo.getData().getOrgName();
            String deptNmae = userInfo.getData().getDeptName();
            String teamName = userInfo.getData().getTeamName();
            possess_org_name.setText(orgName);
            possess_dept_name.setText(deptNmae);
            possess_team_name.setText(teamName);
            possess_team_name.setVisibility(View.VISIBLE);
            possess_team_qr_code.setVisibility(View.VISIBLE);
            return;
        } else if (!orgId.isEmpty() && !deptId.isEmpty() && teamId.isEmpty()) {
            //有组织  有部门  无团队
            not_have_layout.setVisibility(View.GONE);
            possess_org_dept_layout.setVisibility(View.VISIBLE);
            String orgName = userInfo.getData().getOrgName();
            String deptNmae = userInfo.getData().getDeptName();
            possess_org_name.setText(orgName);
            possess_dept_name.setText(deptNmae);
            possess_team_name.setVisibility(View.GONE);
            possess_team_qr_code.setVisibility(View.GONE);
            return;
        } else if (!orgId.isEmpty() && deptId.isEmpty() && teamId.isEmpty()) {
            //有组织  无部门  无团队
            not_have_layout.setVisibility(View.GONE);
            possess_org_dept_layout.setVisibility(View.VISIBLE);
            String orgName = userInfo.getData().getOrgName();
            possess_org_name.setText(orgName);
            possess_dept_name.setVisibility(View.GONE);
            possess_team_name.setVisibility(View.GONE);
            possess_team_qr_code.setVisibility(View.GONE);
            return;
        } else if (!teamId.isEmpty() && !orgId.isEmpty() && deptId.isEmpty()) {
            //有团队 有组织  无部门
            not_have_layout.setVisibility(View.GONE);
            possess_org_dept_layout.setVisibility(View.VISIBLE);
            String orgName = userInfo.getData().getOrgName();
            String teamName = userInfo.getData().getTeamName();
            possess_org_name.setText(orgName);
            possess_dept_name.setVisibility(View.GONE);
            possess_team_name.setText(teamName);
            possess_team_name.setVisibility(View.VISIBLE);
            possess_team_qr_code.setVisibility(View.VISIBLE);
            return;
        } else if (!teamId.isEmpty() && orgId.isEmpty() && deptId.isEmpty()) {
            //有团队 有组织  无部门
            not_have_layout.setVisibility(View.GONE);
            possess_org_dept_layout.setVisibility(View.VISIBLE);
            String teamName = userInfo.getData().getTeamName();
            possess_org_name.setVisibility(View.GONE);
            possess_dept_name.setVisibility(View.GONE);

            possess_team_name.setText(teamName);
            possess_team_name.setVisibility(View.VISIBLE);
            possess_team_qr_code.setVisibility(View.VISIBLE);
        } else if (teamId.isEmpty() && orgId.isEmpty() && deptId.isEmpty()) {
            //没有团队 没有组织 没有部门
            not_have_layout.setVisibility(View.VISIBLE);
            possess_org_dept_layout.setVisibility(View.GONE);
        }
    }

    /**
     * 点击部门查看员工
     */
    @OnClick(R.id.possess_dept_name)
    public void possess_dept_name() {
        Intent intent = new Intent(getActivity(), DeptMemberActivity.class);
        intent.putExtra("deptName", CacheUserInfo.get().getDeptName());
        startActivity(intent);
    }

    /**
     * 点击部门二维码
     */
    @OnClick(R.id.possess_qr_code)
    public void possess_qr_code() {
        UserInfo userInfo = CacheUserInfo.get().getUserInfo();
        QRCodeDialog dialog = new QRCodeDialog();
        Bundle bundle = new Bundle();
        bundle.putParcelable(QRCodeDialog.USER_INFO, userInfo);
        bundle.putInt(QRCodeDialog.TAG_TYPE, Fixed.TAG_ADD_ORG_DEPT);
        dialog.setArguments(bundle);
        dialog.show(getActivity().getFragmentManager(), "dialog");
    }

    /**
     * 查看团队成员
     */
    @OnClick(R.id.possess_team_name)
    public void possess_team_name() {
        Intent intent = new Intent(getActivity(), TeamMenberActivity.class);
        intent.putExtra("teamName", CacheUserInfo.get().getTeamName());
        startActivityForResult(intent, Fixed.UPDATE_USER_INFO);
    }

    /**
     * 打开团队二维码
     */
    @OnClick(R.id.possess_team_qr_code)
    public void possess_team_qr_code() {
        UserInfo userInfo = CacheUserInfo.get().getUserInfo();
        QRCodeDialog dialog = new QRCodeDialog();
        Bundle bundle = new Bundle();
        bundle.putParcelable(QRCodeDialog.USER_INFO, userInfo);
        bundle.putInt(QRCodeDialog.TAG_TYPE, Fixed.TAG_ADD_TEAM);
        dialog.setArguments(bundle);
        dialog.show(getActivity().getFragmentManager(), "dialog");
    }


    /**
     * 创建团队
     */
    @OnClick(R.id.have_create_team)
    public void have_create_team() {
        CreateTeamDialog.newInstance().show(getActivity().getFragmentManager(), "");
    }

    /**
     * 扫码加入团队
     */
    @OnClick(R.id.have_add_team)
    public void have_add_team() {
        Intent intent = new Intent(getActivity(), ScanActivity1.class);
        startActivityForResult(intent, Fixed.TAG_SCAN);
    }


    /**
     * 绑定设备
     */
    @OnClick(R.id.aciotn_binding_devices)
    public void aciotn_binding_devices() {
        if (TextUtils.isEmpty(CacheUserInfo.get().getOrgId())) {
            Toast.makeText(getActivity(), "请先加入组织才能绑定设备！", Toast.LENGTH_SHORT).show();
        } else {
            Intent intent = new Intent(getActivity(), ScanActivity1.class);
            startActivityForResult(intent, Fixed.TAG_SCAN);
        }
    }

    /**
     * 打开帮助与反馈
     */
    @OnClick(R.id.action_helper_propose)
    public void action_helper_propose() {
        Intent intent = new Intent(getActivity(), UserHelpActivity.class);
        startActivity(intent);
    }

    /**
     * 关于
     */
    @OnClick(R.id.action_about)
    public void action_about() {
        Intent intent = new Intent(getActivity(), AboutActivity.class);
        startActivity(intent);
    }

    /**
     * 打开设置
     */
    @OnClick(R.id.action_setting)
    public void action_setting() {
        Intent intent = new Intent(getActivity(), UserSettingActivity.class);
        startActivity(intent);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Fixed.TAG_SCAN && resultCode == RESULT_OK) {
            MyQrCode code = data.getParcelableExtra(Fixed.QR_CONTENT);
            qrCodeResult(code);
        }
        presenter.getUserInfo();
    }

    public void qrCodeResult(MyQrCode code) {
        if (code.getType() == Fixed.TAG_ADD_ORG_DEPT) {
            if (CacheUserInfo.get().getDeptId().isEmpty() || CacheUserInfo.get().getOrgId().isEmpty()) {
                String orgId = code.getOrgId();
                String deptId = code.getDeptId();
                String qrCodeUserId = code.getQrCodeUserId();
                presenter.getDevUserRegisterBandOrg(orgId, deptId, qrCodeUserId);
            } else {
                Toast.makeText(App.getApp(), "你已经有部门了", Toast.LENGTH_SHORT).show();
            }
        } else if (code.getType() == Fixed.TAG_ADD_TEAM) {
            String cacheTeamId = CacheUserInfo.get().getTeamId();
            String teamId = code.getTeamId();
            if (cacheTeamId.equals(teamId)) {
                Toast.makeText(App.getApp(), "你已经是该团队成员", Toast.LENGTH_SHORT).show();
            } else {
                if (cacheTeamId.isEmpty()) {
                    presenter.addTeam(teamId);
                } else {
                    updateTeam(code);
                }
            }
        }

        if (code.getDt() == Fixed.TAG_BINDING) {
            String ids = code.getDs();
            String name = code.getDn();
            String phone = code.getPhone();
            BindingDeviceDialog deviceDialog = new BindingDeviceDialog();
            Bundle args = new Bundle();
            args.putString(Fixed.TAG_IDS_VALUE, ids);
            args.putString(Fixed.TAG_NAME_VALUE, name);
            args.putString(Fixed.TAG_PHONE_VALUE, phone);
            deviceDialog.setArguments(args);
            deviceDialog.show(getActivity().getFragmentManager(), "deviceDialog");
        }
    }

    public void updateTeam(MyQrCode code) {
        UpdateTeamDialog update = new UpdateTeamDialog();
        Bundle bundle = new Bundle();
        bundle.putParcelable(Fixed.QR_CONTENT, code);
        update.setArguments(bundle);
        update.setCancelable(false);
        update.show(getActivity().getFragmentManager(), "1");
        update.setOnDeleteListener(new UpdateTeamDialog.onDeleteListener() {
            @Override
            public void confirm() {
                update.dismiss();
                presenter.updateTeam(code.getTeamId());
            }

            @Override
            public void cancel() {
                update.dismiss();
            }
        });
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MessageEvent event) {
        if (event.getSign() == MessageEvent.UPDATE_TEAM) {
            presenter.getUserInfo();
        } else if (event.getSign() == MessageEvent.ADD_TEAN) {
            Intent intent = new Intent(getActivity(), ScanActivity1.class);
            startActivityForResult(intent, Fixed.TAG_SCAN);
        } else if (event.getSign() == MessageEvent.EVENT_CHANGE_BIND_NAME) {
            bind_device_name.setText(CacheConfig.get().getBindDeviceName(CacheUserInfo.get().getUserPhone()));
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
    }

}

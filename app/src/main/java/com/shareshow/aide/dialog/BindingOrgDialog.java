package com.shareshow.aide.dialog;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.GsonBuilder;
import com.shareshow.App;
import com.shareshow.aide.R;
import com.shareshow.aide.adapter.DeptChildAdapter;
import com.shareshow.aide.adapter.OrgParentAdapter;
import com.shareshow.aide.retrofit.entity.OrgAndDept;
import com.shareshow.aide.retrofit.entity.UserInfo;
import com.shareshow.aide.util.CacheUserInfo;
import com.shareshow.aide.util.Fixed;
import com.shareshow.aide.util.uuid.CacheHelper;
import com.shareshow.aide.util.uuid.Devices;

import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;

/**
 * Created by xiongchengguang on 2018/1/2.
 */

public class BindingOrgDialog extends BaseDialog {

    @BindView(R.id.tv_binding_compile)
    public TextView bindingCompile;

    @BindView(R.id.binding_to_org)
    public Spinner spinnerOrg;

    @BindView(R.id.binding_to_dept)
    public Spinner spinnerDept;

    @BindView(R.id.ed_name)
    public EditText edName;

    private OrgParentAdapter orgAdapter;
    private DeptChildAdapter deptAdapter;
    private List<OrgAndDept> orgLists = new ArrayList<>();
    private List<OrgAndDept> deptLists = new ArrayList<>();

    @Override
    public View layout(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.main_dialog_binding_org, container, false);
        ButterKnife.bind(this, view);
        initSpinner();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        DisplayMetrics dm = getResources().getDisplayMetrics();
        int width = (int) (dm.widthPixels * 0.8);
        int height = (int) (dm.heightPixels * 0.6);
        getDialog().getWindow().setLayout(width, height);
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        String scanPhone = getArguments().getString(Fixed.TAG_PHONE_VALUE);
        api.getOrgAndDeptByPhone(scanPhone)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<OrgAndDept>>() {
                    @Override
                    public void accept(List<OrgAndDept> orgAndDepts) throws Exception {
                        for (OrgAndDept orgAndDept : orgAndDepts) {
                            if (orgAndDept.getGiType().equals("3") || orgAndDept.getGiType().equals("31")) {
                                orgLists.add(orgAndDept);
                            }
                        }
                        for (OrgAndDept orgAndDept : orgAndDepts) {
                            if (orgAndDept.getGiType().equals("32")) {
                                deptLists.add(orgAndDept);
                            }
                        }
                        orgAdapter.notifyDataSetChanged();
                        deptAdapter.notifyDataSetChanged();
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        throwable.printStackTrace();
                        Toast.makeText(getActivity(), "获取数据失败", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public void initSpinner() {
        orgAdapter = new OrgParentAdapter(orgLists);
        spinnerOrg.setAdapter(orgAdapter);
        spinnerOrg.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String name = orgLists.get(position).getGiGroupname();
                String groupid = orgLists.get(position).getGiGroupid();
//                queryDept(groupid);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        deptAdapter = new DeptChildAdapter(deptLists);
        spinnerDept.setAdapter(deptAdapter);
    }

    public void queryDept(String groupid) {
        Observable.just(groupid)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .map(new Function<String, List<OrgAndDept>>() {
                    @Override
                    public List<OrgAndDept> apply(String s) throws Exception {
                        //根据公司组织
//                                return orgAndDeptDao.queryBuilder().where(OrgAndDeptDao.Properties.GiParentgroupid.eq(s), OrgAndDeptDao.Properties.GiType.eq(32)).list();
                        return null;
                    }
                })
                .subscribe(new Consumer<List<OrgAndDept>>() {
                    @Override
                    public void accept(List<OrgAndDept> deptList) throws Exception {
//                                presenter.setBackDept(deptList);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Toast.makeText(App.getApp(), "获取部门信息失败", Toast.LENGTH_SHORT).show();
                    }
                });
    }


    @OnClick(R.id.tv_binding_compile)
    public void bindingCompile() {
        String phoneDevNo = Devices.getInstace(new CacheHelper()).getKey();
        String name = edName.getText().toString();
        OrgAndDept org = ((OrgAndDept) spinnerOrg.getSelectedItem());
        OrgAndDept dept = ((OrgAndDept) spinnerDept.getSelectedItem());
        String orgid = (org == null ? "" : org.getGiGroupid());
        String deptid = (dept == null ? "" : dept.getGiGroupid());
        String cachePhone = CacheUserInfo.get().getUserPhone();
        api.registerString(phoneDevNo, name, cachePhone, orgid, deptid)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<ResponseBody>() {
                    @Override
                    public void accept(ResponseBody responseBody) throws Exception {
                        String string = responseBody.string();
                        UserInfo userInfo = new GsonBuilder().serializeNulls().create().fromJson(string, UserInfo.class);
                        if (userInfo.getReturnCode() == 0) {
                            Toast.makeText(getActivity(), "注册失败", Toast.LENGTH_SHORT).show();
                        } else {
                            FileOutputStream out = getActivity().openFileOutput(Fixed.USER_LOGIN, Context.MODE_PRIVATE);
                            out.write(string.getBytes());
                            out.flush();
                            Toast.makeText(getActivity(), "注册完成", Toast.LENGTH_SHORT).show();
                            getDialog().dismiss();
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        throwable.printStackTrace();
                    }
                });
    }

    public void tvRoger(View view) {
        this.dismiss();
    }
}

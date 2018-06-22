package com.shareshow.aide.mvp.model;

import com.shareshow.aide.mvp.presenter.DeptMemberPresenter;
import com.shareshow.aide.retrofit.Api;
import com.shareshow.aide.retrofit.RetrofitProvider;
import com.shareshow.aide.retrofit.entity.DeptMember;
import com.shareshow.aide.retrofit.entity.UserInfo;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;

/**
 * Created by xiongchengguang on 2018/3/20.
 */

public class DeptMemberModel implements BaseModel {
    private DeptMemberPresenter presenter;
    private Api api;

    public DeptMemberModel(DeptMemberPresenter presenter) {
        this.presenter = presenter;
        api = RetrofitProvider.getApi();
    }

    public void getDeptMemberList(String deptId) {
        api.getDeptMemberList(deptId)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<DeptMember>() {
                    @Override
                    public void accept(DeptMember deptMember) throws Exception {
                        presenter.setOnDeptMemberList(deptMember);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        throwable.printStackTrace();
                        presenter.setOnDeptMemberList(null);
                    }
                });
    }

    public void userCheck(String urbrId, int i) {
        api.userCheck(urbrId,i)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<UserInfo>() {
                    @Override
                    public void accept(UserInfo userInfo) throws Exception {
                        presenter.userCheckListener(userInfo);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        throwable.printStackTrace();
                        presenter.userCheckListener(null);
                    }
                });
    }
}

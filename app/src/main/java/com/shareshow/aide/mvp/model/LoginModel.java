package com.shareshow.aide.mvp.model;

import com.shareshow.aide.mvp.presenter.LoginPresnter;
import com.shareshow.aide.retrofit.Api;
import com.shareshow.aide.retrofit.RetrofitProvider;
import com.shareshow.aide.retrofit.entity.UserInfo;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;

/**
 * Created by xiongchengguang on 2018/1/2.
 */

public class LoginModel implements BaseModel {
    private LoginPresnter presenter;
    private Api api;

    public LoginModel(LoginPresnter presenter) {
        this.presenter = presenter;
        api = RetrofitProvider.getApi();
    }

    public void getTestCode(String phone) {
        api.getTestCode(phone)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.newThread())
                .subscribe(new Consumer<ResponseBody>() {
                    @Override
                    public void accept(ResponseBody responseBody) throws Exception {
                        String string = responseBody.string();
                        presenter.setTestCode(string);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        throwable.printStackTrace();
                    }
                });
    }

    public void userLogin(String phone) {
        api.mobileLogin(phone)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.newThread())
                .subscribe(new Consumer<UserInfo>() {
                    @Override
                    public void accept(UserInfo userInfo) throws Exception {
                        presenter.setLoginCompile(userInfo);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        throwable.printStackTrace();
                        presenter.setLoginCompile(null);
                    }
                });

    }

    public void commitUserName(String userPhone, String name) {
        api.updataUserInfo(userPhone, name, "", "")
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.newThread())
                .subscribe(new Consumer<UserInfo>() {
                    @Override
                    public void accept(UserInfo userInfo) throws Exception {
                        presenter.setLoginCompile(userInfo);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        throwable.printStackTrace();
                        presenter.setLoginCompile(null);
                    }
                });
    }
}

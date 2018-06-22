package com.shareshow.aide.mvp.presenter;

import android.graphics.Color;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.jakewharton.rxbinding2.view.RxView;
import com.jakewharton.rxbinding2.widget.RxTextView;
import com.shareshow.App;
import com.shareshow.aide.mvp.model.LoginModel;
import com.shareshow.aide.mvp.view.LoginView;
import com.shareshow.aide.retrofit.entity.UserInfo;
import com.socks.library.KLog;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

import static com.shareshow.aide.util.Fixed.MAX_COUNT_TIME;

/**
 * Created by xiongchengguang on 2018/1/2.
 */

public class LoginPresnter extends BasePresenter<LoginView> {
    private LoginModel model;

    public LoginPresnter() {
        model = new LoginModel(this);
    }

    public void getTestCode(String phone) {
        model.getTestCode(phone);
    }

    public void setTestCode(String testCode) {
        if (isViewAttached()) {
            getView().setTestCode(testCode);
        }
    }

    public void userLoing(String phone) {
        if (isViewAttached()) {
            getView().showLoading();
        }
        model.userLogin(phone);
    }

    public void setLoginCompile(UserInfo userInfo) {
        if (isViewAttached()) {
            getView().hideLoading();
            getView().setLoginCompile(userInfo);
        }
    }

    public void commitUserName(String phone, String name) {
        if (isViewAttached()) {
            getView().showLoading();
            model.commitUserName(phone, name);
        }
    }

    public void gainCode(Button gainCode, String phone) {
        Observable.interval(0, 1, TimeUnit.SECONDS)
                .take(60 + 1)
                .map(new Function<Long, Long>() {
                    @Override
                    public Long apply(Long aLong) throws Exception {
                        return 60 - aLong;
                    }
                })
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(Disposable disposable) throws Exception {
                        gainCode.setEnabled(false);
                        gainCode.setTextColor(Color.parseColor("#b6b7b8"));
                    }
                })
                .subscribe(new Observer<Long>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        KLog.d("发送验证码");
                        getTestCode(phone);
                    }

                    @Override
                    public void onNext(Long aLong) {
                        gainCode.setText("剩余 " + aLong + " 秒");
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {
                        gainCode.setEnabled(true);
                        gainCode.setTextColor(Color.parseColor("#ffffff"));
                        gainCode.setText("获取验证码");
                    }
                });
    }
}

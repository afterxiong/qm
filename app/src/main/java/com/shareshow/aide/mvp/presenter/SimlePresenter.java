package com.shareshow.aide.mvp.presenter;

import android.widget.TextView;
import android.widget.Toast;

import com.shareshow.App;
import com.shareshow.aide.impl.OnResponseResultListener;
import com.shareshow.aide.mvp.model.SimpleModel;
import com.shareshow.aide.mvp.view.SimpleView;
import com.shareshow.aide.retrofit.entity.UserInfo;
import com.shareshow.aide.util.CacheUserInfo;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

import static com.shareshow.aide.mvp.model.SimpleModel.TAG_PROOF_CODE;
import static com.shareshow.aide.mvp.model.SimpleModel.TAG_UPDATE_PHONE_RESULT;

/**
 * Created by xiongchengguang on 2018/3/30.
 */

public class SimlePresenter extends BasePresenter<SimpleView> {

    private SimpleModel model;

    public SimlePresenter() {
        model = new SimpleModel();
    }

    /**
     * 获取验证吗
     */
    public void getProofCode(String phone) {
        model.getProofCode(phone, new OnResponseResultListener() {
            @Override
            public void result(Object obj) {
                if (isViewAttached()) {
                    getView().getResult(TAG_PROOF_CODE, obj);
                }
            }

            @Override
            public void error() {
                if (isViewAttached()) {
                    getView().error(TAG_PROOF_CODE);
                }
                Toast.makeText(App.getApp(), "验证码获取失败", Toast.LENGTH_LONG).show();
            }
        });
    }

    public void getCountdown(TextView countdown) {
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
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(Long aLong) throws Exception {
                        countdown.setText(aLong + "S");
                        if (aLong == 0) {
                            countdown.setText("获取验证码");
                            countdown.setClickable(true);
                        } else {
                            countdown.setClickable(false);
                        }
                    }
                });

    }

    //修改手机号码
    public void updateUserPhone(String newPhone) {
        String oldPhone = CacheUserInfo.get().getUserPhone();
        model.updateUserPhone(oldPhone, newPhone, new OnResponseResultListener() {
            @Override
            public void result(Object obj) {
                UserInfo userInfo = (UserInfo) obj;
                CacheUserInfo.get().setUserInfo(userInfo);
                if (isViewAttached()) {
                    getView().getResult(TAG_UPDATE_PHONE_RESULT, userInfo);
                }
            }

            @Override
            public void error() {
                if (isViewAttached()) {
                    getView().error(TAG_UPDATE_PHONE_RESULT);
                }
            }
        });
    }

    /**
     * 拜访记录提交
     */
    public void setVisitCommit() {
        model.setVisitCommit();
    }
}

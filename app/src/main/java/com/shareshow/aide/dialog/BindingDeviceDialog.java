package com.shareshow.aide.dialog;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.jakewharton.rxbinding2.view.RxView;
import com.jakewharton.rxbinding2.widget.RxTextView;
import com.shareshow.App;
import com.shareshow.aide.R;
import com.shareshow.aide.event.MessageEvent;
import com.shareshow.aide.retrofit.entity.UserInfo;
import com.shareshow.aide.util.CacheConfig;
import com.shareshow.aide.util.CacheUserInfo;
import com.shareshow.aide.util.Fixed;
import com.shareshow.aide.util.uuid.CacheHelper;
import com.shareshow.aide.util.uuid.Devices;
import com.shareshow.airpc.Collocation;
import com.socks.library.KLog;

import org.greenrobot.eventbus.EventBus;

import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;

import static com.shareshow.aide.util.Fixed.MAX_COUNT_TIME;

/**
 * Created by xiongchengguang on 2018/1/3.
 */

public class BindingDeviceDialog extends BaseDialog {

    @BindView(R.id.tv_org)
    public TextView tvOrg;
    @BindView(R.id.tv_phone)
    public TextView tvPhone;
    @BindView(R.id.btn_binding)
    public Button btnBinding;
    @BindView(R.id.tv_ids_tip)
    public TextView tvIdsTip;
    @BindView(R.id.tv_ids_value)
    public TextView tvIdsValue;
    @BindView(R.id.gain_code)
    public TextView gainCode;
    @BindView(R.id.test_code_value)
    public EditText edCode;
    @BindView(R.id.test_code_layout)
    public LinearLayout testCodeLayout;
    @BindView(R.id.test_code_tip)
    public TextView testCodeTip;

    public boolean isRelieveBinding = true;
    public String defaultCode = "123456";

    private String ids = "";
    private String name = "";
    private String phone = "";

    @Override
    public View layout(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.main_dialog_binding_device, container, false);
        ButterKnife.bind(this, view);
        return view;
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);
        ids = getArguments().getString(Fixed.TAG_IDS_VALUE);
        name = getArguments().getString(Fixed.TAG_NAME_VALUE);
        tvIdsValue.setText(ids);
        UserInfo userInfo = CacheUserInfo.get().getUserInfo();
        tvOrg.setText(userInfo.getData().getOrgName());
        tvPhone.setText(userInfo.getData().getUserPhone());
    }

    @OnClick(R.id.btn_binding)
    public void btnBinding() {
        if (testCodeTip.getVisibility() == View.VISIBLE) {
            String code = edCode.getText().toString();
            if (!code.equals(defaultCode)) {
                Toast.makeText(App.getApp(), "验证码不正确", Toast.LENGTH_LONG).show();
                return;
            }
        }

        if (isRelieveBinding){
            binding();
        } else {
            updateBinding();
        }
    }

    /**
     * 绑定设备
     */
    public void binding(){
        String cachePhone = CacheUserInfo.get().getUserPhone();
        String phoneDevNo = Devices.getInstace(new CacheHelper()).getKey();
        api.bindingDevice(ids, cachePhone, phoneDevNo)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<UserInfo>() {
                    @Override
                    public void accept(UserInfo userInfo) throws Exception {
                        if (userInfo.getReturnCode() == 1) {
                            getDialog().dismiss();
                            bindingSuccess();
                        } else if (userInfo.getReturnCode() == 4) {
                            phone = userInfo.getData().getUserPhone();
                            isRelieveBinding = false;
                            testCodeTip.setVisibility(View.VISIBLE);
                            testCodeLayout.setVisibility(View.VISIBLE);
                            Toast.makeText(App.getApp(), "请先解绑原来手机号", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(App.getApp(), "绑定失败", Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        throwable.printStackTrace();
                    }
                });
    }

    /**
     * 更改绑定
     */
    public void updateBinding() {
        api.relieveDevBind(ids)
                .subscribeOn(Schedulers.newThread())
                .subscribe(new Consumer<ResponseBody>() {
                    @Override
                    public void accept(ResponseBody responseBody) throws Exception {
                        String string = responseBody.string();
                        if (string.equals("1")) {
                            binding();
                        } else {
                            Toast.makeText(App.getApp(), "解绑失败", Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        throwable.printStackTrace();
                        Toast.makeText(App.getApp(), "解绑失败", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @OnClick(R.id.gain_code)
    public void gainCode() {
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
                        gainCode.setTextColor(Color.parseColor("#5a84f7"));
                        gainCode.setText("获取验证码");
                    }
                });

    }

    /**
     * 获取验证码
     *
     * @param phone
     */
    public void getTestCode(String phone) {
        api.getTestCode(phone)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.newThread())
                .subscribe(new Consumer<ResponseBody>() {
                    @Override
                    public void accept(ResponseBody responseBody) throws Exception {
                        String string = responseBody.string();
                        defaultCode = string;
                        KLog.d("验证码:" + string);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        throwable.printStackTrace();
                    }
                });
    }

    public void bindingSuccess(){
        Collocation.getCollocation().saveBindIds(ids);
        CacheConfig.get().saveBindDeviceName(CacheUserInfo.get().getUserPhone(), name);
        BindingSuccessDialog dialog = new BindingSuccessDialog();
        dialog.show(getFragmentManager(), "dialog");
        EventBus.getDefault().post(new MessageEvent(MessageEvent.EVENT_CHANGE_BIND_NAME));
    }
}




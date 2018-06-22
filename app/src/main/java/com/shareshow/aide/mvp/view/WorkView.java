package com.shareshow.aide.mvp.view;

import android.app.Activity;

import com.shareshow.aide.retrofit.entity.AmplyNotify;

import java.util.List;

/**
 * Created by xiongchengguang on 2017/12/7.
 */

public interface WorkView extends BaseView {

    /**
     * 拜访资料和学习资料获取成功
     */
    public void onViewSmvmCompile();

    /**
     * 消息通知
     *
     * @param ans
     */
    public void onViewNotificationListener(AmplyNotify ans);

    public void onViewNotificationListener(List<AmplyNotify> notifyList);


    /**
     * 消息通知
     */
    public void onViewNotificationListener(String msg);

    /**
     * 晨会是否签到
     *
     * @param whether
     */
    public void onViewMorningRegister(boolean whether);

    /**
     * 客户拜访次数
     *
     * @param count
     */
    public void onVisitCountChange(int count);

    /**
     * 学习培训次数
     *
     * @param count
     */
    public void onStudycountChange(int count);

    /**
     * 设备绑定状态改变
     * @param isBind
     * @param ids
     */
    public void onIsBindDevChange(boolean isBind,String ids);

    /**
     * 绑定设备 在线状态改变
     * @param isOnline
     */
    public void onBindDevOnlineChange(boolean isOnline);

    /**
     * 投屏状态改变
     * @param isScreenProjection
     */
    public void onScreenProjectionChang(boolean isScreenProjection);

    /**
     * 需要UI层显示toast
     * @param msg
     */
    public void onToastShow(String msg);


    public Activity getBindActivity();

    public boolean isResume();


}

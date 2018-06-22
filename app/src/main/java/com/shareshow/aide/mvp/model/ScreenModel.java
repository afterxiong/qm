package com.shareshow.aide.mvp.model;

import android.app.Activity;

import com.shareshow.aide.mvp.presenter.ScreenPresenter;
import com.shareshow.airpc.model.RootPoint;
import com.shareshow.airpc.socket.command.CommandExectuorMobile;
import com.shareshow.airpc.socket.command.CommandExecutorBox;
import com.shareshow.airpc.socket.command.CommandExecutorLancher;
import com.shareshow.airpc.socket.command.CommandExecutorLancherFile;
import com.shareshow.airpc.socket.command.CommandListenerBox;
import com.shareshow.airpc.socket.command.CommandListenerLancher;
import com.shareshow.airpc.socket.command.CommandListenerMobile;

/**
 * Created by TEST on 2017/12/11.
 * 监听数据回调结果
 */

public class ScreenModel implements BaseModel, CommandListenerLancher, CommandListenerBox, CommandListenerMobile{

    private ScreenPresenter presenter;

    public  ScreenModel(ScreenPresenter presenter, Activity activity){
//        this.presenter =presenter;
//        initCommand(activity);
    }

    private void initCommand(Activity mContext){
        CommandExecutorLancher.getOnlyExecutor().setListener(this);// 20002端口监听
        CommandExecutorBox.getOnlyExecutor().setListener(this);// 20001端口监听
        CommandExectuorMobile.getOnlyExecutor().setListener(this).setWH(mContext);// 20004端口监听
        CommandExecutorLancherFile.getOnlyExecutor();// 启动20003端口
    }

    @Override
    public void searchMobile(RootPoint rootPoint) {
        searchLancher(rootPoint);
    }

    @Override
    public void screenMobile(RootPoint rootPoint) {
        presenter.OnScreenMobile(rootPoint);
    }

    @Override
    public void screenInterruptMobile(RootPoint rootPoint) {
        presenter.OnScreenInterruptMobile(rootPoint);
    }

    @Override
    public void heartBeatMobile(RootPoint mRootPoint) {
         presenter.OnHeartBeatMobile(mRootPoint);
    }

    @Override
    public void swichMobileScreen(RootPoint mRootPoint) {
         presenter.OnSwichMobileScreen(mRootPoint);
    }

    @Override
    public void requestScreenFrame() {
         presenter.OnRequestScreenFrame();
    }

    @Override
    public void searchLancher(RootPoint rootPoint) {
          presenter.OnSearch(rootPoint);
    }

    @Override
    public void connectLancher(RootPoint rootPoint) {
          presenter.OnConnectLancher(rootPoint);
    }

    @Override
    public void controlLancher(RootPoint rootPoint) {
         presenter.OnControlLancher(rootPoint);
    }

    @Override
    public void screenOpenLancher(RootPoint rootPoint) {
         presenter.OnScreenOpenLancher(rootPoint);
    }

    @Override
    public void passwdAlterLancher(RootPoint rootPoint) {
         presenter.OnPasswdAlterLancher(rootPoint);
    }

    @Override
    public void controlHeartBeatLancher(RootPoint rootPoint) {
         presenter.OnControlHeartBeatLancher(rootPoint);
    }

    @Override
    public void touPingPc(RootPoint rp) {
         presenter.OnTouPingPc(rp);
    }

    @Override
    public void stopPc(RootPoint rp) {
         presenter.OnStopPc(rp);
    }

    @Override
    public void pcTouPing(RootPoint rp) {
        presenter.OnPcTouPing(rp);
    }

    @Override
    public void pcCoverShare(RootPoint rp) {
        presenter.OnPcCoverShare(rp);
    }

    @Override
    public void connectBox(RootPoint rootPoint) {
         presenter.OnConnectBox(rootPoint);
    }

    @Override
    public void playBox(RootPoint rootPoint) {
        presenter.OnPlayBox(rootPoint);
    }

    @Override
    public void exitBox(RootPoint rootPoint) {
        presenter.OnExitBox(rootPoint);
    }

    @Override
    public void heartBeatBox(RootPoint rootPoint) {
        presenter.OnHeartBeatBox(rootPoint);
    }

    @Override
    public void screenSuccessBox(RootPoint rootPoint) {
        presenter.OnScreenSuccessBox(rootPoint);
    }

    @Override
    public void screenCoverBox(RootPoint rootPoint) {
        presenter.OnScreenCoverBox(rootPoint);
    }

}

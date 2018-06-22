package com.shareshow.airpc.activity;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.WindowManager;

import com.shareshow.aide.R;
import com.shareshow.aide.activity.MainActivity;
import com.shareshow.airpc.Collocation;
import com.shareshow.airpc.QMCommander;
import com.shareshow.airpc.model.RootPoint;
import com.shareshow.airpc.ports.PassWDCallBack;
import com.shareshow.airpc.socket.command.CommandExecutorLancher;
import com.shareshow.airpc.socket.command.CommandListenerLancher;
import com.shareshow.airpc.socket.common.PassWDValidate;
import com.shareshow.airpc.socket.common.QMDevice;
import com.shareshow.airpc.util.NetworkUtils;
import com.shareshow.airpc.util.QMUtil;

import qiu.niorgai.StatusBarCompat;

/**
 * 文件共享的总父类  ---主要处理密码验证监听、密码改变监听、网络改变监听
 * @author tanwei
 *
 */
public abstract  class ConnectLancherActivity extends AppCompatActivity implements CommandListenerLancher {

	private String pass_input = "";// 输入密码
	private Handler handler=new Handler();
	private boolean isEmptyPasswd;//发送空密码连接消息不要监听回调

	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		initNetChageBroadCast();//网络改变监听事件
		StatusBarCompat.setStatusBarColor(this, ContextCompat.getColor(this, R.color.colorPrimaryDark));
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);//保证屏幕常亮
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		CommandExecutorLancher.getOnlyExecutor().setListener(this);// 20002端口监听
	}
	
	/**
	 * 注册网络变化广播
	 */
	private NetReceiver newReceiver;

	private void initNetChageBroadCast() {
		// 注册网络变化广播
		newReceiver = new NetReceiver();
		IntentFilter intentFilter = new IntentFilter(
				"android.net.conn.CONNECTIVITY_CHANGE");
		intentFilter.setPriority(3);
		registerReceiver(newReceiver, intentFilter);
	}

	private class NetReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (ConnectivityManager.CONNECTIVITY_ACTION.equals(action)) {
				boolean isConnected = NetworkUtils.isNetworkConnected();
				if(QMDevice.getInstance().getAl().size()>0&&!isConnected)
					startActivity(new Intent(ConnectLancherActivity.this,MainActivity.class));
			}
		}
	}
	
	/**
	 * 密码验证
	 */
	public void passwdVertify(RootPoint rp){
		isEmptyPasswd=false;
		String info = Collocation.getCollocation().getPassWd(rp.getUuid());
		rp.setPassword(info);
		// 本地如果没有保存密码的话弹出输入密码对话框
		if (info.equals(""))
			showPasswdDialog(rp);
		else {
			sendVertify(rp, info);
		}
	}
	
	public void sendEmptyPasswdConnect(RootPoint rp){
		isEmptyPasswd=true;
		CommandExecutorLancher.getOnlyExecutor().connectMessage(QMCommander.TYPE_CONNECT,rp.getAddress(), "");
	}
	
	/**
	 * 显示输入密码的对话框
	 */
	private void showPasswdDialog(final RootPoint rp) {
		new PassWDValidate(this).showPassWDInput(new PassWDCallBack() {
			
			@Override
			public void callBack(String inputContent) {
				rp.setPassword(inputContent + "");
				pass_input = inputContent + "";
				sendVertify(rp, inputContent);
			}
		});
	}

	/**
	 * 向任盒发送密码验证
	 * @param rp
	 * @param info
	 */
	private void sendVertify(RootPoint rp,String info){
		QMUtil.getInstance().showProgressDialog(this, R.string.passwd_verify);
		// 本地保存了密码的话，发送密码验证请求，回回调接口为
		CommandExecutorLancher.getOnlyExecutor().connectMessage(
				QMCommander.TYPE_CONNECT,
				rp.getAddress(), info);
		// 用于验证密码请求有没有响应
		handler.postDelayed(passwd, 5000);
	}
	
	/**
	 * 5秒内密码验证是否有响应
	 */
	private Runnable passwd = new Runnable(){

		@Override
		public void run(){
			QMUtil.getInstance().closeDialog();
			QMUtil.getInstance().showToast(ConnectLancherActivity.this, R.string.no_response);
			pass_input = "";
		}
	};

	@Override
	protected void onDestroy(){
		super.onDestroy();
		unregisterReceiver(newReceiver);
	}
	
	/**
	 * 连接
	 */
	public void connectLancher(RootPoint rootPoint){
		if (isEmptyPasswd)
			return;
		final RootPoint rp=QMDevice.getInstance().getSameRootPoint(rootPoint);
		if(rp==null||rp.getdType()!=-1)
			return ;
		handler.removeCallbacks(passwd);
		QMUtil.getInstance().closeDialog();
		if (rootPoint.getLinkstate().equals("true")) {// 密码验证如果对了的话
			if (!pass_input.equals("")){// 要将密码保存到本地，之所以判断pass_input是否为空
				// 是因为pass_input为空的话是绝对没有在弹出输入密码对话框那里进行赋值，而是用户直接取了本地的
				// 密码进行验证的
				Collocation.getCollocation().savePassWd(rp.getUuid(), pass_input);
				rp.setPassword(pass_input);
			}
			passwdVertifyCallBack(rp);
		} else {
			// 密码验证如果错了的话，提示密码错误，进度条消失，并且在此弹出密码输入框
			QMUtil.getInstance().showToast(this, R.string.error_password);
			showPasswdDialog(rp);
		}
		pass_input = "";// 将手动输入的密码数据清空
	}
	
	
	@Override
	public void searchLancher(RootPoint rootPoint) {
		
	}

	@Override
	public void controlLancher(RootPoint rootPoint) {
		
	}

	@Override
	public void screenOpenLancher(RootPoint rootPoint) {
		
	}


	@Override
	public void controlHeartBeatLancher(RootPoint rootPoint) {
		
	}

	@Override
	public void touPingPc(RootPoint rp) {

	}

	@Override
	public void stopPc(RootPoint rp) {

	}

	@Override
	public void pcTouPing(RootPoint rp) {
		CommandExecutorLancher.getOnlyExecutor().connectPcMessage(QMCommander.TYPE_SCREEN_REFUSED , rp.getAddress());
	}

	@Override
	public void pcCoverShare(RootPoint rp) {

	}

	/**
	 * 盒子修改密码退回主页
	 */
	@Override
	public void passwdAlterLancher(RootPoint rootPoint){
		if (QMDevice.getInstance().getSelectDevice()!=null&&QMDevice.getInstance().getSelectDevice().getAddress().equals(rootPoint.getAddress())) {
			int position=0;
			for (int i = 0; i < QMDevice.getInstance().getAl().size(); i++) {
				if(QMDevice.getInstance().getSelectDevice().getAddress().equals(QMDevice.getInstance().getAl().get(i).getAddress()))
					{
						position=i;
						QMDevice.getInstance().getAl().get(i).setEnablepwd(rootPoint.getEnablepwd());
						Collocation.getCollocation()
						.savePassWd(QMDevice.getInstance().getAl().get(i).getUuid(), "");
						break;
					}
			}
			if (rootPoint.getEnablepwd().equals("true")) {
				Intent intent=new Intent(this,MainActivity.class);
				intent.putExtra("aa", 1);
				intent.putExtra("position", position);
				startActivity(intent);
			}
	}else{
		for (int i = 0; i <QMDevice.getInstance().getAl().size(); i++) {
			if(rootPoint.getAddress().equals(QMDevice.getInstance().getAl().get(i).getAddress()))
			{
				QMDevice.getInstance().getAl().get(i).setEnablepwd(rootPoint.getEnablepwd());
				Collocation.getCollocation()
				.savePassWd(QMDevice.getInstance().getAl().get(i).getUuid(), "");
				break;
			}
		}
		}
	}
	
	/**
	 * 密码验证成功的接口
	 * @param rp
	 */
	public abstract void passwdVertifyCallBack(RootPoint rp);
	
}
